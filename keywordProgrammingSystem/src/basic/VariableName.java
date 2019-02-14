package basic;

import java.math.BigDecimal;
import java.util.List;

import dataBase.DataBase;

public class VariableName extends Expression{
	String varName;
	String typeName;
	
	public VariableName(String vName,String typeName) {
		this.varName = vName;
		this.typeName = typeName;
	}
	
	public String toString() {
		return varName;
	}
	
	@Override
	public BigDecimal getScore(List<String> keywords) {
		BigDecimal score = ScoreDef.DEFSCORE;
		score = ScoreDef.checkInKeyword(score, this.toString(), keywords);
		return score;
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		
		return this.typeName;
	}
}
