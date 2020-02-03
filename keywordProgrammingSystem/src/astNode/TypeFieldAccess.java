 package astNode;

import java.math.BigDecimal;
import java.util.List;

public class TypeFieldAccess extends Expression{
	String type;
	Field field;
	
	public TypeFieldAccess() {
		
	}

	public TypeFieldAccess(String type, Field field) {
		super();
		this.type = type;
		this.field = field;
	}
	
	public String toString() {
		return type + "." + field.toString();
	}
	public BigDecimal getScore(List<String> keywords) {
		BigDecimal score = ScoreDef.DEFSCORE;
//		score = score.add(this.expression.getScore(keywords));
//		score = score.add(this.field.getScore(keywords));
		score = score.add(new TypeName(type).getScore(keywords)).add(this.field.getScore(keywords));
		return score;
	}

	@Override
	public String getReturnType() {
		// TODO Auto-generated method stub
		return field.getReturnType();
	}

	@Override
	public String toPredictString() {
		// TODO Auto-generated method stub
		return type + " " + this.field.toString();
	}
}
