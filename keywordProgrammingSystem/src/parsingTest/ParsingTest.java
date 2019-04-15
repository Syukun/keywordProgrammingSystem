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
		String context ="public class Test {\n" + 
				"	String field_a;\n" + 
				"	String field_b1, field_b2;\n" + 
				"\n" + 
				"	void m(int para_m1, int para_m2) {\n" + 
				"		List<Integer> loi = new ArrayList<Integer>();\n" + 
				"		for (int for_i = 0,for_i2 = 1; for_i < 3; for_i++) {\n" + 
				"			try {\n" + 
				"				int try_i = 0;\n" + 
				"			} catch (Exception e) {\n" + 
				"			}\n" + 
				"		}\n" + 
				"\n" + 
				"		for (Integer enhance_for_i : loi) {\n" + 
				"		}\n" + 
				"	}\n" + 
				"}\n";
		parsingContext(context);
	}
}

class TVisitor extends ASTVisitor{
	
	public boolean visit(SingleVariableDeclaration node) {
		System.out.println(node.getType()+"  " + node.getName() + "   SingleVariableDeclaration");
		
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
