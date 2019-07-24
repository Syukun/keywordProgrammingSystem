package plugin.completionProposalComputer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposalComputer;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;

import basic.Expression;
import dataBase.DataFromSourceFile;
import dataBase.TypeF;
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
		
		try {
			
			/**
			 * Extract data from source codes
			 */
			DataFromSourceFile dataFromSourceFile = new DataFromSourceFile(context,monitor);
			
			// test whether the keyword query have any influence on ast
			String keywords = "add line";

			Vector<Expression> exps = new ExpressionGenerator(dataFromSourceFile).generateExpression(10, keywords);
			for (Expression exp : exps) {
				result.add(new MyCompletionProposal(context, exp));
			}

		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		// get the innerest ASTNode
//		NodeFinder nodeFinder = new NodeFinder(cu, cursorPos, 0);
//		ASTNode innerestNode = nodeFinder.getCoveringNode();
//		ASTNode parsedNode = innerestNode;
//		int startPos = cursorPos;
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