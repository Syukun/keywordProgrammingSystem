package astNode;

import java.math.BigDecimal;
import java.util.List;

/**
* @author Archer Shu
* @date 2019/08/01
*/
public class FieldAccess extends Expression{

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
	 * @Override
	 */	
	public String toString() {
		return this.expression.toString() + "." + field.toString();
	}

	@Override
	public BigDecimal getScore(List<String> keywords) {
		// TODO Auto-generated method stub
		return this.expression.getScore(keywords);
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
	
}
