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
		StringBuffer res =new StringBuffer("");
		
		try {
			int line = document.getLineOfOffset(position);
			int firstPosition = document.getLineOffset(line);
			int lastPosition = document.getLineOffset(line+1);
			
			res.append(currentText.substring(0, firstPosition));
			res.append("\t\t" + getDisplayString()+"\n");
			res.append(currentText.substring(lastPosition));
			document.set(res.toString());
			
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
