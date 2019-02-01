package plugin.completionProposalComputer;

import org.eclipse.jdt.core.dom.ASTVisitor;

import java.util.List;

import org.eclipse.jdt.core.dom.*;

public class MyVisitor extends ASTVisitor{
	public boolean visit(VariableDeclarationExpression node) {
		for(VariableDeclarationFragment vdf :  (List<VariableDeclarationFragment>)node.fragments()) {
			System.out.println(vdf.getName().toString());
		}
		return false;
	}

}
