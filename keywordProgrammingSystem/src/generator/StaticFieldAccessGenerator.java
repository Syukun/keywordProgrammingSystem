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
		if(type == "String") {
			int i = 1;
		}
		
		Set<Field> fields = parent.dataFromExtraction.getFieldsFromReturnType(type);
		for(Field field : fields) {
			
			if(field.isStatic()) {
				String classTypeName = field.getReceiveType();
				res.add(new TypeFieldAccess(classTypeName, field));
			}
		}
		return res;
	}
	
	
}
