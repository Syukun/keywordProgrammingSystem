package dataBase;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import basic.BinaryOperator;
import basic.IntegerLiteral;
import basic.MethodName;
import basic.StringLiteral;
import basic.Type;

public class DataBase {
	public static Map<String, Type> allTypes = new HashMap<String, Type>();
	public static Vector<BinaryOperator> allBinaryOperators = new Vector<BinaryOperator>();
	public static Vector<StringLiteral> allStringLiterals = new Vector<StringLiteral>();
	public static Vector<IntegerLiteral> allIntegerLiterals = new Vector<IntegerLiteral>();
	public static Vector<MethodName> allMethodNames = new Vector<MethodName>();
	
	
	
	public static void initDataBase() {
		initAllTypes();
		initAllBinaryOperators();
		initStringLiterals();
		initIntegerLiterals();
		initMethodNames();
	}

	private static void initAllTypes() {
		// TODO Auto-generated method stub
		allTypes.put("String", new Type("String"));
		allTypes.put("Integer", new Type("Integer"));
		allTypes.put("boolean", new Type("boolean"));
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
		allMethodNames.add(new MethodName("add", 
				new Type[] {allTypes.get("boolean"),allTypes.get("Integer"),allTypes.get("Integer")}));
	}

}
