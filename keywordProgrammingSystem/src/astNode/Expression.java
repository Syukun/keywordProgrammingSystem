package astNode;

import java.math.BigDecimal;
import java.util.List;

import basic.ScoreDef;

/**
* @author Archer Shu
* @date 2019/05/22
*/
public abstract class Expression {
	/**
	 * Expression ==> String
	 */
	public abstract String toString();
	/**
	 * get score of an Expression according to keywords
	 * @param keywords
	 * @return
	 */
	public abstract BigDecimal getScore(List<String> keywords);
	/**
	 * get return type of this expression
	 * @return String
	 */
	public abstract String getReturnType();
	/**
	 * get receive type of this expression
	 * @return receive type : String
	 */
	public abstract String getReceiveType();
	
	/**
	 * TODO is this needed??
	 */
	public BigDecimal getScore(String keywords) {
		return this.getScore(ScoreDef.splitKeyword(keywords));
	}
	
}
