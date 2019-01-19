package generator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import basic.Expression;
import basic.Type;

public class Table {
	
	Map<String, Vector<Vector<Expression>>> root_table;
	
	public static void initTable(Table table, int depth, Set<Type>allReceiverType) {
		table.root_table = new HashMap<String, Vector<Vector<Expression>>>();
		for(Type t: allReceiverType) {
			Vector<Vector<Expression>> expsForAllDepth = new Vector<Vector<Expression>> ();
			for(int d=0 ; d<=depth ; d++) {
				Vector<Expression> expsForEachDepth = new Vector<Expression>();
				expsForAllDepth.add(expsForEachDepth);
			}
			table.root_table.put(t.toString(), expsForAllDepth);
		}
	}

	
}
