package generator;

import java.util.HashMap;
import java.util.HashSet;
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
		Set<String> allTypes = new HashSet<String>(); 
		allTypes.add("void");
//		String[] allTypes = {"String","int","boolean"};
		this.table = new HashMap<String, Vector<Vector<Expression>>>();
		for(String type : allTypes) {
			Vector<Vector<Expression>> expsFromEachDepth = new Vector<Vector<Expression>>();
			this.table.put(type, expsFromEachDepth);
		}
	}
	
	
	public boolean isReady(int depth, String type) {
		if(!table.containsKey(type)) {
			this.addNewKey(type);
			return false;
		}
		int maxDepth = table.get(type).size();
		return maxDepth>=depth;		
	}
	
	/**
	 * get expression which return type is "type" and depth is "depth"
	 * @param depth
	 * @param type
	 * @return
	 */
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
	
	public void addNewKey(String type) {
		if(!this.table.containsKey(type)) {
			Vector<Vector<Expression>> expsFromEachDepth = new Vector<Vector<Expression>>();
			this.table.put(type, expsFromEachDepth);
		}
	}
}
