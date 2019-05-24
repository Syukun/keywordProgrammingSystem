package plugin.completionProposalComputer;

import java.util.List;
import java.util.Stack;


import org.eclipse.jdt.core.dom.*;

import basic.LocalVariable;

public class MyVisitor extends ASTVisitor {
	int cursorPos = -1;
	public Stack<LocalVariable> localVars;
	public String nameOfThis;
	
	
	public MyVisitor(int cursorPos) {
		this.cursorPos = cursorPos;
		this.localVars = new Stack<LocalVariable>();
	}

	// visit the class-level node and get local variable
	public boolean visit(VariableDeclarationExpression node) {

		// get Variable Element if cursor position in its nearest block
		// also field of outtest class do not count as local variable
		int startPos = node.getStartPosition();
//		Stack<String> localVariables = new Stack<String>();
		ASTNode parentBlock = getParentBlock(node);
//		JavaCompletionProposalComputer.localVariables = new Stack<String>();
		if ((startPos < cursorPos) && (isInNode(parentBlock, cursorPos))) {
			// process with field
			// add the type and name of the local variable
			// also should consider the hierarchy!!
			Type type = node.getType();
			List<VariableDeclarationFragment> localVariables = node.fragments();
			for (VariableDeclarationFragment localVariable : localVariables) {
				
				String varName = localVariable.getName().toString();
				LocalVariable lv = new LocalVariable(varName,type);
				localVars.push(lv);
			}

		}

		return false;
	}

	public boolean visit(SingleVariableDeclaration node) {
		int startPos = node.getStartPosition();
		ASTNode parentBlock = getParentBlock(node);
		if ((startPos < cursorPos) && (isInNode(parentBlock, cursorPos))) {
			
			Type type = node.getType();
			String varName = node.getName().toString();
			LocalVariable lv = new LocalVariable(varName,type);
			localVars.push(lv);
		}
		
		return false;
	}

	@SuppressWarnings("unchecked")
	public boolean visit(VariableDeclarationStatement node) {
		int startPos = node.getStartPosition();
		ASTNode parentBlock = getParentBlock(node);
		if ((startPos < cursorPos) && (isInNode(parentBlock, cursorPos))) {
			Type type = node.getType();
			List<VariableDeclarationFragment> localVariables = node.fragments();
			for (VariableDeclarationFragment localVariable : localVariables) {
				String varName = localVariable.getName().toString();
				LocalVariable lv = new LocalVariable(varName,type);
				localVars.push(lv);
			}
		}

		return false;
	}

	private ASTNode getParentBlock(ASTNode node) {
		String TD = "org.eclipse.jdt.core.dom.TypeDeclaration";
		String BLOCK = "org.eclipse.jdt.core.dom.Block";
		String MD = "org.eclipse.jdt.core.dom.MethodDeclaration";
		while (getNodeName(node) != TD && getNodeName(node)  != BLOCK && getNodeName(node)!=MD) {
			if (node.getParent() != null) {
				node = node.getParent();
			} else {
				return null;
			}
		}
		return node;
	}
	
	private String getNodeName(ASTNode node){
		return node.getClass().getName();
	}

	private boolean isInNode(ASTNode node, int cursorPos) {
		if(node == null) return true;
		int startPos = node.getStartPosition();
		int length = node.getLength();
		int endPos = startPos + length;
		return (cursorPos >= startPos && cursorPos <= endPos);
	}
	
	// get the class which the current cursor in.
	//TODO change this to previsit?
	public boolean visit(TypeDeclaration node) {
		int startPos = node.getStartPosition();
		int nodeLength = node.getLength();
		if((startPos < cursorPos) && (cursorPos < startPos + nodeLength)) {
			// TODO get type name of "This"
			nameOfThis = node.getName().toString();
		}
		return true;
	}


}
