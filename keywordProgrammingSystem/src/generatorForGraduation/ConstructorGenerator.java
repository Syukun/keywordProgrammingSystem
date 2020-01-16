package generatorForGraduation;

import java.util.Set;
import java.util.Vector;

import astNode.ConstructorExpression;
import astNode.ConstructorType;
import astNode.Expression;
import dataExtractedFromSource.DataFromSource;

public class ConstructorGenerator extends ExpressionGenerator {

	public Vector<Expression> generateExactExpressionsSub(int depth, String type, String keywords) {
		Vector<Expression> res = new Vector<Expression>();
		if (depth == 1) {
			if (DataFromSource.constructors.containsKey(type)) {
				Set<ConstructorType> constructorTypes = DataFromSource.constructors.get(type);
				for (ConstructorType constructorType : constructorTypes) {
					if (constructorType.getParameterNumber() == 0) {
						res.add(new ConstructorExpression(constructorType.getReturnType()));
					}
				}
			}
		}

		if (depth == 2) {
			if (DataFromSource.constructors.containsKey(type)) {
				Set<ConstructorType> constructorTypes = DataFromSource.constructors.get(type);
				for (ConstructorType constructorType : constructorTypes) {
					int parameterNumber = constructorType.getParameterNumber();

					if (parameterNumber > 0 && parameterNumber < 4) {
						Expression[] subExps = new Expression[parameterNumber];
						Vector<Expression> constructorsInDepthTwo = new Vector<Expression>();
						generateConstructors(parameterNumber, constructorType, subExps, constructorsInDepthTwo);
						res.addAll(constructorsInDepthTwo);
					}
				}

			}
		}

		if (depth > 2) {
			if (DataFromSource.constructors.containsKey(type)) {
				Set<ConstructorType> constructorTypes = DataFromSource.constructors.get(type);
				for (ConstructorType constructorType : constructorTypes) {
					int parameterNumber = constructorType.getParameterNumber();
					if (parameterNumber > 0 && parameterNumber < 4) {
						Vector<Expression> constructorsMoreThanDepthTwo = new Vector<Expression>();
						for (int exactFlags = 1; exactFlags <= (1 << parameterNumber) - 1; exactFlags++) {
							Expression[] subExps = new Expression[parameterNumber];
							generateConstructorsMoreThanDepthTwo(depth, parameterNumber, exactFlags, constructorType,
									constructorsMoreThanDepthTwo, subExps, keywords);
						}

						res.addAll(constructorsMoreThanDepthTwo);

					}

				}
			}
		}

		return res;
	}

	private void generateConstructorsMoreThanDepthTwo(int depth, int arity, int exactFlags,
			ConstructorType constructorType, Vector<Expression> constructorsMoreThanDepthTwo, Expression[] subExps,
			String keywords) {
		if (arity == 0) {
			String constType = constructorType.getReturnType();
			generateConstructionWithSubExpressionsMoreThanDepthTwo(constType, subExps, constructorsMoreThanDepthTwo);
		} else {
			String parameterType = constructorType.getParameterTypeOf(arity - 1);
			Set<String> parameterTypeIncludeSub = getAllTypesIncludeSub(parameterType);
			Vector<Expression> candidate = new Vector<Expression>();
			
			if (isBitOn(exactFlags, arity-1)) {
				for (String paraT : parameterTypeIncludeSub) {
					Vector<Expression> exactDepthM1Expression = ExpressionGenerator.tableExact.getExpression(depth - 1, paraT);
					candidate.addAll(exactDepthM1Expression);
				}
			} else {
				for (String paraT : parameterTypeIncludeSub) {
					Vector<Expression> underDepthM2Expression = ExpressionGenerator.tableUnder.getExpression(depth - 2, paraT);
					candidate.addAll(underDepthM2Expression);
				}
			}

			for (Expression parameterExpression : candidate) {
				Expression[] subExpTemp = subExps.clone();
				subExpTemp[arity - 1] = parameterExpression;
				generateConstructorsMoreThanDepthTwo(depth, arity - 1, exactFlags, constructorType,
						constructorsMoreThanDepthTwo, subExpTemp, keywords);
			}
		}

	}

	private void generateConstructors(int arity, ConstructorType constructorType, Expression[] subExps,
			Vector<Expression> constructorsInDepthTwo) {
		if (arity == 0) {
			String constType = constructorType.getReturnType();
			generateConstructionWithSubExpressions(constType, subExps, constructorsInDepthTwo);
		} else {
			String parameterType = constructorType.getParameterTypeOf(arity - 1);
			Set<String> parameterTypeIncludeSub = getAllTypesIncludeSub(parameterType);
			Vector<Expression> parameterExpressions = new Vector<Expression>();
			for(String paraT: parameterTypeIncludeSub) {
				Vector<Expression> parameterExpressionsAtOne = ExpressionGenerator.tableExact.getExpression(1, paraT);
				parameterExpressions.addAll(parameterExpressionsAtOne);
			}
			// TODO plus subtype and supertype
			try {
				
				
			
			
			
			for (Expression parameterExpression : parameterExpressions) {
				Expression[] subExpTemp = subExps.clone();
				subExpTemp[arity - 1] = parameterExpression;
				generateConstructors(arity - 1, constructorType, subExpTemp, constructorsInDepthTwo);
			}
			
			
			}catch(NullPointerException e) {
				int i = 0;
			}
		}

	}

	private void generateConstructionWithSubExpressions(String constType, Expression[] subExps,
			Vector<Expression> constructorsInDepthTwo) {
		if (subExps[0] != null) {
			ConstructorExpression res = new ConstructorExpression(constType, subExps);
			constructorsInDepthTwo.add(res);
		}
	}

	private void generateConstructionWithSubExpressionsMoreThanDepthTwo(String constType, Expression[] subExps,
			Vector<Expression> constructorsMoreThanDepthTwo) {

		if (subExps[0] != null) {
			ConstructorExpression res = new ConstructorExpression(constType, subExps);
			constructorsMoreThanDepthTwo.add(res);
		}
	}

}
