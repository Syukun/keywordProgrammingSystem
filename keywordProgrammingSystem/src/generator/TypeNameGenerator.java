package generator;

import astNode.Expression;
import astNode.TypeName;

/**
* @author Archer Shu
* @date 2019年9月17日
*/
public class TypeNameGenerator extends ExpressionGenerator {
	ExpressionGenerator parent;
	
	public void setParent(ExpressionGenerator expressionGenerator) {
		this.parent = expressionGenerator;
	}
	
	public Expression generateExactExpressionsSub(int depth, String type) {
		return new TypeName(type);
	}
}
