package plugin.completionProposalComputer;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.search.TypeNameMatch;
import org.eclipse.jdt.core.search.TypeNameMatchRequestor;

public class MyTypeNameMatchRequestor extends TypeNameMatchRequestor {
	IType type;
	
	
	/**
	 * @date 2019/07/03
	 */
	public MyTypeNameMatchRequestor() {
		
	}


	@Override
	public void acceptTypeNameMatch(TypeNameMatch match) {
		// TODO Auto-generated method stub
		this.type = match.getType();
	}
	
	public IType getIType() {
		return this.type;
	}

}
