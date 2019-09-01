package generator;

import java.util.Vector;

import astNode.Expression;

/**
* @author Archer Shu
* @date 2019年9月1日
*/
public abstract class AbstractGenerator {
	Table tableExact = new Table();
	Table tableUnder = new Table();
	
	public Vector<Expression> getExactExpressions(int depth, String type){
		if(tableExact.isReady(depth, type)) {
			return tableExact.getExpression(depth, type);
		}else {
			/**
			 * generate
			 */
			Vector<Expression> exactExpressions = generateExactExpressionsMain(depth, type);
			
			//TODO sort
			
			/**
			 * set tableExact
			 */
			tableExact.addToTable(type, exactExpressions);
	
			
			return exactExpressions;
		}
	}
	
	public Vector<Expression> getUnderExpressions(int depth, String type){
		if(tableUnder.isReady(depth, type)) {
			return tableUnder.getExpression(depth, type);
		}else {
			/**
			 * get expressions from exact depth
			 */
			Vector<Expression> exactExps = getExactExpressions(depth, type);
			
			/**
			 * set tableUnder and return expressions from tableUnder
			 */
			if(depth == 1) {
				tableUnder.addToTable(type, exactExps);
				return exactExps;
			}else {
				Vector<Expression> lessOneUnderExps = getUnderExpressions(depth-1, type);
				Vector<Expression> underExps = new Vector<Expression>();
				underExps.addAll(lessOneUnderExps);
				underExps.addAll(exactExps);
				tableUnder.addToTable(type, underExps);
				return underExps;
			}
		}
	}
	
	public abstract Vector<Expression> generateExactExpressionsMain(int depth, String type);
	
}
