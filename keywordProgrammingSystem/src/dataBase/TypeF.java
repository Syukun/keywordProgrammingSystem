package dataBase;

import java.util.Set;

/**
* @author Archer Shu
* @since 2019/07/07
*/
public class TypeF {
	String name;
	Set<TypeF> superTypes;
	
	public TypeF(String name) {
		this.name = name;
		if(name!="Object") {
			superTypes.add(new TypeF("Object"));
		}else {
			superTypes = null;
		}
	}
	
}