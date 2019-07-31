package astNode;

import java.math.BigDecimal;
import java.util.List;

import basic.ScoreDef;

/**
* @author Archer Shu
* @date 2019/07/30
*/
public class LocalVariable {
/**
 * String str in class ClassA
 */
	
	/**
	 * variable name : str
	 */
	private String varName;
	/**
	 * return type name : String
	 */
	private String typeName;
	
	/**
	 * receive type name : ClassA
	 */
	private String className;
	
	public LocalVariable(String varName, String typeName, String className) {
		this.varName = varName;
		this.typeName = typeName;
		this.className = className;
	}
	
	/**
	 * return shape as "s";
	 * @Override
	 */
	public String toString(){
		return this.varName;
	}
	
	public String getReceiveType() {
		return this.className;
	}
	
	public String getReturnType() {
		return this.typeName;
	}
	
	public BigDecimal getScore(List<String> keywords) {
		BigDecimal score = ScoreDef.DEFSCORE;
		score = ScoreDef.checkInKeyword(score, this.toString(), keywords);
		return score;
	}
	
	
}
