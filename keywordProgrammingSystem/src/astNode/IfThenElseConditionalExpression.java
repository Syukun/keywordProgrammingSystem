package astNode;

import java.math.BigDecimal;
import java.util.List;

/**
* @author Archer Shu
* @date 2019年9月3日
*/
public class IfThenElseConditionalExpression extends Expression{

	Expression ifExpression;
	Expression thenExpression;
	Expression elseExpression;
	
	
	public IfThenElseConditionalExpression(Expression ifExpression, Expression thenExpression, Expression elseExpression) {
		this.ifExpression = ifExpression;
		this.thenExpression = thenExpression;
		this.elseExpression = elseExpression;
	}
	
	@Override
	public String toString() {
		
		return "if" + "(" + ifExpression.toString() + ")"
				+ "?" + thenExpression.toString() + ":" + elseExpression.toString();
	}

	@Override
	public BigDecimal getScore(List<String> keywords) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getReturnType() {
		String thenExpressionType = thenExpression.getReturnType();
		String elseExpressionType = elseExpression.getReturnType();
		if(thenExpressionType.equals(elseExpressionType)) {
			return thenExpressionType;
		}
		return null;
	}

}
