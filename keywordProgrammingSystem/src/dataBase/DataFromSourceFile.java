package dataBase;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.Type;

import java.util.Map;
import java.util.Set;

public class DataFromSourceFile {
	// local variables from editing source file
	// <Name : String, Type : jdt.core.dom.Type>
	public Map<String, Type> localVariables;

	// represent the information of "this"
	public IType thisIType;

	// save the type informations of outer packages, such as imports
	public Set<IType> typesFromOuterPackage;

	public DataFromSourceFile(Map<String, Type> localVariables, IType thisIType, Set<IType> typesFromOuterPackage) {
		super();
		this.localVariables = localVariables;
		this.thisIType = thisIType;
		this.typesFromOuterPackage = typesFromOuterPackage;
	}

	

}
