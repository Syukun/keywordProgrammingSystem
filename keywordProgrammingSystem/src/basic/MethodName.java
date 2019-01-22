package basic;

import java.math.BigDecimal;
import java.util.List;

public class MethodName {
	String methodName;
	private Type[] types;

	public MethodName(String methodName, Type[] types) {
		super();
		this.methodName = methodName;
		this.types = types;
	}

	public Type getReceiveType() {
		return types[0];
	}

	public String getParameterType(int i) {
		return types[i].toString();
	}

	public String toString() {
		return this.methodName;
	}
	
	public Type[] getParameterTypes() {
		int paraNum = this.getParaNumber();
		Type[] res = new Type[paraNum];
		for(int i = 0 ; i < paraNum ; i++ ) {
			res[i] = this.types[i+1];
		}
		return res;
	}
	public int getParaNumber() {
		return this.types.length-1;
	}
	
	public BigDecimal getScore(List<String> keywords) {
		BigDecimal score = ScoreDef.DEFSCORE;
		new ScoreDef().checkInKeyword(score, methodName, keywords);
		return score;
	}
}
