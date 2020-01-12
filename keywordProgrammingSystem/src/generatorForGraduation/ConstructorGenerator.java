package generatorForGraduation;

import java.util.Set;
import java.util.Vector;

import astNode.ConstructorExpression;
import astNode.ConstructorType;
import astNode.Expression;
import dataExtractedFromSource.DataFromSource;

public class ConstructorGenerator extends ExpressionGenerator {

	public Vector<Expression> generateExactExpressionsSub(int depth, String type) {
		Vector<Expression> res = new Vector<Expression>();
		if(depth == 1) {
			if(DataFromSource.constructors.containsKey(type)) {
				Set<ConstructorType>constructorTypes = DataFromSource.constructors.get(type);
				for(ConstructorType constructorType : constructorTypes) {
					if(constructorType.getParameterNumber()==0) {
						res.add(new ConstructorExpression(constructorType.getReturnType()));
					}
				}
			}
		}
		
		if(depth == 2) {
			if(DataFromSource.constructors.containsKey(type)) {
				Set<ConstructorType>constructorTypes = DataFromSource.constructors.get(type);
				for(ConstructorType constructorType : constructorTypes) {
					int parameterNumber = constructorType.getParameterNumber();
					
					if(parameterNumber>0&& parameterNumber<4) {
						Expression[] subExps = new Expression[parameterNumber];
						Vector<Expression> constructorsInDepthTwo = new Vector<Expression>();
						generateConstructors(parameterNumber,constructorType, subExps, constructorsInDepthTwo);
						res.addAll(constructorsInDepthTwo);
					}
				}
					
			}
		}
		
		
//		else {
//			if(DataFromSource.constructors.containsKey(type)) {
//				ConstructorType constructorType = DataFromSource.constructors.get(type);
//				int parameterNumber = constructorType.getParameterNumber();
//				if(parameterNumber>0 && parameterNumber <4) {
//					
//				}
//			}
//		}
		
		return res;
	}

	private void generateConstructors(int arity, ConstructorType constructorType, Expression[] subExps,
			Vector<Expression> constructorsInDepthTwo) {
		if(arity == 0) {
			String constType = constructorType.getReturnType();
			generateConstructionWithSubExpressions(constType, subExps,constructorsInDepthTwo);
		}else {
			String parameterType = constructorType.getParameterTypeOf(arity-1);
			//TODO plus subtype and supertype
			Vector<Expression> parameterExpressions = ExpressionGenerator.tableExact.getExpression(1, parameterType);
			for(Expression parameterExpression : parameterExpressions) {
				Expression[] subExpTemp = subExps.clone();
				subExpTemp[arity-1] = parameterExpression;
				generateConstructors(arity-1, constructorType, subExpTemp, constructorsInDepthTwo);
			}
		}
		
	}

	private void generateConstructionWithSubExpressions(String constType, Expression[] subExps, Vector<Expression> constructorsInDepthTwo) {
		ConstructorExpression res = new ConstructorExpression(constType, subExps);
		constructorsInDepthTwo.add(res);
	}

}
