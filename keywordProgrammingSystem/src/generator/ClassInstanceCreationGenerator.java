 package generator;

import java.util.Vector;

import astNode.ClassInstanceCreation;
import astNode.Expression;

/**
* @author Archer Shu
* @date 2019年9月18日
*/
public class ClassInstanceCreationGenerator extends ExpressionGenerator {
	ExpressionGenerator parent;
	
	public void setParent(ExpressionGenerator expressionGenerator) {
		this.parent=expressionGenerator;
	}
	
	public Vector<Expression> generateExactExpressionsSub(int depth, String type){
		Vector<Expression> res = new Vector<Expression>();
		ClassInstanceCreation classInstanceCreation = new ClassInstanceCreation(type);
		res.add(classInstanceCreation);
		return res;
	}
	
}
