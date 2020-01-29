package astNode;

import java.math.BigDecimal;
import java.util.List;

/**
* @author Archer Shu
* @date 2019年9月1日
*/
public class TypeName extends Expression {
	String typeName;

	public TypeName(String typeName) {
		this.typeName = typeName;
	}
	
	public String toString() {
		// TODO Auto-generated method stub
		return this.typeName;
	}

	@Override
	public BigDecimal getScore(List<String> keywords) {
		BigDecimal score = ScoreDef.DEFSCORE;
		score = ScoreDef.checkInKeyword_LocalVariable(score, this.toString(), keywords);
		return score;
	}

	@Override
	public String getReturnType() {
		// TODO Auto-generated method stub
		return this.typeName;
	}

	@Override
	public String toPredictString() {
		// TODO Auto-generated method stub
		return this.toString();
	}


}
