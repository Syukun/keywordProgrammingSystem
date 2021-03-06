package dataExtractedFromSource;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

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
	 * All local variable <variable name : String,variable Simple type name String>
	 * For example: String str ==> map of (str, String)
	 */
	private Map<String, String> localVariables;


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

	public DataFromSource(ContentAssistInvocationContext context, IProgressMonitor monitor) throws JavaModelException {
		this.context = context;
		this.monitor = monitor;
		this.thisICU = ((JavaContentAssistInvocationContext) context).getCompilationUnit();
		this.initTypeSystem();
		this.setLocalVariables();

	}

	/**
	 * Initial allSimpleTypes, allITypes and typeDictionary
	 * @throws JavaModelException
	 * @since 2019/07/28
	 */
	private void initTypeSystem() throws JavaModelException {
//		this.allSimpleTypes = new HashSet<String>();
//		this.allITypes = new HashSet<IType>();
		this.typeDictionary = new HashMap<String, Type>();

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
//		this.setDefaultTypes();
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
	 * @param typeSimpleName
	 * @param iType
	 * @throws JavaModelException 
	 */
	private void setTypeDictionary(IType iType) throws JavaModelException {
		String typeSimpleName = iType.getElementName();
		if (!this.typeDictionary.containsKey(typeSimpleName)) {
			this.typeDictionary.put(typeSimpleName, new Type(iType,monitor));
		}else {
			System.out.println("There are more than 2 class named " + typeSimpleName);
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

	private void setDefaultTypes() throws JavaModelException {
		SearchEngine se = new SearchEngine();

		int packageMatchRule = SearchPattern.R_PREFIX_MATCH;
		int typeMatchRule = SearchPattern.R_EXACT_MATCH;
		// searchFor : right now just consider classes and interfaces
		int searchFor = IJavaSearchConstants.CLASS_AND_INTERFACE;
		IJavaSearchScope scope = SearchEngine.createWorkspaceScope();
		int waitingPolicy = 0;
		char[] packageNameLang = "java.lang".toCharArray();
		MyTypeNameMatchRequestor nameMatchRequestorLang = new MyTypeNameMatchRequestor();
		se.searchAllTypeNames(packageNameLang, packageMatchRule, null, 0, searchFor, scope, nameMatchRequestorLang,
				waitingPolicy, monitor);
		Vector<IType> iTypesLang = nameMatchRequestorLang.getITypes();
		
		for(IType iTypeLang : iTypesLang) {
			this.setTypeDictionary(iTypeLang);
		}
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
		this.localVariables = mv.getLocalVariables();
		for(String name : localVariables.keySet()) {
			String type = localVariables.get(name);
			this.typeDictionary.get(type).addLocalVariable(name);
		}
	}

	
}
