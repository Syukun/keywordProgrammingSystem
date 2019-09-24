package astNode;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import astNode.Expression;

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

	public static BigDecimal checkInKeyword(BigDecimal score, String word, List<String> keywords) {
		String[] wordArray = splitName(word);

		for (String wordSingle : wordArray) {
			if (keywords.contains(wordSingle)) {
				score = score.add(WIK);
				keywords.remove(wordSingle);
			} else {
				score = score.add(WNIK);
			}
		}

		return score;
	}

	public static BigDecimal checkInKeyword_LocalVariable(BigDecimal score, String word, List<String> keywords) {
		String[] wordArray = splitName(word);

		for (String wordSingle : wordArray) {
			if (keywords.contains(wordSingle)) {
				score = score.add(WIK);
				score = score.add(LMVAR);
				keywords.remove(wordSingle);
			} else {
				score = score.add(WNIK);
			}
		}
		return score;
	}

	public static void selectMaxBWExpressions(Vector<Expression> result, String keywords) {
		sortExpression(result, keywords);
		Vector<Expression> temp = new Vector<Expression>();
		int count = 0;
		// beam width
		while (count < 3) {
			if (count < result.size()) {
				temp.add(result.get(count));
			} else {
				break;
			}
			count++;
		}
		result.clear();
		result.addAll(temp);

	}

	public static List<String> splitKeyword(String keywords) {
		return Arrays.asList(keywords.toLowerCase().split("[^\\w]")).stream().distinct().collect(Collectors.toList());
//		return new ArrayList<String>(Arrays.asList(keywords.toLowerCase().split("[^\\w]")));
	}

	public static void sortExpression(Vector<Expression> result, String keywords) {
		Collections.sort(result, new Comparator<Expression>() {
			@Override
			public int compare(Expression e1, Expression e2) {
				return e2.getScore(keywords).compareTo(e1.getScore(keywords));
			}

		});

	}

	public static String[] splitName(String name) {
		name = name.replaceAll("_", " ");
		name = name.replaceAll("([a-z0-9]+)([A-Z])", "$1 $2");

		/**
		 * recursive
		 */
		name = splitUpCase(name);

		/**
		 * to lower case
		 */
		name = name.toLowerCase();

		return name.split("[^\\w]");
	}

	private static String splitUpCase(String word) {
		Pattern pattern = Pattern.compile("([A-Z]{1})([A-Z])");
		Matcher matcher = pattern.matcher(word);
		if (matcher.find()) {
			word = word.replaceAll("([A-Z]{1})([A-Z])", "$1 $2");
			return splitUpCase(word);
		} else {
			return word;
		}
	}
}
