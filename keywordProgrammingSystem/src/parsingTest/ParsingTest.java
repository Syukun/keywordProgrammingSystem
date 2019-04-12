package parsingTest;

import basic.MethodName;
import basic.VariableName;
import dataBase.DataBase;

import java.util.List;

import org.eclipse.jdt.core.dom.*;



public class ParsingTest {

	public static void parsingContext(String context) {
		ASTParser parser = ASTParser.newParser(AST.JLS11);
		parser.setSource(context.toCharArray());
//		Map options = JavaCore.getOptions();
//		JavaCore.setComplianceOptions(JavaCore.VERSION_9, options);
//		parser.setCompilerOptions(options);
		CompilationUnit cu = (CompilationUnit)parser.createAST(null);
		AST ast = cu.getAST();
//		System.out.println(ast);
		cu.accept(new TVisitor());
	}
	
	public static void main(String[] args) {
		String context ="class A{"
				+ "int a;"
				+ "void m(int b){"
				+ "}"
				+ "}";
		parsingContext(context);
	}
}

class TVisitor extends ASTVisitor{
	
	public boolean visit(SingleVariableDeclaration node) {
		System.out.println(node.getName());
		return false;
	}
	
	public boolean visit(VariableDeclarationStatement node) {
		return false;
	}

	public boolean visit(VariableDeclarationExpression node) {
		return false;
	}
	
	public boolean visit(VariableDeclarationFragment node) {
		return false;
	}
}
