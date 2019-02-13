package plugin.completionProposalComputer;

import org.eclipse.jdt.core.dom.ASTVisitor;

import basic.MethodName;
import basic.VariableName;
import dataBase.DataBase;

import java.util.List;

import org.eclipse.jdt.core.dom.*;

public class MyVisitor extends ASTVisitor{
	public boolean visit(TypeDeclaration node) {
		FieldDeclaration[] fields = node.getFields();
		MethodDeclaration[] methods = node.getMethods();
		for(FieldDeclaration field : fields) {
			String fieldType = field.getType().toString();
			List<VariableDeclarationFragment> vdfs = field.fragments();
			for(VariableDeclarationFragment vdf : vdfs) {
				String vName = vdf.getName().toString();
				VariableName vn = new VariableName(vName,fieldType);
				DataBase.allVariableName.add(vn);
			}
		}
		// could modify
		for(MethodDeclaration method : methods) {
			String methodName = method.getName().toString();
			List<SingleVariableDeclaration> svds = method.parameters();
			int parameterSize = svds.size();
			String[] parameterTypes = new String[parameterSize+1];
			for(int i=0 ; i<parameterSize ; i++) {
				parameterTypes[i+1] = svds.get(i).getType().toString();
			}
			String returnType = method.getReturnType2().toString();
			String receiveType = method.getReceiverType().toString();
			parameterTypes[0] = receiveType;
			MethodName mn = new MethodName(methodName,returnType,parameterTypes);
			DataBase.allMethodNames.add(mn);
		}
		
		return false;
	}

}
