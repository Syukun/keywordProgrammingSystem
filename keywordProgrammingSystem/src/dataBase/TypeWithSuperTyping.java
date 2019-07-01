package dataBase;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.Type;

/**
* @author Archer Shu
* @date 2019/05/22
*/
public class TypeWithSuperTyping {
	// TODO maybe give more weight to superclass than super interface
	String currentTypeName;
	Set<String> superTypes;
	
	
	
	IProgressMonitor monitor;
	
	public TypeWithSuperTyping(){
		
	}
	
	// TODO find a way to get hierarchy from ASTParser
	// TODO a way is using search engine
	public TypeWithSuperTyping(Type type) {
		// if is not resolvable
		ITypeBinding tb = type.resolveBinding();
		this.currentTypeName = type.toString();
		if(tb!=null) {
			
		}else {
			this.superTypes = null;
		}
	}
	

	// TODO test for current type
	public TypeWithSuperTyping(IType iType,IProgressMonitor monitor) throws JavaModelException {
		this.currentTypeName = iType.getFullyQualifiedName();
		this.monitor = monitor;
		this.superTypes = new HashSet<String>();
		ITypeHierarchy ith = iType.newTypeHierarchy(monitor);
		
		IType[] superClasses = ith.getAllSuperclasses(iType);
		IType[] superInterfaces = ith.getAllSuperInterfaces(iType);
		
		for(IType superType : superClasses) {
			String superTypeName = superType.getFullyQualifiedName();
			superTypes.add(superTypeName);
		}
		
		for(IType superInterface : superInterfaces) {
			String superInterfaceName = superInterface.getFullyQualifiedName();
			superTypes.add(superInterfaceName);
		}
		
	}
	
	public boolean isBelongTo(TypeWithSuperTyping type1,TypeWithSuperTyping type2) {
		//TODO encapsulate
		String nameOne = type1.currentTypeName;
		String nameTwo = type2.currentTypeName;
		
		return (nameOne.equals(nameTwo))||(type2.superTypes.contains(nameOne));
	}
}