package generator;

import java.util.Set;
import java.util.Vector;

import astNode.Expression;
import astNode.TypeName;
import dataExtractedFromSource.DataFromSource;

/**
 * @author Archer Shu
 * @date 2019年9月1日
 */
public class ExpressionGenerator extends AbstractGenerator {
	DataFromSource dataFromExtraction;
	
	public void setDataFromSource(DataFromSource data) {
		this.dataFromExtraction = data;
	}
	@Override
	public Vector<Expression> generateExactExpressionsMain(int depth, String type) {
		// this is ugly, hope somebody could modify it
		Vector<Expression> res = new Vector<Expression>();
		if (depth == 1) {
			TypeNameGenerator typeNameGenerator = new TypeNameGenerator();
			typeNameGenerator.setParent(this);
			Expression typeName = typeNameGenerator.generateExactExpressionsSub(depth, type);
			res.add(typeName);
			
		} else {
			/**
			 * FieldAccessGenerator
			 */
			FieldAccessGenerator fieldAccessGenerator = new FieldAccessGenerator();
			fieldAccessGenerator.setParent(this);
			Vector<Expression> fieldAccess = fieldAccessGenerator.generateExactExpressionsSub(depth, type);
			res.addAll(fieldAccess);
			/**
			 * MethodInvocationGenerator
			 */
			MethodInvocationGenerator methodInvocationGenerator = new MethodInvocationGenerator();
			methodInvocationGenerator.setParent(this);
			Vector<Expression> methodInvocation = methodInvocationGenerator.generateExactExpressionsSub(depth, type);
			res.addAll(methodInvocation);
			/**
			 * IfThenElseConditionalExpression
			 */
			IfThenElseConditionalExpressionGenerator ifThenElseConditionalExpressionGenerator = new IfThenElseConditionalExpressionGenerator();
			ifThenElseConditionalExpressionGenerator.setParent(this);
			Vector<Expression>  ifThenElseConditionalExpression = ifThenElseConditionalExpressionGenerator.generateExactExpressionsSub(depth, type);
			res.addAll(ifThenElseConditionalExpression);
		}

		return res;
	}

	/**
	 * main function to return expressions under depth
	 * 
	 * @param depth
	 * @return
	 */
	public Vector<Expression> getFinalExpressions(int depth) {
		// TODO modify it later
//		String[] allTypes = { "String", "int", "boolean" };
		Set<String> allTypes = this.dataFromExtraction.getAllType();
		Vector<Expression> res = new Vector<Expression>();
		for (String type : allTypes) {
			res.addAll(getUnderExpressions(depth, type));
		}

		return res;
	}

	public boolean isBitOn(int x, int i) {
		return (x & (1 << i)) != 0;
	}
}
