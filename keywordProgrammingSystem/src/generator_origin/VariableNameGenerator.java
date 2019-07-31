package generator_origin;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import basic.Expression;
import basic.Type;
import basic.VariableName;
import dataBase.DataBase;

public class VariableNameGenerator extends ExpressionGenerator implements GeneratorWithMultipleReturnType {

	VariableName varName;

	public VariableNameGenerator() {

	}

	public VariableNameGenerator(VariableName varName) {
		this.varName = varName;
	}


	/**
	 * add all local variables from dataFromSourceFile
	 */
	public Vector<ExpressionGenerator> getAllSubGeneratorWithTypeT(String t) {
		Vector<ExpressionGenerator> result = new Vector<ExpressionGenerator>();
		for (VariableName varName : DataBase.allVariableName) {
			if (varName.getType().equals(t)) {
				VariableNameGenerator vName = new VariableNameGenerator(varName) {
					@Override
					public void generateWithSubExps(Expression[] subExps, Vector<Expression> result) {
						result.add(this.varName);
					}
				};
				result.add(vName);
			}

		}
		return result;
	}


}
