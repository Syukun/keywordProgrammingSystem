package dataExtractedFromSource;

import java.util.Arrays; 
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;

import astNode.ConstructorType;
import astNode.Field;
import astNode.LocalVariable;
import astNode.Type;
import astNode.MethodName;
import plugin.completionProposalComputer.JavaCompletionProposalComputer;
import plugin.completionProposalComputer.LocalVariableVisitior;
import plugin.completionProposalComputer.MyTypeNameMatchRequestor;
//import plugin.completionProposalComputer.MyVisitor;

/**
 * the version when assume there is no Type with same Simple name
 * 
 * @author Archer Shu
 * @date 2019/07/28
 */
public class DataFromSource {
	/**
	 * All type with Simple names for example: java.lang.String is String in this
	 * system, we assume that every type have different name
	 * 
	 * <p>
	 * The type are only from 5 parts:
	 * <p>
	 * - Primitive Types
	 * <p>
	 * - Default Types ( From java.lang.*)
	 * <p>
	 * - Types in same package
	 * <p>
	 * - Types in ImportDeclaration
	 * <p>
	 * - Types in SubTypes of ImportDeclaration Types
	 * 
	 */
//	private Set<String> allSimpleTypes;

	/**
	 * Map from name of Type(String) to a type with hierarchy
	 * <p>
	 * Type : Type for Code Completion System, has current type and subTypes and
	 * superTypes
	 */
	public static Map<String, Type> typeDictionary;

	/**
	 * All local variable < variable type : String, localVar : LocalVariable >
	 * <p>
	 * For example: String str ==> map of < str, lv >
	 */
	public static Map<String, Set<LocalVariable>> localVariablesRet;

//	private Map<String, Set<LocalVariable>> localVariablesRec;
	/**
	 * All available methods from allTypes
	 * 
	 * <p>
	 * If the type does not belong to allSimpleType, then don't use this field
	 */
//	private Map<Method, String> methods;

	/**
	 * All ITypes from 4 parts:
	 * 
	 * <p>
	 * - Default ITypes from java.lang.*;
	 * <p>
	 * - ITypes from same Package including itself(This)
	 * <p>
	 * - ITypes from ImportDeclaration
	 * <p>
	 * - ITypes from subtypes of ImportDeclaration
	 */
//	private Set<IType> allITypes;

	/**
	 * context from JavaCompletionProposalComputer in
	 * plugin.completionProposalComputer
	 */
	private ContentAssistInvocationContext context;

	/**
	 * monitor from JavaCompletionProposalComputer in
	 * plugin.completionProposalComputer
	 */
	private IProgressMonitor monitor;

	/**
	 * ICompilationUnit of This
	 */
	private ICompilationUnit thisICU;

	/**
	 * // * fields which receive type is the key //
	 */
//	private Map<String, Set<Field>> fieldsRec;
	/**
	 * methods which receive type is the key
	 */
//	private Map<String, Set<MethodName>> methodsRec;
	/**
	 * fields which return type is the key
	 */
	public static Map<String, Set<Field>> fieldsRet;

	/**
	 * methods which return type is the key
	 */
	public static Map<String, Set<MethodName>> methodsRet;

	/**
	 * Constructors
	 */
	public static Map<String, Set<ConstructorType>> constructors;
	/**
	 * Qualified name of this IType
	 */
	private String thisTypeName;

	/**
	 * Package Name of this Type
	 */
	private String thisPackageName;

	/**
	 * Raw Data Informations
	 */
	private Set<Type4Data> rawTypeInformation;

	/**
	 * 
	 * @param context
	 * @param monitor
	 * @throws JavaModelException
	 */
	public DataFromSource(ContentAssistInvocationContext context, IProgressMonitor monitor) throws JavaModelException {
		this.context = context;
		this.monitor = monitor;
		this.thisICU = ((JavaContentAssistInvocationContext) context).getCompilationUnit();

		rawTypeInformation = this.getDataRaw();
		if(JavaCompletionProposalComputer.count==0) {
			this.setTypeDictionary();
		}
		this.setLocalVariablesWithReturnType();
		if(JavaCompletionProposalComputer.count==0) {
			this.setTypeDictionary();
			this.setFieldsWithReturnType();
			this.setConstructor();
			this.setMethodWithReturnType();
			JavaCompletionProposalComputer.count=1;
		}

//		this.setConstructor();

//		int count = 0;
//		for(Set<MethodName> method : this.methodsRet.values()) {
//			count += method.size();
//		}

//		System.out.print(count);

//		this.initTypeSystem();

	}

	private Set<Type4Data> getDataRaw() {
		Set<Type4Data> res = new HashSet<Type4Data>();

		Set<Type4Data> typesFromImportPackages = getTypesFromImportPackages();
		Set<Type4Data> typesFromSamePackage = getTypesFromSamePackage();
		Set<Type4Data> defaultTypes = getDefaultTypes();
		res.addAll(typesFromImportPackages);
		res.addAll(typesFromSamePackage);
		res.addAll(defaultTypes);

		return res;
	}

	private Set<Type4Data> getDefaultTypes() {
		Set<Type4Data> res = new HashSet<Type4Data>();
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
		/**
		 * add primitive types
		 */
		String[] primitiveTypes = { "int", "double", "float", "byte", "short", "long", "boolean", "char", "void", "Exception", "Object"};
		String[] objectTypes = { "java.lang.Integer", "java.lang.Double", "java.lang.Float", "java.lang.Byte",
				"java.lang.Short", "java.lang.Long", "java.lang.Boolean", "java.lang.Character", "void", "java.lang.Exception", "java.lang.Object" };
		for (int i = 0; i < primitiveTypes.length; i++) {
			String primitiveType = primitiveTypes[i];
			String objectType = objectTypes[i];

			Type4Data type4Data = new Type4Data(primitiveType, objectType);
			type4Data.setSimplifiedName(primitiveType);
			type4Data.setQualifiedName(objectType);
			res.add(type4Data);
		}
		return res;
	}

	private Set<Type4Data> getTypesFromImportPackages() {
		Set<Type4Data> res = new HashSet<Type4Data>();
		try {
			// get import declaration information
			IImportDeclaration[] iImportDeclarations = thisICU.getImports();
			// setting Search Engine parameters
			SearchEngine se = new SearchEngine();
			int packageMatchRule = SearchPattern.R_PREFIX_MATCH;
			int typeMatchRule = SearchPattern.R_EXACT_MATCH;
			// searchFor : right now just consider classes and interfaces
			int searchFor = IJavaSearchConstants.CLASS_AND_INTERFACE;
			IJavaSearchScope scope = SearchEngine.createWorkspaceScope();
			int waitingPolicy = 0;

			for (IImportDeclaration iImportDeclaration : iImportDeclarations) {
				String iImportDeclarationName = iImportDeclaration.getElementName();
				int lastPos = iImportDeclarationName.lastIndexOf('.');
				char[] packageName = this.getPackageName(iImportDeclarationName, lastPos);
				char[] typeName = this.getTypeName(iImportDeclarationName, lastPos);

				MyTypeNameMatchRequestor nameMatchRequestor = new MyTypeNameMatchRequestor();

				se.searchAllTypeNames(packageName, packageMatchRule, typeName, typeMatchRule, searchFor, scope,
						nameMatchRequestor, waitingPolicy, monitor);
				IType importDeclarationType = nameMatchRequestor.getIType();
				Type4Data type4Data = setType4Data(importDeclarationType);
				res.add(type4Data);
			}
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;
	}

	private char[] getPackageName(String iimdName, int lastDotPos) {
		return iimdName.substring(0, lastDotPos).toCharArray();

	}

	private char[] getTypeName(String iimdName, int lastDotPos) {
		return iimdName.substring(lastDotPos + 1).toCharArray();
	}

	private Set<Type4Data> getTypesFromSamePackage() {
		Set<Type4Data> res = new HashSet<Type4Data>();
		// get package declaration information
		IPackageFragment iPackageFragment = (IPackageFragment) thisICU.getParent();
		try {
			ICompilationUnit[] iCompilationUnits = iPackageFragment.getCompilationUnits();
			for (ICompilationUnit iCompilationUnit : iCompilationUnits) {
				IType[] iTypes = iCompilationUnit.getAllTypes();
				for (IType iType : iTypes) {
					Type4Data type4Data = setType4Data(iType);
					res.add(type4Data);
				}
			}
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}

	private Type4Data setType4Data(IType iType) {
		String qualifiedName = iType.getFullyQualifiedName();
		String simplifiedName = iType.getElementName();
		Type4Data res = new Type4Data(simplifiedName, qualifiedName);

		String simpleName = iType.getElementName();
		res.setSimplifiedName(simpleName);

		try {
			// set super and sub types
			ITypeHierarchy ith = iType.newTypeHierarchy(monitor);
			IType[] subITypes = ith.getAllSubtypes(iType);
			IType[] superITypes = ith.getSupertypes(iType);
			String[] subTypes = Arrays.stream(subITypes).map(x -> x.getElementName()).toArray(String[]::new);
			String[] superTypes = Arrays.stream(superITypes).map(x -> x.getElementName()).toArray(String[]::new);
			res.setSubTypes(subTypes);
			res.setSuperTypes(superTypes);

			// setField
			IField[] iFields = iType.getFields();
			for (IField iField : iFields) {
				int modifier = iField.getFlags();
				String fieldName = iField.getElementName();
				String fieldSimplifiedTypeName = getFieldSimplifiedTypeName(iField);
				Field4Data field4Data = new Field4Data(modifier, fieldSimplifiedTypeName, fieldName);
				res.setFields(field4Data);

			}
			// set Method
			IMethod[] iMethods = iType.getMethods();
			for (IMethod iMethod : iMethods) {
				int modifier = iMethod.getFlags();
				String methodName = iMethod.getElementName();
				String returnTypeSimplifiedName = getMethodReturnTypeSimplifiedName(iMethod);
				String[] parameterTypeSimplifiedNames = getParameterTypeSimplifiedNames(iMethod);
				Method4Data method4Data = new Method4Data(modifier, methodName, returnTypeSimplifiedName,
						parameterTypeSimplifiedNames);
				res.setMethods(method4Data);
			}

		} catch (JavaModelException e) {
			e.printStackTrace();
		}

		return res;
	}

	private String[] getParameterTypeSimplifiedNames(IMethod iMethod) {
		String[] parameterTypesSig = iMethod.getParameterTypes();
		String[] res = Arrays.stream(parameterTypesSig).map(x -> Signature.getSignatureSimpleName(x))
				.toArray(String[]::new);
		return res;
	}

	private String getMethodReturnTypeSimplifiedName(IMethod iMethod) {
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
		try {
			return Signature.getSignatureSimpleName(iMethod.getReturnType());
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "";
	}

	/**
	 * Return the simplified type name of a field
	 * 
	 * @param iField
	 * @return
	 */
	private String getFieldSimplifiedTypeName(IField iField) {
		String res = "";
		try {
//			String iFieldTypeSig = iField.getTypeSignature();
//			res += sign2QualifiedType(iFieldTypeSig);
			return Signature.getSignatureSimpleName(iField.getTypeSignature());

		} catch (JavaModelException e) {
			e.printStackTrace();
		}

		return res;
	}

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
//	========================================================================================-

	public void setTypeDictionary() {
		typeDictionary = new HashMap<String, Type>();
		for (Type4Data type4Data : this.rawTypeInformation) {
			String simpleName = type4Data.getSimplifiedName();
			String qualifiedName = type4Data.getQualifiedName();
			String[] superClass = type4Data.getSuperTypes();
			String[] subClass = type4Data.getSubTypes();
			Type type = new Type(simpleName, qualifiedName, superClass, subClass);
			typeDictionary.put(simpleName, type);
		}
	}

//	public Map<String, Type> getTypeDictionary() {
//		return this.typeDictionary;
//	}

	/**
	 * @since 2019/09/18
	 * @return all possible type name
	 */
//	public Set<String> getAllType() {
//		return this.typeDictionary.keySet();
//	}

	public Set<String> getAllTypesIncludeSuper(String type) {

		Set<String> res = new HashSet<String>();
		Type t = typeDictionary.get(type);
		if (t != null) {
			String[] superClasses = t.getSuperClass();
			res.add(type);
			if (superClasses != null) {
				for (String superClass : superClasses) {
					res.add(superClass);
				}
			}
		}
		return res;
	}

	public Set<String> getAllTypesIncludeSub(String type) {
		Set<String> res = new HashSet<String>();
		Type t = typeDictionary.get(type);
		if (t != null) {
			String[] subClasses = t.getSubClass();
			res.add(type);
			if (subClasses != null) {
				for (String subClass : subClasses) {
					res.add(subClass);
				}
			}
		}

		return res;

	}

	/**
	 * extract all local variables by ASTParser
	 * 
	 * @throws JavaModelException
	 */
	private void setLocalVariablesWithReturnType() {
		localVariablesRet = new HashMap<String, Set<LocalVariable>>();
		// Step-1 : create a parser
		ASTParser parser = ASTParser.newParser(AST.JLS11);
		parser.setSource(thisICU);
		CompilationUnit cu = (CompilationUnit) parser.createAST(monitor);
		// get the cursor position
		int cursorPos = context.getViewer().getSelectedRange().x;
		// Use ASTVisitor to get This Type and Local Variables
//		MyVisitor mv = new MyVisitor(cursorPos);
		LocalVariableVisitior mv = new LocalVariableVisitior(cursorPos, cu);
		cu.accept(mv);

		// Step-2 : Use ASTVisitor
		String thisType = mv.getNameOfThis();
		this.setThisTypeName(thisType);
		String thisPackage = mv.getNameOfThisPackage();
		this.setThisPackageName(thisPackage);
		Map<String, String> localVariables = mv.getLocalVariables();

		for (String localVarName : localVariables.keySet()) {
			String localVarType = localVariables.get(localVarName);
			checkTypeIsSet(localVarType);
			LocalVariable lv = new LocalVariable(localVarName, localVarType, thisType);
//			this.addLocalVariableRec(thisType, lv);
			this.addLocalVariableRet(localVarType, lv);
		}
	}

	private void addLocalVariableRet(String type, LocalVariable lv) {
		if (!localVariablesRet.containsKey(type)) {
			DataFromSource.localVariablesRet.put(type, new HashSet<LocalVariable>());
		}
		DataFromSource.localVariablesRet.get(type).add(lv);
	}

	private void setThisTypeName(String name) {
		this.thisTypeName = name;
	}

	public String getThisTypeName() {
		return thisTypeName;
	}

	public String getThisPackageName() {
		// TODO Auto-generated method stub
		return this.thisPackageName;
	}

	/**
	 * Set Field with Return Type
	 */
	private void setFieldsWithReturnType() {
		DataFromSource.fieldsRet = new HashMap<String, Set<Field>>();
		for (Type4Data type4Data : this.rawTypeInformation) {
//			Set<Field> fields = new HashSet<Field>();
			String typeSimpleName = type4Data.getSimplifiedName();
			String typeQualifiedName = type4Data.getQualifiedName();
			Set<Field4Data> field4Datas = type4Data.getFields();
			for (Field4Data field4Data : field4Datas) {
				int modifier = field4Data.getModifier();
				if (thisTypeName.equals(typeSimpleName)) {
					setFieldWithReturnTypeAuxi(typeSimpleName, field4Data);
				} else {
					if (typeQualifiedName.contains(thisPackageName)) {
						if (!Flags.isPrivate(modifier)) {
							setFieldWithReturnTypeAuxi(typeSimpleName, field4Data);
						}
					} else {
						if (Flags.isPublic(modifier)) {
							setFieldWithReturnTypeAuxi(typeSimpleName, field4Data);
						}
					}
				}

			}
		}
	}

	private void setFieldWithReturnTypeAuxi(String typeSimpleName, Field4Data field4Data) {
		int modifier = field4Data.getModifier();
		String fieldName = field4Data.getFieldName();
		String simpleTypeName = field4Data.getSimpleTypeName();
		checkTypeIsSet(simpleTypeName);

		Field field = new Field(modifier, fieldName, simpleTypeName, typeSimpleName);
		if (!DataFromSource.fieldsRet.containsKey(simpleTypeName)) {
			DataFromSource.fieldsRet.put(simpleTypeName, new HashSet<Field>());
		}
		DataFromSource.fieldsRet.get(simpleTypeName).add(field);
	}

	private void checkTypeIsSet(String simpleTypeName) {
		if (!typeDictionary.containsKey(simpleTypeName)) {
			typeDictionary.put(simpleTypeName, new Type(simpleTypeName, simpleTypeName, null, null));
		}
	}

	private void setThisPackageName(String thisPackage) {
		this.thisPackageName = thisPackage;

	}

	private void setMethodWithReturnType() {
		DataFromSource.methodsRet = new HashMap<String, Set<MethodName>>();
		for (Type4Data type4Data : this.rawTypeInformation) {
			String typeSimpleName = type4Data.getSimplifiedName();
			String typeQualifiedName = type4Data.getQualifiedName();
			Set<Method4Data> method4Datas = type4Data.getMethods();
			Set<ConstructorType> constructorsWithCertainType = new HashSet<ConstructorType>();
			for (Method4Data method4Data : method4Datas) {
				if (method4Data.getMethodName().equals(typeSimpleName)) {
					String[] parameterTypes = method4Data.getSimpleParameterType();
					constructorsWithCertainType.add(new ConstructorType(typeSimpleName, parameterTypes));
				} else {
					if (thisTypeName.equals(typeSimpleName)) {
						setMethodWithReturnTypeAuxi(typeSimpleName, method4Data);

					} else {
						int modifier = method4Data.getModifier();
						if (typeQualifiedName.contains(thisPackageName)) {
							if (!Flags.isPrivate(modifier)) {
								setMethodWithReturnTypeAuxi(typeSimpleName, method4Data);
							}
						} else {
							if (Flags.isPublic(modifier)) {
								setMethodWithReturnTypeAuxi(typeSimpleName, method4Data);
							}
						}
					}
				}

			}
			
			DataFromSource.constructors.put(typeSimpleName, constructorsWithCertainType);

		}

	}

	private void setConstructor() {
		DataFromSource.constructors = new HashMap<String, Set<ConstructorType>>();

	}

	private void setMethodWithReturnTypeAuxi(String typeSimpleName, Method4Data method4Data) {
		int modifier = method4Data.getModifier();
		String simpleReturnTypeName = method4Data.getSimpleReturnType();
		String methodName = method4Data.getMethodName();
		String[] parameterSimpleTypes = method4Data.getSimpleParameterType();

		checkTypeIsSet(simpleReturnTypeName);
		for (String parameterSimpleType : parameterSimpleTypes) {
			checkTypeIsSet(parameterSimpleType);
		}

		MethodName method = new MethodName(modifier, methodName, simpleReturnTypeName, typeSimpleName,
				parameterSimpleTypes);
		if (!DataFromSource.methodsRet.containsKey(simpleReturnTypeName)) {
			DataFromSource.methodsRet.put(simpleReturnTypeName, new HashSet<MethodName>());
		}
		DataFromSource.methodsRet.get(simpleReturnTypeName).add(method);
	}

	/**
	 * get all local variable according to return types
	 * 
	 * @param retType
	 * @return
	 */
//	public static Set<LocalVariable> getLocalVariablesFromRetType(String retType) {
//		if (DataFromSource.localVariablesRet.containsKey(retType)) {
//			return DataFromSource.localVariablesRet.get(retType);
//		} else {
//			return new HashSet<LocalVariable>();
//		}
//	}

//	public Set<Field> getFieldsFromReturnType(String type) {
//		return DataFromSource.fieldsRet.containsKey(type) ? DataFromSource.fieldsRet.get(type) : new HashSet<Field>();
//	}

	/**
	 * @param type
	 * @return
	 */
//	public Set<MethodName> getMethodFromReturnType(String type) {
//		return DataFromSource.methodsRet.containsKey(type) ? DataFromSource.methodsRet.get(type) : new HashSet<MethodName>();
//	}

}
