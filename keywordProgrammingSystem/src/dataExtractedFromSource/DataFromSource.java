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

import astNode.Field;
import astNode.LocalVariable;
import astNode.Type;
import astNode.MethodName;
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
	private Map<String, Type> typeDictionary;

	/**
	 * All local variable < variable type : String, localVar : LocalVariable >
	 * <p>
	 * For example: String str ==> map of < str, lv >
	 */
	private Map<String, Set<LocalVariable>> localVariablesRet;

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
	private Map<String, Set<Field>> fieldsRet;

	/**
	 * methods which return type is the key
	 */
	private Map<String, Set<MethodName>> methodsRet;

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
		this.setTypeDictionary();
		this.setLocalVariablesWithReturnType();
		this.setFieldsWithReturnType();
		this.setMethodWithReturnType();

//		this.initTypeSystem();

	}

	private Set<Type4Data> getDataRaw() {
		Set<Type4Data> res = new HashSet<Type4Data>();

//		Set<Type4Data> typesFromImportPackages = getTypesFromImportPackages();
		Set<Type4Data> typesFromSamePackage = getTypesFromSamePackage();
//		Set<Type4Data> defaultTypes = getDefaultTypes();
//		res.addAll(typesFromImportPackages);
		res.addAll(typesFromSamePackage);
//		res.addAll(defaultTypes);

		return res;
	}

	private Set<Type4Data> getDefaultTypes() {
		Set<Type4Data> res = new HashSet<Type4Data>();
		SearchEngine se = new SearchEngine();

		int packageMatchRule = SearchPattern.R_PREFIX_MATCH;
//		int typeMatchRule = SearchPattern.R_EXACT_MATCH;
		// searchFor : right now just consider classes and interfaces
		int searchFor = IJavaSearchConstants.CLASS_AND_INTERFACE;
		IJavaSearchScope scope = SearchEngine.createWorkspaceScope();
		int waitingPolicy = 0;
		char[] packageNameLang = "java.lang".toCharArray();
		MyTypeNameMatchRequestor nameMatchRequestorLang = new MyTypeNameMatchRequestor();
		try {
			se.searchAllTypeNames(packageNameLang, packageMatchRule, null, 0, searchFor, scope, nameMatchRequestorLang,
					waitingPolicy, monitor);
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Vector<IType> iTypeInJavaDotLang = nameMatchRequestorLang.getITypes();

		for (IType iType : iTypeInJavaDotLang) {
			Type4Data type4Data = setType4Data(iType);
			res.add(type4Data);
		}
		/**
		 * add primitive types
		 */
		String[] primitiveTypes = { "int", "double", "float", "byte", "short", "long", "boolean", "char", "void" };
		for (String primitiveType : primitiveTypes) {
			Type4Data type4Data = new Type4Data(primitiveType);
			type4Data.setSimplifiedName(primitiveType);
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
		Type4Data res = new Type4Data(qualifiedName);

		String simpleName = iType.getElementName();
		res.setSimplifiedName(simpleName);

		try {
			// set super and sub types
			ITypeHierarchy ith = iType.newTypeHierarchy(monitor);
			IType[] subITypes = ith.getAllSubtypes(iType);
			IType[] superITypes = ith.getSupertypes(iType);
			String[] subTypes = Arrays.stream(subITypes).map(x -> x.getFullyQualifiedName()).toArray(String[]::new);
			String[] superTypes = Arrays.stream(superITypes).map(x -> x.getFullyQualifiedName()).toArray(String[]::new);
			res.setSubTypes(subTypes);
			res.setSuperTypes(superTypes);

			// setField
			IField[] iFields = iType.getFields();
			for (IField iField : iFields) {
				int modifier = iField.getFlags();
				String fieldName = iField.getElementName();
				String fieldQualifiedTypeName = getFieldQualifiedTypeName(iField);
				Field4Data field4Data = new Field4Data(modifier, fieldQualifiedTypeName, fieldName);
				res.setFields(field4Data);

			}
			// set Method
			IMethod[] iMethods = iType.getMethods();
			for (IMethod iMethod : iMethods) {
				int modifier = iMethod.getFlags();
				String methodName = iMethod.getElementName();
				String returnTypeQualifiedName = getMethodReturnTypeQualifiedName(iMethod);
				String[] parameterTypeQualifiedNames = getParameterTypeQualifiedNames(iMethod);
				Method4Data method4Data = new Method4Data(modifier, methodName, returnTypeQualifiedName,
						parameterTypeQualifiedNames);
				res.setMethods(method4Data);
			}

		} catch (JavaModelException e) {
			e.printStackTrace();
		}

		return res;
	}

	private String[] getParameterTypeQualifiedNames(IMethod iMethod) {
		String[] parameterTypesSig = iMethod.getParameterTypes();
		String[] res = Arrays.stream(parameterTypesSig).map(x -> this.sign2QualifiedType(x)).toArray(String[]::new);
		return res;
	}

	private String getMethodReturnTypeQualifiedName(IMethod iMethod) {
		String res = "";
		String returnTypeSig;
		try {
			returnTypeSig = iMethod.getReturnType();
			res += sign2QualifiedType(returnTypeSig);
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}

	private String getFieldQualifiedTypeName(IField iField) {
		String res = "";
		try {
			String iFieldTypeSig = iField.getTypeSignature();
			res += sign2QualifiedType(iFieldTypeSig);

		} catch (JavaModelException e) {
			e.printStackTrace();
		}

		return res;
	}

	private String sign2QualifiedType(String signature) {
		// TODO need to be testified
		String qualifier = Signature.getSignatureQualifier(signature);
		String simpleName = Signature.getSignatureSimpleName(signature);
		if (qualifier.equals("")) {
			return simpleName;
		} else {
			return qualifier + "." + simpleName;
		}
	}
//	========================================================================================-

	public void setTypeDictionary() {
		this.typeDictionary = new HashMap<String, Type>();
		for (Type4Data type4Data : this.rawTypeInformation) {
			String simpleName = type4Data.getSimplifiedName();
			String qualifiedName = type4Data.getQualifiedName();
			String[] superClass = type4Data.getSuperTypes();
			String[] subClass = type4Data.getSubTypes();
			Type type = new Type(simpleName, qualifiedName, superClass, subClass);
			typeDictionary.put(simpleName, type);
		}
	}

	public Map<String, Type> getTypeDictionary() {
		return this.typeDictionary;
	}

	/**
	 * @since 2019/09/18
	 * @return all possible type name
	 */
	public Set<String> getAllType() {
		return this.typeDictionary.keySet();
	}

	public Set<String> getAllTypesIncludeSuper(String type) {

		Set<String> res = new HashSet<String>();
		Type t = this.typeDictionary.get(type);
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
		Type t = this.typeDictionary.get(type);
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
		this.localVariablesRet = new HashMap<String, Set<LocalVariable>>();
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
			LocalVariable lv = new LocalVariable(localVarName, localVarType, thisType);
//			this.addLocalVariableRec(thisType, lv);
			this.addLocalVariableRet(localVarType, lv);
		}
	}

	private void addLocalVariableRet(String type, LocalVariable lv) {
		if (!this.localVariablesRet.containsKey(type)) {
			this.localVariablesRet.put(type, new HashSet<LocalVariable>());
		}
		this.localVariablesRet.get(type).add(lv);
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
		this.fieldsRet = new HashMap<String, Set<Field>>();
		for (Type4Data type4Data : this.rawTypeInformation) {
			Set<Field> fields = new HashSet<Field>();
			String typeSimpleName = type4Data.getSimplifiedName();
			String typeQualifiedName = type4Data.getQualifiedName();
			Set<Field4Data> field4Datas = type4Data.getFields();
			for (Field4Data field4Data : field4Datas) {
				/**
				 * modifier=1 : public can be used in every case
				 * 
				 * modifier=2 : private can be used in this class only
				 * 
				 * modifier=0 : default can be used in a same package
				 * 
				 * modifier=4 : protected TODO later
				 * 
				 */
				int modifier = field4Data.getModifier();
				if (thisTypeName.equals(typeSimpleName)) {
					Field field = setFieldWithReturnTypeAuxi(typeSimpleName, field4Data, modifier);
					fields.add(field);
				} else {
					if (typeQualifiedName.contains(thisPackageName)) {
						if (!Flags.isPrivate(modifier)) {
							Field field = setFieldWithReturnTypeAuxi(typeSimpleName, field4Data, modifier);
							fields.add(field);
						}
					} else {
						if (Flags.isPublic(modifier)) {
							Field field = setFieldWithReturnTypeAuxi(typeSimpleName, field4Data, modifier);
							fields.add(field);
						}
					}
				}
			
			}
			this.fieldsRet.put(typeSimpleName, fields);
		}
	}

	private Field setFieldWithReturnTypeAuxi(String typeSimpleName, Field4Data field4Data, int modifier) {
		String fieldName = field4Data.getFieldName();
		String simpleTypeName = field4Data.getSimpleTypeName();
		Field field = new Field(modifier, fieldName, simpleTypeName, typeSimpleName);
		return field;
	}


	private void setThisPackageName(String thisPackage) {
		this.thisPackageName = thisPackage;

	}

	private void setMethodWithReturnType() {
		this.methodsRet = new HashMap<String, Set<MethodName>>();
		for (Type4Data type4Data : this.rawTypeInformation) {
			Set<MethodName> methodNames = new HashSet<MethodName>();
			String typeSimpleName = type4Data.getSimplifiedName();
			String typeQualifiedName = type4Data.getQualifiedName();
			Set<Method4Data> method4Datas = type4Data.getMethods();
			for (Method4Data method4Data : method4Datas) {
				int modifier = method4Data.getModifier();
				if (thisTypeName.equals(typeSimpleName)) {
					setMethodWithReturnTypeAuxi(typeSimpleName, method4Data, methodNames);
				} else {
					if (typeQualifiedName.contains(thisPackageName)) {
						if (modifier != 2) {
							setMethodWithReturnTypeAuxi(typeSimpleName, method4Data, methodNames);
						}
					} else {
						if (modifier == 1) {
							setMethodWithReturnTypeAuxi(typeSimpleName, method4Data, methodNames);
						}
					}
				}
			}
		}

	}

	private void setMethodWithReturnTypeAuxi(String typeSimpleName, Method4Data method4Data,
			Set<MethodName> methodNames) {
		String simpleReturnTypeName = method4Data.getSimpleReturnType();
		String methodName = method4Data.getMethodName();
		String[] parameterSimpleTypes =  method4Data.getSimpleParameterType();
		MethodName method = new MethodName(methodName, simpleReturnTypeName, typeSimpleName, parameterSimpleTypes);
		methodNames.add(method);
	}

	/**
	 * get all local variable according to return types
	 * 
	 * @param retType
	 * @return
	 */
	public Set<LocalVariable> getLocalVariablesFromRetType(String retType) {
		if (this.localVariablesRet.containsKey(retType)) {
			return this.localVariablesRet.get(retType);
		} else {
			return new HashSet<LocalVariable>();
		}
	}

	public Set<Field> getFieldsFromReturnType(String type) {
		return this.fieldsRet.containsKey(type) ? this.fieldsRet.get(type) : new HashSet<Field>();
	}

	public Set<MethodName> getMethodFromReturnType(String type) {
		return this.methodsRet.containsKey(type) ? this.methodsRet.get(type) : new HashSet<MethodName>();
	}

}
