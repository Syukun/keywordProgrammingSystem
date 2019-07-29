package dataExtractedFromSource;
/**
* @author Archer Shu
* @date 2019/05/22
*/
public class LocalVariable {
	String name;
	String type;
	String thisType;
	public LocalVariable(String localVarName, String localVarType, String thisType) {
		this.name = localVarName;
		this.type = localVarType;
		this.thisType = thisType;
	}

}
