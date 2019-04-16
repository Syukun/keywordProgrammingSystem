package basic;

import java.math.BigDecimal;
import java.util.List;

public class TypeName {
	public String typeName;
	public PackageName packageName;
	
	public TypeName(String typeName) {
		this.typeName = typeName;
	}
	
	public TypeName(String typeName,PackageName packageName) {
		this.typeName = typeName;
		this.packageName = packageName;
	}

	public String toString() {
		return typeName;
	}

	public BigDecimal getScore(List<String> keywords) {
		BigDecimal score = ScoreDef.DEFSCORE;
		score = ScoreDef.checkInKeyword(score, this.toString(), keywords);
		return score;
	}
	
	// did not consider the sub class yet

}
