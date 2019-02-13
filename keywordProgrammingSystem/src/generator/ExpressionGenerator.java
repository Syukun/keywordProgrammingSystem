package generator;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import basic.Expression;
import basic.ScoreDef;
import basic.Type;
import dataBase.DataBase;

public class ExpressionGenerator implements Generator,GeneratorWithMultipleReturnType {
	// do not use right now might be used later
	String className = "Expression";
	int BW = RelateBeamSearch.BEAMWIDTH;

//	
	// table1 : store expressions less than depth d
	// table2 : store expressions at exact depth d
	public Table expsLEQDepth_Table;
	public Table expsAtExactDepth_Table;
	
	
	public ExpressionGenerator() {
		
	}


	public Vector<Expression> generateExpression(int depth, String keywords) {
//		DataBase.initDataBase();
		
		Vector<Expression> result = new Vector<Expression>();
		Set<String> receiveTypes = this.getAllPossibleReturnTypes();
		
		this.expsLEQDepth_Table = new Table();
		this.expsAtExactDepth_Table = new Table();
		this.expsLEQDepth_Table.initTable(depth,receiveTypes);
		this.expsAtExactDepth_Table.initTable(depth,receiveTypes);	
		
		for (int d = 1; d <= depth; d++) {
//				fillTwoTables
			fillLEQTable(d,keywords,receiveTypes);
		}

		for (String t : receiveTypes) {
			result.addAll(expsLEQDepth_Table.root_table.get(t).get(depth));
		}
		ScoreDef.selectMaxBWExpressions(result, keywords);
		return result;
	}

	private void fillExactTable(int d,String keywords, Set<String> receiveTypes) {
		for (String t : receiveTypes) {
			// expression with type t in depth d
			Vector<Expression> result = new Vector<Expression>();
			Vector<ExpressionGenerator> allSubGs = this.allSubGeneratorsIncludeTypeT(t,d);
			for (ExpressionGenerator g : allSubGs) {
				g.generateExpressionExact(d, result);
			}
			if (d > 1) {
				ScoreDef.selectMaxBWExpressions(result, keywords);
			}
			this.expsAtExactDepth_Table.root_table.get(t).get(d).addAll(result);
		}

	}

	private void fillLEQTable(int d,String keywords, Set<String> receiveTypes) {
		fillExactTable(d,keywords, receiveTypes);
		if (d == 1) {
			for(String type : receiveTypes) {
				this.expsLEQDepth_Table.root_table.get(type).get(d).addAll(expsAtExactDepth_Table.root_table.get(type.toString()).get(d));
			}
			
		} else {
			for (String t : receiveTypes) {
				Vector<Expression> result = new Vector<Expression>();
				result.addAll(expsAtExactDepth_Table.root_table.get(t).get(d));
				result.addAll(expsLEQDepth_Table.root_table.get(t).get(d-1));
				ScoreDef.selectMaxBWExpressions(result, keywords);
				expsLEQDepth_Table.root_table.get(t).get(d).addAll(result);
			}
		}
	}

	private Vector<ExpressionGenerator> allSubGeneratorsIncludeTypeT(String t, int d) {
		Vector<ExpressionGenerator> subGeneratorsIncludeTypeT = new Vector<ExpressionGenerator>();
		for (ExpressionGenerator g : getAllSubGenerators(d)) {
//			if (g.getAllPossibleReturnTypes().contains(t)) {
//				g.changeProperty(t);
//				subGeneratorsIncludeTypeT.add(g);
//			}
			
			Set<String> allPossibleReturnTypes = g.getAllPossibleReturnTypes();
			int typesNum = allPossibleReturnTypes.size();
			if(allPossibleReturnTypes.contains(t)) {
				if(typesNum>1) {
					// could create an interface
					subGeneratorsIncludeTypeT.addAll(g.getAllSubGeneratorWithTypeT(t));
				}else {
					subGeneratorsIncludeTypeT.add(g);
				}
			}
			
		}
		return subGeneratorsIncludeTypeT;
	}


	private ExpressionGenerator[] getAllSubGenerators(int d) {
		// TODO Auto-generated method stub
		ExpressionGenerator[] res;
		if(d >1) {
			res = new ExpressionGenerator[] { 
//					new BinaryConditionalExpressionGenerator(expsLEQDepth_Table,expsAtExactDepth_Table)
//					,
					new MethodInvocationGenerator(expsLEQDepth_Table,expsAtExactDepth_Table)
					}; 
		}else {
			res = new ExpressionGenerator[] {
					new StringLiteralGenerator()
					,
					new IntLiteralGenerator()
					,new VariableNameGenerator()
			};
		}
		return res;
	}

	public Set<String> getAllPossibleReturnTypes(){
//		Set<String> allPossibleReceiveType = new HashSet<String>();
//		allPossibleReceiveType.add("boolean");
//		allPossibleReceiveType.add("Integer");
//		allPossibleReceiveType.add("String");
//		allPossibleReceiveType.add("BufferedReader");
//		allPossibleReceiveType.add("List<String>");
//		return allPossibleReceiveType;
		
		return DataBase.allTypes.keySet();
	}

	
	public void generateExpressionExact(int d, Vector<Expression> result) {
//		int arity = this.getParameterGenerators().length;
		int arity = this.getArity();
		if (arity == 0) {
			if (d == 1) {
				generateWithSubExps(new Expression[0], result);
			}
		} else {
			generateWithArity(d, arity, result);
		}
	}

	public int getArity() {
		// TODO Auto-generated method stub
		return 0;
	}


	private void generateWithArity(int d, int arity, Vector<Expression> result) {
		for (int exactFlags = 1; exactFlags <= (1 << arity) - 1; exactFlags++) {
			Expression[] subExps = new Expression[arity];
			generateWithArityAuxi(d, arity, exactFlags, result, subExps);
		}

	}

	private void generateWithArityAuxi(int d, int arity, int exactFlags, Vector<Expression> result,
			Expression[] subExps) {
		if (arity == 0) {
			generateWithSubExps(subExps, result);
		} else {
			String possibleParaType_arity = this.getParameterTypes()[arity - 1];
			// need add code about parameter generator name
			Vector<Expression> candidates = isBitOn(exactFlags, arity - 1)
					? getPossibleExpressionsInDepth(d - 1, possibleParaType_arity)
					: getPossibleExpressionsUnderDepth(d - 2, possibleParaType_arity);
			if(candidates.size()>0) {
				for (Expression e : candidates) {
					subExps[arity - 1] = e;
					generateWithArityAuxi(d, arity - 1, exactFlags, result, subExps);
				}
			}
//			if(arity == 1 && this.getReceiveType()==null) {
//				subExps[0] = null;
//			}

		}

	}
	

	private Vector<Expression> getPossibleExpressionsInDepth(int d, String possibleParaType_arity) {
		Vector<Expression> result = new Vector<Expression>();
		String[] subT = Type.getSubType(possibleParaType_arity);
//		if(subT == null) {
//			return result;
//		}
		for(String t_s : subT) {
			// t_s : BufferReader
			// d = 2
			Table table2 = this.expsAtExactDepth_Table;
			Map<String, Vector<Vector<Expression>>> root_table_2 = table2.root_table;
			Vector<Vector<Expression>> exps = root_table_2.get(t_s);
			Vector<Expression> res = exps.get(d);
				result.addAll(res);
			
		}
		return result;
	}

	private Vector<Expression> getPossibleExpressionsUnderDepth(int d, String possibleParaType_arity) {
		Vector<Expression> result = new Vector<Expression>();
		String[] subT = Type.getSubType(possibleParaType_arity);
//		if(subT == null) {
//			return result;
//		}
		for(String t_s : subT) {
			result.addAll(this.expsLEQDepth_Table.root_table.get(t_s).get(d));
		}
		return result;
	}

	public boolean isBitOn(int x, int i) {
		return (x & (1 << i)) != 0;
	}


	public Generator[] getParameterGenerators() {
		// TODO Auto-generated method stub
		return new Generator[] {};
	}


	@Override
	public void generateWithSubExps(Expression[] subExps, Vector<Expression> result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Collection<? extends ExpressionGenerator> getAllSubGeneratorWithTypeT(String t) {
		// TODO Auto-generated method stub
		return new Vector<ExpressionGenerator>();
	}

	@Override
	public String getReturnType() {
		// modify to try and catch later
		System.out.println("method getReturnType() in class ExpressionGenerator");
		return null;
	}

	@Override
	public String[] getParameterTypes() {
		// modify to try and catch later
		System.out.println("method getParameterTypes() in class ExpressionGenerator");
		return null;
	}

	@Override
	public String getReceiveType() {
		return this.getParameterTypes()[0];
	}



}
