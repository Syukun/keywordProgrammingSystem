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

	public Vector<Expression> getFinalExpressions(int depth, String keywords){
		Vector<Expression> res = new Vector<Expression>();
		for(int d = 1; d <= depth; d++) {
			generateAllPossibleExpression(d, keywords);
		}
		for(String type: allTypes) {
			res.addAll(getExpressionUnderDepth(depth, type));
		}
		
		return res;
	}

	private void generateAllPossibleExpression(int depth, String keywords) {
		if(depth == 1) {
			for(String type : allTypes) {
				Vector<Expression> allExpressionsAtDepthOneForAType = new Vector<Expression>();
				LocalVariableGenerator localVariableGenerator = new LocalVariableGenerator();
				ConstructorGenerator constructorGenerator = new ConstructorGenerator();
				
				Vector<Expression> localVariables = localVariableGenerator.generateExactExpressionsSub(depth, type);
				Vector<Expression> constructors = constructorGenerator.generateExactExpressionsSub(depth, type);
				
				allExpressionsAtDepthOneForAType.addAll(localVariables);
				allExpressionsAtDepthOneForAType.addAll(constructors);
				
				tableExact.addToTable(type, allExpressionsAtDepthOneForAType);
				tableUnder.addToTable(type, allExpressionsAtDepthOneForAType);
				
			}
		}else {
			for(String type : allTypes) {
				Vector<Expression> allExpressionGreaterThanOneForAType = new Vector<Expression>();
				
				ConstructorGenerator constructorGenerator = new ConstructorGenerator();
				StaticFieldAccessGenerator staticFieldAccessGenerator = new StaticFieldAccessGenerator();
				NonStaticFieldAccessGenerator nonStaticFieldAccessGenerator = new NonStaticFieldAccessGenerator();
				StaticMethodInvocationGenerator staticMethodInvocationGenerator = new StaticMethodInvocationGenerator();
				NonStaticMethodInvocationGenertor nonStaticMethodInvocationGenertor = new NonStaticMethodInvocationGenertor();
				
				Vector<Expression> constructors = constructorGenerator.generateExactExpressionsSub(depth, type);
				Vector<Expression> staticFieldAccesses = staticFieldAccessGenerator.generateExactExpressionsSub(depth, type);
				Vector<Expression> nonStaticFieldAccesses = nonStaticFieldAccessGenerator.generateExactExpressionsSub(depth, type);
				Vector<Expression> staticMethodInvocations = staticMethodInvocationGenerator.generateExactExpressionsSub(depth, type);
				Vector<Expression> nonStaticMethodInvocations = nonStaticMethodInvocationGenertor.generateExactExpressionsSub(depth, type);
				
				allExpressionGreaterThanOneForAType.addAll(constructors);
				allExpressionGreaterThanOneForAType.addAll(staticFieldAccesses);
				allExpressionGreaterThanOneForAType.addAll(nonStaticFieldAccesses);
				allExpressionGreaterThanOneForAType.addAll(staticMethodInvocations);
				allExpressionGreaterThanOneForAType.addAll(nonStaticMethodInvocations);
				tableExact.addToTable(type, allExpressionGreaterThanOneForAType);
				
				Vector<Expression> exactExpressionsAtDepthMinusOne = tableExact.getExpression(depth-1, type);
				exactExpressionsAtDepthMinusOne.addAll(allExpressionGreaterThanOneForAType);
				
				Vector<Expression> sortedUnderExpressions = ScoreDef.selectMaxBWExpressions(exactExpressionsAtDepthMinusOne, keywords);
				tableUnder.addToTable(type, sortedUnderExpressions);
				
				
			} 
		}
		
	}

	private Vector<Expression> getExpressionUnderDepth(int depth, String type) {
		
		return tableUnder.getExpression(depth, type);
	}
	
	public Set<String> getAllTypesIncludeSub(String type){
		String[] subTypes = DataFromSource.typeDictionary.get(type).getSubClass();
		Set<String> res = new HashSet<String>();
		res.add(type);
		if(subTypes!=null) {
			for(String subType:subTypes) {
				res.add(subType);
			}
		}
		
		return res;
	}
	
	public Set<String> getAllTypesIncludesSuper(String type){
		String[] superTypes = DataFromSource.typeDictionary.get(type).getSuperClass();
		Set<String> res = new HashSet<String>();
		res.add(type);
		res.add("Object");
		if(superTypes!=null) {
			for(String superType:superTypes) {
				res.add(superType);
			}
		}
		
		
		return res;
	}
	
}
