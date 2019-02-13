package dataBase;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import basic.BinaryOperator;
import basic.IntegerLiteral;
import basic.MethodName;
import basic.StringLiteral;
import basic.Type;
import basic.VariableName;

public class DataBase {
	public static Map<String, Type> allTypes = new HashMap<String, Type>();
	public static Set<BinaryOperator> allBinaryOperators = new HashSet<BinaryOperator>();
	public static Set<StringLiteral> allStringLiterals = new HashSet<StringLiteral>();
	public static Set<IntegerLiteral> allIntegerLiterals = new HashSet<IntegerLiteral>();
	public static Set<MethodName> allMethodNames = new HashSet<MethodName>();
	public static Set<VariableName> allVariableName = new HashSet<VariableName>();
	
	
	
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
		allStringLiterals.add(new StringLiteral("a"));
		allStringLiterals.add(new StringLiteral("b"));

	}

	private static void initIntegerLiterals() {
		allIntegerLiterals.add(new IntegerLiteral(0));		
		allIntegerLiterals.add(new IntegerLiteral(1));
	}
	
	private static void initMethodNames() {
		
		allMethodNames.add(new MethodName("add", "boolean",
				new String[] {"List<String>","String"}));
		
		allMethodNames.add(new MethodName("readLine","String",
				new String[] {"BufferedReader"}));
		
		allMethodNames.add(new MethodName("concat","String",
				new String[] {"String","String"}));
	}

}
