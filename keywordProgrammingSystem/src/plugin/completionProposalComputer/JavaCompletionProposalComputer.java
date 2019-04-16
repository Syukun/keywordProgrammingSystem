package plugin.completionProposalComputer;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
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
import basic.TypeName;
import basic.VariableName;
import basic.Expression;
import basic.FieldName;
import basic.LocalVariable;
import basic.MethodName;
import basic.PackageName;
import dataBase.DataBase;
import dataBase.DataFromSourceFile;
import generator.ExpressionGenerator;

public class JavaCompletionProposalComputer implements IJavaCompletionProposalComputer {

	public static Stack<String> localVariables;

	@Override
	public void sessionStarted() {
		// TODO Auto-generated method stub

	}

// 
	@Override
	public List<ICompletionProposal> computeCompletionProposals(ContentAssistInvocationContext context,
			IProgressMonitor monitor) {

		List<ICompletionProposal> result = new ArrayList<ICompletionProposal>();

		// initialize database
		DataFromSourceFile dataInfos = new DataFromSourceFile();
		
		// get cursor location
		int cursorPos = context.getViewer().getSelectedRange().x;

		ASTParser parser = ASTParser.newParser(AST.JLS11);
		ICompilationUnit cu = ((JavaContentAssistInvocationContext) context).getCompilationUnit();

		parser.setSource(cu);
		IJavaProject javaproject = cu.getJavaProject();

		String projectName = javaproject.getElementName();
		dataInfos.projectName = projectName;

		// package level
		IPackageFragment[] packages;
		try {
			packages = javaproject.getPackageFragments();

			for (IPackageFragment mypackage : packages) {
				PackageName packageName = new PackageName(mypackage.getElementName());
				dataInfos.packages.add(packageName);

				for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
					// might be used when use the modifiers (public, protect ,etc.)
//					String cuName = unit.getElementName();

					for (IType type : unit.getAllTypes()) {
						
						String typeName = type.getElementName();
						TypeName tname = new TypeName(typeName,packageName);
						dataInfos.types.add(tname);
						IMethod[] methods = type.getMethods();
						IField[] fields = type.getFields();

						// method part
						for (IMethod method : methods) {
							String methodName = method.getElementName();
							String returnType = method.getReturnType();
							String[] parameterTypes = method.getParameterTypes();
							MethodName mname = new MethodName(methodName,returnType,parameterTypes,tname);
							dataInfos.methods.add(mname);
						}

						// field part
						for (IField field : fields) {
							// not sure this is right or not??
							String fieldTypeName = field.getTypeSignature();
							String fieldName = field.getElementName();
							FieldName fname = new FieldName(fieldName,fieldTypeName,tname);
							dataInfos.fields.add(fname);
						}

					}
				}

			}
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// get local variables
		CompilationUnit cu_ast = (CompilationUnit) parser.createAST(null);
		Stack<LocalVariable> localVars = new Stack<LocalVariable>();
		MyVisitor mv = new MyVisitor(cursorPos, localVars);
		cu_ast.accept(mv);
		
		// imports local variables from stack to set
		while(!localVars.empty()) {
			LocalVariable  lv = localVars.pop();
			if(!dataInfos.localVariables.contains(lv)) {
				dataInfos.localVariables.add(lv);
			}
		}

//		// get the innerest ASTNode
//		NodeFinder nodeFinder = new NodeFinder(cu, cursorPos, 0);
//		ASTNode innerestNode = nodeFinder.getCoveringNode();
//		ASTNode parsedNode = innerestNode;
//		int startPos = cursorPos;

		// test whether the keyword query have any influence on ast
		String keywords = "add line";

		Vector<Expression> exps = new ExpressionGenerator().generateExpression(10, keywords);
		for (Expression exp : exps) {
			result.add(new MyCompletionProposal(context, exp));
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
		return res;
	}

}