package dataBase;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IMethod;
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class DataFromSourceFile {
	// represent the information of "this"
	public IType thisIType;

	// save the type informations of outer packages, such as imports
	public Set<IType> typesFromOuterPackage;

	/**
	 * Step - 1 : icu : current Compilation unit from Java Model monitor : current
	 * monitor
	 */
	ContentAssistInvocationContext context;
	ICompilationUnit icu;
	public IProgressMonitor monitor;

	/**
	 * name : String ==> t : TypeF
	 * e.g. "byte" ==> new TypeF("byte")
	 */
	Map<String, TypeF> primitiveTypesF;
	/**
	 * TODO complete this
	 * name : String ==> t : TypeF
	 * e.g. "String" ==> new TypeF("java.lang.String")
	 */
//	Map<String, TypeF> defaultTypesF;
	
	Map<String,Map<String, TypeF>> DictionaryFindFinalTypeFromTypeName;
	Vector<IType> allTypes;
	Map<String, IType> allLocalVariables;
	Set<Field> allFields;
	Set<Method> allMethods;

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
		this.icu = ((JavaContentAssistInvocationContext) context).getCompilationUnit();
		this.monitor = monitor;
		this.primitiveTypesF = new HashMap<String, TypeF>();
//		TODO complete this
//		this.defaultTypesF = new HashMap<String, TypeF>();
		this.allTypes = new Vector<IType>();
		this.allLocalVariables = new HashMap<String, IType>();
		this.allFields = new HashSet<Field>();
	}

	/**
	 * put elements on map of name:String ==> type : IType
	 * 
	 * @throws JavaModelException
	 * @since 2019/07/07
	 */
	public void initialPrimitiveType() throws JavaModelException {
		String[] priTypes = {"byte","short","int","long","float","double","boolean","char"};
		for(String priType : priTypes) {
			this.primitiveTypesF.put(priType, new TypeF(priType));
		}
		

	}

	// Step - 2 : get all Types
	// TODO for right now NOT consider two classes with duplicated name
	/**
	 * extract all Type
	 * 
	 * @return
	 * @date 2019/07/01
	 */
	public void extractAllTypes() throws JavaModelException {

		// check the range of package Declaration whether it contains this TYpe?
		// answer is yes

		// Deal with package:
		try {
			IPackageFragment ipf = (IPackageFragment) icu.getParent();
			ICompilationUnit[] icus = ipf.getCompilationUnits();

			for (ICompilationUnit icu_inner : icus) {
				IType[] itypes = icu_inner.getAllTypes();
				for (IType itype : itypes) {
					allTypes.add(itype);
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

			allTypes.add(nameMatchRequestor.getIType());

		}
		/**
		 * TODO right now not consider primitive type: which means we need to write down
		 * import informations when we use a type even for primitive type such as String
		 */

	}

	private String getPackageName(String importDeclarationName) {
		int lastDotPos = importDeclarationName.lastIndexOf('.');
		return importDeclarationName.substring(0, lastDotPos);
	}

	private String getTypeName(String importDeclarationName) {
		int lastDotPos = importDeclarationName.lastIndexOf('.');
		return importDeclarationName.substring(lastDotPos + 1);
	}
	
	/**
	 * TODO next mission --- finish this method
	 * Map(IType ==> String, Map( String, TypeF))
	 */
	public void initialDictionary() {
		for(IType iType : allTypes) {
			String iTypeName = iType.getFullyQualifiedName();
			Map<String,TypeF> strToTypeF = getStrToTypeF(iType);
		}
	}

	private Map<String, TypeF> getStrToTypeF(IType iType) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Step - 3 : get all local variables
	 * 
	 * @return map of < Name of Type , IType in Java Model of that type >
	 * @throws JavaModelException
	 * @date 2019/07/03
	 */
	public void extractLocalVariables() throws JavaModelException {
		// Step-1 : create a parser for this
		ASTParser parser = ASTParser.newParser(AST.JLS11);
		parser.setSource(icu);

		CompilationUnit cu = (CompilationUnit) parser.createAST(monitor);
		// get the position of cursor
		int cursorPos = context.getViewer().getSelectedRange().x;

		// ASTVisitor to get local variables
		MyVisitor mv = new MyVisitor(cursorPos);
		cu.accept(mv);

		// TODO finish all possible situations of local variable
		Map<String, Type> localVariables_AST = mv.getLocalVariables();
		// TODO END of TODAY=======================================================
		/**
		 * @since 2019/07/04 translate Map<X,Y> to Map<X,Z> TODO translate to functional
		 *        programming to make it more concise
		 */

		for (String localVarName : localVariables_AST.keySet()) {
			Type localVarType = localVariables_AST.get(localVarName);
			try {
				IType localVarIType = findIType(localVarType);
				allLocalVariables.put(localVarName, localVarIType);
			} catch (NullPointerException e) {
				e.getStackTrace();
			}

		}
	}

	/**
	 * return all field information extracted from source file
	 * 
	 * @return set of Field
	 * @throws JavaModelException
	 * @since 2019/07/04
	 */
	public void extractFields() throws JavaModelException {
		for (IType t : allTypes) {
			// add field from itself
			IField[] selfFields = t.getFields();
			for (IField selfField : selfFields) {
				String selfFieldName = selfField.getElementName();
				Field selfF = new Field(selfFieldName, t);
				allFields.add(selfF);
			}
			// get super classes and interfaces
			ITypeHierarchy ith = t.newTypeHierarchy(monitor);
			// 1. get all super classes
			IType[] superClasses = ith.getAllSuperclasses(t);
			// get field info from those super classes
			for (IType superClass : superClasses) {
				IField[] superClassFields = superClass.getFields();
				for (IField superClassField : superClassFields) {
					String superClassFieldName = superClassField.getElementName();
					Field superClassF = new Field(superClassFieldName, superClass);
					allFields.add(superClassF);
				}
			}

			// 2. get all interfaces
			// TODO check difference between getAllSuperClasses and getAllSuperClasses(Type
			// t)
			IType[] superInterfaces = ith.getAllSuperInterfaces(t);
			// get field info from those super interfaces
			for (IType superInterface : superInterfaces) {
				IField[] superInterfaceFields = superInterface.getFields();
				for (IField superInterfaceField : superInterfaceFields) {
					String superInterfaceFieldName = superInterfaceField.getElementName();
					Field superInterfaceF = new Field(superInterfaceFieldName, superInterface);
					allFields.add(superInterfaceF);
				}
			}
		}
	}

	/**
	 * return all methods
	 * 
	 * @return
	 * @throws JavaModelException
	 * @since 2019/07/04,2019/07/05
	 */
	public void extractMethods() throws JavaModelException {
		for (IType iType : allTypes) {
			// get methods from self class
			IMethod[] selfMethods = iType.getMethods();
			for (IMethod selfMethod : selfMethods) {
				/**
				 * TODO check three things: 1. What will happen when Return Type is void 2. What
				 * will happen when Receive Type is null 3. Does subclass has methods in super
				 * Class when use get Methods(); --- ok 4. Translate String to IType by analyze
				 * the signature
				 */
				// find return Type of self method

				Method selfM = new Method(selfMethod, iType, allTypes);

			}

		}
	}

	/**
	 * translate Type from AST to IType from Java Model
	 * 
	 * @param t
	 * @param allTypes
	 * @return
	 * @since 2019/07/04
	 */
	private IType findIType(Type t) {
		String typeName = t.toString();
		return this.findIType(typeName);

	}

	/**
	 * find proper IType (Java Model) from allTypes by checking the name(String)
	 * 
	 * @param typeName
	 * @return
	 * @since 2019/07/04
	 */
	private IType findIType(String typeName) {
		for (IType iType : this.allTypes) {
			String iTypeName = iType.getElementName();
			if (iTypeName.equals(typeName)) {
				return iType;
			}
		}
		return null;
	}

}
