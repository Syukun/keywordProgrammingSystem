package astNode;

import java.math.BigDecimal;
import java.util.List;

/**
* @author Archer Shu
* @date 2019年9月18日
*/
public class ClassInstanceCreation extends Expression {
	String typeName;
	public ClassInstanceCreation(String type) {
		this.typeName = type;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "new " + this.typeName + "()";
	}

	@Override
	public BigDecimal getScore(List<String> keywords) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getReturnType() {
		// TODO Auto-generated method stub
		return this.typeName;
	}

	@Override
	public String toPredictString() {
		// TODO Auto-generated method stub
		return "new  " + this.typeName ;
	}

}
