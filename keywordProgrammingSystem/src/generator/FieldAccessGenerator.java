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
		Vector<Expression> res = new Vector<Expression>();
		
		Set<Field> fields = parent.dataFromExtraction.getFieldsFromReturnType(type);
		
		for(Field field : fields) {
			String fieldClassName = field.getReceiveType();
			Set<String> typesIncludingSuperType = parent.getAllTypesIncludeSub(fieldClassName);
			// expressions in depth of exact d minus one
			Vector<Expression> exactM1Exps = new Vector<Expression>();
			//inheritance
			for(String typeIncludingSuperType : typesIncludingSuperType) {
				exactM1Exps.addAll(parent.getExactExpressions(depth-1, typeIncludingSuperType, keywords));
			}
			
			for(Expression exactM1Exp : exactM1Exps) {
				FieldAccess fieldAccess = new FieldAccess(exactM1Exp,field);
				res.add(fieldAccess);
			}
			
		}
		return res;
	}

}
