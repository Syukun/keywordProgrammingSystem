package generator;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import basic.Expression;
import basic.StringLiteral;
import basic.Type;
import dataBase.DataBase;

public class StringLiteralGenerator extends ExpressionGenerator {

	@Override
	public Generator[] getParameterGenerators() {
		// TODO Auto-generated method stub
		return new Generator[] {};
	}

	@Override
	public String[] getParameterTypes() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Set<String> getAllPossibleReceiveTypes(){
		Set<String> allPossibleReceiveType = new HashSet<String>();
		allPossibleReceiveType.add("String");
		return allPossibleReceiveType;
	}

	@Override
	public void generateWithSubExps(Expression[] subExps, Vector<Expression> result) {
		for(StringLiteral stringLiteral : DataBase.allStringLiterals) {
			result.add(stringLiteral);
		}

	}

}
