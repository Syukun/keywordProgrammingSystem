package dataBase;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ITypeBinding;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class DataFromSourceFile {
	// local variables from editing source file
	// <Name : String, Type : jdt.core.dom.Type>
	public Map<String, ITypeBinding> localVariables;

	// represent the information of "this"
	public IType thisIType;

	// save the type informations of outer packages, such as imports
	public Set<IType> typesFromOuterPackage;

	public DataFromSourceFile(Map<String, ITypeBinding> localVariables, IType thisIType,
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
	
	public Vector<Method> getMemberMethod() {
		Vector<Method> res = new Vector<Method>();

		return res;
	}

	public Vector<Method> getOutterMethod() {
		Vector<Method> res = new Vector<Method>();

		return res;
	}

}
