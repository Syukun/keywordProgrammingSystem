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
			if (exp != null) {
				res.append(seperator + exp.toString());
				seperator = ",";
			}
		}
		res.append(")");
		return res.toString();
	}

	@Override
	public BigDecimal getScore(List<String> keywords) {
		BigDecimal score = ScoreDef.DEFSCORE;
		String words = "new " + typeName;
		score = score.add(ScoreDef.checkInKeyword(score, words, keywords));

		if (this.parameters != null) {
			for (Expression paraExpression : this.parameters) {
				if (paraExpression != null) {
					try {
						score = score.add(paraExpression.getScore(keywords));
					} catch (NullPointerException e) {
						int i = 0;
					}
				}

			}
		}

		return score;
	}

	@Override
	public String getReturnType() {
		// TODO Auto-generated method stub
		return typeName;
	}

	@Override
	public String toPredictString() {
		StringBuffer res = new StringBuffer();
		res.append("new " + this.typeName);
		for(Expression paraExp: this.parameters) {
			res.append(" " + paraExp.toPredictString());
		}
		return res.toString();
	}

}
