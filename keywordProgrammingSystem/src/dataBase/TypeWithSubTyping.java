package dataBase;

import java.util.Set;

import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.Type;

/**
* @author Archer Shu
* @date 2019/05/22
*/
public class TypeWithSubTyping {
	// TODO maybe give more weight to superclass than super interface
	String currentTypeName;
	Set<TypeWithSubTyping> subTyping;
	
	
	public TypeWithSubTyping(){
		
	}
	
	// TODO find a way to get hierarchy from ASTParser
	public TypeWithSubTyping(Type type) {
		// if is not resolvable
		ITypeBinding tb = type.resolveBinding();
		this.currentTypeName = type.toString();
		if(tb!=null) {
			
		}else {
			this.subTyping = null;
		}
	}
	
	public boolean isBelongTo(TypeWithSubTyping type1,TypeWithSubTyping type2) {
		return false;
	}
}