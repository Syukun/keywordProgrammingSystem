package basic;

import java.math.BigDecimal;
import java.util.List;

public class FieldName {
	public String fieldName;
	public String typeName;
	// the class or interface this field belongs to
	public TypeName type;
	
	public FieldName(String fieldName,String typeName,TypeName type) {
		this.fieldName = fieldName;
		this.typeName = typeName;
		this.type = type;
	}
	
	public String toString() {
		return fieldName;
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
