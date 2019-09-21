package astNode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

import dataExtractedFromSource.DataFromSource;
import astNode.Field;
import dataExtractedFromSource.Method;

/**
 * @author Archer Shu
 * @date 2019/07/28
 */
public class Type {
	String simpleName;
	String qualifiedName;

	IType iType;
	IProgressMonitor monitor;

	Set<String> subTypes;
	Set<String> superTypes;

//	/**
//	 * change this to Set < LocalVariable >
//	 */
//	Set<String> localVariables;
	
	/**
	 * All available fields from allTypes which receive type is this type
	 * 
	 * <p>
	 * If the type does not belong to allSimpleType, then don't use this field
	 */
//	Set<Field> fields_rec;
//	/**
//	 * all available fields which return type is this type;
//	 */
//	Set<Field> field_ret;
	/**
	 * All available methods which receive type is this type
	 */
//	Set<Method> methods_rec;
//	/**
//	 * All available methods which return type is this type
//	 */
//	Set<Method> methods_ret;
	
	DataFromSource dfs;
	
	public Type(String primitiveType) throws JavaModelException {
//		this.localVariables = new HashSet<String>();
		this.qualifiedName = primitiveType;
		this.simpleName = primitiveType;
		
	}

	public Type(IType iType, DataFromSource dfs) throws JavaModelException {
		this.iType = iType;
		this.dfs = dfs;
		this.simpleName = iType.getElementName();
		this.qualifiedName = iType.getFullyQualifiedName();
		this.monitor = dfs.getMonitor();
//		this.localVariables = new HashSet<String>();
		
		this.setField();
		this.setMethod();
		
		ITypeHierarchy ith = iType.newTypeHierarchy(monitor);
		this.setSubTypes(ith);
		this.setSuperTypes(ith);
	}

//	public void addLocalVariable(String localVariable) {
//		this.localVariables.add(localVariable);
//	}

	/**
	 * set fields of an IType instance
 	 * 
	 * @throws JavaModelException
	 */
	private void setField() throws JavaModelException {
//		this.fields_rec = new HashSet<Field>();
		IField[] iFields = this.iType.getFields();
		for (IField iField : iFields) {
			String fieldName = iField.getElementName();
			String iFieldTypeSig = iField.getTypeSignature();
			String fieldType = this.sign2Type(iFieldTypeSig);
			Field field = new Field(fieldName, fieldType,simpleName);
			this.dfs.addFieldRec(simpleName, field);
			this.dfs.addFieldRet(fieldType, field);
		}
	}

	private void setMethod() throws JavaModelException {
//		this.methods_rec = new HashSet<Method>();
		IMethod[] iMethods = this.iType.getMethods();
		for (IMethod iMethod : iMethods) {
			String methodName = iMethod.getElementName();

			String returnTypeSig = iMethod.getReturnType();
			String returnType = this.sign2Type(returnTypeSig);

			String[] parameterTypesSig = iMethod.getParameterTypes();
			String[] parameterTypes = Arrays.stream(parameterTypesSig).map(x -> this.sign2Type(x))
					.toArray(String[]::new);
			
			Method method = new Method(methodName, returnType, simpleName, parameterTypes);
			this.dfs.addMethodRec(simpleName, method);
			this.dfs.addMethodRet(returnType, method);
		}
	}

	private String sign2Type(String signature) {
		return Signature.getSignatureSimpleName(signature);
	}
	
	private void setSubTypes(ITypeHierarchy ith) {
		this.subTypes = new HashSet<String>();
		IType[] subITypes = ith.getAllSubtypes(iType);
		// TODO make it faster by using stream
		for(IType subIType : subITypes) {
			String subTypeSimpleName = subIType.getElementName();
			this.subTypes.add(subTypeSimpleName);
		}
	}
	
	private void setSuperTypes(ITypeHierarchy ith) {
		this.superTypes = new HashSet<String>();
		IType[] superITypes = ith.getSupertypes(iType);
		// TODO use stream
		for(IType superIType : superITypes) {
			String superTypeSimpleName = superIType.getElementName();
			this.superTypes.add(superTypeSimpleName);
		}
		
	}
}
