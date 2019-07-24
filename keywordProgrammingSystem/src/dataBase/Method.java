package dataBase;

public class Method {
	String methodName;
	TypeF receiveType;
	TypeF returnType;
	TypeF[] parameterTypes;
//	TypeF[] exceptionTypes;
	public Method(String methodName, TypeF receiveType, TypeF returnType, TypeF[] parameterTypes) {
		super();
		this.methodName = methodName;
		this.receiveType = receiveType;
		this.returnType = returnType;
		this.parameterTypes = parameterTypes;
	}
	
	
}
