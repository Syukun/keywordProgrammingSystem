package astNode;

import java.math.BigDecimal;
import java.util.List;

public class ConstructorType{
	
	private String typeName;
	private String[] parameterTypes;
	
	public ConstructorType(String typeName) {
		this.typeName = typeName;
		this.parameterTypes = new String[] {};
	}
	
	public ConstructorType(String typeName, String[] parameterTypes) {
		this.typeName = typeName;
		this.parameterTypes = parameterTypes;
	}
	
	public String getReturnType() {
		return this.typeName;
	}
	
	public String getParameterTypeOf(int i) {
		return parameterTypes[i];
	}
	
	public String[] getParameterTypes() {
		return this.parameterTypes;
	}
	
	public int getParameterNumber() {
		return this.parameterTypes.length;
	}




}
