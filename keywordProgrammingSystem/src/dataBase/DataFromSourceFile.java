package dataBase;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.Type;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class DataFromSourceFile {
	// local variables from editing source file
	// <Name : String, Type : jdt.core.dom.Type>
	public Map<String, Type> localVariables;

	// represent the information of "this"
	public IType thisIType;

	// save the type informations of outer packages, such as imports
	public Set<IType> typesFromOuterPackage;

	public DataFromSourceFile(Map<String, Type> localVariables, IType thisIType,
			Set<IType> typesFromOuterPackage) {
		super();
		this.localVariables = localVariables;
		this.thisIType = thisIType;
		this.typesFromOuterPackage = typesFromOuterPackage;
	}

	public Map<String, IType> getMemberFields() throws JavaModelException {
		Map<String, IType> res = new HashMap<String, IType>();
		// deal with this type
		IField[] fieldsOfThis = thisIType.getFields();
		for (IField field_this : fieldsOfThis) {
			String field_this_name = field_this.getElementName();
			res.put(field_this_name, thisIType);
		}

		return res;
	}
	
	public Map<String,TypeWithSubTyping> getLocalVariables(){
		Map<String,TypeWithSubTyping> res = new HashMap<String,TypeWithSubTyping>();
		// TODO maybe could use stream
		for(String s : localVariables.keySet()) {
			Type type = localVariables.get(s);
			TypeWithSubTyping type_s = resolveTypeBinding(type);
			
		}
		
		return res;
	}

	private TypeWithSubTyping resolveTypeBinding(Type type) {		
		return new TypeWithSubTyping(type);
	}

	public Map<String, IType> getOutterFields() throws JavaModelException {
		Map<String, IType> res = new HashMap<String, IType>();

		for (IType type : typesFromOuterPackage) {
			IField[] fieldsOfOutter = type.getFields();
			for (IField field_outter : fieldsOfOutter) {
				String field_outter_name = field_outter.getElementName();
				res.put(field_outter_name, type);
			}
		}

		return res;
	}
	
	public Vector<Method> getMemberMethod() throws JavaModelException {
		Vector<Method> res = new Vector<Method>();
		IMethod[] memberMethods = thisIType.getMethods();
		for(IMethod memberMethod : memberMethods) {
			res.add(new Method(memberMethod));
		}
		return res;
	}

	public Vector<Method> getOutterMethod() throws JavaModelException {
		Vector<Method> res = new Vector<Method>();

		for(IType type : typesFromOuterPackage) {
			IMethod[] outerMethods = type.getMethods();
			for(IMethod outerMethod : outerMethods) {
				res.add(new Method(outerMethod)); 
			}
		}
		return res;
	}

}
