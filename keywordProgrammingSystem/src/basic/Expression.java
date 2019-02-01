package basic;

import java.math.BigDecimal;
import java.util.List;

public abstract class Expression {
	public abstract String toString();
	public abstract BigDecimal getScore(List<String> keywords);
	public abstract String getType();
	public BigDecimal getScore(String keywords) {
		return this.getScore(ScoreDef.splitKeyword(keywords));
	}
}
