package plugin.completionProposalComputer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposalComputer;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;

import basic.Expression;
import basic.LocalVariable;
import dataBase.DataFromSourceFile;
import generator.ExpressionGenerator;

public class JavaCompletionProposalComputer implements IJavaCompletionProposalComputer {

	public static Stack<String> localVariables;

	@Override
	public void sessionStarted() {
		// TODO Auto-generated method stub

	}

// 
	@SuppressWarnings("null")
	@Override
	public List<ICompletionProposal> computeCompletionProposals(ContentAssistInvocationContext context,
			IProgressMonitor monitor) {

		List<ICompletionProposal> result = new ArrayList<ICompletionProposal>();

		// initialize database
		DataFromSourceFile dataInfos = new DataFromSourceFile();

		ASTParser parser = ASTParser.newParser(AST.JLS11);
		ICompilationUnit cu = ((JavaContentAssistInvocationContext) context).getCompilationUnit();
		parser.setSource(cu);

		// get local variables
		CompilationUnit cu_ast = (CompilationUnit) parser.createAST(null);
		// TODO modify the code of Local Variable
		Stack<LocalVariable> localVars = new Stack<LocalVariable>();
		// get cursor location
		int cursorPos = context.getViewer().getSelectedRange().x;
		MyVisitor mv = new MyVisitor(cursorPos, localVars);
		cu_ast.accept(mv);
		// imports local variables from stack to set
		while (!localVars.empty()) {
			LocalVariable lv = localVars.pop();
			Type typeOfLv = lv.getType();
			String nameOfLv = lv.getName();
			if (!dataInfos.localVariables.containsKey(nameOfLv)) {
				dataInfos.localVariables.put(nameOfLv, typeOfLv);
			}
		}

		try {
			IPackageDeclaration[] packageDeclarations = cu.getPackageDeclarations();
			IImportDeclaration[] importDeclarations = cu.getImports();

			// this could be solved by just using Java Model
			Map<IPackageDeclaration, List<IType>> currentPackageToTypes = new HashMap<IPackageDeclaration, List<IType>>();
			Map<IImportDeclaration, List<IType>> importPackageToTypes = new HashMap<IImportDeclaration, List<IType>>();

			SearchEngine se = new SearchEngine();
			// using search engine
			// set Scope
			IJavaSearchScope scope = SearchEngine.createWorkspaceScope();

			for (IPackageDeclaration pd : packageDeclarations) {
				char[] pdName = pd.getElementName().toCharArray();
				List<IType> types = new ArrayList<IType>();

				MyTypeNameMatchRequestor nameRequestor = new MyTypeNameMatchRequestor(pd, types);
				se.searchAllTypeNames(pdName, SearchPattern.R_PREFIX_MATCH, null, 0, IJavaSearchConstants.TYPE, scope,
						nameRequestor, 0, monitor);

				currentPackageToTypes.put(pd, types);
				

			}
			
			
			
			// TODO do something to process import declaration informations.
			for (IImportDeclaration id : importDeclarations) {

				String idName = id.getElementName().trim();
				String packageName;
				List<IType> types = new ArrayList<IType>();
				MyTypeNameMatchRequestor nameRequestor = new MyTypeNameMatchRequestor(id, types);
				
				int id_len = idName.length();
				if (idName.endsWith(".*")) {
					packageName = idName.substring(0, id_len - 2);
					char[] pName = packageName.toCharArray();
					se.searchAllTypeNames(pName, SearchPattern.R_PREFIX_MATCH, null, 0, IJavaSearchConstants.TYPE, scope,
							nameRequestor, 0, monitor);
				} else {
					int lastDotPos = idName.lastIndexOf('.');
					packageName = idName.substring(0, lastDotPos);
					String typeName = idName.substring(lastDotPos+1);
					// TODO should figure out the exact way to use Search Pattern
					se.searchAllTypeNames(packageName.toCharArray(), SearchPattern.R_PREFIX_MATCH, typeName.toCharArray(), SearchPattern.R_SUBSTRING_MATCH, IJavaSearchConstants.TYPE, scope,
							nameRequestor, 0, monitor);
					
				}
				
				importPackageToTypes.put(id, types);
				// TODO 将下面的代码放到MyTypeNameMatchRequestor中
				for(IType type : types) {
					IMethod[] methods = type.getMethods();
					IField[] fields = type.getFields();
				}
			}

		} catch (JavaModelException jme) {
			// TODO Auto-generated catch block
			jme.printStackTrace();
		}

		// get packageName
		IJavaProject javaproject = cu.getJavaProject();

		String projectName = javaproject.getElementName();
		dataInfos.projectName = projectName;

		// package level
		IPackageFragment[] packages;

//		// get the innerest ASTNode
//		NodeFinder nodeFinder = new NodeFinder(cu, cursorPos, 0);
//		ASTNode innerestNode = nodeFinder.getCoveringNode();
//		ASTNode parsedNode = innerestNode;
//		int startPos = cursorPos;

		// test whether the keyword query have any influence on ast
		String keywords = "add line";

		Vector<Expression> exps = new ExpressionGenerator().generateExpression(10, keywords);
		for (Expression exp : exps) {
			result.add(new MyCompletionProposal(context, exp));
		}

		return result;
	}

	@Override
	public List<IContextInformation> computeContextInformation(ContentAssistInvocationContext context,
			IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sessionEnded() {
		// TODO Auto-generated method stub
	}

	private String getKeywords(ContentAssistInvocationContext context) {
		String res = "";
		IDocument document = context.getDocument();
		String currentText = document.get();
		// position of . or cursor
		int position = context.getViewer().getSelectedRange().x;
		try {
			int line = document.getLineOfOffset(position);
			int firstPosition = document.getLineOffset(line);
			int lastPosition = document.getLineOffset(line + 1) - 1;
			res += currentText.substring(firstPosition, lastPosition);

		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}

}