package basic;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class MethodInvocation extends Expression{
	public Expression expressionFront;
	public MethodName methodName;
	public Expression[] expressionsBack;
	
	public MethodInvocation(Expression expFront,Expression[] expsBack,MethodName methodName) {
		this.methodName = methodName;
		this.expressionFront = expFront;
		this.expressionsBack = expsBack;
	}
	
	public String toString() {
		StringBuffer result = new StringBuffer("");
		if(expressionFront != null) {
			result.append(expressionFront.toString());
			result.append(".");
		}
		result.append(methodName.toString()+"(");
		String seperator = "";
		for(Expression exp: expressionsBack) {
			result.append(exp.toString()+seperator);
			seperator = ",";
		}
		result.append(")");
		
		return result.toString();
	}

	@Override
	public BigDecimal getScore(List<String> keywords) {
		BigDecimal score = ScoreDef.DEFSCORE;
		score = score.add(expressionFront.getScore(keywords)).add(methodName.getScore(keywords));
		for(Expression exp : expressionsBack) {
			score = score.add(exp.getScore(keywords));
		}
		return score;
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return this.methodName.getReturnType();
	}
	
	
}
