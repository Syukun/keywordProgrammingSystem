package generator;


import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import astNode.Expression;
import astNode.MethodInvocation;
import astNode.MethodName;
import dataExtractedFromSource.DataFromSource;

/**
 * @author Archer Shu
 * @date 2019年9月1日
 */
public class MethodInvocationGenerator extends ExpressionGenerator {
	ExpressionGenerator parent;
//	Map<String, Type> typeDictionary;

	public void setParent(ExpressionGenerator expressionGenerator) {
		this.parent = expressionGenerator;
//		this.typeDictionary = parent.dataFromExtraction.getTypeDictionary();
	}

	public Vector<Expression> generateExactExpressionsSub(int depth, String type, String keywords) {
		// inheritance types

//		Set<MethodName> methodNames = parent.dataFromExtraction.getMethodFromReturnType(type);
		
		Set<MethodName> methodNames = DataFromSource.methodsRet.containsKey(type) ? DataFromSource.methodsRet.get(type) : new HashSet<MethodName>();
		// result
		Vector<Expression> res = new Vector<Expression>();

		for (MethodName mthName : methodNames) {
			int parametersNumber = mthName.getParameterNumber();
			int arity = 0;
			if(mthName.isStatic()) {
				
				arity = parametersNumber;
				if(arity==0) {
					if(depth==2) {
						generateStaticWithSubExps(new Expression[] {}, res, mthName);
					}
				}else {
					for (int exactFlags = 1; exactFlags <= (1 << arity) - 1; exactFlags++) {
						Expression[] subExps = new Expression[arity];
						generateStaticWithArityAuxi(depth, arity, exactFlags, res, subExps, mthName, keywords);
					}
				}
				
			}
			
			arity = parametersNumber + 1;

			// a trick to get non-duplicated method invocations with bit computation
			for (int exactFlags = 1; exactFlags <= (1 << arity) - 1; exactFlags++) {
				Expression[] subExps = new Expression[arity];
				generateWithArityAuxi(depth, arity, exactFlags, res, subExps, mthName, keywords);
			}
		}

		return res;
	}

	private void generateStaticWithArityAuxi(int depth, int arity, int exactFlags, Vector<Expression> result,
			Expression[] subExps, MethodName mthName, String keywords) {
			
		if (arity == 0) {
			generateStaticWithSubExps(subExps, result, mthName);
		} else {
//			String elementType = (arity==1)?mthName.getReceiveType():mthName.getParameterTypeOf(arity-1);
			Set<String> elementTypes;
			String paraType = mthName.getParameterTypeOf(arity);
			elementTypes = parent.getAllTypesIncludeSub(paraType);
			Vector<Expression> candidate;
			for (String elementType : elementTypes) {
				if (isBitOn(exactFlags, arity)) {
					candidate = parent.getExactExpressions(depth - 1, elementType, keywords);
				} else {
					if (depth > 2) {
						candidate = parent.getUnderExpressions(depth - 2, elementType, keywords);
					} else {
						candidate = new Vector<Expression>();
					}
				}
				if (candidate.size() > 0) {
					for (Expression c : candidate) {
						subExps[arity - 1] = c;
						generateStaticWithArityAuxi(depth, arity - 1, exactFlags, result, subExps, mthName, keywords);
					}
				}
			}
		}
		
	}

	private void generateWithArityAuxi(int depth, int arity, int exactFlags, Vector<Expression> result,
			Expression[] subExps, MethodName mthName, String keywords) {
		if (arity == 0) {
			generateWithSubExps(subExps, result, mthName);
		} else {
//			String elementType = (arity==1)?mthName.getReceiveType():mthName.getParameterTypeOf(arity-1);
			Set<String> elementTypes;
			if (arity == 1) {
				String receiveType = mthName.getReceiveType();
				elementTypes = parent.getAllTypesIncludeSub(receiveType);
			} else {
				String paraType = mthName.getParameterTypeOf(arity - 1);
				elementTypes = parent.getAllTypesIncludeSub(paraType);
			}
			Vector<Expression> candidate;
			for (String elementType : elementTypes) {
				if (isBitOn(exactFlags, arity - 1)) {
					candidate = parent.getExactExpressions(depth - 1, elementType, keywords);
				} else {
					if (depth > 2) {
						candidate = parent.getUnderExpressions(depth - 2, elementType, keywords);
					} else {
						candidate = new Vector<Expression>();
					}
				}
				if (candidate.size() > 0) {
					for (Expression c : candidate) {
						subExps[arity - 1] = c;
						generateWithArityAuxi(depth, arity - 1, exactFlags, result, subExps, mthName, keywords);
					}
				}
			}
		}

	}

	private void generateStaticWithSubExps(Expression[] subExps, Vector<Expression> result, MethodName mthName) {
		String receiveType = mthName.getReceiveType();
//		int paraNum = mthName.getParameterNumber();
//		Expression[] parameters = new Expression[paraNum];
//		for (int i = 0; i < paraNum; i++) {
//			parameters[i] = subExps[i];
//		}
		MethodInvocation methodInvocation = new MethodInvocation(receiveType, mthName, subExps);
		result.add(methodInvocation);
		
		
	}

	private void generateWithSubExps(Expression[] subExps, Vector<Expression> result, MethodName mthName) {
		Expression receiver = subExps[0];
		int paraNum = mthName.getParameterNumber();
		Expression[] parameters = new Expression[paraNum];
		for (int i = 0; i < paraNum; i++) {
			parameters[i] = subExps[i + 1];
		}
		MethodInvocation methodInvocation = new MethodInvocation(receiver, mthName, parameters);
		result.add(methodInvocation);

	}

}
