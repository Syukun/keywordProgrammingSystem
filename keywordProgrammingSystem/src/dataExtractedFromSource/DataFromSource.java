package dataExtractedFromSource;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
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
import plugin.completionProposalComputer.MyTypeNameMatchRequestor;
import plugin.completionProposalComputer.MyVisitor;

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

	private Map<String, Set<LocalVariable>> localVariablesRec;
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
	 * fields which receive type is the key
	 */
	private Map<String, Set<Field>> fieldsRec;
	/**
	 * methods which receive type is the key
	 */
	private Map<String, Set<MethodName>> methodsRec;
	/**
	 * fields which return type is the key
	 */
	private Map<String, Set<Field>> fieldsRet;

	/**
	 * methods which return type is the key
	 */
	private Map<String, Set<MethodName>> methodsRet;

	public DataFromSource(ContentAssistInvocationContext context, IProgressMonitor monitor) throws JavaModelException {
		this.context = context;
		this.monitor = monitor;
		this.thisICU = ((JavaContentAssistInvocationContext) context).getCompilationUnit();
		this.initTypeSystem();
		this.setLocalVariables();

	}

	/**
	 * Initial allSimpleTypes, allITypes and typeDictionary
	 * 
	 * @throws JavaModelException
	 * @since 2019/07/28
	 */
	private void initTypeSystem() throws JavaModelException {
//		this.allSimpleTypes = new HashSet<String>();
//		this.allITypes = new HashSet<IType>();
		this.typeDictionary = new HashMap<String, Type>();
		this.fieldsRec = new HashMap<String, Set<Field>>();
		this.methodsRec = new HashMap<String, Set<MethodName>>();
		this.fieldsRet = new HashMap<String, Set<Field>>();
		this.methodsRet = new HashMap<String, Set<MethodName>>();
		this.localVariablesRec = new HashMap<String, Set<LocalVariable>>();
		this.localVariablesRet = new HashMap<String, Set<LocalVariable>>();
		/**
		 * Process with ITypes from same package
		 */
		IPackageFragment iPackageFragment = (IPackageFragment) thisICU.getParent();
		ICompilationUnit[] iCompilationUnits = iPackageFragment.getCompilationUnits();

		for (ICompilationUnit iCompilationUnit : iCompilationUnits) {
			IType[] iTypes = iCompilationUnit.getAllTypes();
			for (IType iType : iTypes) {
				this.setTypeDictionary(iType);
			}
		}

		/**
		 * Extract all ITypes from ImportDeclaration and its sub Classes
		 */
		IImportDeclaration[] iImportDeclarations = this.thisICU.getImports();

		SearchEngine se = new SearchEngine();

		int packageMatchRule = SearchPattern.R_PREFIX_MATCH;
		int typeMatchRule = SearchPattern.R_EXACT_MATCH;
		// searchFor : right now just consider classes and interfaces
		int searchFor = IJavaSearchConstants.CLASS_AND_INTERFACE;
		IJavaSearchScope scope = SearchEngine.createWorkspaceScope();
		int waitingPolicy = 0;

		for (IImportDeclaration iImportDelcaration : iImportDeclarations) {
			String iimdName = iImportDelcaration.getElementName();
			int lastDotPos = iimdName.lastIndexOf('.');
			char[] packageName = getPackageName(iimdName, lastDotPos);
			char[] typeName = getTypeName(iimdName, lastDotPos);

			MyTypeNameMatchRequestor nameMatchRequestor = new MyTypeNameMatchRequestor();

			se.searchAllTypeNames(packageName, packageMatchRule, typeName, typeMatchRule, searchFor, scope,
					nameMatchRequestor, waitingPolicy, monitor);

			IType importDeclarationType = nameMatchRequestor.getIType();
			this.setTypeDictionary(importDeclarationType);
			// get subTypes of Import Declaration
			this.setAllSubITypesFromImDe(importDeclarationType);
		}

		// TODO try later set default types
		this.setDefaultTypes();
	}
//
//	private void setTypeSystem(IType iType) {
//		String typeSimpleName = iType.getElementName();
//		this.setAllSimpleTypes(typeSimpleName);
//		this.setAllITypes(iType);
//		this.setTypeDictionary(typeSimpleName, iType);
//
//	}
//
//	private void setAllSimpleTypes(String typeSimpleName) {
//		if (!this.allSimpleTypes.contains(typeSimpleName)) {
//			this.allSimpleTypes.add(typeSimpleName);
//		} else {
//			System.out.println("There is two type with same name of : " + typeSimpleName);
//		}
//	}
//
//	private void setAllITypes(IType iType) {
//		if (!this.allITypes.contains(iType)) {
//			this.allITypes.add(iType);
//		}
//	}
//

	/**
	 * set simpleName, qualifiedName, hierarchy, field, methods of a type
	 * 
	 * @param typeSimpleName
	 * @param iType
	 * @throws JavaModelException
	 */
	private void setTypeDictionary(IType iType) throws JavaModelException {
		String typeSimpleName = iType.getElementName();
		if (!this.typeDictionary.containsKey(typeSimpleName)) {
			this.typeDictionary.put(typeSimpleName, new Type(iType, this));
		} else {
			System.out.println("There are more than 2 class named " + typeSimpleName);
		}

	}

	private void setTypeDictionary(String primitiveType) throws JavaModelException {
		if (!this.typeDictionary.containsKey(primitiveType)) {
			this.typeDictionary.put(primitiveType, new Type(primitiveType));
		} else {
			System.out.println("There are more than 2 class named " + primitiveType);
		}
	}

	private char[] getPackageName(String iimdName, int lastDotPos) {
		return iimdName.substring(0, lastDotPos).toCharArray();

	}

	private char[] getTypeName(String iimdName, int lastDotPos) {
		return iimdName.substring(lastDotPos + 1).toCharArray();
	}

	private void setAllSubITypesFromImDe(IType importDeclarationType) throws JavaModelException {
		ITypeHierarchy ith = importDeclarationType.newTypeHierarchy(monitor);
		IType[] subITypes = ith.getAllSubtypes(importDeclarationType);
		for (IType subIType : subITypes) {
			this.setTypeDictionary(subIType);
		}

	}

	/**
	 * Get ITypes from java.lang.*;
	 * 
	 * @throws JavaModelException
	 */
	private void setDefaultTypes() throws JavaModelException {
//		SearchEngine se = new SearchEngine();
//
//		int packageMatchRule = SearchPattern.R_PREFIX_MATCH;
//		int typeMatchRule = SearchPattern.R_EXACT_MATCH;
//		// searchFor : right now just consider classes and interfaces
//		int searchFor = IJavaSearchConstants.CLASS_AND_INTERFACE;
//		IJavaSearchScope scope = SearchEngine.createWorkspaceScope();
//		int waitingPolicy = 0;
//		char[] packageNameLang = "java.lang".toCharArray();
//		MyTypeNameMatchRequestor nameMatchRequestorLang = new MyTypeNameMatchRequestor();
//		se.searchAllTypeNames(packageNameLang, packageMatchRule, null, 0, searchFor, scope, nameMatchRequestorLang,
//				waitingPolicy, monitor);
//		Vector<IType> iTypesLang = nameMatchRequestorLang.getITypes();
//
//		for (IType iTypeLang : iTypesLang) {
//			this.setTypeDictionary(iTypeLang);
//		}
		this.setTypeDictionary("int");
		this.setTypeDictionary("double");
		this.setTypeDictionary("float");
		this.setTypeDictionary("byte");
		this.setTypeDictionary("short");
		this.setTypeDictionary("long");
		this.setTypeDictionary("boolean");
		this.setTypeDictionary("void");
	}

	/**
	 * extract all local variables by ASTParser
	 * 
	 * @throws JavaModelException
	 */
	private void setLocalVariables() throws JavaModelException {
		// Step-1 : create a parser
		ASTParser parser = ASTParser.newParser(AST.JLS11);
		parser.setSource(thisICU);
		CompilationUnit cu = (CompilationUnit) parser.createAST(monitor);
		// get the cursor position
		int cursorPos = context.getViewer().getSelectedRange().x;
		// Use ASTVisitor to get This Type and Local Variables
		MyVisitor mv = new MyVisitor(cursorPos);
		cu.accept(mv);

		// Step-2 : Use ASTVisitor
		String thisType = mv.getNameOfThis();
		Map<String, String> localVariables = mv.getLocalVariables();

		for (String localVarName : localVariables.keySet()) {
			String localVarType = localVariables.get(localVarName);
			LocalVariable lv = new LocalVariable(localVarName, localVarType, thisType);
			this.addLocalVariableRec(thisType, lv);
			this.addLocalVariableRet(localVarType, lv);
		}
	}

	private void addLocalVariableRec(String thisType, LocalVariable lv) {
		if (!this.localVariablesRec.containsKey(thisType)) {
			this.localVariablesRec.put(thisType, new HashSet<LocalVariable>());
		}
		this.localVariablesRec.get(thisType).add(lv);

	}

	private void addLocalVariableRet(String type, LocalVariable lv) {
		if (!this.localVariablesRet.containsKey(type)) {
			this.localVariablesRet.put(type, new HashSet<LocalVariable>());
		}
		this.localVariablesRet.get(type).add(lv);
	}

	public IProgressMonitor getMonitor() {
		return this.monitor;
	}

	public void addFieldRec(String type, Field field) {
		if (!this.fieldsRec.containsKey(type)) {
			this.fieldsRec.put(type, new HashSet<Field>());
		}
		this.fieldsRec.get(type).add(field);
	}

	public void addMethodRec(String type, MethodName method) {
		if (!this.methodsRec.containsKey(type)) {
			this.methodsRec.put(type, new HashSet<MethodName>());
		}
		this.methodsRec.get(type).add(method);
	}

	public void addFieldRet(String type, Field field) {
		if (!this.fieldsRet.containsKey(type)) {
			this.fieldsRet.put(type, new HashSet<Field>());
		}
		this.fieldsRet.get(type).add(field);
	}

	public void addMethodRet(String type, MethodName method) {
		if (!this.methodsRet.containsKey(type)) {
			this.methodsRet.put(type, new HashSet<MethodName>());
		}
		this.methodsRet.get(type).add(method);
	}

	/**
	 * @since 2019/09/18
	 * @return all possible type name
	 */
	public Set<String> getAllType() {
		return this.typeDictionary.keySet();
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

	public Set<Field> getFieldsFromReceiveType(String type) {

		return this.fieldsRec.containsKey(type) ? this.fieldsRec.get(type) : new HashSet<Field>();
	}
	
	public Set<MethodName> getMethodFromReturnType(String type){
		return this.methodsRet.containsKey(type)? this.methodsRet.get(type) : new HashSet<MethodName>();
	}
}
