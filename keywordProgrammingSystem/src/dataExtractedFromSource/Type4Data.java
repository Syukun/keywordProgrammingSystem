package dataExtractedFromSource;

import java.util.HashSet;
import java.util.Set;

public class Type4Data {
	
	private String simplifiedName;
	String qualifiedName;
	
	public String[] superTypes;
	public String[] subTypes;
	
	private Set<Field4Data> fields; 
	private Set<Method4Data> methods;
	
	public Type4Data(String qualifiedName) {
		this.qualifiedName = qualifiedName;
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
	
	

}
