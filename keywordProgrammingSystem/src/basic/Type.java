package basic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class Type{
	private String typeName;
	private List<String> subTypes;
	public Type(String typeNames) {
		String[] t = typeNames.split(",");
		this.typeName = t[0];
		subTypes = new ArrayList<String>();
		subTypes.addAll(Arrays.asList(t));
		
	}
	public String toString() {
		return this.typeName;
	}
	
	// need modified later
	public List<String> getSubType(){
		return this.subTypes;
	}
	public static String[] getSubType(String possibleParaType_arity) {
		// TODO Auto-generated method stub
		return possibleParaType_arity.split(",");
	}
	
}
