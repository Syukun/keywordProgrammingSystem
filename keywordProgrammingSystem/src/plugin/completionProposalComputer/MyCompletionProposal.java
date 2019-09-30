package plugin.completionProposalComputer;

import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import astNode.Expression;

public class MyCompletionProposal implements ICompletionProposal {

	private Expression expression;
	private ContentAssistInvocationContext context;
	private int index;

	private String keywords;

//	need parameter keywords
	public MyCompletionProposal(ContentAssistInvocationContext context, Expression exp, int index) {
		this.expression = exp;
		this.context = context;
		this.index = index;
	}

	@Override
	public void apply(IDocument document) {
		int position = context.getViewer().getSelectedRange().x;

		// context for current text
		String currentText = document.get();
		StringBuffer res = new StringBuffer("");

		try {
			int line = document.getLineOfOffset(position);
			int firstPosition = document.getLineOffset(line);
			int lastPosition = document.getLineOffset(line + 1);

			res.append(currentText.substring(0, firstPosition));
			res.append("\t\t" + getExpressionString() + "\n");
			res.append(currentText.substring(lastPosition));
			document.set(res.toString());

		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		context.getViewer().setSelectedRange(position + getExpressionString().length(), -1);
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

	public String getExpressionString() {
		return this.expression.toString();
	}

	@Override
	public String getDisplayString() {
		return "No." + (index+1) + "  " + this.expression.toString() + "  Score : ("
				+ this.expression.getScore(keywords).toString() + ") ";
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

	public void setKeywords(String keywords) {
		this.keywords = keywords;

	}

}
