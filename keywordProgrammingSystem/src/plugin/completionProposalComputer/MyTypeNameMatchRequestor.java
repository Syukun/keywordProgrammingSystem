package plugin.completionProposalComputer;

import java.util.List;

import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.search.TypeNameMatch;
import org.eclipse.jdt.core.search.TypeNameMatchRequestor;

public class MyTypeNameMatchRequestor extends TypeNameMatchRequestor {
	IImportDeclaration importDeclaration;
	IPackageDeclaration packageDeclaration;
	List<IType> types;
	
	
	public MyTypeNameMatchRequestor(IPackageDeclaration packageDeclaration, List<IType> types) {
		super();
		this.packageDeclaration = packageDeclaration;
		this.types = types;
	}

	public MyTypeNameMatchRequestor(IImportDeclaration importDeclaration, List<IType> types) {
		super();
		this.importDeclaration = importDeclaration;
		this.types = types;
	}

	@Override
	public void acceptTypeNameMatch(TypeNameMatch match) {
		IType t = match.getType();
		this.types.add(t);
	}

}
