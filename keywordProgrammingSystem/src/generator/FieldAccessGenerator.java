package generator;

import java.util.Vector;

import astNode.Expression;
import astNode.Field;
import astNode.FieldAccess;

/**
* @author Archer Shu
* @date 2019年9月1日
*/
public class FieldAccessGenerator extends ExpressionGenerator{
	public Vector<Expression> generateExactExpressionSub(int depth, String type){
		Vector<Expression> res = new Vector<Expression>();
		Vector<Expression> exactM1Exps = getExpressionsExact(depth-1, type);
		Field[] fields = {new Field("String","int","length"), new Field("int", "int", "bitcode")};
		for(Expression exactM1Exp : exactM1Exps){
			//TODO just for test
			//TODO use stream later
			Vector<Field> fieldsReceiveEqualType = new Vector<Field>();
			for(Field f : fields) {
				if(f.getReceiveType() == type) {
					fieldsReceiveEqualType.add(f);
				}
			}
			for(Field f : fieldsReceiveEqualType){
				FieldAccess fa = new FieldAccess(exactM1Exp, f);
				res.add(fa);
			}
		}
		return res;
	}
}
