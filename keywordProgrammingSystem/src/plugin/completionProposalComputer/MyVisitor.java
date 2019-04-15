package plugin.completionProposalComputer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.eclipse.jdt.core.dom.*;

import basic.LocalVariable;

public class MyVisitor extends ASTVisitor {
	int cursorPos = -1;
	Stack<LocalVariable> localVars;

	public MyVisitor(int cursorPos,Stack<LocalVariable> localVars) {
		this.cursorPos = cursorPos;
		this.localVars = localVars;
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
			String typeName = node.getType().toString();
			List<VariableDeclarationFragment> localVariables = node.fragments();
			for (VariableDeclarationFragment localVariable : localVariables) {
				
				String varName = localVariable.getName().toString();
				LocalVariable lv = new LocalVariable(typeName,varName);
				localVars.push(lv);
			}

		}

		return false;
	}

	public boolean visit(SingleVariableDeclaration node) {
		int startPos = node.getStartPosition();
		ASTNode parentBlock = getParentBlock(node);
		if ((startPos < cursorPos) && (isInNode(parentBlock, cursorPos))) {
			String typeName = node.getType().toString();
			String varName = node.getName().toString();
			LocalVariable lv = new LocalVariable(typeName,varName);
			localVars.push(lv);
		}
		
		return false;
	}

	public boolean visit(VariableDeclarationStatement node) {
		int startPos = node.getStartPosition();
		ASTNode parentBlock = getParentBlock(node);
		if ((startPos < cursorPos) && (isInNode(parentBlock, cursorPos))) {
			String typeName = node.getType().toString();
			List<VariableDeclarationFragment> localVariables = node.fragments();
			for (VariableDeclarationFragment localVariable : localVariables) {
				String varName = localVariable.getName().toString();
				LocalVariable lv = new LocalVariable(typeName,varName);
				localVars.push(lv);
			}
		}

		return false;
	}

	private ASTNode getParentBlock(ASTNode node) {
		String TD = "org.eclipse.jdt.core.dom.TypeDeclaration";
		String BLOCK = "org.eclipse.jdt.core.dom.Block";
		while (getNodeName(node) != TD && getNodeName(node)  != BLOCK) {
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

	public boolean visit(CompilationUnit node) {
		// change node from AST to JavaModel
		return false;
	}

}
