package astNode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Archer Shu
 * @date 2019/08/01
 */
public class FieldAccess extends Expression {

	Expression expression;
	Field field;

	public FieldAccess() {

	}

	public FieldAccess(Expression expression, Field field) {
		super();
		this.expression = expression;
		this.field = field;
	}

	/**
	 * shape is like "this.foo"
	 * 
	 * @Override
	 */
	public String toString() {
		return this.expression.toString() + "." + field.toString();
	}

	@Override
	public BigDecimal getScore(List<String> keywords) {
		BigDecimal score = ScoreDef.DEFSCORE;
//		score = score.add(this.expression.getScore(keywords));
//		score = score.add(this.field.getScore(keywords));
		score = score.add(this.expression.getScore(keywords)).add(this.field.getScore(keywords));
		return score;
	}

	@Override
	public String getReturnType() {
		return this.field.getReturnType();
	}

	/**
	 * Don't have a receive type
	 */
	public String getReceiveType() {
		return null;
	}

	@Override
	public String toPredictString() {
		
		return this.expression.toPredictString() + " " + this.field.toString();
	}

}
