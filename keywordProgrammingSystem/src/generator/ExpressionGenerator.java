package generator;

import java.util.Vector;

import astNode.Expression;

/**
* @author Archer Shu
* @date 2019/07/31
*/
public abstract class ExpressionGenerator extends Generator{
	
	public abstract String getReceiveType();
	public abstract String getReturnType();
	
	public abstract void generateWithSubExpression(Expression[] subExps, Vector<Expression> result);
	
}
