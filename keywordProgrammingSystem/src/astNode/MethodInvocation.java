package astNode;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Archer Shu
 * @date 2019/08/01
 */
public class MethodInvocation extends Expression {

	Expression receiver;
	MethodName methodName;
	Expression[] parameters;

	String typeName = null;

	public MethodInvocation(Expression receiver, MethodName methodName, Expression[] parameters) {
		this.receiver = receiver;
		this.methodName = methodName;
		this.parameters = parameters;
	}

	public MethodInvocation(String typeName, MethodName methodName, Expression[] parameters) {
		this.typeName = typeName;
		this.methodName = methodName;
		this.parameters = parameters;
	}

	/**
	 * receiver.methodName(para1,para2...)
	 */
	public String toString() {
		StringBuffer res = new StringBuffer();

		if (typeName != null) {
			res.append(typeName.toString() + ".");
		} else {
			if (receiver != null) {
				res.append(receiver.toString() + ".");
			}
		}

		res.append(methodName.toString() + "(");
		if (parameters != null) {
			String seperator = "";
			for (Expression exp : parameters) {
				res.append(seperator + exp.toString());
				seperator = ",";
			}
		}
		res.append(")");
		return res.toString();
	}

	@Override
	public BigDecimal getScore(List<String> keywords) {
		BigDecimal score = ScoreDef.DEFSCORE;
		if (typeName != null) {
			score = score.add(new TypeName(typeName).getScore(keywords));
		} else {
			if (receiver != null) {
				score = score.add(this.receiver.getScore(keywords));
			}
		}
		score = score.add(this.methodName.getScore(keywords));
		for (Expression paraExpression : this.parameters) {
			score = score.add(paraExpression.getScore(keywords));
		}

		return score;
	}

	@Override
	public String getReturnType() {
		// TODO Auto-generated method stub
		return this.methodName.getReturnType();
	}

	@Override
	public String toPredictString() {
		StringBuffer res = new StringBuffer();

		if (typeName != null) {
			res.append(typeName.toString() + "  ");
		} else {
			if (receiver != null) {
				res.append(receiver.toString() + "  ");
			}
		}
		res.append("  " + methodName.toString() + "  ");
		if (parameters != null) {
			String seperator = " ";
			for (Expression exp : parameters) {
				res.append(seperator + exp.toString());
			}
		}
		return res.toString();
	}

}
