package generator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import astNode.Expression;

/**
* @author Archer Shu
* @date 2019/08/20
*/
public class Table {
	
	Map<String, Vector<Vector<Expression>>> table;
	
	public Table() {
		String[] allTypes = {"String","int","boolean"};
		this.table = new HashMap<String, Vector<Vector<Expression>>>();
		for(String type : allTypes) {
			Vector<Vector<Expression>> expsFromEachDepth = new Vector<Vector<Expression>>();
			this.table.put(type, expsFromEachDepth);
		}
	}
	
	
	public boolean isReady(int depth, String type) {
		int maxDepth = table.get(type).size();
		return maxDepth>=depth;		
	}
	
	public Vector<Expression> getExpression(int depth, String type){
		return table.get(type).get(depth-1);
	}
	
	public void addToTable(String type, Vector<Expression> expressions) {
		
		Vector<Vector<Expression>> expressionsForEachDepth = this.table.get(type);
		if(expressions.isEmpty()) {
			expressionsForEachDepth.add(new Vector<Expression>());
		}else {
			expressionsForEachDepth.add(expressions);
		}
	}
}
