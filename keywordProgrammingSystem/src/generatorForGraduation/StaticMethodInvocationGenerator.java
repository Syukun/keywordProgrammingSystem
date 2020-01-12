package generatorForGraduation;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import astNode.Expression;
import astNode.MethodInvocation;
import astNode.MethodName;
import dataExtractedFromSource.DataFromSource;

public class StaticMethodInvocationGenerator extends ExpressionGenerator {

	public Vector<Expression> generateExactExpressionsSub(int depth, String type) {
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
		return res;
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
