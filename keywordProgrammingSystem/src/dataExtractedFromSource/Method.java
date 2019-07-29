package dataExtractedFromSource;
/**
* @author Archer Shu
* @date 2019/05/22
*/
public class Method {

	String methodName;
	String returnType;
	String[] parameterTypes;
	
	public Method(String methodName, String returnType, String[] parameterTypes) {
		this.methodName = methodName;
		this.returnType = returnType;
		this.parameterTypes = parameterTypes;
	}
}
