package plugin.completionProposalComputer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import astNode.LocalVariable;
/**
 * Return Local Variable according to Cursor Position
 * @author aochishu
 *
 */
public class LocalVariableVisitior extends ASTVisitor {

	private String packageName;
	private final String BLOCK = "org.eclipse.jdt.core.dom.Block";

	private final String EFS = "org.eclipse.jdt.core.dom.EnhancedForStatement";
	private final String CATCHCLAUSE = "org.eclipse.jdt.core.dom.CatchClause";
	private final String MD = "org.eclipse.jdt.core.dom.MethodDeclaration";

	private final String FS = "org.eclipse.jdt.core.dom.ForStatement";
	private final String LAMBDA = "rg.eclipse.jdt.core.dom.LambdaExpression";

	private Set<String> scopeSingleVariableDeclaration;
	private Set<String> scopeVariableDeclarationExpression;
	private Set<String> scopeCursor;

	int cursorPos = -1;
	CompilationUnit cu;

	private String nameOfThis;
	private Stack<LocalVariable> localVariables = new Stack<LocalVariable>();

	ASTNode cursorMinScope;

	public LocalVariableVisitior(int cursorPos, CompilationUnit cu) {
		this.cursorPos = cursorPos;
		this.cu = cu;
		this.initScopeSingleVariableDeclaration();
		this.initScopeVariableDeclarationExpression();
		this.initScopeCursor();

		NodeFinder nodeFinder = new NodeFinder(cu, cursorPos, 0);
		this.cursorMinScope = getCursorMinScope(nodeFinder.getCoveringNode());

	}

	private void initScopeSingleVariableDeclaration() {
		this.scopeSingleVariableDeclaration = new HashSet<String>();
//		scopeSingleVariableDeclaration.add(BLOCK);
		scopeSingleVariableDeclaration.add(CATCHCLAUSE);
		scopeSingleVariableDeclaration.add(EFS);
		scopeSingleVariableDeclaration.add(MD);

	}

	private void initScopeVariableDeclarationExpression() {
		this.scopeVariableDeclarationExpression = new HashSet<String>();
		scopeVariableDeclarationExpression.add(FS);
		scopeVariableDeclarationExpression.add(BLOCK);
		scopeVariableDeclarationExpression.add(LAMBDA);

	}

	private void initScopeCursor() {

		this.scopeCursor = new HashSet<String>();
		scopeCursor.addAll(scopeSingleVariableDeclaration);
		scopeCursor.addAll(scopeVariableDeclarationExpression);
	}

	private ASTNode getCursorMinScope(ASTNode coveringNode) {
		initScopeCursor();
		while (!isProperForCursor(coveringNode)) {
			coveringNode = coveringNode.getParent();
			if (coveringNode == null) {
				return null;
			}
		}
		return coveringNode;
	}

	private boolean isProperForCursor(ASTNode coveringNode) {
		return scopeCursor.contains(getNodeClassName(coveringNode));
	}

	/**
	 * Formal parameter lists and catch clauses
	 * <p>
	 * * Parameter in Method Declaration <br>
	 * * Enhanced ForStatement <br>
	 * * Exception parameter <br>
	 * </p>
	 */
	public boolean visit(SingleVariableDeclaration node) {
		int nodeEndPos = getNodeEndPosition(node);
		if (nodeEndPos <= cursorPos) {
			setLocalVariables(node);
		}
		return false;
	}

	/**
	 * <p>
	 * * ForStatement initializers <br>
	 * </p>
	 */
	public boolean visit(VariableDeclarationExpression node) {
		int nodeEndPos = getNodeEndPosition(node);
		if (nodeEndPos <= cursorPos) {
			setLocalVariables(node);
		}
		return false;
	}

	/**
	 * <p>
	 * <del>* field declarations</del> <br>
	 * * local variable declarations <br>
	 * <del>* ForStatement initializers</del> <br>
	 * * LambdaExpression parameters.<br>
	 * </p>
	 */
	public boolean visit(VariableDeclarationStatement node) {
		int nodeEndPos = getNodeEndPosition(node);
		if (nodeEndPos <= cursorPos) {
			setLocalVariables(node);
		}
		return false;
	}

	private int getNodeEndPosition(ASTNode node) {
		int startPos = node.getStartPosition();
		int length = node.getLength();
		int endPos = startPos + length;
		return endPos;
	}

	private void setLocalVariables(SingleVariableDeclaration node) {
		ASTNode minScopeForNode = getMinScope(node);
		if (belongTo(cursorMinScope, minScopeForNode)) {
			addLocalVariable(node);
		}

	}

	private ASTNode getMinScope(SingleVariableDeclaration node) {

		ASTNode nodeParent = node;
		while (!isProperForSVD(nodeParent)) {
			nodeParent = nodeParent.getParent();
		}
		return nodeParent;
	}

	private void setLocalVariables(VariableDeclarationExpression node) {
		ASTNode minScopeForNode = getMinScope(node);
		if (belongTo(cursorMinScope, minScopeForNode)) {
			addLocalVariable(node);
		}
	}

	private ASTNode getMinScope(VariableDeclarationExpression node) {
		ASTNode nodeParent = node;
		while (!isProperForVDE(nodeParent)) {
			nodeParent = nodeParent.getParent();
		}
		return nodeParent;
	}

	private void setLocalVariables(VariableDeclarationStatement node) {
		ASTNode minScopeForNode = getMinScope(node);
		if (belongTo(cursorMinScope, minScopeForNode)) {
			addLocalVariable(node);
		}
	}

	private ASTNode getMinScope(VariableDeclarationStatement node) {
		ASTNode nodeParent = node;
		while (!isProperForVDS(nodeParent)) {
			nodeParent = nodeParent.getParent();
		}
		return nodeParent;
	}

	/**
	 * decide whether this is a Enhanced For Statement, Catch Clause, or Method
	 * Declaration ASTNode
	 * 
	 * @param nodeParent
	 * @return
	 */
	private boolean isProperForSVD(ASTNode nodeParent) {
		return scopeSingleVariableDeclaration.contains(getNodeClassName(nodeParent));
	}

	private boolean isProperForVDE(ASTNode nodeParent) {
		String className = getNodeClassName(nodeParent);
		boolean res = FS.equals(className);
		return res;
	}

	private boolean isProperForVDS(ASTNode nodeParent) {
		return BLOCK.equals(getNodeClassName(nodeParent));
	}

	private String getNodeClassName(ASTNode node) {
		return node.getClass().getName();
	}

	private boolean belongTo(ASTNode scope1, ASTNode scope2) {
		if (scope1 == null) {
			return false;
		}
		int scopeOneStartPosition = scope1.getStartPosition();
		int scopeOneEndPosition = getNodeEndPosition(scope1);

		int scopeTwoStartPosition = scope2.getStartPosition();
		int scopeTwoEndPosition = getNodeEndPosition(scope2);

		return ((scopeOneStartPosition >= scopeTwoStartPosition) && (scopeOneEndPosition <= scopeTwoEndPosition));
	}

	private void addLocalVariable(SingleVariableDeclaration node) {
		Type type = node.getType();
		String variableName = node.getName().toString();
		LocalVariable lv = new LocalVariable(variableName, type.toString(), this.getNameOfThis());
		this.localVariables.push(lv);
	}

	private void addLocalVariable(VariableDeclarationExpression node) {
		Type type = node.getType();
		@SuppressWarnings("unchecked")
		List<VariableDeclarationFragment> localVariables = node.fragments();
		for (VariableDeclarationFragment localVariable : localVariables) {
			String varName = localVariable.getName().toString();
			LocalVariable lv = new LocalVariable(varName, type.toString(), this.getNameOfThis());
			this.localVariables.push(lv);

		}
	}
	
	private void addLocalVariable(VariableDeclarationStatement node) {
		Type type = node.getType();
		@SuppressWarnings("unchecked")
		List<VariableDeclarationFragment> localVariables = node.fragments();
		for (VariableDeclarationFragment localVariable : localVariables) {
			String varName = localVariable.getName().toString();
			LocalVariable lv = new LocalVariable(varName, type.toString(), this.getNameOfThis());
			this.localVariables.push(lv);

		}
	}

	public String getNameOfThis() {
		return this.nameOfThis;
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

	public Map<String, String> getLocalVariables() {
		Map<String, String> res = new HashMap<String, String>();

		while (!this.localVariables.empty()) {
			LocalVariable lv = this.localVariables.pop();
			String nameOfLv = lv.getVarName();
			String typeOfLv = lv.getTypeName();

			if (!res.containsKey(nameOfLv)) {
				res.put(nameOfLv, typeOfLv);
			}
		}
		return res;
	}
	
	public boolean visit(PackageDeclaration node) {
		this.packageName = node.getName().toString();
		return true;
	}

	public String getNameOfThisPackage() {
		
		return this.packageName;
	}

}
