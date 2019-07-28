package dataExtractedFromSource;

import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IType;

/**
* @author Archer Shu
* @date 2019/07/28
*/
public class Type {

	String simpleName;
	String qualifiedName;
	
	IType iType;
	
	Set<String> subTypes;
	Set<String> superTypes;
	
	public Type() {
		
	}
	
	public Type(IType iType, IProgressMonitor monitor) {
		this.iType = iType;
		this.simpleName = iType.getElementName();
	}
}
