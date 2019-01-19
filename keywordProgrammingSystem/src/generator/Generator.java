package generator;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import basic.Expression;
import basic.Type;

public interface Generator {

	public abstract Set<Type> getAllPossibleReceiveTypes();
	
	public abstract Generator[] getParameterGenerators();

	public abstract Vector<Type> getParameterTypes();

	public abstract void generateWithSubExps(Expression[] subExps, Vector<Expression> result);


}
