package generator;

import java.util.Vector;

import astNode.Expression;

/**
* @author Archer Shu
* @date 2019年8月20日
*/
public class ExpressionGenerator extends AbsGenerator {

	FieldAccessGenerator fieldAccessGenerator;
	MethodInvocationGenerator methodInvocationGenerator;
	
	//TODO import allTypes;
	String[] allTypes = {"String", "Integer"};
	
	public Vector<Expression> getAllExpressionsUnderDepth(int depth){
		Vector<Expression> res = new Vector<Expression>();
		
		for(String type : allTypes) {
			res.addAll(getExpressionsUnderDepthFromTable(depth, type));
		}
		
		return res;
	}
	
	@Override
	public Vector<Expression> generateExactExpressionsMain(int depth, String type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector<Expression> generateExactExpressionsSub(int depth, String type) {
		// TODO Auto-generated method stub
		return null;
	}

}
