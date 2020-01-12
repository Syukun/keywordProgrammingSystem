package astNode;

import java.math.BigDecimal;
import java.util.List;

import org.eclipse.jdt.core.Flags;

/**
* @author Archer Shu
* @date 2019/07/31
*/
public class MethodName {
	
	private String methodName;
	private String returnType;
	private String receiveType;
	private String[] parameterTypes;
	
	private int modifier;
	
	public MethodName(int modifier, String methodName, String returnType, String receiveType, String[] parameterTypes) {
		this.modifier = modifier;
		this.methodName = methodName;
		this.returnType = returnType;
		this.receiveType = receiveType;
		this.parameterTypes = parameterTypes;
	}
	
	public String getReceiveType() {
		return this.receiveType;
	}
	
	
	
	public String getParameterTypeOf(int i) {
		return parameterTypes[i];
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
		BigDecimal score = ScoreDef.ZERO;
		String[] methodNameArray = ScoreDef.splitName(methodName);
		for(String word : methodNameArray) {
			score = ScoreDef.checkInKeyword(score, word.toLowerCase(), keywords);
		}
		
		return score;
	}
	
	public int getParameterNumber() {
		return this.parameterTypes.length;
	}
	
	public String toString() {
		return this.methodName;
	}

	public boolean isPublic() {
		return Flags.isPublic(this.modifier);
	}
	
	public boolean isPrivate() {
		return Flags.isPrivate(modifier);
	}
	
	public boolean isProtected() {
		return Flags.isProtected(this.modifier);
	}
	
	public boolean isStatic() {
		return Flags.isStatic(modifier);
	}
	
	public boolean isAbstract() {
		return Flags.isAbstract(modifier);
	}
	
	
}
