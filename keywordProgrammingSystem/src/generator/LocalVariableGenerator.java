package generator;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import astNode.Expression;
import astNode.LocalVariable;
import dataExtractedFromSource.DataFromSource;

/**
 * @author Archer Shu
 * @date 2019年9月19日
 */
public class LocalVariableGenerator extends ExpressionGenerator {
	ExpressionGenerator parent;

	public void setParent(ExpressionGenerator expressionGenerator) {
		this.parent = expressionGenerator;
	}

	/**
	 * generate local variable expressions which return type is "type" and depth is
	 * exactly "depth"
	 * 
	 * @param depth
	 * @param type
	 * @return
	 */
	public Vector<Expression> generateExactExpressionsSub(int depth, String type) {
		Vector<Expression> res = new Vector<Expression>();
//		Set<LocalVariable> localVariables = parent.dataFromExtraction.getLocalVariablesFromRetType(type);
		Set<LocalVariable> localVariables = DataFromSource.localVariablesRet.containsKey(type)
				? DataFromSource.localVariablesRet.get(type)
				: new HashSet<LocalVariable>();
		for (LocalVariable localVariable : localVariables) {
			res.add(localVariable);
		}
		return res;
	}
}
