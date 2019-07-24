package generator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import basic.Expression;
import basic.Type;

public class Table {
	
	Map<String, Vector<Vector<Expression>>> root_table;
	
	public void initTable(int depth, Set<String> allTypeFS) {
		this.root_table = new HashMap<String, Vector<Vector<Expression>>>();
		for(String t: allTypeFS) {
			Vector<Vector<Expression>> expsForAllDepth = new Vector<Vector<Expression>> ();
			for(int d=0 ; d<=depth ; d++) {
				Vector<Expression> expsForEachDepth = new Vector<Expression>();
				expsForAllDepth.add(expsForEachDepth);
			}
			this.root_table.put(t, expsForAllDepth);
		}
	}

	
}
