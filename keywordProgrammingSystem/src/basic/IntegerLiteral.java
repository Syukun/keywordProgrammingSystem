package basic;

import java.math.BigDecimal;
import java.util.List;

import dataBase.DataBase;

public class IntegerLiteral extends Literal {
	int i;
	
	
	public IntegerLiteral(int i) {
		super();
		this.i = i;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return i+"";
	}

	@Override
	public BigDecimal getScore(List<String> keywords) {
		BigDecimal score = ScoreDef.DEFSCORE;
		ScoreDef.checkInKeyword(score, this.toString(), keywords);
		return score;
	}

	@Override
	public Type getType() {
		// TODO Auto-generated method stub
		return DataBase.allTypes.get("Integer");
	}

}
