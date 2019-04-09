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

	
	// visit the class-level node and get local variable
	@SuppressWarnings({ "unchecked", "static-access" })
	public boolean visit(TypeDeclaration node) {
		List<StructuralPropertyDescriptor> spds = (List<StructuralPropertyDescriptor>)node.propertyDescriptors(AST.JLS11);
		Map properties = node.properties();
		for(StructuralPropertyDescriptor spd : spds) {
			String spd_id = spd.getId();
			System.out.println(spd_id);
		}
		return false;
	}






}
