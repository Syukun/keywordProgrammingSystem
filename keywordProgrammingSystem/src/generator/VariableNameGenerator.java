package generator;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import basic.Expression;
import basic.Type;
import basic.VariableName;
import dataBase.DataBase;

public class VariableNameGenerator extends ExpressionGenerator {

	Vector<VariableName> varNames;
	
	@Override
	public void changeProperty(String t) {
		Vector<VariableName> result = new Vector<VariableName>();
		for(VariableName varName : DataBase.allVariableName) {
			// need to be modified later
			if(varName.getType().toString().equals(t)) {
				result.add(varName);
			}
		}
		this.varNames = result;
	}
	
	@Override
	public Generator[] getParameterGenerators() {
		// TODO Auto-generated method stub
		return new Generator[] {};
	}

	@Override
	public Type[] getParameterTypes() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void generateWithSubExps(Expression[] subExps, Vector<Expression> result) {
		result.addAll(this.varNames);
	}
	
	
}
