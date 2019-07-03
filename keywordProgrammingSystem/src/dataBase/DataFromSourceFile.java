package dataBase;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class DataFromSourceFile {
	// local variables from editing source file
	// <Name : String, Type : jdt.core.dom.Type>
	public Map<String, Type> localVariables;

	// represent the information of "this"
	public IType thisIType;

	// save the type informations of outer packages, such as imports
	public Set<IType> typesFromOuterPackage;

	public Set<TypeWithSuperTyping> allTypes;

	/**
	 * Step - 1 : icu : current Compilation unit from Java Model monitor : current
	 * monitor
	 */
	ContentAssistInvocationContext context;
	ICompilationUnit icu;
	public IProgressMonitor monitor;

	// not useful
	public DataFromSourceFile(Map<String, Type> localVariables, IType thisIType, Set<IType> typesFromOuterPackage,
			IProgressMonitor monitor) {
		super();
		this.localVariables = localVariables;
		this.thisIType = thisIType;
		this.typesFromOuterPackage = typesFromOuterPackage;
		this.allTypes = new HashSet<TypeWithSuperTyping>();
		this.monitor = monitor;
	}

	/**
	 * Get compilationUnit (Java Model) and monitor from Class
	 * JavaCompletionProposalComputer
	 * 
	 * @param icu
	 * @param monitor
	 * @date 2019/07/01
	 */
	public DataFromSourceFile(ContentAssistInvocationContext context, IProgressMonitor monitor) {
		this.context = context;
		this.icu =  ((JavaContentAssistInvocationContext) context).getCompilationUnit();
		this.monitor = monitor;
	}

	// Step - 2 : get all Types
	// TODO for right now NOT consider two classes with duplicated name
	/**
	 * 
	 * @return Set of IType(Java Model)
	 * @date 2019/07/01
	 */
	public Set<IType> getAllTypes() throws JavaModelException {
		Set<IType> res = new HashSet<IType>();

		// check the range of package Declaration whether it contains this TYpe?
		// answer is yes

		// Deal with package:
		try {
			IPackageFragment ipf = (IPackageFragment) icu.getParent();
			ICompilationUnit[] icus = ipf.getCompilationUnits();

			for (ICompilationUnit icu_inner : icus) {
				IType[] itypes = icu_inner.getAllTypes();
				for (IType itype : itypes) {
					res.add(itype);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		// Search Engine : search types and interfaces in workspace
		IImportDeclaration[] importDeclarations = icu.getImports();

		SearchEngine se = new SearchEngine();

		// TODO check packageMatchRule
		int packageMatchRule = SearchPattern.R_PREFIX_MATCH;
		
		int typeMatchRule = SearchPattern.R_EXACT_MATCH;
		// searchFor : right now just classes and interfaces
		int searchFor = IJavaSearchConstants.CLASS_AND_INTERFACE;
		IJavaSearchScope scope = SearchEngine.createWorkspaceScope();
		// TODO waitingPolicy??
		int waitingPolicy = 0;

		/**
		 * @current Need to write down full name of package when importing one.
		 */
		for (IImportDeclaration iimd : importDeclarations) {
			// get name of type ( in Import Declarations)
			String imdName = iimd.getElementName();

			// get packageName and typeName
			char[] packageName = getPackageName(imdName).toCharArray();
			
			char[] typeName = getTypeName(imdName).toCharArray();

			MyTypeNameMatchRequestor nameMatchRequestor = new MyTypeNameMatchRequestor();

			// TODO test whether could gain excepted Types

			se.searchAllTypeNames(packageName, packageMatchRule, typeName, typeMatchRule, searchFor, scope,
					nameMatchRequestor, waitingPolicy, monitor);

			res.add(nameMatchRequestor.getIType());

		}

		return res;
	}

	
	private String getPackageName(String importDeclarationName) {
		int lastDotPos = importDeclarationName.lastIndexOf('.');
		return importDeclarationName.substring(0, lastDotPos);
	}

	private String getTypeName(String importDeclarationName) {
		int lastDotPos = importDeclarationName.lastIndexOf('.');
		return importDeclarationName.substring(lastDotPos+1);
	}



	/**
	 * 
	 * @return map of < Name of Type : IType in Java Model of that type >
	 * @date 2019/07/03
	 */
	public Map<String, IType> getLocalVariables() {
		Map<String, IType> res = new HashMap<String, IType>();
		
		// Step-1 : create a parser for this
		ASTParser parser = ASTParser.newParser(AST.JLS11);
		parser.setSource(icu);
		
		CompilationUnit cu = (CompilationUnit)parser.createAST(monitor);
		// get the position of cursor
		int cursorPos = context.getViewer().getSelectedRange().x;
		
		// ASTVisitor to get local variables
		MyVisitor mv = new MyVisitor(cursorPos);
		
		// TODO finish all possible situations of local variable
		Map<String,Type> localVariable_AST = mv.getLocalVariables();
		// TODO translate to Map<String,IType>,  maybe could use stream
		// TODO END of TODAY=======================================================
		for (String s : localVariables.keySet()) {
			Type type = localVariables.get(s);
			
		}

		return res;
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

	public Vector<Method> getMemberMethod() throws JavaModelException {
		Vector<Method> res = new Vector<Method>();
		IMethod[] memberMethods = thisIType.getMethods();
		for (IMethod memberMethod : memberMethods) {
			res.add(new Method(memberMethod));
		}
		return res;
	}

	public Vector<Method> getOutterMethod() throws JavaModelException {
		Vector<Method> res = new Vector<Method>();

		for (IType type : typesFromOuterPackage) {
			IMethod[] outerMethods = type.getMethods();
			for (IMethod outerMethod : outerMethods) {
				res.add(new Method(outerMethod));
			}
		}
		return res;
	}

}
