package dataBase;

import java.util.HashSet;
import java.util.Set;

import basic.FieldName;
import basic.LocalVariable;
import basic.MethodName;
import basic.PackageName;
import basic.TypeName;

public class DataFromSourceFile {
	public String projectName;
	public Set<LocalVariable> localVariables;
	public Set<PackageName> packages;
	public Set<TypeName> types;
	public Set<MethodName> methods;
	public Set<FieldName> fields;

	public DataFromSourceFile() {
		this.localVariables = new HashSet<LocalVariable>();
		this.packages = new HashSet<PackageName>();
		this.types = new HashSet<TypeName>();
		this.methods = new HashSet<MethodName>();
		this.fields = new HashSet<FieldName>();
	}

}
