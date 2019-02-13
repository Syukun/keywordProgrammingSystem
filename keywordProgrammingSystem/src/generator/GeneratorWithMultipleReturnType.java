package generator;

import java.util.Collection;

public interface GeneratorWithMultipleReturnType {

	public Collection<? extends ExpressionGenerator> getAllSubGeneratorWithTypeT(String t);
	
}
