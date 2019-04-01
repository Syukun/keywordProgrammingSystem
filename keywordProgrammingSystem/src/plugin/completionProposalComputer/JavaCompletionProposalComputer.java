package plugin.completionProposalComputer;

import java.util.ArrayList;
import java.util.List;
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

import basic.Type;
import basic.VariableName;
import basic.Expression;
import basic.MethodName;
import dataBase.DataBase;
import generator.ExpressionGenerator;

public class JavaCompletionProposalComputer implements IJavaCompletionProposalComputer {

	@Override
	public void sessionStarted() {
		// TODO Auto-generated method stub

	}

// 
	@Override
	public List<ICompletionProposal> computeCompletionProposals(ContentAssistInvocationContext context,
			IProgressMonitor monitor) {
		
		
		List<ICompletionProposal> result = new ArrayList<ICompletionProposal>();
		
		// get cursor location
		int cursorPos = context.getViewer().getSelectedRange().x;
		
		// get current Compilation Unit
		// currently parsing the whole source file, looking whether I can get AST directly from context later
//		char[] source = context.getDocument().get().toCharArray();
		ASTParser parser = ASTParser.newParser(AST.JLS11);
		parser.setSource(((JavaContentAssistInvocationContext)context).getCompilationUnit());
		
		ASTNode cu = parser.createAST(null);
		
		// get the innerest ASTNode
		NodeFinder nodeFinder = new NodeFinder(cu,cursorPos,0);
		ASTNode innerestNode = nodeFinder.getCoveringNode();
		ASTNode parsedNode =  innerestNode;
		
		// get elements
		while(parsedNode!=null) {
//			parsedNode.accept(new MyVisitor(cursorPos));
			
//			getElement(parsedNode);
			parsedNode = parsedNode.getParent();
		}

		// test whether the keyword query have any influence on ast
		String keywords = "add line";

		Vector<Expression> exps = new ExpressionGenerator().generateExpression(10, keywords);
		for (Expression exp : exps) {
			result.add(new MyCompletionProposal(context, exp));
		}

		return result;
	}

	List<String> outcome = new ArrayList<String>();
	
	private void getElement(CompilationUnit node) {
		PackageDeclaration packageDeclaration = node.getPackage();
		if (packageDeclaration != null) {
			String packageName = packageDeclaration.getName().toString();
			outcome.add("Package Name : " + packageName + "\n");
		}
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