package basic;

import java.util.List;

public class MethodName {
	String methodName;
	Type[] types;

	public MethodName(String methodName, Type[] types) {
		super();
		this.methodName = methodName;
		this.types = types;
	}

	public Type getReceiveType() {
		return types[0];
	}

	public Type getParameterType(int i) {
		return types[i];
	}

}
