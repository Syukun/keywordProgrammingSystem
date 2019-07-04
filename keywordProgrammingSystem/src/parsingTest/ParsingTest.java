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
		parser.setResolveBindings(true);
//		Map options = JavaCore.getOptions();
//		JavaCore.setComplianceOptions(JavaCore.VERSION_9, options);
//		parser.setCompilerOptions(options);
		CompilationUnit cu = (CompilationUnit)parser.createAST(null);
		AST ast = cu.getAST();
//		System.out.println(ast);
		cu.accept(new TVisitor());
	}
	
	public static void main(String[] args) {
		String context ="public class A{\n" + 
				"    B field_a;\n" + 
				"    \n" + 
				"    public A(A.B constructor_a){\n" + 
				"    }\n" + 
				"    \n" + 
				"    public void fun_a(B b){\n" + 
				"        for(int i = 1; i < 5; i++){\n" + 
				"        }\n" + 
				"    }\n" + 
				"}\n" + 
				"\n" + 
				"class B extends A{\n" + 
				"    String field_b;\n" + 
				"}";
		parsingContext(context);
	}
}

class TVisitor extends ASTVisitor{
	
	public boolean visit(SingleVariableDeclaration node) {
		System.out.println(node.getType()+"  " + node.getName() + "   SingleVariableDeclaration");
		Type nodeType = node.getType();
		String nodeName = nodeType.toString();
		ITypeBinding tb = nodeType.resolveBinding();
		if(tb!=null) {
			
		}
		return false;
	}
	
	public boolean visit(VariableDeclarationStatement node) {
		System.out.println(node.getType() + "  " + print(node.fragments()) + "  VariableDeclarationStatement");
		return false;
	}

	public boolean visit(VariableDeclarationExpression node) {
		System.out.println(node.getType() + "  " + print(node.fragments()) + "  VariableDeclarationExpression");
		return false;
	}
	
	public boolean visit(VariableDeclarationFragment node) {
		return false;
	}
	
	private String print(List<VariableDeclarationFragment> nodes) {
		String res = "";
		for(VariableDeclarationFragment node :nodes) {
			res += (node.getName() + "  ");
		}
		return res;
	}
}

class AA{
	int fieldA;
}

class BB extends AA{
	public void foo() {
		int i = new BB().fieldA;
	}
}