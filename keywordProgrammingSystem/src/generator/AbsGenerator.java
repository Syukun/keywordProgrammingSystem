package generator;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import astNode.Expression;

/**
* @author Archer Shu
* @date 2019/08/20
*/
public abstract class AbsGenerator {
	Table tableUnder;
	Table tableExact;
	
	public Vector<Expression> getExpressionsUnderDepthFromTable(int depth, String type) {
		
		if(tableUnder.isReady(depth, type)) {
			return getExpressionsUnder(depth, type);
		}else {
			Vector<Expression> exactExps;
			if(tableExact.isReady(depth, type)) {
				exactExps = getExpressionsExact(depth, type);
			}else {
				exactExps = generateExactExpressionsMain(depth, type);
//				exactExps.sort(keywords);
				tableExact.addToTable(type, exactExps);
			}
			
			if(depth == 1) {
				tableUnder.addToTable(type, exactExps);
				return exactExps;
			}else {
				Vector<Expression> lessOneExpsUnder = getExpressionsUnder(depth, type);
				Vector<Expression> expsUnder = getTopTenExps(exactExps, lessOneExpsUnder);
				tableUnder.addToTable(type, expsUnder);
				return expsUnder;
			}
		}
		
	}



	public Vector<Expression> getExpressionsUnder(int depth, String type) {
		return tableUnder.getExpression(depth, type);
	}



	public Vector<Expression> getExpressionsExact(int depth, String type) {
		return tableExact.getExpression(depth, type);
	}
	
	

	private Vector<Expression> getTopTenExps(Vector<Expression> exactExps, Vector<Expression> lessOneExpsUnder) {
		// TODO modify it later
		exactExps.addAll(lessOneExpsUnder);
		return exactExps;
	}


	public abstract Vector<Expression> generateExactExpressionsMain(int depth, String type);
	
	public abstract Vector<Expression> generateExactExpressionsSub(int depth, String type);
	

}
