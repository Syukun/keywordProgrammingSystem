//package dataExtractedFromSource;
//
//import java.io.Serializable;
//import java.util.Arrays;
//import java.util.HashSet;
//import java.util.Set;
//import java.util.Vector;
//
//import org.eclipse.core.runtime.IProgressMonitor;
//import org.eclipse.jdt.core.ICompilationUnit;
//import org.eclipse.jdt.core.IField;
//import org.eclipse.jdt.core.IImportDeclaration;
//import org.eclipse.jdt.core.IMethod;
//import org.eclipse.jdt.core.IPackageFragment;
//import org.eclipse.jdt.core.IType;
//import org.eclipse.jdt.core.ITypeHierarchy;
//import org.eclipse.jdt.core.JavaModelException;
//import org.eclipse.jdt.core.Signature;
//import org.eclipse.jdt.core.search.IJavaSearchConstants;
//import org.eclipse.jdt.core.search.IJavaSearchScope;
//import org.eclipse.jdt.core.search.SearchEngine;
//import org.eclipse.jdt.core.search.SearchPattern;
//import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
//import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
//
//import plugin.completionProposalComputer.MyTypeNameMatchRequestor;
//
//public class LibraryInfo implements Serializable {
//
//	//TODO figure out what is this
//	private static final long serialVersionUID = 4309974393317254006L;
//
//	private Set<Type4Data> rawTypeInformation;
//
//	private transient IProgressMonitor monitor;
//	private transient ICompilationUnit thisICU;
//
//	public LibraryInfo(ContentAssistInvocationContext context, IProgressMonitor monitor) {
//		this.monitor = monitor;
//		this.thisICU = ((JavaContentAssistInvocationContext) context).getCompilationUnit();
//
//		setRawTypeInformation(this.getRawDataFromSourceCode());
//	}
//
//	private Set<Type4Data> getRawDataFromSourceCode() {
//		Set<Type4Data> res = new HashSet<Type4Data>();
//
//		Set<Type4Data> typesFromImportPackages = getTypesFromImportPackages();
//		Set<Type4Data> typesFromSamePackage = getTypesFromSamePackage();
//		Set<Type4Data> defaultTypes = getDefaultTypes();
//		res.addAll(typesFromImportPackages);
//		res.addAll(typesFromSamePackage);
//		res.addAll(defaultTypes);
//
//		return res;
//	}
//	
//	private Set<Type4Data> getDefaultTypes() {
//		Set<Type4Data> res = new HashSet<Type4Data>();
//		SearchEngine se = new SearchEngine();
//
//		int packageMatchRule = SearchPattern.R_PREFIX_MATCH;
////		int typeMatchRule = SearchPattern.R_EXACT_MATCH;
//		// searchFor : right now just consider classes and interfaces
//		int searchFor = IJavaSearchConstants.CLASS_AND_INTERFACE;
//		IJavaSearchScope scope = SearchEngine.createWorkspaceScope();
//		int waitingPolicy = 0;
//		char[] packageNameLang = "java.lang".toCharArray();
//		MyTypeNameMatchRequestor nameMatchRequestorLang = new MyTypeNameMatchRequestor();
//		try {
//			se.searchAllTypeNames(packageNameLang, packageMatchRule, null, 0, searchFor, scope, nameMatchRequestorLang,
//					waitingPolicy, monitor);
//		} catch (JavaModelException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		Vector<IType> iTypeInJavaDotLang = nameMatchRequestorLang.getITypes();
//
//		for (IType iType : iTypeInJavaDotLang) {
//			Type4Data type4Data = setType4Data(iType);
//			res.add(type4Data);
//		}
//		/**
//		 * add primitive types
//		 */
//		String[] primitiveTypes = { "int", "double", "float", "byte", "short", "long", "boolean", "char", "void" };
//		for (String primitiveType : primitiveTypes) {
////			Type4Data type4Data = new Type4Data(primitiveType);
////			type4Data.setSimplifiedName(primitiveType);
////			res.add(type4Data);
//		}
//		return res;
//	}
//
//	private Set<Type4Data> getTypesFromImportPackages() {
//		Set<Type4Data> res = new HashSet<Type4Data>();
//		try {
//			// get import declaration information
//			IImportDeclaration[] iImportDeclarations = thisICU.getImports();
//			// setting Search Engine parameters
//			SearchEngine se = new SearchEngine();
//			int packageMatchRule = SearchPattern.R_PREFIX_MATCH;
//			int typeMatchRule = SearchPattern.R_EXACT_MATCH;
//			// searchFor : right now just consider classes and interfaces
//			int searchFor = IJavaSearchConstants.CLASS_AND_INTERFACE;
//			IJavaSearchScope scope = SearchEngine.createWorkspaceScope();
//			int waitingPolicy = 0;
//
//			for (IImportDeclaration iImportDeclaration : iImportDeclarations) {
//				String iImportDeclarationName = iImportDeclaration.getElementName();
//				int lastPos = iImportDeclarationName.lastIndexOf('.');
//				char[] packageName = this.getPackageName(iImportDeclarationName, lastPos);
//				char[] typeName = this.getTypeName(iImportDeclarationName, lastPos);
//
//				MyTypeNameMatchRequestor nameMatchRequestor = new MyTypeNameMatchRequestor();
//
//				se.searchAllTypeNames(packageName, packageMatchRule, typeName, typeMatchRule, searchFor, scope,
//						nameMatchRequestor, waitingPolicy, monitor);
//				IType importDeclarationType = nameMatchRequestor.getIType();
//				Type4Data type4Data = setType4Data(importDeclarationType);
//				res.add(type4Data);
//			}
//		} catch (JavaModelException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		return res;
//	}
//
//	private char[] getPackageName(String iimdName, int lastDotPos) {
//		return iimdName.substring(0, lastDotPos).toCharArray();
//
//	}
//
//	private char[] getTypeName(String iimdName, int lastDotPos) {
//		return iimdName.substring(lastDotPos + 1).toCharArray();
//	}
//
//	private Set<Type4Data> getTypesFromSamePackage() {
//		Set<Type4Data> res = new HashSet<Type4Data>();
//		// get package declaration information
//		IPackageFragment iPackageFragment = (IPackageFragment) thisICU.getParent();
//		try {
//			ICompilationUnit[] iCompilationUnits = iPackageFragment.getCompilationUnits();
//			for (ICompilationUnit iCompilationUnit : iCompilationUnits) {
//				IType[] iTypes = iCompilationUnit.getAllTypes();
//				for (IType iType : iTypes) {
//					Type4Data type4Data = setType4Data(iType);
//					res.add(type4Data);
//				}
//			}
//		} catch (JavaModelException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return res;
//	}
//
////	private Type4Data setType4Data(IType iType) {
////		String qualifiedName = iType.getFullyQualifiedName();
//////		Type4Data res = new Type4Data(qualifiedName);
////
////		String simpleName = iType.getElementName();
////		res.setSimplifiedName(simpleName);
////
////		try {
////			// set super and sub types
////			ITypeHierarchy ith = iType.newTypeHierarchy(monitor);
////			IType[] subITypes = ith.getAllSubtypes(iType);
////			IType[] superITypes = ith.getSupertypes(iType);
////			String[] subTypes = Arrays.stream(subITypes).map(x -> x.getFullyQualifiedName()).toArray(String[]::new);
////			String[] superTypes = Arrays.stream(superITypes).map(x -> x.getFullyQualifiedName()).toArray(String[]::new);
////			res.setSubTypes(subTypes);
////			res.setSuperTypes(superTypes);
////
////			// setField
////			IField[] iFields = iType.getFields();
////			for (IField iField : iFields) {
////				int modifier = iField.getFlags();
////				String fieldName = iField.getElementName();
////				String fieldQualifiedTypeName = getFieldQualifiedTypeName(iField);
////				Field4Data field4Data = new Field4Data(modifier, fieldQualifiedTypeName, fieldName);
////				res.setFields(field4Data);
////
////			}
////			// set Method
////			IMethod[] iMethods = iType.getMethods();
////			for (IMethod iMethod : iMethods) {
////				int modifier = iMethod.getFlags();
////				String methodName = iMethod.getElementName();
////				String returnTypeQualifiedName = getMethodReturnTypeQualifiedName(iMethod);
////				String[] parameterTypeQualifiedNames = getParameterTypeQualifiedNames(iMethod);
////				Method4Data method4Data = new Method4Data(modifier, methodName, returnTypeQualifiedName,
////						parameterTypeQualifiedNames);
////				res.setMethods(method4Data);
////			}
////
////		} catch (JavaModelException e) {
////			e.printStackTrace();
////		}
////
////		return res;
////	}
//
//	private String[] getParameterTypeQualifiedNames(IMethod iMethod) {
//		String[] parameterTypesSig = iMethod.getParameterTypes();
//		String[] res = Arrays.stream(parameterTypesSig).map(x -> this.sign2QualifiedType(x)).toArray(String[]::new);
//		return res;
//	}
//
//	private String getMethodReturnTypeQualifiedName(IMethod iMethod) {
//		String res = "";
//		String returnTypeSig;
//		try {
//			returnTypeSig = iMethod.getReturnType();
//			res += sign2QualifiedType(returnTypeSig);
//		} catch (JavaModelException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return res;
//	}
//
//	private String getFieldQualifiedTypeName(IField iField) {
//		String res = "";
//		try {
//			String iFieldTypeSig = iField.getTypeSignature();
//			res += sign2QualifiedType(iFieldTypeSig);
//
//		} catch (JavaModelException e) {
//			e.printStackTrace();
//		}
//
//		return res;
//	}
//
//	private String sign2QualifiedType(String signature) {
//		return Signature.getSignatureSimpleName(signature);
////		// TODO need to be testified
////		String qualifier = Signature.getSignatureQualifier(signature);
////		String simpleName = Signature.getSignatureSimpleName(signature);
////		if (qualifier.equals("")) {
////			return simpleName;
////		} else {
////			return qualifier + "." + simpleName;
////		}
//	}
//
//	/**
//	 * @return the rawTypeInformation
//	 */
//	public Set<Type4Data> getRawTypeInformation() {
//		return rawTypeInformation;
//	}
//
//	/**
//	 * @param rawTypeInformation the rawTypeInformation to set
//	 */
//	public void setRawTypeInformation(Set<Type4Data> rawTypeInformation) {
//		this.rawTypeInformation = rawTypeInformation;
//	}
//}
