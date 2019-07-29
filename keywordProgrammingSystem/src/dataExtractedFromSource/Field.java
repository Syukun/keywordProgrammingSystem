package dataExtractedFromSource;
/**
* @author Archer Shu
* @date 2019/07/28
*/
public class Field {
	String classSimpleName;
	String typeSimpleName;
	String fieldName;
	
	public Field(String className, String type, String field) {
		this.classSimpleName = className;
		this.typeSimpleName = type;
		this.fieldName = field;
	}
	
}
