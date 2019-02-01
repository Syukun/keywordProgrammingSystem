package generator;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import basic.Expression;
import basic.ScoreDef;
import basic.Type;
import dataBase.DataBase;

public class ExpressionGenerator implements Generator {
	// do not use right now might be used later
	String className = "Expression";
	int BW = RelateBeamSearch.BEAMWIDTH;
//
//	public static int depth = 1;
//	public static  String keywords = "";
//	public static Table expsLEQDepth_Table = new Table(depth, getReceiveTypeForExpression());
//	public static Table expsAtExactDepth_Table = new Table(depth, getReceiveTypeForExpression());
	
//	private static Set<Type> getReceiveTypeForExpression() {
//		return Generator.getAllPossibleReceiveTypes_static(new ExpressionGenerator());
//	}
//	
	public static Table expsLEQDepth_Table = new Table();
	public static Table expsAtExactDepth_Table = new Table();


	public static Vector<Expression> generateExpression(int depth, String keywords) {
		DataBase.initDataBase();
		
		Vector<Expression> result = new Vector<Expression>();
		Set<String> receiveTypes = new ExpressionGenerator().getAllPossibleReceiveTypes();
		Table.initTable(expsLEQDepth_Table,depth,receiveTypes);
		Table.initTable(expsAtExactDepth_Table,depth,receiveTypes);	
		
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

	private static void fillExactTable(int d,String keywords, Set<String> receiveTypes) {
		for (String t : receiveTypes) {
			// expression with type t in depth d
			Vector<Expression> result = new Vector<Expression>();
			Vector<ExpressionGenerator> allSubGs = allSubGeneratorsIncludeTypeT(t,d);
			for (ExpressionGenerator g : allSubGs) {
				g.generateExpressionExact(d, result);
			}
//			if(d==1) {
//				ScoreDef.sortExpression(result,keywords);
//			}
			if (d > 1) {
				ScoreDef.selectMaxBWExpressions(result, keywords);
			}
			expsAtExactDepth_Table.root_table.get(t).get(d).addAll(result);
		}

	}

	private static void fillLEQTable(int d,String keywords, Set<String> receiveTypes) {
		fillExactTable(d,keywords, receiveTypes);
		if (d == 1) {
			for(String type : receiveTypes) {
				expsLEQDepth_Table.root_table.get(type).get(d).addAll(expsAtExactDepth_Table.root_table.get(type.toString()).get(d));
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

	private static Vector<ExpressionGenerator> allSubGeneratorsIncludeTypeT(String t, int d) {
		Vector<ExpressionGenerator> subGeneratorsIncludeTypeT = new Vector<ExpressionGenerator>();
		for (ExpressionGenerator g : getAllSubGenerators(d)) {
			if (g.getAllPossibleReceiveTypes().contains(t)) {
				g.changeProperty(t);
				subGeneratorsIncludeTypeT.add(g);
			}
		}
		return subGeneratorsIncludeTypeT;
	}

	public void changeProperty(String t) {
		// TODO Auto-generated method stub
		
	}

	private static ExpressionGenerator[] getAllSubGenerators(int d) {
		// TODO Auto-generated method stub
		ExpressionGenerator[] res;
		if(d >1) {
			res = new ExpressionGenerator[] { 
					new StringLiteralGenerator()
					,new IntLiteralGenerator()
					,new BinaryConditionalExpressionGenerator()
					,new MethodInvocationGenerator()
					,new VariableNameGenerator()
					}; 
		}else {
			res = new ExpressionGenerator[] {
					new StringLiteralGenerator()
					,new IntLiteralGenerator()
					,new VariableNameGenerator()
			};
		}
		return res;
	}

	public Set<String> getAllPossibleReceiveTypes(){
		Set<String> allPossibleReceiveType = new HashSet<String>();
		allPossibleReceiveType.add("boolean");
		allPossibleReceiveType.add("Integer");
		allPossibleReceiveType.add("String");
		allPossibleReceiveType.add("BufferedReader");
		allPossibleReceiveType.add("List<String>");
		return allPossibleReceiveType;
	}

	
	public void generateExpressionExact(int d, Vector<Expression> result) {
		int arity = this.getParameterGenerators().length;
		if (arity == 0) {
			if (d == 1) {
				generateWithSubExps(new Expression[0], result);
			}
		} else {
			generateWithArity(d, arity, result);
		}
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
					? getPossibleExpressionsUnderDepth(d - 1, possibleParaType_arity)
					: getPossibleExpressionInDepth(d - 2, possibleParaType_arity);
			if(candidates.size()>0) {
				for (Expression e : candidates) {
					subExps[arity - 1] = e;
					generateWithArityAuxi(d, arity - 1, exactFlags, result, subExps);
				}
			}
			

		}

	}
	
	

	private Vector<Expression> getPossibleExpressionInDepth(int d, String possibleParaType_arity) {
		Vector<Expression> result = new Vector<Expression>();
		for(String t_s : Type.getSubType(possibleParaType_arity)) {
			result.addAll(expsAtExactDepth_Table.root_table.get(t_s).get(d));
		}
		return result;
	}

	private Vector<Expression> getPossibleExpressionsUnderDepth(int d, String possibleParaType_arity) {
		Vector<Expression> result = new Vector<Expression>();
		for(String t_s : Type.getSubType(possibleParaType_arity)) {
			result.addAll(expsLEQDepth_Table.root_table.get(t_s).get(d));
		}
		return result;
	}

	public static boolean isBitOn(int x, int i) {
		return (x & (1 << i)) != 0;
	}

	@Override
	public Generator[] getParameterGenerators() {
		// TODO Auto-generated method stub
		return new Generator[] {};
	}

	@Override
	public String[] getParameterTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void generateWithSubExps(Expression[] subExps, Vector<Expression> result) {
		// TODO Auto-generated method stub
		
	}


}
