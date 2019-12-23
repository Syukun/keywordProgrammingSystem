package astNode;

import java.math.BigDecimal;
import java.util.List;

import org.eclipse.jdt.core.Flags;

/**
* @author Archer Shu
* @date 2019/07/30
*/
public class Field {
	private String classSimpleName;
	private String typeSimpleName;
	private String fieldName;
	private int modifier;
	
	
	public Field(int modifier, String fieldName, String typeName, String className) {
		this.modifier = modifier;
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
	
	public boolean isPublic() {
		return Flags.isPublic(this.modifier);
	}
	
	public boolean isPrivate() {
		return Flags.isPrivate(modifier);
	}
	
	public boolean isProtected() {
		return Flags.isProtected(this.modifier);
	}
	
	public boolean isStatic() {
		return Flags.isStatic(modifier);
	}
	
	public boolean isAbstract() {
		return Flags.isAbstract(modifier);
	}

}
