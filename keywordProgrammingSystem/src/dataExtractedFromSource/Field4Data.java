package dataExtractedFromSource;

public class Field4Data implements java.io.Serializable{
	private String simpleTypeName;
	private String fieldName;
	private int modifier;
	
	public Field4Data(int modifier, String simpleTypeName, String fieldName) {
		this.setModifier(modifier);
		this.setSimpleTypeName(simpleTypeName);
		this.setFieldName(fieldName);
	}


	/**
	 * @return the fieldName
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * @param fieldName the fieldName to set
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
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
	 * @return the simpleTypeName
	 */
	public String getSimpleTypeName() {
		return simpleTypeName;
	}


	/**
	 * @param simpleTypeName the simpleTypeName to set
	 */
	public void setSimpleTypeName(String simpleTypeName) {
		this.simpleTypeName = simpleTypeName;
	}
	
	

}
