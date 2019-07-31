package generator_origin;

import java.util.Collection;

public interface GeneratorWithMultipleReturnType {

	public Collection<? extends ExpressionGenerator> getAllSubGeneratorWithTypeT(String t);
	
}
