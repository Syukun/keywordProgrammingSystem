package plugin.completionProposalComputer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.eclipse.jdt.core.dom.*;

public class MyVisitor extends ASTVisitor {
	int cursorPos = -1;

	public MyVisitor(int cursorPos) {
		this.cursorPos = cursorPos;
	}

	// this is wrong
	public static List<String> data_tmp = new ArrayList<String>();

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
			if (getParentBlock(parentBlock) != null) {
				// add the type and name of the local variable
				String typeName = node.getType().toString();
				List<VariableDeclarationFragment> localVars = new ArrayList<VariableDeclarationFragment>();
				for(VariableDeclarationFragment localVar : localVars) {
					String varName = localVar.getName().toString();
					System.out.println("Type " + typeName + "  Name : " + varName);
				}
				
			}

		}

		return false;
	}

	public boolean visit(SingleVariableDeclaration node) {
		// maybe could refract this as a method to fit all those AST
		int startPos = node.getStartPosition();
		return false;
	}
	
	public boolean visit(VariableDeclarationFragment node) {
		return false;
	}
	
	public boolean visit(VariableDeclarationStatement node) {
		return false;
	}
	private ASTNode getParentBlock(ASTNode node) {
		String TD = "org.eclipse.jdt.core.dom.TypeDeclaration";
		String BLOCK = "org.eclipse.jdt.core.dom.Block";
		String nodeName = node.getParent().getClass().getName();
		while (nodeName != TD || nodeName != BLOCK) {
			if(node.getParent()!=null) {
				node = node.getParent();	
			}else {
				return null;
			}
		}
		return  node;
	}

	private boolean isInNode(ASTNode node,int cursorPos) {
		int startPos = node.getStartPosition();
		int length = node.getLength();
		int endPos = startPos + length;
		return (cursorPos>=startPos&&cursorPos<=endPos);
	}
	// get all methodName and field by visit the CompilationUnit node
	public boolean visit(CompilationUnit node) {
		// change node from AST to Java Model

		// get all possible Method Element

		// get all possible Field Element

		return false;
	}

}
