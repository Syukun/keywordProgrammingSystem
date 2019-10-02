package generator;

import java.util.Set;
import java.util.Vector;

import astNode.Expression;
import astNode.Field;
import astNode.FieldAccess;

/**
 * @author Archer Shu
 * @date 2019年9月1日
 */
public class FieldAccessGenerator extends ExpressionGenerator {

	ExpressionGenerator parent;

	/**
	 * an ugly way to modify the tables in parent class
	 * 
	 * @param expressionGenerator
	 */
	public void setParent(ExpressionGenerator expressionGenerator) {
		this.parent = expressionGenerator;
	}

	public Vector<Expression> generateExactExpressionsSub(int depth, String type, String keywords) {
		// TODO modify this later
//		Field[] fields = { new Field("length", "int", "String"), 
//				new Field("number", "int", "int")};
		Set<String> typesIncludingSuperType = parent.getAllTypesIncludeSuper(type);
		Set<Field> fields = parent.dataFromExtraction.getFieldsFromReceiveType(type);
		Vector<Expression> res = new Vector<Expression>();
		// expressions in depth of exact d minus one
		Vector<Expression> exactM1Exps = new Vector<Expression>();
		//inheritance
		for(String typeIncludingSuperType : typesIncludingSuperType) {
			exactM1Exps.addAll(parent.getExactExpressions(depth-1, typeIncludingSuperType, keywords));
		}
//		Vector<Expression> exactM1Exps = parent.getExactExpressions(depth - 1, type, keywords);
		for (Expression exactM1Exp : exactM1Exps) {
			
			for (Field f : fields) {
				FieldAccess fa = new FieldAccess(exactM1Exp, f);
				res.add(fa);
			}
		}
		return res;
	}

}
