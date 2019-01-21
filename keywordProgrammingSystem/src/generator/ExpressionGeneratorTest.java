package generator;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Vector;

import org.junit.jupiter.api.Test;

import basic.Expression;
import basic.IntegerLiteral;
import basic.MethodInvocation;
import basic.MethodName;
import basic.Type;
import dataBase.DataBase;

class ExpressionGeneratorTest {

	@Test
	void test() {
		Vector<Expression> exps = ExpressionGenerator.generateExpression(2, "1");
		for (Expression exp : exps) {
			System.out.println(exp.toString());
		}
	}

//	@Test
//	void testMethodInvocation() {
//		IntegerLiteral i1 = new IntegerLiteral(1);
//		IntegerLiteral i2 = new IntegerLiteral(2);
//		MethodName mn1 = new MethodName("add", 
//				new Type[] {DataBase.allTypes.get("boolean"),DataBase.allTypes.get("int"),DataBase.allTypes.get("int")});
//		MethodInvocation mi = new MethodInvocation(i1,new Expression[] {i2},mn1);
//		System.out.println(mi.toString());
//	
//	}
}
