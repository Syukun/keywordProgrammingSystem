package generator;

import java.util.Vector;

import basic.Expression;
import basic.MethodInvocation;
import basic.MethodName;
import basic.Type;
import dataBase.DataBase;

public class MethodInvocationGenerator extends ExpressionGenerator {
	Vector<MethodName> methodNames;
	
	@Override
	public void changeProperty(String t) {
		Vector<MethodName> res = new Vector<MethodName>();
		for(MethodName methodName : DataBase.allMethodNames) {
			if(t.equals(methodName.getReceiveType().toString())) {
				res.add(methodName);
			}
		}
		this.methodNames = res;
	}
	
	@Override
	public void generateExpressionExact(int d, Vector<Expression> result) {
		for(MethodName mname : this.methodNames) {
			new ExpressionGenerator() {
				@Override
				public Generator[] getParameterGenerators() {
					int paraNum = mname.getParaNumber();
					Generator[] res = new Generator[paraNum];
					for(int i=0; i<paraNum ; i++) {
						res[i] = new ExpressionGenerator();
					}
					return res;
				}
				
				@Override
				public String[] getParameterTypes() {
					return mname.getParameterTypes();
				}
				
				@Override
				public void generateWithSubExps(Expression[] subExps,Vector<Expression> result) {
					Expression expFront = subExps[0];
					int length = subExps.length;
					Expression[] expsBack = new Expression[length-1];
					for(int i = 0 ; i < length-1 ; i++) {
						expsBack[i] = subExps[i+1];
					}
					result.add(new MethodInvocation(expFront,expsBack,mname));
				}
			}.generateExpressionExact(d, result);
		}
	}
}
