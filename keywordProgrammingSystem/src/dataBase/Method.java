package dataBase;

import java.util.Vector;
import java.util.stream.Stream;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.dom.Type;

public class Method {

	IMethod iMethod;
	Vector<IType> allTypes;

	private String methodName;
	private IType receiveType;
	private IType returnType;
	private IType[] parameterTypes;
	private IType[] exceptionTypes;
	
	private static final String B = "java.lang.Byte";
	private static final String C = "java.lang.Character";
	private static final String D = "java.lang.Double";
	private static final String F = "java.lang.Float";
	private static final String I = "java.lang.Integer";
	private static final String J = "java.lang.Long";
	private static final String S = "java.lang.Short";
	private static final String V = "void";
	private static final String Z = "java.lang.Boolean";
	

	public Method(IMethod iMethod, IType iType, Vector<IType> allTypes) throws JavaModelException {
		this.iMethod = iMethod;
		this.setReceiveType(iType);
		this.allTypes = allTypes;

		this.fillAllFields();

	}

	private void fillAllFields() throws JavaModelException {
		findMethodName();
		findReturnType();
		findParameterTypes();
		findExceptionTypes();
	}
	/**
	 * @throws JavaModelException 
	 * @since 2019/07/05
	 * 
	 */
	private void findExceptionTypes() throws JavaModelException {
		// String[] ==> IType[]
		// X:String ==> findTypeFromSign(X) : IType
 		// get type signature
		String[] exceptionTypeSigns = this.iMethod.getExceptionTypes();
		Stream<String> typeSignStream = Stream.of(exceptionTypeSigns);
		typeSignStream.forEach(x -> this.findITypeFromSign(x));
		this.exceptionTypes = typeSignStream.toArray(IType[]::new);
	}

	private void findParameterTypes() {
		// TODO 
		String[] parameterTypeSigns = this.iMethod.getParameterTypes();
		Stream<String> typeSignStream = Stream.of(parameterTypeSigns);
		typeSignStream.forEach(x -> this.findITypeFromSign(x));
		this.parameterTypes = typeSignStream.toArray(IType[]::new);
	}

	private void findReturnType() throws JavaModelException {
		// TODO 
		String returnTypeSign = this.iMethod.getReturnType();
		this.returnType = this.findITypeFromSign(returnTypeSign);
	}

	private void findMethodName() {
		this.methodName = this.iMethod.getElementName();

	}
	
	// TODO signature : String ==> t : IType
	private IType findITypeFromSign(String signature) {
		//TODO deal with array
		int arrayCount = Signature.getArrayCount(signature);
		String elementType = signature;
		if(arrayCount!=0) {
			elementType = Signature.getElementType(signature);
		}
		
		String signSimpleName = Signature.getSignatureSimpleName(elementType);
		
		switch(signSimpleName) {
		//TODO could modified after update to java 12
			case "V" : return null;
			case "byte":
			case "char":
			case "double":
			case "float":
			case "int":
			case "long":
			case "short":
			case "boolean":
				return findPrimitiveIType(Signature.getSignatureSimpleName(signSimpleName));
			default :
				return findIType(Signature.getSignatureSimpleName(signSimpleName));
		}
	}
	
	
	private IType findPrimitiveIType(String signatureSimpleName) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * find proper IType (Java Model) from allTypes by checking the name(String)
	 * @param typeName
	 * @return
	 * @since 2019/07/04
	 */
	private IType findIType(String typeName) {
		for(IType iType : this.allTypes) {
			String iTypeName = iType.getElementName();
			if(iTypeName.equals(typeName)) {
				return iType;
			}
		}
		return null;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public IType getReturnType() {
		return returnType;
	}

	public void setReturnType(IType returnType) {
		this.returnType = returnType;
	}

	public IType[] getParameterTypes() {
		return parameterTypes;
	}

	public void setParameterTypes(IType[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}

	public IType[] getExceptionTypes() {
		return exceptionTypes;
	}

	public void setExceptionTypes(IType[] exceptionTypes) {
		this.exceptionTypes = exceptionTypes;
	}

	public IType getReceiveType() {
		return receiveType;
	}

	public void setReceiveType(IType receiveType) {
		this.receiveType = receiveType;
	}

}
