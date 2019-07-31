package generator_origin;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import basic.Expression;
import basic.Type;

public interface Generator {

//	public abstract Set<String> getAllPossibleReturnTypes();
	
//	public abstract Generator[] getParameterGenerators();
	
	public String getReturnType();
	
	public String getReceiveType();

	public String[] getParameterTypes();

	public abstract void generateWithSubExps(Expression[] subExps, Vector<Expression> result);
	



}
