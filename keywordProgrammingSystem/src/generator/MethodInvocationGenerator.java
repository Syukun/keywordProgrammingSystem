package generator;

import java.util.Collection;
import java.util.HashSet;
import java.util.Vector;

import basic.Expression;
import basic.MethodInvocation;
import basic.MethodName;
import basic.Type;
import dataBase.DataBase;

public class MethodInvocationGenerator extends ExpressionGenerator implements GeneratorWithMultipleReturnType {
	MethodName methodName;


	public MethodInvocationGenerator() {

	}
	
	public MethodInvocationGenerator(Table expsLEQDepth_Table,Table expsAtExactDepth_Table) {
		this.expsLEQDepth_Table = expsLEQDepth_Table;
		this.expsAtExactDepth_Table = expsAtExactDepth_Table;
	}

	public MethodInvocationGenerator(MethodName methodName,Table expsLEQDepth_Table,Table expsAtExactDepth_Table) {
		this.methodName = methodName;
		this.expsLEQDepth_Table = expsLEQDepth_Table;
		this.expsAtExactDepth_Table = expsAtExactDepth_Table;

	}

	@Override
	public Vector<ExpressionGenerator> getAllSubGeneratorWithTypeT(String t) {
		Vector<ExpressionGenerator> result = new Vector<ExpressionGenerator>();
		for (MethodName mName : DataBase.allMethodNames) {
			if (mName.getReturnType().equals(t)) {
				Table table1 = this.expsLEQDepth_Table;
				Table table2 = this.expsAtExactDepth_Table;
				MethodInvocationGenerator methodInv = new MethodInvocationGenerator(mName,table1,table2) {
					@Override
					public String getReturnType() {
						return mName.getReturnType();
					}

					@Override
					public String[] getParameterTypes() {
						return mName.getParameterTypes();
					}

					@Override
					public Generator[] getParameterGenerators() {
						int paraNum = mName.getParaNumber();
						Generator[] res = new Generator[paraNum];
						for (int i = 0; i < paraNum; i++) {
							res[i] = new ExpressionGenerator();
						}
						return res;
					}

					@Override
					public void generateWithSubExps(Expression[] subExps, Vector<Expression> result) {
						Expression expFront;
						Expression[] expsBack;
						expFront = subExps[0];
						int length = subExps.length;
						expsBack = new Expression[length - 1];
						for (int i = 0; i < length - 1; i++) {
							expsBack[i] = subExps[i + 1];
						}

						result.add(new MethodInvocation(expFront, expsBack, mName));
					}
				};
				result.add(methodInv);
			}
		}
		return result;
	}

}
