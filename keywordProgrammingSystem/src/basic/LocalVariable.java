package basic;

import java.math.BigDecimal;
import java.util.List;

public class LocalVariable {

	String typeName;
	String varName;

	public LocalVariable(String vName, String typeName) {
		this.varName = vName;
		this.typeName = typeName;
	}

	public String toString() {
		return varName;
	}

	public BigDecimal getScore(List<String> keywords) {
		BigDecimal score = ScoreDef.DEFSCORE;
		score = ScoreDef.checkInKeyword(score, this.toString(), keywords);
		return score;
	}

	public String getType() {
		// TODO Auto-generated method stub

		return this.typeName;
	}
}
