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

	public Vector<Expression> generateExactExpressionsSub(int depth, String type) {
		Vector<Expression> res = new Vector<Expression>();
		Set<MethodName> methodNames = DataFromSource.methodsRet.containsKey(type) ? DataFromSource.methodsRet.get(type)
				: new HashSet<MethodName>();
		if (depth == 2) {
			for (MethodName methodName : methodNames) {
				int parameterNumber = methodName.getParameterNumber();
				if (parameterNumber < 4) {
					Expression[] subExps = new Expression[parameterNumber + 1];
					Vector<Expression> methodInvocationsInDepthTwo = new Vector<Expression>();
					generateNonStaticMethodInvocation(parameterNumber + 1, methodName, subExps,
							methodInvocationsInDepthTwo);
					res.addAll(methodInvocationsInDepthTwo);
				}
			}
		}

		return res;
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
				Vector<Expression> exactDepthM1Expression = ExpressionGenerator.tableExact.getExpression(1, recT);
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
				Vector<Expression> exactDepthM1Expression = ExpressionGenerator.tableExact.getExpression(1, paraT);
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
		if(parameterNumber == 1) {
			MethodInvocation res = new MethodInvocation(subExps[0], methodName, new Expression[] {});
			methodInvocationsInDepthTwo.add(res);
		}else {
			MethodInvocation res = new MethodInvocation(subExps[0], methodName,
					Arrays.copyOfRange(subExps, 1, subExps.length));
			methodInvocationsInDepthTwo.add(res);
		}
	}

}
