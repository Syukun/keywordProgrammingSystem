package generator;

import java.util.Set;
import java.util.Vector;

import astNode.Expression;
import astNode.Field;
import astNode.TypeFieldAccess;

public class StaticFieldAccessGenerator extends ExpressionGenerator {
	

	ExpressionGenerator parent;
	
	public void setParent(ExpressionGenerator expressionGenerator) {
		this.parent = expressionGenerator;
	}
	
	public Vector<Expression> generateExactExpressionsSub(int depth, String type, String keywords){
		Vector<Expression> res = new Vector<Expression>();
		
		Set<Field> fields = parent.dataFromExtraction.getFieldsFromReturnType(type);
		for(Field field : fields) {
			if(field.isStatic()) {
				res.add(new TypeFieldAccess(type, field));
			}
		}
		return res;
	}
	
	
}
