package generatorForGraduation;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import astNode.Expression;
import astNode.ScoreDef;
import dataExtractedFromSource.DataFromSource;
import generator.Table;

public class ExpressionGenerator {

	public static Table tableExact = new Table();
	public static Table tableUnder = new Table();
	Set<String> allTypes = DataFromSource.typeDictionary.keySet();

	public Vector<Expression> getFinalExpressions(int depth, String keywords) {

		for (String type : allTypes) {
			Vector<Vector<Expression>> expsExactFromEachDepth = new Vector<Vector<Expression>>();
			Vector<Vector<Expression>> expsUnderFromEachDepth = new Vector<Vector<Expression>>();
			ExpressionGenerator.tableExact.table.put(type, expsExactFromEachDepth);
			ExpressionGenerator.tableUnder.table.put(type, expsUnderFromEachDepth);
		}

		Vector<Expression> res = new Vector<Expression>();
		for (int d = 1; d <= depth; d++) {
			generateAllPossibleExpression(d, keywords);
		}
		for (String type : allTypes) {
			res.addAll(getExpressionUnderDepth(depth, type));

		}
		ScoreDef.selectMaxExpressions(res, keywords, 40);
//		Vector<Expression> dummy3 = res.stream().distinct()
//				.collect(Collectors.toCollection(Vector::new));

		return res;
//		return dummy3;
	}

	private void generateAllPossibleExpression(int depth, String keywords) {
		if (depth == 1) {
			for (String type : allTypes) {
				Vector<Expression> allExpressionsAtDepthOneForAType = new Vector<Expression>();
				LocalVariableGenerator localVariableGenerator = new LocalVariableGenerator();
				ConstructorGenerator constructorGenerator = new ConstructorGenerator();

				Vector<Expression> localVariables = new Vector<Expression>();
				localVariables.addAll(localVariableGenerator.generateExactExpressionsSub(depth, type));
				Vector<Expression> constructors = new Vector<Expression>();
				constructors.addAll(constructorGenerator.generateExactExpressionsSub(depth, type, keywords));

				allExpressionsAtDepthOneForAType.addAll(localVariables);
				allExpressionsAtDepthOneForAType.addAll(constructors);

				tableExact.addToTable(type, allExpressionsAtDepthOneForAType);
				tableUnder.addToTable(type, allExpressionsAtDepthOneForAType);

			}

		} else {
			for (String type : allTypes) {
				Vector<Expression> allExpressionGreaterThanOneForAType = new Vector<Expression>();

				ConstructorGenerator constructorGenerator = new ConstructorGenerator();
				StaticFieldAccessGenerator staticFieldAccessGenerator = new StaticFieldAccessGenerator();
				NonStaticFieldAccessGenerator nonStaticFieldAccessGenerator = new NonStaticFieldAccessGenerator();
				StaticMethodInvocationGenerator staticMethodInvocationGenerator = new StaticMethodInvocationGenerator();
				NonStaticMethodInvocationGenertor nonStaticMethodInvocationGenertor = new NonStaticMethodInvocationGenertor();

				Vector<Expression> constructors = new Vector<Expression>();
				constructors.addAll(constructorGenerator.generateExactExpressionsSub(depth, type, keywords));

				Vector<Expression> staticFieldAccesses = new Vector<Expression>();
				staticFieldAccesses.addAll(staticFieldAccessGenerator.generateExactExpressionsSub(depth, type));

				Vector<Expression> nonStaticFieldAccesses = new Vector<Expression>();
				nonStaticFieldAccesses.addAll(nonStaticFieldAccessGenerator.generateExactExpressionsSub(depth, type));

				Vector<Expression> staticMethodInvocations = new Vector<Expression>();
				staticMethodInvocations
						.addAll(staticMethodInvocationGenerator.generateExactExpressionsSub(depth, type, keywords));
				Vector<Expression> nonStaticMethodInvocations = new Vector<Expression>();
				nonStaticMethodInvocations
						.addAll(nonStaticMethodInvocationGenertor.generateExactExpressionsSub(depth, type, keywords));

				allExpressionGreaterThanOneForAType.addAll(constructors);
				allExpressionGreaterThanOneForAType.addAll(staticFieldAccesses);
				allExpressionGreaterThanOneForAType.addAll(nonStaticFieldAccesses);
				allExpressionGreaterThanOneForAType.addAll(staticMethodInvocations);
				allExpressionGreaterThanOneForAType.addAll(nonStaticMethodInvocations);

				Vector<Expression> expsExact = new Vector<Expression>();
				expsExact.addAll(ScoreDef.selectMaxExpressions(allExpressionGreaterThanOneForAType, keywords, 10-depth));

				tableExact.addToTable(type, expsExact);

//				Vector<Expression> dummy = allExpressionGreaterThanOneForAType.stream().distinct()
//						.collect(Collectors.toCollection(Vector::new));
//				tableExact.addToTable(type, dummy);

				Vector<Expression> exactExpressionsAtDepthMinusOne = new Vector<Expression>();
				exactExpressionsAtDepthMinusOne.addAll(tableUnder.getExpression(depth - 1, type));
				exactExpressionsAtDepthMinusOne.addAll(allExpressionGreaterThanOneForAType);

//				Vector<Expression> dummy2 = exactExpressionsAtDepthMinusOne.stream().distinct()
//						.collect(Collectors.toCollection(Vector::new));

				Vector<Expression> sortedUnderExpressions = new Vector<Expression>();
				sortedUnderExpressions
						.addAll(ScoreDef.selectMaxExpressions(exactExpressionsAtDepthMinusOne, keywords, 10-depth));
//				Vector<Expression> sortedUnderExpressions = ScoreDef.selectMaxBWExpressions(dummy2, keywords);
				tableUnder.addToTable(type, sortedUnderExpressions);

			}
		}

	}
//
//	public static void addToExactTable(String type, Vector<Expression> expressions) {
//		Vector<Vector<Expression>> expressionsForEachDepth = tableExact.table.get(type);
//		if (expressions.isEmpty()) {
//			expressionsForEachDepth.add(new Vector<Expression>());
//		} else {
//			expressionsForEachDepth.add(expressions);
//		}
//
//	}
//	
//	public static void addToUnderTable(String type, Vector<Expression> expressions) {
//		Vector<Vector<Expression>> expressionsForEachDepth = tableUnder.table.get(type);
//		if (expressions.isEmpty()) {
//			expressionsForEachDepth.add(new Vector<Expression>());
//		} else {
//			expressionsForEachDepth.add(expressions);
//		}
//
//	}

	private Vector<Expression> getExpressionUnderDepth(int depth, String type) {

		return tableUnder.getExpression(depth, type);
	}

	public Set<String> getAllTypesIncludeSub(String type) {
		Set<String> res = new HashSet<String>();
		try {

			if (DataFromSource.typeDictionary.containsKey(type)) {
				String[] subTypes = DataFromSource.typeDictionary.get(type).getSubClass();
				res.add(type);
				if (subTypes != null) {
					for (String subType : subTypes) {
						res.add(subType);
					}
				}
			} else {
				return new HashSet<String>();
			}
		} catch (Exception e) {
			int i = 0;
		}

		return res;
	}

	public Set<String> getAllTypesIncludesSuper(String type) {
		String[] superTypes = DataFromSource.typeDictionary.get(type).getSuperClass();
		Set<String> res = new HashSet<String>();
		res.add(type);
		res.add("Object");
		if (superTypes != null) {
			for (String superType : superTypes) {
				res.add(superType);
			}
		}

		return res;
	}

	public boolean isBitOn(int x, int i) {
		return (x & (1 << i)) != 0;
	}

}
