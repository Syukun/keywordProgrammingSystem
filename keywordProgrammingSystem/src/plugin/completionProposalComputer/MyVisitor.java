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

	private final String BLOCK = "org.eclipse.jdt.core.dom.Block";
	private final String FS = "org.eclipse.jdt.core.dom.ForStatement";

	private final String EFS = "org.eclipse.jdt.core.dom.EnhancedForStatement";
	private final String CATCHCLAUSE = "org.eclipse.jdt.core.dom.CatchClause";
	

	public MyVisitor(int cursorPos) {
		this.cursorPos = cursorPos;
		this.localVars_tmp = new Stack<LocalVariable>();
	}

	// visit the class-level node and get local variable
	public boolean visit(VariableDeclarationExpression node) {

		boolean isProperParent = false;
		ASTNode nodeParent = node.getParent();

		int startPos_node = node.getStartPosition();
		int length_node = node.getLength();
		int endPos_node = startPos_node + length_node;

		while (!isProperParent) {

			String nodeParentClassName = getNodeName(nodeParent);
			switch (nodeParentClassName) {
			case BLOCK:
				isProperParent = true;

				boolean isLocalVariable = isLocalVariable(endPos_node, nodeParent);

				if (isLocalVariable) {
					addToStackForVariableDeclarationExpression(node);
				}

				break;
			case FS:
				isProperParent = true;
				// TODO check what is statement
				Statement innerStatement = ((ForStatement) nodeParent).getBody();
				isLocalVariable = isLocalVariable(endPos_node, innerStatement);

				if (isLocalVariable) {
					addToStackForVariableDeclarationExpression(node);
				}

				break;

			default:
				nodeParent = nodeParent.getParent();
				break;
			}
		}

		return false;
	}

	private boolean isLocalVariable(int endPos_node, ASTNode node) {
		int startPos_Scope = node.getStartPosition();
		int length_Scope = node.getLength();
		int endPos_Scope = startPos_Scope + length_Scope;
		
		return (endPos_node < cursorPos) && (startPos_Scope < cursorPos) && (cursorPos < endPos_Scope);
	}

	public boolean visit(SingleVariableDeclaration node) {

		boolean isProperParent = false;
		ASTNode nodeParent = node.getParent();

		int startPos_node = node.getStartPosition();
		int length_node = node.getLength();
		int endPos_node = startPos_node + length_node;


		while (!isProperParent) {
			String nodeParentClassName = getNodeName(nodeParent);
			switch (nodeParentClassName) {
			// Block:
			case BLOCK:
				isProperParent = true;

				boolean isLocalVariable = isLocalVariable(endPos_node, nodeParent);

				if (isLocalVariable) {
					addToStackSingleVariableDeclaration(node);
				}
				
				break;
			// Enhanced For Statement ----------- OK
			case EFS:
				isProperParent = true;
				Statement innerStatement= ((EnhancedForStatement)nodeParent).getBody();
				
				isLocalVariable = isLocalVariable(endPos_node, innerStatement);
				if (isLocalVariable) {
					addToStackSingleVariableDeclaration(node);
				}
				
				break;
			case CATCHCLAUSE:
				isProperParent = true;
				innerStatement= ((CatchClause)nodeParent).getBody();
				
				isLocalVariable = isLocalVariable(endPos_node, innerStatement);
				if (isLocalVariable) {
					addToStackSingleVariableDeclaration(node);
				}
				
				break;
				
			// TODO find other stuff 
			default:
				nodeParent = nodeParent.getParent();
				break;
				
			}
		}

		return false;
	}
	
	public boolean visit(VariableDeclarationStatement node) {
		
		boolean isProperParent = false;
		ASTNode nodeParent = node.getParent();

		int startPos_node = node.getStartPosition();
		int length_node = node.getLength();
		int endPos_node = startPos_node + length_node;
		
		while(!isProperParent) {
			String nodeParentClassName = getNodeName(nodeParent);
			switch(nodeParentClassName) {
			case BLOCK:
				isProperParent = true;

				boolean isLocalVariable = isLocalVariable(endPos_node, nodeParent);

				if (isLocalVariable) {
					addToStackVariableDeclarationStatement(node);
				}
				
				break;
			default:
				nodeParent = nodeParent.getParent();
				break;
			}
		}
		
		return false;
	}
	
	private void addToStackForVariableDeclarationExpression(VariableDeclarationExpression node) {
		Type type = node.getType();
		ITypeBinding typeBind= type.resolveBinding();
		@SuppressWarnings("unchecked")
		List<VariableDeclarationFragment> localVariables = node.fragments();
		for (VariableDeclarationFragment localVariable : localVariables) {

			String varName = localVariable.getName().toString();
			LocalVariable lv = new LocalVariable(varName, typeBind);
			localVars_tmp.push(lv);
		}
	}

	private void addToStackVariableDeclarationStatement(VariableDeclarationStatement node) {
		Type type = node.getType();
		ITypeBinding typeBind= type.resolveBinding();
		@SuppressWarnings("unchecked")
		List<VariableDeclarationFragment> localVariables = node.fragments();
		for (VariableDeclarationFragment localVariable : localVariables) {

			String varName = localVariable.getName().toString();
			LocalVariable lv = new LocalVariable(varName, typeBind);
			localVars_tmp.push(lv);
		}
		
	}

	private void addToStackSingleVariableDeclaration(SingleVariableDeclaration node) {

		Type type = node.getType();
		ITypeBinding typeBind= type.resolveBinding();
		String varName = node.getName().toString();
		LocalVariable lv = new LocalVariable(varName, typeBind);
		localVars_tmp.push(lv);
	
	}


	private String getNodeName(ASTNode node) {
		return node.getClass().getName();
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

	public Map<String, ITypeBinding> getLocalVariables() {
		Map<String, ITypeBinding> localVariables = new HashMap<String, ITypeBinding>();

		while (!localVars_tmp.empty()) {
			LocalVariable lv = localVars_tmp.pop();
			String nameOfLv = lv.getName();
			ITypeBinding typeOfLv_bind = lv.getTypeBinding();

			if (!localVariables.containsKey(nameOfLv)) {
				localVariables.put(nameOfLv, typeOfLv_bind);
			}
		}

		return localVariables;
	}

}
