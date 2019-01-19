package basic;

import java.math.BigDecimal;
import java.util.List;

public class BinaryOperator {
	String binaryOperator;
	boolean isRelationalExpression;
	
	public BinaryOperator(String binaryOperator, boolean isRelationalExpression) {
		super();
		this.binaryOperator = binaryOperator;
		this.isRelationalExpression = isRelationalExpression;
	}

	public String toString() {
		return binaryOperator;
	}

	public BigDecimal getScore(List<String> keywords) {
		BigDecimal score = ScoreDef.DEFSCORE;
		ScoreDef.checkInKeyword(score, binaryOperator, keywords);
		return score;
	}

	public boolean isRelationalExpression() {
		// TODO Auto-generated method stub
		return this.isRelationalExpression;
	}

}
