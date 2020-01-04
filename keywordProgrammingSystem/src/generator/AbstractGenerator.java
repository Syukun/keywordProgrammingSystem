package generator;

import java.util.ArrayList;
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
	
	/**
	 * return expressions which return type is "type" and depth is exactly "depth" and also sorted by keywords
	 * @param depth
	 * @param type
	 * @param keywords
	 * @return
	 */
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
			tableExact.addToTable(type, depth,  sortedExactExpressions);
	
			
			return exactExpressions;
		}
	}
	
	/**
	 * return expressions which return type is "type" and depth is under "depth" also sorted by keywords. 
	 * @param depth
	 * @param type
	 * @param keywords
	 * @return
	 */
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
				//sort
				Vector<Expression> sortedExactExpressions = ScoreDef.selectMaxBWExpressions(exactExps, keywords);
				tableUnder.addToTable(type, depth, sortedExactExpressions);
				return exactExps;
			}else {
				Vector<Expression> lessOneUnderExps = getUnderExpressions(depth-1, type, keywords);
				Vector<Expression> underExps = new Vector<Expression>();
				underExps.addAll(lessOneUnderExps);
				underExps.addAll(exactExps);
				//sort
				Vector<Expression> sortedUnderExpressions = ScoreDef.selectMaxBWExpressions(underExps, keywords);
				tableUnder.addToTable(type, depth, sortedUnderExpressions);
				return underExps;
			}
		}
	}
	
	/**
	 * generate expressions which return type is "type" and depth is exactly "depth" and sorted by keywords
	 * @param depth
	 * @param type
	 * @param keywords
	 * @return
	 */
	public abstract Vector<Expression> generateExactExpressionsMain(int depth, String type, String keywords);
	
}
