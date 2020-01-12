package astNode;

import java.math.BigDecimal;
import java.util.List;

public class ConstructorExpression extends Expression {
	
	private String typeName;
	private Expression[] parameters;
	
	public ConstructorExpression(String typeName) {
		this.typeName = typeName;
		this.parameters = new Expression[] {};
	}
	
	public ConstructorExpression(String typeName, Expression[] parameters) {
		this.typeName = typeName;
		this.parameters = parameters;
	}

	@Override
	public String toString() {
		StringBuffer res = new StringBuffer();
		res.append("new ");
		res.append(this.typeName);
		res.append("(");
		String seperator = "";
		for (Expression exp : parameters) {
			res.append(seperator + exp.toString());
			seperator = ",";
		}
		res.append(")");
		return res.toString();
	}

	@Override
	public BigDecimal getScore(List<String> keywords) {
		BigDecimal score = ScoreDef.DEFSCORE;
		String[] methodNameArray = ScoreDef.splitName("new " + typeName);
		for(String word : methodNameArray) {
			score = score.add(ScoreDef.checkInKeyword(score, word.toLowerCase(), keywords));
		}
		for (Expression paraExpression : this.parameters) {
			score = score.add(paraExpression.getScore(keywords));
		}
		return score;
	}

	@Override
	public String getReturnType() {
		// TODO Auto-generated method stub
		return typeName;
	}
	
	

}
