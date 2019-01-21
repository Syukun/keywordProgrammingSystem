package plugin.completionProposalComputer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposalComputer;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import basic.Expression;
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
		String keywords = "a";
		for(Expression exp : ExpressionGenerator.generateExpression(1, keywords)) {
			result.add(new MyCompletionProposal(context,exp));
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

}
