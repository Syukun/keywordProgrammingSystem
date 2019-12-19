package dataExtractedFromSource;

import java.util.HashSet;
import java.util.Set;

public class Type4Data {
	
	private String simplifiedName;
	private String qualifiedName;
	
	private String[] superTypes;
	private String[] subTypes;
	
	private Set<Field4Data> fields; 
	private Set<Method4Data> methods;
	
	public Type4Data(String qualifiedName) {
		this.setQualifiedName(qualifiedName);
		this.fields = new HashSet<Field4Data>();
		this.methods = new HashSet<Method4Data>();
	}
	
// getter and setter
	
	public Set<Field4Data> getFields() {
		return fields;
	}
	public void setFields(Field4Data field) {
		this.fields.add(field);
	}
	public Set<Method4Data> getMethods() {
		return methods;
	}
	public void setMethods(Method4Data method) {
		this.methods.add(method);
	}
	public String getSimplifiedName() {
		return simplifiedName;
	}
	public void setSimplifiedName(String simplifiedName) {
		this.simplifiedName = simplifiedName;
	}

	public String[] getSuperTypes() {
		return superTypes;
	}

	public void setSuperTypes(String[] superTypes) {
		this.superTypes = superTypes;
	}

	public String[] getSubTypes() {
		return subTypes;
	}

	public void setSubTypes(String[] subTypes) {
		this.subTypes = subTypes;
	}
	
	public String getQualifiedName() {
		return this.qualifiedName;
	}
	
	public void setQualifiedName(String qualifiedName) {
		this.qualifiedName = qualifiedName;
	}
	

}
