package dataBase;


import java.util.Collection;
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
import org.eclipse.jdt.core.dom.Type;
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
	Map<IType,Map<String,TypeF>> typeDictionary;

	public DataFromSourceFile(ContentAssistInvocationContext context, IProgressMonitor monitor) {
		this.context = context;
		this.monitor = monitor;
		this.setThisICompilationUnit();
		this.typeDictionary = new HashMap<IType, Map<String, TypeF>>();
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

	public void setThisICompilationUnit() {
		this.icu = ((JavaContentAssistInvocationContext) context).getCompilationUnit();		
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
		for(IType subIType : subITypes) {
			res.add(subIType);
		}
		return res;
	}
	
	public Vector<IType> getAllITypesFromSourceFile() {
		return allITypesFromSourceFile;
	}
	
	public Map<String,TypeF> getPrimitiveType(){
		Map<String,TypeF> res = new HashMap<String,TypeF>();
		String[] priTypes = {"byte","int","short","long","char","double","boolean","float"};
		for(String priType : priTypes) {
			res.put(priType, new TypeF(priType));
		}
		return res;
	}
	/**
	 * extract all local variables by ASTParser
	 * @throws JavaModelException 
	 */
	public void setLocalVariables() throws JavaModelException {
		//Step-1 : create a parser
		ASTParser parser = ASTParser.newParser(AST.JLS11);
		parser.setSource(icu);
		CompilationUnit cu = (CompilationUnit) parser.createAST(monitor);
		// get the cursor position
		int cursorPos = context.getViewer().getSelectedRange().x;
		// Use ASTVisitor to get This Type and Local Variables
		MyVisitor mv = new MyVisitor(cursorPos);
		cu.accept(mv);
		
		Map<String,Type> localVariables_AST = mv.getLocalVariables();
		IType thisIType = findThisIType(mv.getNameOfThis());
		
		
		
		
		
	}


	private IType findThisIType(String nameOfThis) throws JavaModelException {
		IType[] currentTypes = icu.getTypes();
		for(IType type : currentTypes) {
			if(type.getElementName().equals(nameOfThis)) {
				return type;
			}
		}
		return null;
	}
}
