package basic;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

import generator.RelateBeamSearch;

public class ScoreDef {
	// score = 0
	public static final BigDecimal ZERO = new BigDecimal(Float.toString(0));
	// default score is -0.05
	public static final BigDecimal DEFSCORE = new BigDecimal(Float.toString(-0.05f));
	// add 1.00 when words in keywords query
	public static final BigDecimal WIK = new BigDecimal(Float.toString(1.00f));
	// subtract 0.01 when words not in keywords query
	public static final BigDecimal WNIK = new BigDecimal(Float.toString(-0.01f));
	// add +0.001 where f is a local variable , member variable or member method
	// of the enclosing class;
	public static final BigDecimal LMVAR = new BigDecimal(Float.toString(0.001f));

	
	public static void checkInKeyword(BigDecimal score,String word,List<String> keywords) {
		if(keywords.contains(word)) {
			score = score.add(WIK);
			keywords.remove(word);
		}else {
			score = score.add(WNIK);
		}
	}
	
	public static void checkInKeyword_LocalVariable(BigDecimal score,String word,List<String> keywords) {
		if(keywords.contains(word)) {
			score = score.add(WIK);
			score = score.add(LMVAR);
			keywords.remove(word);
		}else {
			score = score.add(WNIK);
		}
	}
	
	public static void selectMaxBWExpressions(Vector<Expression> result, String keywords) {
		Collections.sort(result,new Comparator<Expression>() {
			@Override
			public int compare(Expression e1, Expression e2) {
				return e1.getScore(keywords).compareTo(e2.getScore(keywords));
			}
			
		});
		Vector<Expression> temp = new Vector<Expression>();
		int count = 0;
		while(count < RelateBeamSearch.BEAMWIDTH) {
			if(count < result.size()) {
				temp.add(result.get(count));
			}else {
				break;
			}
			count ++;
		}
		result.clear();
		result.addAll(temp);

	}
	
	public static List<String> splitKeyword(String keywords) {
		return Arrays.asList(keywords.toLowerCase().split("[^\\w]")).stream().distinct().collect(Collectors.toList());
//		return new ArrayList<String>(Arrays.asList(keywords.toLowerCase().split("[^\\w]")));
	}
	
}
