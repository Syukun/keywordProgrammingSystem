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
				"		\n" + 
				"	}\n" + 
				"	\n" + 
				"}";
		parsingContext(context);
	}
}

class TVisitor extends ASTVisitor{
	
	@SuppressWarnings("unchecked")
	public boolean visit(TypeDeclaration node) {
		String className = node.getName().toString();
		System.out.println("Class Name is : " + className);
		
		FieldDeclaration[] fields = node.getFields();
		MethodDeclaration[] methods = node.getMethods();
		for(FieldDeclaration field : fields) {
			String fieldType = field.getType().toString();
			if(fieldType == "int") {
				fieldType = "Integer";
			}
			List<VariableDeclarationFragment> vdfs = field.fragments();
			for(VariableDeclarationFragment vdf : vdfs) {
				String vName = vdf.getName().toString();
				System.out.println("varaible name is : " + vName + "  Type is : " + fieldType);
		
			}
		}
		
		for(MethodDeclaration method : methods) {
			String methodName = method.getName().toString();
			System.out.println("Method Name is : " + methodName);
			List<SingleVariableDeclaration> svds = method.parameters();
			int parameterSize = svds.size();
			for(SingleVariableDeclaration svd : svds) {
				System.out.println("Parameter Type is : " + svd.getType().toString()
						+ "  Parameter Name is : " + svd.getName().toString());
			}
			
			String returnType = method.getReturnType2().toString();
			System.out.println("Return Type is : " + returnType);
			
			String receiveType;
			if(method.getReceiverType() == null) {
				receiveType = "null";
			}else {
				receiveType = method.getReceiverType().toString();
			}
			System.out.println("Receive Type is : " + receiveType);
			
			Block body = method.getBody();
			List<Statement> statements = body.statements();
			for(Statement statement : statements) {
				if(statement instanceof VariableDeclarationStatement) {
					String fieldType = ((VariableDeclarationStatement) statement).getType().toString();
					System.out.println("Field Type in method is : " + fieldType);
					List<VariableDeclarationFragment> vdfs = ((VariableDeclarationStatement) statement).fragments();
					for(VariableDeclarationFragment vdf : vdfs) {
						String vName = vdf.getName().toString();
						System.out.println("varaible name is : " + vName + "  Type is : " + fieldType);
				
					}
				}
			}
			System.out.println();
//			System.out.println(method.getStartPosition());
			
		}
		
		return false;
	}

}
