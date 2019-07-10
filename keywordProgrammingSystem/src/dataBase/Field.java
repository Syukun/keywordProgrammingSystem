package dataBase;

import org.eclipse.jdt.core.IType;

/**
* @author Archer Shu
* @date 2019/07/04
*/
public class Field {

	String name;
	TypeF fieldType;
	TypeF classType;
	
	public Field(String name, TypeF fieldType, TypeF classType) {
		this.name = name;
		this.fieldType = fieldType;
		this.classType = classType;
	}
	
}
