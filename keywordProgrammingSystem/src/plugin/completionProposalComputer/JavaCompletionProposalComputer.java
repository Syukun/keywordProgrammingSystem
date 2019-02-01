package plugin.completionProposalComputer;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposalComputer;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import basic.Expression;
import dataBase.DataBase;
import generator.ExpressionGenerator;

public class JavaCompletionProposalComputer implements
	IJavaCompletionProposalComputer{

	@Override
	public void sessionStarted() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<ICompletionProposal> computeCompletionProposals(ContentAssistInvocationContext context,
			IProgressMonitor monitor) {
		List<ICompletionProposal> result = new ArrayList<ICompletionProposal>();
		// modify later
		String keywords = this.getKeywords(context);
//		String currentText = context.getDocument().get();
//		ParsingContext(context.getDocument().get());
		Vector<Expression> exps = new ExpressionGenerator().generateExpression(10, keywords);
		for(Expression exp : exps) {
			result.add(new MyCompletionProposal(context,exp));
		}


		return result;
	}

	private void ParsingContext(String currentText) {
		ASTParser parser = ASTParser.newParser(AST.JLS11);
		parser.setSource(currentText.toCharArray());
		CompilationUnit cu = (CompilationUnit)parser.createAST(null);
		cu.accept(new MyVisitor());
		
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
			int lastPosition = document.getLineOffset(line+1)-1;
			res += currentText.substring(firstPosition,lastPosition);
			
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}

}
