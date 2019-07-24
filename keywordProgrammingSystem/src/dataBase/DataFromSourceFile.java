package dataBase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.eclipse.core.runtime.IProgressMonitor;
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

import plugin.completionProposalComputer.MyTypeNameMatchRequestor;
import plugin.completionProposalComputer.MyVisitor;

public class DataFromSourceFile {

	ContentAssistInvocationContext context;
	IProgressMonitor monitor;

	private Vector<IType> allITypesFromSourceFile;
	private ICompilationUnit icu;
	// (name : IType) -> (SimpleName : String) -> TypeF
//	Map<IType,Map<String,TypeF>> typeDictionary;
	// now just consider the type in current class
	private Map<String, TypeF> typeDictionary;

	private Map<String, TypeF> localVariables;

	private Set<Field> fields;

	private Set<Method> methods;

	public DataFromSourceFile(ContentAssistInvocationContext context, IProgressMonitor monitor)
			throws JavaModelException {
		this.context = context;
		this.monitor = monitor;
		this.setThisICompilationUnit();
		this.setAllITypesFromSourceFile();
		this.setTypeDictionary();
//		this.typeDictionary = new HashMap<IType, Map<String, TypeF>>();
		this.setLocalVariables();
		this.setFields();
		this.setMethods();
	}

	public void setThisICompilationUnit() {
		this.icu = ((JavaContentAssistInvocationContext) context).getCompilationUnit();
	}

	/**
	 * Extract all IType from source files. Basically from package and import
	 * declaration information.
	 * 
	 * TODO right now don't consider two classes with same name in different
	 * package.
	 * 
	 * @throws JavaModelException
	 * @since 2019/07/21
	 */
	public void setAllITypesFromSourceFile() throws JavaModelException {
		this.allITypesFromSourceFile = new Vector<IType>();

		/**
		 * get ICompilationUnit of source file.
		 */
		this.allITypesFromSourceFile.addAll(getAllITypes(icu));

	}

	private Map<String, TypeF> getPrimitiveTypes() {
		Map<String, TypeF> res = new HashMap<String, TypeF>();
		String[] priTypes = { "byte", "int", "short", "long", "char", "double", "boolean", "float" };
		for (String priType : priTypes) {
			res.put(priType, new TypeF(priType));
		}
		return res;
	}

	public void setTypeDictionary() throws JavaModelException {
		typeDictionary = new HashMap<String, TypeF>();
		typeDictionary.putAll(getPrimitiveTypes());
		for (IType iType : allITypesFromSourceFile) {
			String iTypeName = iType.getElementName();
			typeDictionary.put(iTypeName, new TypeF(iType, monitor));
		}

	}

	public Vector<IType> getAllITypes(ICompilationUnit icu) throws JavaModelException {
		Vector<IType> res = new Vector<IType>();
		/**
		 * Extract all ITypes from same Package
		 */
		IPackageFragment ipf = (IPackageFragment) icu.getParent();
		ICompilationUnit[] icus = ipf.getCompilationUnits();

		for (ICompilationUnit icu_inner : icus) {
			IType[] itypes = icu_inner.getAllTypes();
			for (IType itype : itypes) {
				res.add(itype);
			}
		}

		/**
		 * Extract all ITypes from ImportDeclaration and its sub Classes
		 * 
		 * TODO consider the imports are independent which means one imports should not
		 * be a super class or a sub class of another one.
		 */
		IImportDeclaration[] iimds = icu.getImports();

		SearchEngine se = new SearchEngine();

		int packageMatchRule = SearchPattern.R_PREFIX_MATCH;
		int typeMatchRule = SearchPattern.R_EXACT_MATCH;
		// searchFor : right now just consider classes and interfaces
		int searchFor = IJavaSearchConstants.CLASS_AND_INTERFACE;
		IJavaSearchScope scope = SearchEngine.createWorkspaceScope();
		int waitingPolicy = 0;

		for (IImportDeclaration iimd : iimds) {
			String iimdName = iimd.getElementName();
			int lastDotPos = iimdName.lastIndexOf('.');
			char[] packageName = getPackageName(iimdName, lastDotPos);
			char[] typeName = getTypeName(iimdName, lastDotPos);

			MyTypeNameMatchRequestor nameMatchRequestor = new MyTypeNameMatchRequestor();

			se.searchAllTypeNames(packageName, packageMatchRule, typeName, typeMatchRule, searchFor, scope,
					nameMatchRequestor, waitingPolicy, monitor);

			IType importDeclarationType = nameMatchRequestor.getIType();
			res.add(importDeclarationType);
			res.addAll(getAllSubITypes(importDeclarationType));

		}

		/**
		 * TODO Extract all ITypes from java.lang.*;
		 */
		char[] packageNameLang = "java.lang".toCharArray();
		MyTypeNameMatchRequestor nameMatchRequestorLang = new MyTypeNameMatchRequestor();
		se.searchAllTypeNames(packageNameLang, packageMatchRule, null, 0, searchFor, scope, nameMatchRequestorLang,
				waitingPolicy, monitor);
		Vector<IType> iTypesLang = nameMatchRequestorLang.getITypes();
		res.addAll(iTypesLang);

		return res;
	}

	private char[] getPackageName(String iimdName, int lastDotPos) {
		return iimdName.substring(0, lastDotPos).toCharArray();

	}

	private char[] getTypeName(String iimdName, int lastDotPos) {
		return iimdName.substring(lastDotPos + 1).toCharArray();
	}

	private Vector<IType> getAllSubITypes(IType importDeclarationType) throws JavaModelException {
		Vector<IType> res = new Vector<IType>();
		ITypeHierarchy ith = importDeclarationType.newTypeHierarchy(monitor);
		IType[] subITypes = ith.getAllSubtypes(importDeclarationType);
		for (IType subIType : subITypes) {
			res.add(subIType);
		}
		return res;
	}

	public Vector<IType> getAllITypesFromSourceFile() {
		return allITypesFromSourceFile;
	}

	/**
	 * extract all local variables by ASTParser
	 * 
	 * @throws JavaModelException
	 */
	public void setLocalVariables() throws JavaModelException {
		this.localVariables = new HashMap<String, TypeF>();
		// Step-1 : create a parser
		ASTParser parser = ASTParser.newParser(AST.JLS11);
		parser.setSource(icu);
		CompilationUnit cu = (CompilationUnit) parser.createAST(monitor);
		// get the cursor position
		int cursorPos = context.getViewer().getSelectedRange().x;
		// Use ASTVisitor to get This Type and Local Variables
		MyVisitor mv = new MyVisitor(cursorPos);
		cu.accept(mv);

		// Step-2 : Use ASTVisitor
		Map<String, String> localVariables_AST = mv.getLocalVariables();

		for (String localVarName : localVariables_AST.keySet()) {
			String localTypeName = localVariables_AST.get(localVarName);
			TypeF localTypeF = typeDictionary.get(localTypeName);
			this.localVariables.put(localVarName, localTypeF);
		}

	}

	public Map<String, TypeF> getLocalVariables(){
		return this.localVariables;
	}
	/**
	 * extract all fields
	 * 
	 * @throws JavaModelException
	 */
	public void setFields() throws JavaModelException {
		this.fields = new HashSet<Field>();
		for (IType iType : this.allITypesFromSourceFile) {
			IField[] iFields = iType.getFields();
			TypeF classType = new TypeF(iType, monitor);
			for (IField iField : iFields) {
				String iFieldName = iField.getElementName();
				String iFieldTypeSig = iField.getTypeSignature();
				TypeF iFieldTypeF = this.getTypeFFromSign(iFieldTypeSig);

				if (iFieldTypeF != null) {
					this.fields.add(new Field(classType, iFieldTypeF, iFieldName));
				}
			}
		}
	}
	
	public Set<Field> getFields(){
		return this.fields;
	}

	public void setMethods() throws JavaModelException {
		this.methods = new HashSet<Method>();
		for (IType iType : this.allITypesFromSourceFile) {
			IMethod[] iMethods = iType.getMethods();
			TypeF receiveTypeF = new TypeF(iType, monitor);

			for (IMethod iMethod : iMethods) {
				String methodName = iMethod.getElementName();

				String returnTypeSig = iMethod.getReturnType();
				TypeF returnTypeF = this.getTypeFFromSign(returnTypeSig);

				String[] parameterTypeSig = iMethod.getParameterTypes();
//				Stream<String> typeSignStream = Stream.of(parameterTypeSig);
//				typeSignStream.forEach(x -> getTypeFFromSign(x));
//				TypeF[] parameterTypesF = typeSignStream.toArray(TypeF[]::new);

				TypeF[] parameterTypesF = Arrays.stream(parameterTypeSig).map(x -> getTypeFFromSign(x))
						.toArray(TypeF[]::new);

				Method method = new Method(methodName, receiveTypeF, returnTypeF, parameterTypesF);
				this.methods.add(method);
			}
		}
	}

	private TypeF getTypeFFromSign(String signature) {
		String typeName = Signature.getSignatureSimpleName(signature);
		return this.typeDictionary.get(typeName);
	}
	
	public Set<Method> getMethods(){
		return this.methods;
	}

	public Set<TypeF> getAllTypeF() {
//		TODO complete this method
		return new HashSet<TypeF>(this.typeDictionary.values());
	}
	
	public Set<String> getAllTypes(){
		return this.typeDictionary.keySet();
	}
	
	public Map<String, TypeF> getTypeDictionary(){
		return this.typeDictionary;
	}
	
	
//	private IType findThisIType(String nameOfThis) throws JavaModelException {
//		IType[] currentTypes = icu.getTypes();
//		for(IType type : currentTypes) {
//			if(type.getElementName().equals(nameOfThis)) {
//				return type;
//			}
//		}
//		return null;
//	}
}
