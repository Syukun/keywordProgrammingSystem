package dataBase;

import java.util.Set;

public class TypeF {
	String qulifiedName;
	Set<TypeF> superTypes;
	Set<TypeF> subTypes;
	
	public TypeF(String primitiveType) {
		this.qulifiedName = primitiveType;
	}
	
}
