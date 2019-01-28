package basic;

import java.math.BigDecimal;
import java.util.List;

import dataBase.DataBase;

public class MethodName {
	String methodName;
	String[] typeNames;

	public MethodName(String methodName, String[] typeNames) {
		super();
		this.methodName = methodName;
		this.typeNames = typeNames;
	}

	public Type getReceiveType() {
		return DataBase.allTypes.get(typeNames[0]);
	}

	public String getParameterType(int i) {
		return typeNames[i];
	}

	public String toString() {
		return this.methodName;
	}
	
	public Type[] getParameterTypes() {
		int paraNum = this.getParaNumber();
		Type[] res = new Type[paraNum];
		for(int i = 0 ; i < paraNum ; i++ ) {
			res[i] = DataBase.allTypes.get(this.typeNames[i+1]);
		}
		return res;
	}
	public int getParaNumber() {
		return this.typeNames.length-1;
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
