package dataBase;

import org.eclipse.jdt.core.IType;

/**
* @author Archer Shu
* @date 2019/07/04
*/
public class Field {

	String name;
	IType type;
	
	public Field(String name, IType type) {
		this.name = name;
		this.type = type;
	}
	
	public String getTypeName() {
		return type.getElementName();
	}
	
	public String getFullQulifiedName() {
		return type.getFullyQualifiedName();
	}
}
