package plugin.completionProposalComputer;

import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jface.text.BadLocationException;
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
		// context for current text
		String currentText = document.get();
//		try {
//			// line number before keywords line
//			int line = document.getLineOfOffset(position);
//			int length = document.getLineLength(line+1);
//			String keywords = document.get(position-5, 4);
//			document.set("keywords are : " + keywords);
//		} catch (BadLocationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		int index = context.getInvocationOffset();
//		// return all line number
//		int lineNum = document.getNumberOfLines();
		String before = currentText.substring(0, index);
		String after = currentText.substring(index);
//		document.set(before + getDisplayString() + after);
		
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
