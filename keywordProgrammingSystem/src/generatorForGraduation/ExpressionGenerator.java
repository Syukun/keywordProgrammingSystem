package generatorForGraduation;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.util.stream.Collectors;

import astNode.Expression;
import astNode.ScoreDef;
import dataExtractedFromSource.DataFromSource;
import generator.Table;

public class ExpressionGenerator {

	public static Table tableExact = new Table();
	public static Table tableUnder = new Table();
	Set<String> allTypes = DataFromSource.typeDictionary.keySet();

	public Vector<Expression> getFinalExpressions(int depth, String keywords) {
		
		for(String type : allTypes) {
			Vector<Vector<Expression>> expsFromEachDepth = new Vector<Vector<Expression>>();
			ExpressionGenerator.tableExact.table.put(type, expsFromEachDepth);
			ExpressionGenerator.tableUnder.table.put(type, expsFromEachDepth);
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

				Vector<Expression> localVariables = localVariableGenerator.generateExactExpressionsSub(depth, type);
				Vector<Expression> constructors = constructorGenerator.generateExactExpressionsSub(depth, type,
						keywords);

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

				Vector<Expression> constructors = constructorGenerator.generateExactExpressionsSub(depth, type,
						keywords);
				Vector<Expression> staticFieldAccesses = staticFieldAccessGenerator.generateExactExpressionsSub(depth,
						type);
				Vector<Expression> nonStaticFieldAccesses = nonStaticFieldAccessGenerator
						.generateExactExpressionsSub(depth, type);
				Vector<Expression> staticMethodInvocations = staticMethodInvocationGenerator
						.generateExactExpressionsSub(depth, type, keywords);
				Vector<Expression> nonStaticMethodInvocations = nonStaticMethodInvocationGenertor
						.generateExactExpressionsSub(depth, type, keywords);

				allExpressionGreaterThanOneForAType.addAll(constructors);
				allExpressionGreaterThanOneForAType.addAll(staticFieldAccesses);
				allExpressionGreaterThanOneForAType.addAll(nonStaticFieldAccesses);
				allExpressionGreaterThanOneForAType.addAll(staticMethodInvocations);
				allExpressionGreaterThanOneForAType.addAll(nonStaticMethodInvocations);

				Vector<Expression> dummy = allExpressionGreaterThanOneForAType.stream().distinct()
						.collect(Collectors.toCollection(Vector::new));
				tableExact.addToTable(type, dummy);

				Vector<Expression> exactExpressionsAtDepthMinusOne = tableUnder.getExpression(depth - 1, type);
				exactExpressionsAtDepthMinusOne.addAll(allExpressionGreaterThanOneForAType);

				Vector<Expression> dummy2 = exactExpressionsAtDepthMinusOne.stream().distinct()
						.collect(Collectors.toCollection(Vector::new));

				Vector<Expression> sortedUnderExpressions = ScoreDef.selectMaxBWExpressions(dummy2, keywords);
				tableUnder.addToTable(type, sortedUnderExpressions);

			}
		}

	}

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
			}else {
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
