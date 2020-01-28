package generatorForGraduation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import astNode.Expression;
import astNode.MethodInvocation;
import astNode.MethodName;
import dataExtractedFromSource.DataFromSource;

public class NonStaticMethodInvocationGenertor extends ExpressionGenerator {

	public Vector<Expression> generateExactExpressionsSub(int depth, String type, String keywords) {
		Vector<Expression> res = new Vector<Expression>();
		Set<MethodName> methodNames = DataFromSource.methodsRet.containsKey(type) ? DataFromSource.methodsRet.get(type)
				: new HashSet<MethodName>();

		if (depth == 2) {
			for (MethodName methodName : methodNames) {
				
				int parameterNumber = methodName.getParameterNumber();
				if (parameterNumber < 3) {
					Expression[] subExps = new Expression[parameterNumber + 1];
					Vector<Expression> methodInvocationsInDepthTwo = new Vector<Expression>();
					generateNonStaticMethodInvocation(parameterNumber + 1, methodName, subExps,
							methodInvocationsInDepthTwo);
					res.addAll(methodInvocationsInDepthTwo);
				}
			}
		}

		if (depth > 2) {
			for (MethodName methodName : methodNames) {
				int parameterNumber = methodName.getParameterNumber();
				if (parameterNumber < 3) {
					
					int arity = parameterNumber + 1;
					for (int exactFlags = 1; exactFlags <= (1 << arity) - 1; exactFlags++) {
						Vector<Expression> methodInvocationsMoreThanDepthTwo = new Vector<Expression>();
						Expression[] subExps = new Expression[arity];
						generateNonStaticMethodInvocationMoreThanDepthTwo(depth, arity, exactFlags, methodName,
								methodInvocationsMoreThanDepthTwo, subExps, keywords);
						res.addAll(methodInvocationsMoreThanDepthTwo);
					}
				}
				

			}
		}

		return res;
	}

	private void generateNonStaticMethodInvocationMoreThanDepthTwo(int depth, int arity, int exactFlags,
			MethodName methodName, Vector<Expression> methodInvocationsMoreThanDepthTwo, Expression[] subExps,
			String keywords) {
		if (arity == 0) {
			generateNonStaticMethodInvocationWithSubExpressions(methodName, subExps, methodInvocationsMoreThanDepthTwo);
		}

		if (arity == 1) {
			String receiverType = methodName.getReceiveType();
			Set<String> receiverTypesIncludeSub = getAllTypesIncludeSub(receiverType);
			Vector<Expression> candidate = new Vector<Expression>();
			if (isBitOn(exactFlags, 0)) {
				for (String recT : receiverTypesIncludeSub) {
					Vector<Expression> exactDepthM1Expression = new Vector<Expression>();
					exactDepthM1Expression.addAll(ExpressionGenerator.tableExact.getExpression(depth - 1,
							recT));
					candidate.addAll(exactDepthM1Expression);
				}
			} else {
				for (String recT : receiverTypesIncludeSub) {
					Vector<Expression> underDepthM2Expression = new Vector<Expression>();
					underDepthM2Expression.addAll(ExpressionGenerator.tableUnder.getExpression(depth - 2,
							recT));
					candidate.addAll(underDepthM2Expression);
				}
			}

			for (Expression receiverExpression : candidate) {
				Expression[] subExpTemp = subExps.clone();
				subExpTemp[0] = receiverExpression;
				generateNonStaticMethodInvocationMoreThanDepthTwo(depth, 0, exactFlags, methodName,
						methodInvocationsMoreThanDepthTwo, subExpTemp, keywords);
			}
		}
		
		if(arity > 1) {
			String parameterType = methodName.getParameterTypeOf(arity - 2);
			Set<String> parameterTypeIncludeSub = getAllTypesIncludeSub(parameterType);
			Vector<Expression> candidate = new Vector<Expression>();
			
			if (isBitOn(exactFlags, arity-1)) {
				for (String paraT : parameterTypeIncludeSub) {
					Vector<Expression> exactDepthM1Expression = new Vector<Expression>();
					exactDepthM1Expression.addAll(ExpressionGenerator.tableExact.getExpression(depth - 1, paraT));
					candidate.addAll(exactDepthM1Expression);
				}
			} else {
				for (String paraT : parameterTypeIncludeSub) {
					Vector<Expression> underDepthM2Expression = new Vector<Expression>();
					underDepthM2Expression.addAll(ExpressionGenerator.tableUnder.getExpression(depth - 2, paraT));
					candidate.addAll(underDepthM2Expression);
				}
			}
			
			for(Expression parameterExpression : candidate) {
				Expression[] subExpTemp = subExps.clone();
				subExpTemp[arity - 1] = parameterExpression;
				generateNonStaticMethodInvocationMoreThanDepthTwo(depth, arity - 1, exactFlags, methodName,
						methodInvocationsMoreThanDepthTwo, subExpTemp, keywords);
			}
			
		}

	}

	private void generateNonStaticMethodInvocation(int arity, MethodName methodName, Expression[] subExps,
			Vector<Expression> methodInvocationsInDepthTwo) {
		if (arity == 0) {
			generateNonStaticMethodInvocationWithSubExpressions(methodName, subExps, methodInvocationsInDepthTwo);
		}

		if (arity == 1) {
			String receiverType = methodName.getReceiveType();
			Set<String> receiverTypesIncludeSub = getAllTypesIncludeSub(receiverType);
			Vector<Expression> receiverExpressions = new Vector<Expression>();
			for (String recT : receiverTypesIncludeSub) {
				Vector<Expression> exactDepthM1Expression = new Vector<Expression>();
				exactDepthM1Expression.addAll(ExpressionGenerator.tableExact.getExpression(1, recT));
				receiverExpressions.addAll(exactDepthM1Expression);
			}

			for (Expression receiverExpression : receiverExpressions) {
				Expression[] subExpTemp = subExps.clone();
				subExpTemp[0] = receiverExpression;
				generateNonStaticMethodInvocation(0, methodName, subExpTemp, methodInvocationsInDepthTwo);
			}

		}

		if (arity > 1) {
			String parameterType = methodName.getParameterTypeOf(arity - 2);
			Set<String> parameterTypeIncludeSub = getAllTypesIncludeSub(parameterType);
			Vector<Expression> parameterExpressions = new Vector<Expression>();
			for (String paraT : parameterTypeIncludeSub) {
				Vector<Expression> exactDepthM1Expression = new Vector<Expression>();
				exactDepthM1Expression.addAll(ExpressionGenerator.tableExact.getExpression(1, paraT));
				parameterExpressions.addAll(exactDepthM1Expression);
			}

			for (Expression parameterExpression : parameterExpressions) {
				Expression[] subExpTemp = subExps.clone();
				subExpTemp[arity - 1] = parameterExpression;
				generateNonStaticMethodInvocation(arity - 1, methodName, subExpTemp, methodInvocationsInDepthTwo);
			}
		}

	}

	private void generateNonStaticMethodInvocationWithSubExpressions(MethodName methodName, Expression[] subExps,
			Vector<Expression> methodInvocationsInDepthTwo) {
		int parameterNumber = subExps.length;
		if (parameterNumber == 1) {
			MethodInvocation res = new MethodInvocation(subExps[0], methodName, new Expression[] {});
			methodInvocationsInDepthTwo.add(res);
		} else {
			MethodInvocation res = new MethodInvocation(subExps[0], methodName,
					Arrays.copyOfRange(subExps, 1, subExps.length));
			methodInvocationsInDepthTwo.add(res);
		}
	}

}
