package generator;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Vector;

import org.junit.jupiter.api.Test;

import basic.Expression;
import basic.IntegerLiteral;
import basic.MethodInvocation;
import basic.MethodName;
import basic.ScoreDef;
import basic.StringLiteral;
import basic.Type;
import basic.VariableName;
import dataBase.DataBase;

class ExpressionGeneratorTest {

	@Test
	void test() {
		DataBase.initDataBase();
		String keywords = "add line";
		Vector<Expression> exps = new ExpressionGenerator().generateExpression(5, keywords);
		for (Expression exp : exps) {
			System.out.println(exp.toString() + " : " +exp.getScore(keywords));
		}
		
		System.out.println("**************************************");
//		
//		Vector<Expression> exps2 = new ExpressionGenerator().generateExpression(3, keywords);
//		for (Expression exp : exps2) {
//			System.out.println(exp.toString() + " : " +exp.getScore(keywords));
//		}
//		
//		IntegerLiteral i1 = new IntegerLiteral(1);
//		BigDecimal score = i1.getScore(keywords);
//		System.out.println(score.floatValue());		
	
	}

//	@Test
//	void testMethodInvocation() {
//		IntegerLiteral i1 = new IntegerLiteral(1);
//		IntegerLiteral i2 = new IntegerLiteral(2);
//		MethodName mn1 = new MethodName("add","boolean",
//				new String[] {null,"Integer","Integer"});
//		MethodInvocation mi = new MethodInvocation(null,new Expression[] {i1,i2},mn1);
//		System.out.println(mi.toString());
//	
//	}
	
	@Test
	void ScoreFunctionTest() {
		// need more consideration in StringLiteral
//		String keywords = "add line";
		String keywords = "src read line";
//		String sl1 = "\"a\"";
//		StringLiteral sl_1 = new StringLiteral(sl1);
//		float score = sl_1.getScore(keywords).floatValue();
//		keywords = sl1;
		
		VariableName varName_1 = new VariableName("src","BufferedReader");
		VariableName varName_2 = new VariableName("array","String");
//		BigDecimal score1 = varName_1.getScore(keywords);
//		BigDecimal score2 = varName_2.getScore(keywords);
//		System.out.println(score1.floatValue());
//		System.out.println(score2.floatValue());
		
//		MethodName mName_1 = new MethodName("readLine","String",
//				new String[] {"BufferReader"});
//		BigDecimal score3 = mName_1.getScore(keywords);
//		MethodInvocation mi_1 = new MethodInvocation(varName_1,new Expression[] {},mName_1);
//		
//		BigDecimal score4 = mi_1.getScore(keywords);
	}
}
