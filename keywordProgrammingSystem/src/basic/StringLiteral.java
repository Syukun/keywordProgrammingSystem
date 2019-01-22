package basic;

import java.math.BigDecimal;
import java.util.List;

import dataBase.DataBase;

public class StringLiteral extends Literal {

	String sl;
	
	
	public StringLiteral(String sl) {
		super();
		this.sl = sl;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "\""+sl+"\"";
	}

	@Override
	public BigDecimal getScore(List<String> keywords) {
		BigDecimal score = ScoreDef.DEFSCORE;
		new ScoreDef().checkInKeyword(score, sl, keywords);
		return score;
	}

	@Override
	public Type getType() {
		// TODO Auto-generated method stub
		return DataBase.allTypes.get("String");
	}

}
