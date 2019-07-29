package dataBase;

import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;

public class TypeF {
	public String qulifiedName;
	Set<TypeF> superTypes;
	Set<TypeF> subTypes;
	
	IType iType;
	IProgressMonitor monitor;
	
	public TypeF(String primitiveType) {
		this.qulifiedName = primitiveType;
	}
	
	public TypeF(IType iType, IProgressMonitor monitor) throws JavaModelException {
		this.qulifiedName = iType.getFullyQualifiedName();
//		ITypeHierarchy ith = iType.newTypeHierarchy(monitor);
//		IType[] superITypes = ith.getAllSupertypes(iType);
//		IType[] subITypes = ith.getAllSubtypes(iType);
		this.iType = iType;
		this.monitor = monitor;
	}
	
}
