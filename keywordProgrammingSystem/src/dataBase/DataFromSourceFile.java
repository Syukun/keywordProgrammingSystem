package dataBase;

import org.eclipse.jdt.core.dom.Type;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import basic.FieldName;
//import basic.LocalVariable;
import basic.MethodName;
import basic.PackageName;
import basic.TypeName;

public class DataFromSourceFile {
	public String projectName;
	// local variables from editing source file 
	public Map<String,Type> localVariables;
	// member methods from editing source file
	public Set<Method> memberMethods;
	// local field from editing source file
//	public Map<String,Type> localFields;
	
	public Set<InfoFromOutterClass> infos;

	public DataFromSourceFile() {
		this.localVariables = new HashMap<String,Type>();
	}
	

}