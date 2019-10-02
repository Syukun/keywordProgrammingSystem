package generator;

import java.util.Vector;

import astNode.Expression;
import astNode.ScoreDef;

/**
* @author Archer Shu
* @date 2019年9月1日
*/
public abstract class AbstractGenerator {
	Table tableExact = new Table();
	Table tableUnder = new Table();
	
	public Vector<Expression> getExactExpressions(int depth, String type,String keywords){
		if(tableExact.isReady(depth, type)) {
			return tableExact.getExpression(depth, type);
		}else {
			/**
			 * generate
			 */
			Vector<Expression> exactExpressions = generateExactExpressionsMain(depth, type, keywords);
			
			//TODO sort
			Vector<Expression> sortedExactExpressions = ScoreDef.selectMaxBWExpressions(exactExpressions, keywords);
			/**
			 * set tableExact
			 */
			tableExact.addToTable(type, sortedExactExpressions);
	
			
			return exactExpressions;
		}
	}
	
	public Vector<Expression> getUnderExpressions(int depth, String type, String keywords){
		if(tableUnder.isReady(depth, type)) {
			return tableUnder.getExpression(depth, type);
		}else {
			/**
			 * get expressions from exact depth
			 */
			Vector<Expression> exactExps = getExactExpressions(depth, type, keywords);
			
			/**
			 * set tableUnder and return expressions from tableUnder
			 */
			if(depth == 1) {
//				tableUnder.addNewKey(type);
				Vector<Expression> sortedExactExpressions = ScoreDef.selectMaxBWExpressions(exactExps, keywords);
				tableUnder.addToTable(type, sortedExactExpressions);
				return exactExps;
			}else {
				Vector<Expression> lessOneUnderExps = getUnderExpressions(depth-1, type, keywords);
				Vector<Expression> underExps = new Vector<Expression>();
				underExps.addAll(lessOneUnderExps);
				underExps.addAll(exactExps);
				Vector<Expression> sortedUnderExpressions = ScoreDef.selectMaxBWExpressions(underExps, keywords);
				tableUnder.addToTable(type, sortedUnderExpressions);
				return underExps;
			}
		}
	}
	
	public abstract Vector<Expression> generateExactExpressionsMain(int depth, String type, String keywords);
	
}
