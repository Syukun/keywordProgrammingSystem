package plugin.completionProposalComputer;

import java.util.Vector;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.search.TypeNameMatch;
import org.eclipse.jdt.core.search.TypeNameMatchRequestor;

public class MyTypeNameMatchRequestor extends TypeNameMatchRequestor {
	Vector<IType> types = new Vector<IType>();
	
	
	/**
	 * @date 2019/07/03
	 */
	public MyTypeNameMatchRequestor() {
		
	}


	@Override
	public void acceptTypeNameMatch(TypeNameMatch match) {

		IType type = match.getType();
		this.types.add(type);
	}
	
	public IType getIType() throws NullPointerException{
		return this.types.get(0);
	}
	
	public Vector<IType> getITypes(){
		return this.types;
	}
 
}