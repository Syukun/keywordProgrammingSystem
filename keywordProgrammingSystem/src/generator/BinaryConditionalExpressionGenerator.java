package generator;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import basic.BinaryConditionalExpression;
import basic.BinaryOperator;
import basic.Expression;
import basic.Type;
import dataBase.DataBase;

public class BinaryConditionalExpressionGenerator extends ExpressionGenerator
		implements GeneratorWithMultipleReturnType {
	BinaryOperator binOperator;

	public BinaryConditionalExpressionGenerator() {

	}
	
	public BinaryConditionalExpressionGenerator(Table expsLEQDepth_Table,Table expsAtExactDepth_Table) {
		this.expsLEQDepth_Table = expsLEQDepth_Table;
		this.expsAtExactDepth_Table = expsAtExactDepth_Table;
	}

	public BinaryConditionalExpressionGenerator(BinaryOperator binOperator,Table expsLEQDepth_Table,Table expsAtExactDepth_Table) {
		this.binOperator = binOperator;
		this.expsLEQDepth_Table = expsLEQDepth_Table;
		this.expsAtExactDepth_Table = expsAtExactDepth_Table;
	}

	@Override
	public Vector<ExpressionGenerator> getAllSubGeneratorWithTypeT(String t) {
		Vector<ExpressionGenerator> result = new Vector<ExpressionGenerator>();
		for (BinaryOperator binOperator : DataBase.allBinaryOperators) {
			if (binOperator.getReturnType().equals(t)) {
				Table table1 = this.expsLEQDepth_Table;
				Table table2 = this.expsAtExactDepth_Table;
				BinaryConditionalExpressionGenerator binExp = new BinaryConditionalExpressionGenerator(binOperator,table1,table2) {
					@Override
					public String getReturnType() {
						return binOperator.getReturnType();
					}

					@Override
					public String[] getParameterTypes() {
						String paraT = binOperator.typeName;
						return new String[] { paraT, paraT };
					}

					@Override
					public Generator[] getParameterGenerators() {
						return new Generator[] { new ExpressionGenerator(), new ExpressionGenerator() };
					}
					
					@Override
					public void generateWithSubExps(Expression[] subExps, Vector<Expression> result) {
						result.add(new BinaryConditionalExpression(subExps[0], binOperator, subExps[1]));
					}
				};
				result.add(binExp);
			}

		}
		return result;
	}
	
	public int getArity() {
		// TODO Auto-generated method stub
		return 2;
	}


}
