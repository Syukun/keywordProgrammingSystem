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
		String context ="package test;\n" + 
				"\n" + 
				"public class Test {\n" + 
				"	\n" + 
				"int[]  i = new int[2];" +
				"	public List<String> getLines(BufferedReader src) throws Exception{\n" + 
				"		List<String> array = new ArrayList<String>();\n" + 
				"		while(src.ready()) {\n" + 
				"			\n" + 
				"		}\n" + 
				"		\n" + 
				"		return array;\n" + 
				"	}\n" + 
				"	\n" + 
				"	public static void main(String[] args) {\n" +
				"   { int i;}\n" +
				"		\n" + 
				"	}\n" +
				"	\n" + 
				"}";
		parsingContext(context);
	}
}

class TVisitor extends ASTVisitor{
	
	@SuppressWarnings("unchecked")
	public boolean visit(AnonymousClassDeclaration node) {
		System.out.println("have");
		return false;
	}

}
