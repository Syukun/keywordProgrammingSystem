package generator;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Vector;

import org.junit.jupiter.api.Test;

import basic.Expression;

class ExpressionGeneratorTest {

	@Test
	void test() {
		Vector<Expression> exps = ExpressionGenerator.generateExpression(2, "0");
		for(Expression exp : exps) {
			System.out.println(exp.toString());
		}
	}

}
