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
	
	public MethodInvocation(Expression receiver, MethodName methodName, Expression[] parameters) {
		this.receiver = receiver;
		this.methodName = methodName;
		this.parameters = parameters;
	}
	
	/**
	 * receiver.methodName(para1,para2...)
	 */
	public String toString() {
		StringBuffer res = new StringBuffer();
		
		if(receiver != null) {
			res.append(receiver.toString() + ".");
		}
		
		res.append(methodName.toString()+"(");
		if(parameters != null) {
			String seperator = "";
			for(Expression exp : parameters) {
				res.append(seperator + exp.toString());
				seperator = ",";
			}
		}
		res.append(")");
		return res.toString();
	}

	@Override
	public BigDecimal getScore(List<String> keywords) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getReturnType() {
		// TODO Auto-generated method stub
		return null;
	}

}
