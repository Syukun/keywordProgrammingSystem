package plugin.completionProposalComputer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

		// Step-1 : get the Local Variable and "this" information from ASTParser
		ICompilationUnit cu = ((JavaContentAssistInvocationContext) context).getCompilationUnit();

		ASTParser parser = ASTParser.newParser(AST.JLS11);
		parser.setSource(cu);

		// get local variables
		CompilationUnit cu_ast = (CompilationUnit) parser.createAST(null);
		// get cursor location
		int cursorPos = context.getViewer().getSelectedRange().x;

		MyVisitor mv = new MyVisitor(cursorPos);
		cu_ast.accept(mv);


		// test
		DataFromSourceFile dataFromS = new DataFromSourceFile(context,monitor);
		try {
			dataFromS.extractAllTypes();
			dataFromS.extractLocalVariables();
			dataFromS.extractFields();

		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
//		/**
//		 * Test
//		 */
//		NodeFinder nf = new NodeFinder(cu_ast,cursorPos,0);
//		nf.getCoveredNode();
//		nf.getCoveringNode();
		
		
		Map<String,Type> localVars = mv.getLocalVariables();
		String nameOfThis = mv.getNameOfThis();
	
		/**
		 * get IType of "this"
		 */
		IType thisIType = null;
		// get information of "this"
		try {
			thisIType = cu.getType(nameOfThis);
			
//			/**
//			 * Test
//			 */
//			ITypeHierarchy ith = thisIType.newTypeHierarchy(monitor);
//			IType[] ts = ith.getAllClasses();
//			IType[] subs = ith.getAllSubtypes(thisIType);
			
		} catch (NullPointerException npe) {
			// TODO need to specify this code
			System.out.println("Cursor is not in any Class");
		} 
		
		Set<IType> typesFromOuterPackage = new HashSet<IType>();

			


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