package generator;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import basic.Expression;
import basic.IntegerLiteral;
import basic.Type;
import dataBase.DataBase;

public class IntLiteralGenerator extends ExpressionGenerator {

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
		allPossibleReceiveType.add("Integer");
		return allPossibleReceiveType;
	}

	@Override
	public void generateWithSubExps(Expression[] subExps, Vector<Expression> result) {
		// TODO Auto-generated method stub
		for(IntegerLiteral integerLiteral : DataBase.allIntegerLiterals) {
			result.add(integerLiteral);
		}
	}

}
