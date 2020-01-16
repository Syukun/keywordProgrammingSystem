package generatorForGraduation;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import astNode.Expression;
import astNode.MethodInvocation;
import astNode.MethodName;
import dataExtractedFromSource.DataFromSource;

public class StaticMethodInvocationGenerator extends ExpressionGenerator {

	public Vector<Expression> generateExactExpressionsSub(int depth, String type, String keywords) {
		Vector<Expression> res = new Vector<Expression>();
		Set<MethodName> methodNames = DataFromSource.methodsRet.containsKey(type) ? DataFromSource.methodsRet.get(type)
				: new HashSet<MethodName>();
		if (depth == 2) {
			for (MethodName methodName : methodNames) {
				if (methodName.isStatic()) {
					int parameterNumber = methodName.getParameterNumber();
					if (parameterNumber < 4) {
						Expression[] subExps = new Expression[parameterNumber];
						Vector<Expression> methodInvocationsInDepthTwo = new Vector<Expression>();
						generateStaticMethodInvocation(parameterNumber, methodName, subExps,
								methodInvocationsInDepthTwo);
						res.addAll(methodInvocationsInDepthTwo);
					}
				}
			}
		}

		if (depth > 2) {
			for (MethodName methodName : methodNames) {
				if (methodName.isStatic()) {
					int parameterNumber = methodName.getParameterNumber();
					if (parameterNumber < 4) {
						Vector<Expression> methodInvocationsMoreThanDepthTwo = new Vector<Expression>();
						for (int exactFlags = 1; exactFlags <= (1 << parameterNumber) - 1; exactFlags++) {
							Expression[] subExps = new Expression[parameterNumber];
							generateStaticMethodInvocationMoreThanDepthTwo(depth, parameterNumber, exactFlags,
									methodName, methodInvocationsMoreThanDepthTwo, subExps, keywords);
						}
						res.addAll(methodInvocationsMoreThanDepthTwo);
					}
				}
			}
		}
		return res;
	}

	private void generateStaticMethodInvocationMoreThanDepthTwo(int depth, int arity, int exactFlags,
			MethodName methodName, Vector<Expression> methodInvocationsMoreThanDepthTwo, Expression[] subExps,
			String keywords) {
		if(arity == 0 ) {
			generateStaticMethodInvocationWithSubExpressions(methodName, subExps, methodInvocationsMoreThanDepthTwo);
		}else {
			String parameterType = methodName.getParameterTypeOf(arity - 1);
			Set<String> parameterTypeIncludeSub = getAllTypesIncludeSub(parameterType);
			Vector<Expression> candidate = new Vector<Expression>();
			if (isBitOn(exactFlags, arity-1)) {
				for (String paraT : parameterTypeIncludeSub) {
					Vector<Expression> exactDepthM1Expression = ExpressionGenerator.tableExact.getExpression(depth - 1, paraT);
					candidate.addAll(exactDepthM1Expression);
				}
			} else {
				for (String paraT : parameterTypeIncludeSub) {
					Vector<Expression> underDepthM2Expression = ExpressionGenerator.tableUnder.getExpression(depth - 2, paraT);
					candidate.addAll(underDepthM2Expression);
				}
			}
			
			for(Expression parameterExpression : candidate) {
				Expression[] subExpTemp = subExps.clone();
				subExpTemp[arity - 1] = parameterExpression;
				generateStaticMethodInvocationMoreThanDepthTwo(depth, arity-1, exactFlags, methodName, methodInvocationsMoreThanDepthTwo,subExpTemp,keywords);
			}
		}
		
	}

	private void generateStaticMethodInvocation(int arity, MethodName methodName, Expression[] subExps,
			Vector<Expression> methodInvocationsInDepthTwo) {
		if (arity == 0) {
			generateStaticMethodInvocationWithSubExpressions(methodName, subExps, methodInvocationsInDepthTwo);
		} else {
			String parameterType = methodName.getParameterTypeOf(arity - 1);
			Set<String> parameterTypeIncludeSub = getAllTypesIncludeSub(parameterType);
			Vector<Expression> parameterExpressions = new Vector<Expression>();
			for (String paraT : parameterTypeIncludeSub) {
				Vector<Expression> exactDepthM1Expression = ExpressionGenerator.tableExact.getExpression(1, paraT);
				parameterExpressions.addAll(exactDepthM1Expression);
			}

			for (Expression parameterExpression : parameterExpressions) {
				Expression[] subExpTemp = subExps.clone();
				subExpTemp[arity - 1] = parameterExpression;
				generateStaticMethodInvocation(arity - 1, methodName, subExpTemp, methodInvocationsInDepthTwo);
			}
		}

	}

	private void generateStaticMethodInvocationWithSubExpressions(MethodName methodName, Expression[] subExps,
			Vector<Expression> methodInvocationsInDepthTwo) {
	
			String receiverType = methodName.getReceiveType();
			MethodInvocation res = new MethodInvocation(receiverType, methodName, subExps);
			methodInvocationsInDepthTwo.add(res);
		

	}

}
