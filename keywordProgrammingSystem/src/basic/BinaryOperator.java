package basic;

import java.math.BigDecimal;
import java.util.List;

public class BinaryOperator {
	public String binaryOperator;
	public boolean isRelationalExpression;
	public String typeName;
	
	public BinaryOperator(String binaryOperator,String type, boolean isRelationalExpression) {
		super();
		this.binaryOperator = binaryOperator;
		this.isRelationalExpression = isRelationalExpression;
		this.typeName = type;
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
