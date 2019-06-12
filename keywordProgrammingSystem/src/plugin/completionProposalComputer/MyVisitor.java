package plugin.completionProposalComputer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.eclipse.jdt.core.dom.*;

import basic.LocalVariable;

public class MyVisitor extends ASTVisitor {
	int cursorPos = -1;
	private Stack<LocalVariable> localVars_tmp;
	private String nameOfThis;

	public MyVisitor(int cursorPos) {
		this.cursorPos = cursorPos;
		this.localVars_tmp = new Stack<LocalVariable>();
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
				LocalVariable lv = new LocalVariable(varName, type);
				localVars_tmp.push(lv);
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
			LocalVariable lv = new LocalVariable(varName, type);
			localVars_tmp.push(lv);
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
				LocalVariable lv = new LocalVariable(varName, type);
				localVars_tmp.push(lv);
			}
		}

		return false;
	}

	private ASTNode getParentBlock(ASTNode node) {
		Set<String> nodeNames = new HashSet<String>();
		String TD = "org.eclipse.jdt.core.dom.TypeDeclaration";
		String BLOCK = "org.eclipse.jdt.core.dom.Block";
		String MD = "org.eclipse.jdt.core.dom.MethodDeclaration";
		String IS = "org.eclipse.jdt.core.dom.IfStatement";
		String WS = "org.eclipse.jdt.core.dom.WhileStatement";
		String FS = "org.eclipse.jdt.core.dom.ForStatement";
		// TODO consider do while is correct or not

		nodeNames.add(TD);
		nodeNames.add(BLOCK);
		nodeNames.add(MD);
		nodeNames.add(IS);
		nodeNames.add(WS);
		nodeNames.add(FS);

		while (!nodeNames.contains(getNodeName(node))) {
			if (node.getParent() != null) {
				node = node.getParent();
			} else {
				return null;
			}
		}
		return node;
	}

	private String getNodeName(ASTNode node) {
		return node.getClass().getName();
	}

	private boolean isInNode(ASTNode node, int cursorPos) {
		if (node == null)
			return true;
		int startPos = node.getStartPosition();
		int length = node.getLength();
		int endPos = startPos + length;
		return (cursorPos >= startPos && cursorPos <= endPos);
	}

	// get the class which the current cursor in.
	public boolean visit(TypeDeclaration node) {
		int startPos = node.getStartPosition();
		int nodeLength = node.getLength();
		if ((startPos < cursorPos) && (cursorPos < startPos + nodeLength)) {

			nameOfThis = node.getName().toString();
		}
		return true;
	}

	public String getNameOfThis() {
		return this.nameOfThis;
	}

	public Map<String, Type> getLocalVariables() {
		Map<String, Type> localVariables = new HashMap<String, Type>();

		while (!localVars_tmp.empty()) {
			LocalVariable lv = localVars_tmp.pop();
			String nameOfLv = lv.getName();
			Type typeOfLv = lv.getType();

			if (!localVariables.containsKey(nameOfLv)) {
				localVariables.put(nameOfLv, typeOfLv);
			}
		}

		return localVariables;
	}

}
