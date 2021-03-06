package basic;

import java.math.BigDecimal;
import java.util.List;

import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.Type;

public class LocalVariable {

	private Type typeName;
	private String varName;

	public LocalVariable(String vName, Type typeName) {
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
	
	public String getName() {
		return varName;
	}

	public Type getType() {
		// TODO Auto-generated method stub

		return this.typeName;
	}
}
