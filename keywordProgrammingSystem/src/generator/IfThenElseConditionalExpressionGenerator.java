package generator;

import java.util.Vector;

import astNode.Expression;
import astNode.IfThenElseConditionalExpression;

/**
 * @author Archer Shu
 * @date 2019年9月3日
 */
public class IfThenElseConditionalExpressionGenerator extends ExpressionGenerator {
	ExpressionGenerator parent;

	public void setParent(ExpressionGenerator expressionGenerator) {
		this.parent = expressionGenerator;
	}

	public Vector<Expression> generateExactExpressionsSub(int depth, String type) {
		// result
		Vector<Expression> res = new Vector<Expression>();

		int arity = 3;
		for (int exactFlags = 1; exactFlags <= (1 << arity) - 1; exactFlags++) {
			Expression[] subExps = new Expression[arity];
			generateWithArityAuxi(depth, arity, exactFlags, res, subExps, type);
		}
		
		return res;
	}

	private void generateWithArityAuxi(int depth, int arity, int exactFlags, Vector<Expression> result,
			Expression[] subExps, String type) {
		if(arity == 0) {
			generateWithSubExps(subExps, result);
		}else {
			String elementType = (arity == 1)? "boolean" : type;
			
			Vector<Expression> candidate;
			if(isBitOn(exactFlags, arity-1)) {
				candidate = parent.getExactExpressions(depth-1, elementType);
			}else {
				if(depth>2) {
					candidate = parent.getUnderExpressions(depth-2, elementType);
				}else {
					candidate = new Vector<Expression>();
				}
			}
			
			if(candidate.size()>0) {
				for(Expression c : candidate) {
					subExps[arity-1] = c;
					generateWithArityAuxi(depth, arity-1, exactFlags, result, subExps, type);
				}
			}
			
			
		}
		
	}

	private void generateWithSubExps(Expression[] subExps, Vector<Expression> result) {
		IfThenElseConditionalExpression ifThenElseConditionalExpression = new IfThenElseConditionalExpression(subExps[0],subExps[1],subExps[2]);
		result.add(ifThenElseConditionalExpression);
	}
}
