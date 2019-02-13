package basic;

import java.math.BigDecimal;
import java.util.List;

import dataBase.DataBase;

public class BinaryConditionalExpression extends Expression {
	Expression leftExpression;
	BinaryOperator binaryOperator;
	Expression rightExpression;

	public BinaryConditionalExpression(Expression leftExpression, BinaryOperator binaryOperator,
			Expression rightExpression) {
		super();
		this.leftExpression = leftExpression;
		this.binaryOperator = binaryOperator;
		this.rightExpression = rightExpression;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return leftExpression.toString() + binaryOperator.toString() + rightExpression.toString();
	}

	@Override
	public BigDecimal getScore(List<String> keywords) {
		BigDecimal score = ScoreDef.DEFSCORE;
		score = score.add(leftExpression.getScore(keywords)).add(binaryOperator.getScore(keywords))
				.add(rightExpression.getScore(keywords));
		return score;
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return binaryOperator.isRelationalExpression ? "boolean" : leftExpression.getType();
	}

}
