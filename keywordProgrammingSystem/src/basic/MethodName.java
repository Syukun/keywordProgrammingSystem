package basic;

import java.math.BigDecimal;
import java.util.List;

import dataBase.DataBase;

public class MethodName {
	String methodName;
	String returnType;
	String[] parameterTypes;
	TypeName typeName;

	public MethodName(String methodName,String returnType,String[] parameterTypes,TypeName typeName) {
		super();
		this.methodName = methodName;
		this.returnType = returnType;
		this.parameterTypes = parameterTypes;
		this.typeName = typeName;
		
	}

	public String getReceiveType() {
		return this.parameterTypes[0];
	}

	public String getParameterType(int i) {
		return parameterTypes[i];
	}
	
	public String[] getParameterTypes() {

		if(this.getReceiveType()!=null) {
			return this.parameterTypes;	
		}else {
			String[] res = new String[parameterTypes.length];
			for(int i=1; i<parameterTypes.length ;i++) {
				res[i-1] = parameterTypes[i];
			}
			return res;
		}
		
	}

	public String toString() {
		return this.methodName;
	}
	
	public int getParaNumber() {
		
		if(this.getReceiveType() == null) {
			return this.parameterTypes.length - 1;
		}
		return this.parameterTypes.length;
	}
	
	public String getReturnType() {
		return this.returnType;
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
}
