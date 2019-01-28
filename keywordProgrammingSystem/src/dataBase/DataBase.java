package dataBase;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import basic.BinaryOperator;
import basic.IntegerLiteral;
import basic.MethodName;
import basic.StringLiteral;
import basic.Type;
import basic.VariableName;

public class DataBase {
	public static Map<String, Type> allTypes = new HashMap<String, Type>();
	public static Vector<BinaryOperator> allBinaryOperators = new Vector<BinaryOperator>();
	public static Vector<StringLiteral> allStringLiterals = new Vector<StringLiteral>();
	public static Vector<IntegerLiteral> allIntegerLiterals = new Vector<IntegerLiteral>();
	public static Vector<MethodName> allMethodNames = new Vector<MethodName>();
	public static Vector<VariableName> allVariableName = new Vector<VariableName>();
	
	
	
	public static void initDataBase() {
		initAllTypes();
		initAllBinaryOperators();
		initStringLiterals();
		initIntegerLiterals();
		initMethodNames();
		initVariableName();
	}

	private static void initVariableName() {
		allVariableName.add(new VariableName("src","BufferedReader"));
		allVariableName.add(new VariableName("array","List<String>"));
	}

	private static void initAllTypes() {
		// TODO Auto-generated method stub
		allTypes.put("String", new Type("String"));
		allTypes.put("Integer", new Type("Integer"));
		allTypes.put("boolean", new Type("boolean"));
		allTypes.put("BufferedReader", new Type("BufferedReader"));
		allTypes.put("List<String>", new Type("List<String>"));
	}

	private static void initAllBinaryOperators() {
		allBinaryOperators.add(new BinaryOperator(">","Integer",true));
		allBinaryOperators.add(new BinaryOperator("<","Integer",true));
		allBinaryOperators.add(new BinaryOperator("+","Integer", false));
		allBinaryOperators.add(new BinaryOperator("-","Integer", false));
	}

	private static void initStringLiterals() {
//		allStringLiterals.add(new StringLiteral("a"));
//		allStringLiterals.add(new StringLiteral("b"));

	}

	private static void initIntegerLiterals() {
		allIntegerLiterals.add(new IntegerLiteral(0));		
		allIntegerLiterals.add(new IntegerLiteral(1));
	}
	
	private static void initMethodNames() {
//		allMethodNames.add(new MethodName("add", 
//				new Type[] {allTypes.get("boolean"),allTypes.get("Integer"),allTypes.get("Integer")}));
		
		allMethodNames.add(new MethodName("add", 
				new String[] {"boolean","List<String>","String"}));
		
		allMethodNames.add(new MethodName("readLine",
				new String[] {"String","BufferedReader"}));
	}

}
