package plugin.completionProposalComputer;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposalComputer;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;

import astNode.Expression;
import dataExtractedFromSource.DataFromSource;
import generator.ExpressionGenerator;

public class JavaCompletionProposalComputer implements IJavaCompletionProposalComputer {

//	public static Stack<String> localVariables;

	@Override
	public void sessionStarted() {

	}

// 
	@SuppressWarnings("null")
	@Override
	public List<ICompletionProposal> computeCompletionProposals(ContentAssistInvocationContext context,
			IProgressMonitor monitor) {

		List<ICompletionProposal> result = new ArrayList<ICompletionProposal>();

//		/**
//		 * Extract Local Variables
//		 */
//		int cursorPos = context.getViewer().getSelectedRange().x;
//		ASTParser parser = ASTParser.newParser(AST.JLS12);
//		parser.setSource(((JavaContentAssistInvocationContext) context).getCompilationUnit());
//		CompilationUnit cu = (CompilationUnit) parser.createAST(monitor);
//
//		LocalVariableVisitior lvv = new LocalVariableVisitior(cursorPos, cu);
//		cu.accept(lvv);
//
//		Map<String, String> localVariables = lvv.getLocalVariables();
//
//		for (String name : localVariables.keySet()) {
//			String type = localVariables.get(name);
//			ICompletionProposal icp = new ICompletionProposal() {
//
//				@Override
//				public void apply(IDocument document) {
//					// TODO Auto-generated method stub
//
//				}
//
//				@Override
//				public Point getSelection(IDocument document) {
//					// TODO Auto-generated method stub
//					return null;
//				}
//
//				@Override
//				public String getAdditionalProposalInfo() {
//					// TODO Auto-generated method stub
//					return null;
//				}
//
//				@Override
//				public String getDisplayString() {
//					// TODO Auto-generated method stub
//					return "Name : " + name + "  Type : " + type;
//				}
//
//				@Override
//				public Image getImage() {
//					// TODO Auto-generated method stub
//					return null;
//				}
//
//				@Override
//				public IContextInformation getContextInformation() {
//					// TODO Auto-generated method stub
//					return null;
//				}
//
//			};
//			result.add(icp);
//		}

		try {
			
			DataFromSource dfs = new DataFromSource(context,monitor);
			
			// test whether the keyword query have any influence on ast
			String keywords = getKeywords(context);
			int depth = 3;
			
			ExpressionGenerator expressionGenerator = new ExpressionGenerator();
			expressionGenerator.setDataFromSource(dfs);
			Vector<Expression> finalChoicesExpressions = expressionGenerator.getFinalExpressions(depth,keywords);
			//TODO eliminate duplicated one need to be modified
			Vector<Expression> finalExps = finalChoicesExpressions.stream().distinct().collect(Collectors.toCollection(Vector::new));
			int finalResultSize = finalExps.size();
			
			for(int i=0;i<finalResultSize;i++) {
				Expression finalChoiceExpression = finalExps.get(i);
				MyCompletionProposal mcp = new MyCompletionProposal(context, finalChoiceExpression,i);
				mcp.setKeywords(keywords);
				result.add(mcp);
			}

		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		// eliminate "//"
		res = res.replaceFirst("//", "");
		return res;
	}

}