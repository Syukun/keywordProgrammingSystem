package generator;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import basic.BinaryConditionalExpression;
import basic.BinaryOperator;
import basic.Expression;
import basic.Type;
import dataBase.DataBase;

public class BinaryConditionalExpressionGenerator extends ExpressionGenerator {
	Vector<BinaryOperator> binOperators;
	
//	public Generator[] getParameterGenerators() {
//		return new Generator[] {
//				new ExpressionGenerator(),new ExpressionGenerator()
//		};
//	}
//	
//	public Vector<Type> getParameterTypes(){
//		return null;
//	}
	
	@Override
	public void changeProperty(String t) {
		Vector<BinaryOperator> result = new Vector<BinaryOperator>();
		for(BinaryOperator binOperator : DataBase.allBinaryOperators) {
			if(binOperator.isRelationalExpression) {
				if(t == "boolean") {
					result.add(binOperator);
				}
			}else {
				if(t == binOperator.typeName) {
					result.add(binOperator);
				}
			}
		}
		this.binOperators = result;
	}

	@Override
	public void generateExpressionExact(int d, Vector<Expression> result) {
		for(BinaryOperator binOperator : this.binOperators) {
			new ExpressionGenerator() {
				@Override
				public Generator[] getParameterGenerators() {
					return new Generator[] {
							new ExpressionGenerator(),new ExpressionGenerator()
					};
				}
				@Override
				public Type[] getParameterTypes(){
					Type t = DataBase.allTypes.get(binOperator.typeName);
					return new Type[] {t,t};
				}
				@Override
				public void generateWithSubExps(Expression[] subExps,Vector<Expression> result) {
					result.add(new BinaryConditionalExpression(subExps[0],binOperator,subExps[1]));
				}
				
			}.generateExpressionExact(d, result);
		}
	}
	
	
	
}
