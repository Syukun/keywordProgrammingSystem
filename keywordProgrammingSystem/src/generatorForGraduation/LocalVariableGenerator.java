package generatorForGraduation;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import astNode.Expression;
import astNode.LocalVariable;
import dataExtractedFromSource.DataFromSource;

public class LocalVariableGenerator extends ExpressionGenerator {

	public Vector<Expression> generateExactExpressionsSub(int depth, String type) {
		Vector<Expression> res = new Vector<Expression>();
		Set<LocalVariable> localVariables = DataFromSource.localVariablesRet.containsKey(type)
				? DataFromSource.localVariablesRet.get(type)
				: new HashSet<LocalVariable>();
		for (LocalVariable localVariable : localVariables) {
			res.add(localVariable);
		}
		return res;
	}
	
}
