package generator;

import java.util.Map;
import java.util.Set;
import java.util.Vector;

import astNode.Expression;

/**
* @author Archer Shu
* @date 2019/08/20
*/
public class Table {
	
	Map<String, Vector<Expression>> table;
	
	public Table(Set<String> allTypes, int depth) {
		for(String type : allTypes) {
			Vector<Expression> exps = new Vector<Expression>();
			table.put(type, exps);
		}
	}
	
	public Vector<Expression> getExpressions(int depth, String type){
		return table.get(type).get(depth-1).getExpression();
	}
	
	public void addToTable(int depth, String type, Vector<Expression> exps) {
		table.get(type).get(depth)
	}
	//TODO maybe not use this method
	public void setReady(int depth, String type) {
		table.get(type).get(depth-1).setReady(true);
	}
}
