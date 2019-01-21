package plugin.completionProposalComputer;

import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import basic.Expression;

public class MyCompletionProposal implements ICompletionProposal {

	public Expression expression;
	public ContentAssistInvocationContext context;

//	need parameter keywords
	public MyCompletionProposal(ContentAssistInvocationContext context,Expression exp) {
		this.expression = exp;
		this.context = context;
	}

	@Override
	public void apply(IDocument document) {
		int position = context.getViewer().getSelectedRange().x;
		String currentText = document.get();
		int index = context.getInvocationOffset();
		String before = currentText.substring(0, index);
		String after = currentText.substring(index);
		document.set(before + getDisplayString() + after);
		context.getViewer().setSelectedRange(position + getDisplayString().length(), -1);
	}

	@Override
	public Point getSelection(IDocument document) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAdditionalProposalInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDisplayString() {
		// TODO Auto-generated method stub
		return this.expression.toString();
	}

	@Override
	public Image getImage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IContextInformation getContextInformation() {
		// TODO Auto-generated method stub
		return null;
	}

}
