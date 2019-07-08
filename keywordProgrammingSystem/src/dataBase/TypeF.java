package dataBase;

import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;

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
	
	public TypeF(IType iType, IProgressMonitor monitor) throws JavaModelException {
		this.name = iType.getFullyQualifiedName();
		ITypeHierarchy ith = iType.newSupertypeHierarchy(monitor);
		IType[] superITypes = ith.getAllSupertypes(iType);
		for(IType superIType : superITypes) {
			this.superTypes.add(new TypeF(superIType,monitor));
		}
	}
	
}