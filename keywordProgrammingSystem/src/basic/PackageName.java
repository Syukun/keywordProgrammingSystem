package basic;

import java.math.BigDecimal;
import java.util.List;

public class PackageName {

	String packageName;
	public PackageName(String packageName) {
		this.packageName = packageName;
	}

	public String toString() {
		return packageName;
	}

	public BigDecimal getScore(List<String> keywords) {
		BigDecimal score = ScoreDef.DEFSCORE;
		score = ScoreDef.checkInKeyword(score, this.toString(), keywords);
		return score;
	}


}
