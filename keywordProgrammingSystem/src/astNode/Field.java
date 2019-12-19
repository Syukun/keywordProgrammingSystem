package astNode;

import java.math.BigDecimal;
import java.util.List;

/**
* @author Archer Shu
* @date 2019/07/30
*/
public class Field {
	private String classSimpleName;
	private String typeSimpleName;
	private String fieldName;
	
	
	public Field(String fieldName, String typeName, String className) {
		this.fieldName = fieldName;
		this.typeSimpleName = typeName;
		this.classSimpleName = className;
	}
	
	public String toString() {
		return this.fieldName;
	}
	
	public String getReceiveType() {
		return this.classSimpleName;
	}
	
	public String getReturnType() {
		return this.typeSimpleName;
	}
	
	public BigDecimal getScore(List<String> keywords) {
		BigDecimal score = ScoreDef.ZERO;
		score = ScoreDef.checkInKeyword(score, this.toString(), keywords);
		return score;
	}

}
