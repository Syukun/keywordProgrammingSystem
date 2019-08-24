package generator;

import java.util.Vector;

import astNode.Expression;

/**
 * @author Archer Shu
 * @date 2019年8月21日
 */
public class TableElement {
	private Vector<Expression> expression;
	private boolean isReady;

	public TableElement(Vector<Expression> expression, boolean isReady) {
		super();
		this.expression = expression;
		this.isReady = isReady;
	}

	public Vector<Expression> getExpression() {
		return expression;
	}

	public void setExpression(Vector<Expression> expression) {
		this.expression = expression;
	}

	public boolean isReady() {
		return isReady;
	}

	public void setReady(boolean isReady) {
		this.isReady = isReady;
	}

}
