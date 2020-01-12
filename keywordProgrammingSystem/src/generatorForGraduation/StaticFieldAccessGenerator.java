package generatorForGraduation;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import astNode.Expression;
import astNode.Field;
import astNode.TypeFieldAccess;
import dataExtractedFromSource.DataFromSource;

public class StaticFieldAccessGenerator extends ExpressionGenerator {

	public Vector<Expression> generateExactExpressionsSub(int depth, String type) {
		Vector<Expression> res = new Vector<Expression>();
		if (depth == 2) {
			Set<Field> fields = DataFromSource.fieldsRet.containsKey(type) ? DataFromSource.fieldsRet.get(type)
					: new HashSet<Field>();
			for (Field field : fields) {

				if (field.isStatic()) {
					String classTypeName = field.getReceiveType();
					res.add(new TypeFieldAccess(classTypeName, field));
				}
			}
		}
		return res;

	}

}
