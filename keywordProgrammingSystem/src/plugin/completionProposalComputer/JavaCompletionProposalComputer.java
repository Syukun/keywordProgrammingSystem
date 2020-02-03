package plugin.completionProposalComputer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import astNode.LocalVariable;
import astNode.Type;
import dataExtractedFromSource.DataFromSource;
//import generator.AbstractGenerator;
//import generator.ExpressionGenerator;
import generatorForGraduation.ExpressionGenerator;

public class JavaCompletionProposalComputer implements IJavaCompletionProposalComputer {

	public static int count = 0;

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

		String keywords = getKeywords(context);
		int depth = 3;
		new DataFromSource().setInitialData(context, monitor);

		ExpressionGenerator expressionGenerator = new ExpressionGenerator();
		
		Vector<Expression> finalChoicesExpressions = expressionGenerator.getFinalExpressions(depth, keywords);
		int finalResultSize = finalChoicesExpressions.size();
		
		for (int i = 0; i < finalResultSize; i++) {
			Expression finalChoiceExpression = finalChoicesExpressions.get(i);
			MyCompletionProposal mcp = new MyCompletionProposal(context, finalChoiceExpression, i);
			mcp.setKeywords(keywords);
			mcp.setSize(finalResultSize);
			result.add(mcp);
		}
		
		ExpressionGenerator.tableExact.clear();
		ExpressionGenerator.tableUnder.clear();
		
		
		
		

//			==============================================================

//			// test whether the keyword query have any influence on ast
//			String keywords = getKeywords(context);
//			int depth = 3;
//			
//			DataFromSource dfs = new DataFromSource(context, monitor);
//
//			ExpressionGenerator expressionGenerator = new ExpressionGenerator();
//			Vector<Expression> finalChoicesExpressions = expressionGenerator.getFinalExpressions(depth, keywords);
////			Vector<Expression> finalExps = finalChoicesExpressions.stream().distinct()
////					.collect(Collectors.toCollection(Vector::new));
//			int finalResultSize = finalChoicesExpressions.size();
//
//			for (int i = 0; i < finalResultSize; i++) {
//				Expression finalChoiceExpression = finalChoicesExpressions.get(i);
//				MyCompletionProposal mcp = new MyCompletionProposal(context, finalChoiceExpression, i);
//				mcp.setKeywords(keywords);
//				result.add(mcp);
//			}
//			
//			DataFromSource.localVariablesRet.clear();
//			ExpressionGenerator.tableExact.clear();
//			ExpressionGenerator.tableUnder.clear();
//			
//			Set<String> allTypes = DataFromSource.typeDictionary.keySet();
//			for(String type : allTypes) {
//				Vector<Vector<Expression>> expsFromEachDepth = new Vector<Vector<Expression>>();
//				ExpressionGenerator.tableExact.table.put(type, expsFromEachDepth);
//				ExpressionGenerator.tableUnder.table.put(type, expsFromEachDepth);
//			}
//			

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