package generator;

import java.util.Vector;

import astNode.Expression;
import astNode.TypeName;

/**
 * @author Archer Shu
 * @date 2019年9月1日
 */
public class ExpressionGenerator extends AbstractGenerator {

	@Override
	public Vector<Expression> generateExactExpressionsMain(int depth, String type) {
		// this is ugly, hope somebody could modify it
		Vector<Expression> res = new Vector<Expression>();
		if (depth == 1) {
			res.add(new TypeName(type));
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
		String[] allTypes = { "String", "int" };
		Vector<Expression> res = new Vector<Expression>();
		for (String type : allTypes) {
			res.addAll(getUnderExpressions(depth, type));
		}

		return res;
	}

}
