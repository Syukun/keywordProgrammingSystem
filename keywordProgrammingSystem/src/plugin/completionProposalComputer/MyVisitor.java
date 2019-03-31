package plugin.completionProposalComputer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.*;

public class MyVisitor extends ASTVisitor {
	int cursorPos = -1;

	public MyVisitor(int cursorPos) {
		this.cursorPos = cursorPos;
	}

	// this is wrong
	public static List<String> data_tmp = new ArrayList<String>();

	// case one : CompiltionUnit
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
	 * CompilationUnit) CompilationUnit: [ PackageDeclaration ] { ImportDeclaration
	 * } { TypeDeclaration | EnumDeclaration | AnnotationTypeDeclaration | ; }
	 */
	@SuppressWarnings("unchecked")
	public boolean visit(CompilationUnit node) {
		// get package message
		// not completed yet
		PackageDeclaration packageDeclaration = node.getPackage();
		if (packageDeclaration != null) {
			String packageName = packageDeclaration.getName().toString();
			data_tmp.add("Package Name : " + packageName);
		}

		// get import declaration
		// not completed yet
		List<ImportDeclaration> importDeclarations = (List<ImportDeclaration>) node.imports();
		if (importDeclarations != null) {
			for (ImportDeclaration importDeclaration : importDeclarations) {
				String importName = importDeclaration.getName().toString();
				data_tmp.add("Import Package Name : " + importName);
			}
		}

		// get type declaration message
		// consider about the cursor position
		List<AbstractTypeDeclaration> typeDeclarations = (List<AbstractTypeDeclaration>) node.types();
		if (typeDeclarations != null) {
			for (AbstractTypeDeclaration typeDeclaration : typeDeclarations) {
				int startPos = typeDeclaration.getStartPosition();
				int len = typeDeclaration.getLength();

				String typeName = typeDeclaration.getName().toString();
				data_tmp.add("Class Name : " + typeName);
				if (!(cursorPos >= startPos && cursorPos <= startPos + len)) {
					this.visitOtherClass(typeDeclaration);
				}

			}
		}

		return false;
	}

	// maybe could be omitted??
	public boolean visit(PackageDeclaration node) {
		return false;
	}

	// the class which the cursor in
	@SuppressWarnings("unchecked")
	public boolean visit(AbstractTypeDeclaration node) {
		List<BodyDeclaration> bodyDeclarations = (List<BodyDeclaration>) node.bodyDeclarations();
		if (bodyDeclarations != null) {
			for (BodyDeclaration bodyDeclaration : bodyDeclarations) {
				if (bodyDeclaration instanceof AbstractTypeDeclaration) {
					String typeName = ((AbstractTypeDeclaration) bodyDeclaration).getName().toString();
					data_tmp.add("Class in inside class : " + typeName);
					this.visit((AbstractTypeDeclaration) bodyDeclaration);
				}

				if (bodyDeclaration instanceof FieldDeclaration) {
					String typeName = ((FieldDeclaration) bodyDeclaration).getType().toString();
					List<VariableDeclarationFragment> variableDeclarationFragments = ((FieldDeclaration) bodyDeclaration)
							.fragments();
					for (VariableDeclarationFragment variableDeclarationFragment : variableDeclarationFragments) {
						data_tmp.add("Member Field Name : \n\tType Name is : " + typeName + ", name is : "
								+ variableDeclarationFragment.getName().toString());
					}
				}

				if (bodyDeclaration instanceof MethodDeclaration) {

					Type returnType = ((MethodDeclaration) bodyDeclaration).getReturnType2();
					String returnTypeName = (returnType != null) ? returnType.toString() : "void";

					Type receiveType = ((MethodDeclaration) bodyDeclaration).getReceiverType();
					String receiveTypeName = (receiveType != null) ? receiveType.toString() : "";
					String methodName = ((MethodDeclaration) bodyDeclaration).getName().toString();
					String parameterTypeNames = "";
					for (SingleVariableDeclaration singleVariableDeclaration : (List<SingleVariableDeclaration>) ((MethodDeclaration) bodyDeclaration)
							.parameters()) {
						parameterTypeNames += (singleVariableDeclaration.getType().toString() + " ");
					}

					data_tmp.add("Inner Class Method : " + "Return Type Name : " + returnTypeName + "\n"
							+ "Reveiver Type Name : " + receiveTypeName + "\n" + "Method Name : " + methodName + "\n"
							+ "Parameter Types Names : " + parameterTypeNames);
				}

			}
		}
		return false;
	}

	// the class which the cursor not in
	@SuppressWarnings("unchecked")
	private void visitOtherClass(AbstractTypeDeclaration typeDeclaration) {
		// Need to try the situation class in another class

		// get body declarations
		// right now just get fields and methods
		List<BodyDeclaration> bodyDeclarations = (List<BodyDeclaration>) typeDeclaration.bodyDeclarations();
		if (bodyDeclarations != null) {
			for (BodyDeclaration bodyDeclaration : bodyDeclarations) {
				// need to check whether it is correct??
				if (bodyDeclaration instanceof AbstractTypeDeclaration) {
					String typeName = ((AbstractTypeDeclaration) bodyDeclaration).getName().toString();
					data_tmp.add("Class in outside class : " + typeName);
					this.visitOtherClass((AbstractTypeDeclaration) bodyDeclaration);
				}

				// omit right now
				if (bodyDeclaration instanceof AnnotationTypeMemberDeclaration) {

				}

				if (bodyDeclaration instanceof EnumConstantDeclaration) {

				}

				// did not consider the extra dimension yet
				if (bodyDeclaration instanceof FieldDeclaration) {
					String typeName = ((FieldDeclaration) bodyDeclaration).getType().toString();
					@SuppressWarnings("unchecked")
					List<VariableDeclarationFragment> variableDeclarationFragments = ((FieldDeclaration) bodyDeclaration)
							.fragments();
					for (VariableDeclarationFragment variableDeclarationFragment : variableDeclarationFragments) {
						data_tmp.add("Other Class Field Name : Type Name is : " + typeName + ", name is : "
								+ variableDeclarationFragment.getName().toString());
					}
				}

				if (bodyDeclaration instanceof Initializer) {

				}

				if (bodyDeclaration instanceof MethodDeclaration) {

					Type returnType = ((MethodDeclaration) bodyDeclaration).getReturnType2();
					String returnTypeName = (returnType != null) ? returnType.toString() : "void";

					Type receiveType = ((MethodDeclaration) bodyDeclaration).getReceiverType();
					String receiveTypeName = (receiveType != null) ? receiveType.toString() : "";
					String methodName = ((MethodDeclaration) bodyDeclaration).getName().toString();
					String parameterTypeNames = "";
					for (SingleVariableDeclaration singleVariableDeclaration : (List<SingleVariableDeclaration>) ((MethodDeclaration) bodyDeclaration)
							.parameters()) {
						parameterTypeNames += (singleVariableDeclaration.getType().toString() + " ");
					}

					data_tmp.add("Other Class Method : " + "Return Type Name : " + returnTypeName + "\n"
							+ "Reveiver Type Name : " + receiveTypeName + "\n" + "Method Name : " + methodName + "\n"
							+ "Parameter Types Names : " + parameterTypeNames);
				}

			}
		}

	}

	// case AnonymousClassDeclaration
	public boolean visit(AnonymousClassDeclaration node) {
		return false;
	}

	// case AnnotationTypeDeclaration
	public boolean visit(AnnotationTypeDeclaration node) {
		return false;
	}

	// case EnumConstantDeclaration
	public boolean visit(EnumConstantDeclaration node) {
		return false;
	}

	// case Initializer
	public boolean visit(Initializer node) {
		return false;
	}

	// case Block
	@SuppressWarnings("unchecked")
	public boolean visit(Block node) {
		List<Statement> statements = node.statements();
		if (statements != null) {
			for (Statement statement : statements) {
				this.visitStatement(statement);

			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private void visitStatement(Statement statement) {
		/*
		 * Don't need to consider, BreakStatement, ContinueStatement, EmptyStatement,
		 * 
		 * Block, body in DoStatement, ForStatement, IfStatement,
		 */

		if (statement instanceof AssertStatement) {
			Expression exp = ((AssertStatement) statement).getExpression();
			Expression message = ((AssertStatement) statement).getMessage();
			this.visitExpression(exp);
			this.visitExpression(message);
		}

//		if(statement instanceof BreakStatement) {
//			// this is actually a  funny case, but I do not want to conside it right now
//		}

		if (statement instanceof ConstructorInvocation) {
			List<Expression> expressions = (List<Expression>) ((ConstructorInvocation) statement).arguments();
			for (Expression exp : expressions) {
				this.visitExpression(exp);
			}
		}

		if (statement instanceof DoStatement) {
//			Statement body = ((DoStatement) statement).getBody();
			Expression exp = ((DoStatement) statement).getExpression();
//			this.visitStatement(body);
			this.visitExpression(exp);
		}

		if (statement instanceof ExpressionStatement) {
			Expression exp = ((ExpressionStatement) statement).getExpression();
			this.visitExpression(exp);
		}

		// a: b: c: if(true) { int i;}
		if (statement instanceof LabeledStatement) {
			// did not try yet
		}

		// return int i=2;
		if (statement instanceof ReturnStatement) {
			// did not try yet+

		}

		if (statement instanceof SuperConstructorInvocation) {
			// did not try
		}

		if (statement instanceof SwitchCase) {

		}

		if (statement instanceof SwitchStatement) {

		}

		if (statement instanceof SynchronizedStatement) {

		}

		if (statement instanceof ThrowStatement) {

		}

		if (statement instanceof TryStatement) {

		}

		if (statement instanceof TypeDeclarationStatement) {

		}

		if (statement instanceof VariableDeclarationStatement) {
			String typeName = ((VariableDeclarationStatement) statement).getType().toString();
			List<VariableDeclarationFragment> localVariables = (List<VariableDeclarationFragment>)((VariableDeclarationStatement) statement).fragments();
			for(VariableDeclarationFragment localVariable : localVariables) {
				data_tmp.add("Local Varible is : (" + typeName + ", " + localVariable.getName().toString() + ")");
				// did not consider the expression part
			}
		}

		if (statement instanceof WhileStatement) {

		}
	}

	private void visitExpression(Expression exp) {
		// TODO Auto-generated method stub

	}

}
