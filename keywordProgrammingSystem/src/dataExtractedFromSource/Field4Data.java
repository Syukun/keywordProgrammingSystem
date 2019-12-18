package dataExtractedFromSource;

public class Field4Data {
	String qualifiedTypeName;
	String fieldName;
	int modifier;
	
	public Field4Data(int modifier, String simpleTypeName, String fieldName) {
		this.modifier = modifier;
		this.qualifiedTypeName = simpleTypeName;
		this.fieldName = fieldName;
	}

}
