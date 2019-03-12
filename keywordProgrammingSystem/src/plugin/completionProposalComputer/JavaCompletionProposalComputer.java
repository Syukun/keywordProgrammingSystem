package plugin.completionProposalComputer;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.*;

import org.eclipse.jdt.core.ICompilationUnit;

import org.eclipse.jdt.core.JavaModelException;
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
		
		ICompilationUnit cu = ((JavaContentAssistInvocationContext)context).getCompilationUnit();				
		CompilationUnit cu_ast = (CompilationUnit)cu;
		
		String keywords = "add line";
		try {
			IType[] types = cu.getAllTypes();
			for(IType t : types) {
				String type = t.getElementName();
				DataBase.allTypes.put(type,new Type(type));
				
				IField[] fields = t.getFields();
				for(IField f : fields) {
					
				}
				
				IMethod[] methods = t.getMethods();
				for(IMethod m : methods) {
					m.getElementName();
				}
			}
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		String currentText = context.getDocument().get();
//		int cursorPosition = context.getViewer().getSelectedRange().x;

//		parseContext(currentText, cursorPosition);

//		TextViewer tv = (TextViewer) context.getViewer();
//		// modify later
//		String keywords = this.getKeywords(context);
//		
//		// get context without keyword query
//		String currentText = context.getDocument().get();
//		Document document = (Document) context.getDocument(); 
//		
//		StringBuffer textWithoutKeyword = new StringBuffer("");
//		int cursorPosition = context.getViewer().getSelectedRange().x;
//		int line;
//		try {
//			line = document.getLineOfOffset(cursorPosition);
//			int firstPosition = document.getLineOffset(line);
//			int lastPosition = document.getLineOffset(line+1);
//			textWithoutKeyword.append(currentText.substring(0, firstPosition));
//			textWithoutKeyword.append(currentText.substring(lastPosition));
//		} catch (BadLocationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//			
//		
//		DataBase.initDataBase();
//		parsingContext(textWithoutKeyword.toString(),cursorPosition);
		Vector<Expression> exps = new ExpressionGenerator().generateExpression(10, keywords);
		for (Expression exp : exps) {
			result.add(new MyCompletionProposal(context, exp));
		}

		return result;
	}

//	private void parseContext(String currentText, int cursorPosition) {
//		ASTParser parser = ASTParser.newParser(AST.JLS11);
//		parser.setSource(currentText.toCharArray());
//		CompilationUnit cu = (CompilationUnit) parser.createAST(null);
//		
//		cu.accept(new ASTVisitor() {
//			
//			
//		}
//		);
//		
//		
//	}

	private void parsingContext(String currentText, int cursorPosition) {
//		System.out.println("Line 71 cursor position is : "+ cursorPosition);

		ASTParser parser = ASTParser.newParser(AST.JLS11);
		parser.setSource(currentText.toCharArray());
		CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		cu.accept(new ASTVisitor() {
			public boolean visit(TypeDeclaration node) {
				// class name and receiver type
				String className = node.getName().toString();
				DataBase.allTypes.put(className, new Type(className));

				FieldDeclaration[] fields = node.getFields();
				MethodDeclaration[] methods = node.getMethods();

				for (FieldDeclaration field : fields) {
					String fieldType = field.getType().toString();
					if (fieldType == "int") {
						fieldType = "Integer";
					}
					DataBase.allTypes.put(fieldType, new Type(fieldType));

					List<VariableDeclarationFragment> vdfs = field.fragments();
					for (VariableDeclarationFragment vdf : vdfs) {
						String vName = vdf.getName().toString();
						DataBase.allVariableName.add(new VariableName(vName, fieldType));
					}
				}
				int methodNumber = methods.length;

				for (int i = 0; i < methodNumber - 1; i++) {
					MethodDeclaration method = methods[i];

					String methodName = method.getName().toString();
					String returnType = method.getReturnType2().toString();
					DataBase.allTypes.put(returnType, new Type(returnType));

					String receiveType;
					if (method.getReceiverType() != null) {
						receiveType = method.getReceiverType().toString();
					} else {
						receiveType = className;
					}
					DataBase.allTypes.put(receiveType, new Type(receiveType));

					List<SingleVariableDeclaration> svds = method.parameters();
					int paraNum = svds.size();

					String[] paraTypes = new String[paraNum + 1];
					paraTypes[0] = receiveType;
					for (int j = 1; j <= paraNum; j++) {
						String parameterType = svds.get(j - 1).getType().toString();
						DataBase.allTypes.put(parameterType, new Type(parameterType));
						paraTypes[j] = parameterType;

					}
					DataBase.allMethodNames.add(new MethodName(methodName, returnType, paraTypes));

					int startPosition = method.getStartPosition();
					int endPosition = methods[i + 1].getStartPosition();
//					System.out.println("Name " + methods[i+1].getName().toString());
//					System.out.println("Num" + i+1 + " Start position is : " + startPosition);
//					System.out.println("Num" + i+1 + " End position is : " + endPosition);
					if (cursorPosition >= startPosition && cursorPosition < endPosition) {
						for (int k = 0; k < paraNum; k++) {
							String parameterType = svds.get(k).getType().toString();
							String parameterName = svds.get(k).getName().toString();
							DataBase.allVariableName.add(new VariableName(parameterName, parameterType));
						}
						Block body = method.getBody();
						// need modify later by change the position of panduan cursor position
						List<Statement> statements = body.statements();
						for (Statement statement : statements) {
							if (statement instanceof VariableDeclarationStatement) {
								String fieldType = ((VariableDeclarationStatement) statement).getType().toString();
								DataBase.allTypes.put(fieldType, new Type(fieldType));

								List<VariableDeclarationFragment> vdfs = ((VariableDeclarationStatement) statement)
										.fragments();
								for (VariableDeclarationFragment vdf : vdfs) {
									String vName = vdf.getName().toString();
									DataBase.allVariableName.add(new VariableName(vName, fieldType));
								}
							}
						}
					}
				}

				if (methodNumber > 0) {
					// could refractor this as a method
					MethodDeclaration method = methods[methodNumber - 1];

					String methodName = method.getName().toString();
					String returnType = method.getReturnType2().toString();
					DataBase.allTypes.put(returnType, new Type(returnType));

					String receiveType;
					if (method.getReceiverType() != null) {
						receiveType = method.getReceiverType().toString();
					} else {
						receiveType = className;
					}
					DataBase.allTypes.put(receiveType, new Type(receiveType));

					List<SingleVariableDeclaration> svds = method.parameters();
					int paraNum = svds.size();

					String[] paraTypes = new String[paraNum + 1];
					paraTypes[0] = receiveType;
					for (int j = 1; j <= paraNum; j++) {
						String parameterType = svds.get(j - 1).getType().toString();
						DataBase.allTypes.put(parameterType, new Type(parameterType));
						paraTypes[j] = parameterType;
					}
					DataBase.allMethodNames.add(new MethodName(methodName, returnType, paraTypes));

					int startPosition = method.getStartPosition();
					if (cursorPosition >= startPosition) {
						for (int k = 0; k < paraNum; k++) {
							String parameterType = svds.get(k).getType().toString();
							String parameterName = svds.get(k).getName().toString();
							DataBase.allVariableName.add(new VariableName(parameterName, parameterType));
						}
						// could refractor later
						Block body = method.getBody();
						// need modify later by change the position of panduan cursor position
						List<Statement> statements = body.statements();
						for (Statement statement : statements) {
							if (statement instanceof VariableDeclarationStatement) {
								String fieldType = ((VariableDeclarationStatement) statement).getType().toString();
								DataBase.allTypes.put(fieldType, new Type(fieldType));

								List<VariableDeclarationFragment> vdfs = ((VariableDeclarationStatement) statement)
										.fragments();
								for (VariableDeclarationFragment vdf : vdfs) {
									String vName = vdf.getName().toString();
									DataBase.allVariableName.add(new VariableName(vName, fieldType));
								}
							}
						}
					}

				}

				return false;
			}
		});

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
