package generator;

import java.util.Vector;

import astNode.Expression;

/**
* @author Archer Shu
* @date 2019/08/20
*/
public abstract class AbsGenerator {
	Table table;
	
	public Vector<Expression> getExpressionsUnderDepthFromTable(int depth, String type) {
		if(table.isUnderReady(depth, type)) {
			return table.getExpressionsUnderDepth(depth, type);
		}else {
			Vector<Expression> exactExps;
			if(table.isExactReady(depth, type)) {
				exactExps = getExpressionsExactDepthFromTable(depth, type);
			}else {
				exactExps = generateExactExpressionMain(depth, type);
				exactExps.sort(keywords);
				// TODO setExactReady();
				table.addToExactTable(depth, type, exactExps);
			}
			
			if(depth == 1) {
				//TODO setUnderReady();
				table.addToUnderTable(depth, type, exactExps);
				return exactExps;
			}else {
				Vector<Expression> underOneExps = getExpressionsUnderDepthFromTable(depth-1, type);
				Vector<Expression> underExps = getTopTenExps(exactExps, underOneExps);
				//TODO setUnderReady();
				table.addToUnderTable(depth, type, underExps);
				return underExps;
			}
		}
	}
	
	public Vector<Expression> getExpressionsExactDepthFromTable(int depth, String type){
		if(table.isExactReady(depth, type)) {
			return table.getExpressionsExactDepth(depth, type);
		}else {
			Vector<Expression> exactExps = generateExactExpressionsMain(depth, type);
			exactExps.sort(keywords);
			//TODO setExactReady();
			table.addToExactTable(depth, type, exactExps);
			return exactExps;
		}
	}

	public abstract Vector<Expression> generateExactExpressionsMain(int depth, String type);
	
	public abstract Vector<Expression> generateExactExpressionsSub(int depth, String type);
}
