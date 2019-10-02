package generator;

import java.util.Set;
import java.util.Vector;

import astNode.Expression;
import astNode.MethodInvocation;
import astNode.MethodName;

/**
* @author Archer Shu
* @date 2019年9月1日
*/
public class MethodInvocationGenerator extends ExpressionGenerator {
	ExpressionGenerator parent;
	
	public void setParent(ExpressionGenerator expressionGenerator) {
		this.parent = expressionGenerator;
	}
	
	public Vector<Expression> generateExactExpressionsSub(int depth, String type, String keywords){
		//get all method name information from database later
//		MethodName methodName1 = new MethodName("concat","String","String",new String[] {"String"});
//		MethodName methodName2 = new MethodName("add","int","int",new String[] {"int","int"});
//		MethodName methodName3 = new MethodName("addzero", "String", "String", new String[] {"String","int"});
//		MethodName[] methodNames = {methodName1
//				};
//				,methodName2,methodName3};
		
		Set<MethodName> methodNames = parent.dataFromExtraction.getMethodFromReturnType(type);
		//result
		Vector<Expression> res = new Vector<Expression>();
		
//		Vector<MethodName> methodNamesReturnEqualTypes = new Vector<MethodName>();
//		for(MethodName methodName : methodNames) {
//			if(methodName.getReturnType() == type) {
//				methodNamesReturnEqualTypes.add(methodName);
//			}
//		}
		
		for(MethodName mthName : methodNames) {
			int parametersNumber = mthName.getParameterNumber();
			int arity = parametersNumber+1;
			
			// a trick to get non-duplicated method invocations with bit computation
			for(int exactFlags = 1; exactFlags <= (1 << arity)-1 ; exactFlags++) {
				Expression[] subExps = new Expression[arity];
				generateWithArityAuxi(depth, arity, exactFlags, res, subExps,mthName, keywords);
			}
		}
		
		
		
		return res;
	}

	private void generateWithArityAuxi(int depth, int arity, int exactFlags, Vector<Expression> result,
			Expression[] subExps, MethodName mthName, String keywords) {
		if(arity == 0) {
			generateWithSubExps(subExps,result, mthName);
		}else {
			String elementType = (arity==1)?mthName.getReceiveType():mthName.getParameterTypeOf(arity-1);
			Vector<Expression> candidate;
			if(isBitOn(exactFlags, arity-1)) {
				candidate = parent.getExactExpressions(depth-1, elementType, keywords);
			}else {
				if(depth>2) {
					candidate = parent.getUnderExpressions(depth-2, elementType, keywords);
				}else {
					candidate = new Vector<Expression>();
				}
			}
			if(candidate.size()>0) {
				for(Expression c : candidate) {
					subExps[arity-1] = c;
					generateWithArityAuxi(depth, arity-1, exactFlags, result, subExps, mthName, keywords);
				}
			}
			
		}
		
	}
	

	private void generateWithSubExps(Expression[] subExps, Vector<Expression> result, MethodName mthName) {
		Expression receiver = subExps[0];
		int paraNum = mthName.getParameterNumber();
		Expression[] parameters = new Expression[paraNum];
		for(int i = 0; i < paraNum ; i++) {
			parameters[i] = subExps[i+1];
		}
		MethodInvocation methodInvocation = new MethodInvocation(receiver,mthName,parameters);
		result.add(methodInvocation);
		
	}
	
	
}
