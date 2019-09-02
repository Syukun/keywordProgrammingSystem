package astNode;

import java.math.BigDecimal;
import java.util.List;

import basic.ScoreDef;

/**
* @author Archer Shu
* @date 2019/07/31
*/
public class MethodName {
	private String methodName;
	private String returnType;
	private String receiveType;
	private String[] parameterTypes;
	
	public MethodName(String methodName, String returnType, String receiveType, String[] parameterTypes) {
		this.methodName = methodName;
		this.returnType = returnType;
		this.receiveType = receiveType;
		this.parameterTypes = parameterTypes;
	}
	
	public String getReceiveType() {
		return this.receiveType;
	}
	
	public String getParameterTypeOf(int i) {
		return parameterTypes[i-1];
	}
	
	public String getReturnType() {
		return this.returnType;
	}
	
	public String[] getParameterTypes() {
		return this.parameterTypes;
	}
	
	public BigDecimal getScore(String keywords) {
		return this.getScore(ScoreDef.splitKeyword(keywords));
	}
	
	// need split methodName
	public BigDecimal getScore(List<String> keywords) {
		BigDecimal score = ScoreDef.DEFSCORE;
		String[] methodNameArray = this.methodName.split("(?<!^)(?=[A-Z])");
		for(String word : methodNameArray) {
			score = ScoreDef.checkInKeyword(score, word.toLowerCase(), keywords);
		}
		
		return score;
	}
	
	public int getParameterNumber() {
		return this.parameterTypes.length;
	}
	
	
	
}
