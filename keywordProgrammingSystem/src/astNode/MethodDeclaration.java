package astNode;
/**
* @author Archer Shu
* @date 2019/07/31
*/
public class MethodDeclaration {
	String methodName;
	String returnType;
	String receiveType;
	String[] parameterTypes;
	
	public MethodDeclaration(String methodName, String returnType, String receiveType, String[] parameterTypes) {
		this.methodName = methodName;
		this.returnType = returnType;
		this.receiveType = receiveType;
		this.parameterTypes = parameterTypes;
	}
	
	public String getReceiveType() {
		return this.receiveType;
	}
	
	public String getParameterType(int i) {
		return parameterTypes[i];
	}
	
	public String getReturnType() {
		return this.returnType;
	}
	
	
}
