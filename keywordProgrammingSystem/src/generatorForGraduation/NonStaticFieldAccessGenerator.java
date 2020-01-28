package generatorForGraduation;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import astNode.Expression;
import astNode.Field;
import astNode.FieldAccess;
import dataExtractedFromSource.DataFromSource;

public class NonStaticFieldAccessGenerator extends ExpressionGenerator {

	public Vector<Expression> generateExactExpressionsSub(int depth, String type) {
		Vector<Expression> res = new Vector<Expression>();

		Set<Field> fields = DataFromSource.fieldsRet.containsKey(type) ? DataFromSource.fieldsRet.get(type)
				: new HashSet<Field>();
		
		for(Field field : fields) {
			String fieldClassName = field.getReceiveType();
			Set<String> typesIncludingSuperType = getAllTypesIncludesSuper(fieldClassName);
			
			for(String typeIncludingSuperType : typesIncludingSuperType) {
				Vector<Expression> exactM1Expressions = new Vector<Expression>();
				exactM1Expressions.addAll(ExpressionGenerator.tableExact.getExpression(depth-1, typeIncludingSuperType));
				for(Expression exactM1Expression : exactM1Expressions) {
					FieldAccess fieldAccess = new FieldAccess(exactM1Expression,field);
					res.add(fieldAccess);
				}
			}
			
		}
		return res;
	}

}
