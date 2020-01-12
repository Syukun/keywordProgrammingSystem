package dataExtractedFromSource;

public class Method4Data{
	private int modifier;
	private String methodName;
	private String simpleReturnType;
	private String[] simpleParameterType;
	
	public Method4Data(int modifier, String methodName, String simpleReturnType, String[] simpleParameterType) {
		super();
		this.setModifier(modifier);
		this.setMethodName(methodName);
		this.setSimpleReturnType(simpleReturnType);
		this.setSimpleParameterType(simpleParameterType);
	}

	/**
	 * @return the modifier
	 */
	public int getModifier() {
		return modifier;
	}

	/**
	 * @param modifier the modifier to set
	 */
	public void setModifier(int modifier) {
		this.modifier = modifier;
	}

	/**
	 * @return the methodName
	 */
	public String getMethodName() {
		return methodName;
	}

	/**
	 * @param methodName the methodName to set
	 */
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	/**
	 * @return the qualifiedReturnType
	 */
	public String getSimpleReturnType() {
		return simpleReturnType;
	}

	/**
	 * @param qualifiedReturnType the qualifiedReturnType to set
	 */
	public void setSimpleReturnType(String simpleReturnType) {
		this.simpleReturnType = simpleReturnType;
	}

	/**
	 * @return the simpleParameterType
	 */
	public String[] getSimpleParameterType() {
		return simpleParameterType;
	}

	/**
	 * @param simpleParameterType the simpleParameterType to set
	 */
	public void setSimpleParameterType(String[] simpleParameterType) {
		this.simpleParameterType = simpleParameterType;
	}


	
	
	
}
