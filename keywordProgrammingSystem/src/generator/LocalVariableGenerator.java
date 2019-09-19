package generator;

import java.util.Set;
import java.util.Vector;

import astNode.Expression;
import astNode.LocalVariable;

/**
* @author Archer Shu
* @date 2019年9月19日
*/
public class LocalVariableGenerator extends ExpressionGenerator {
	ExpressionGenerator parent;
	
	public void setParent(ExpressionGenerator expressionGenerator) {
		this.parent = expressionGenerator;
	}
	
	public Vector<Expression> generateExactExpressionsSub(int depth, String type){
		Vector<Expression> res = new Vector<Expression>();
		Set<LocalVariable> localVariables = parent.dataFromExtraction.getLocalVariablesFromRetType(type);
		for(LocalVariable localVariable : localVariables) {
			res.add(localVariable);
		}
		return res;
	}
}
