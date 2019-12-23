package astNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
* @author Archer Shu
* @date 2019/05/22
*/
public abstract class Expression extends Code{
	/**
	 * Expression ==> String
	 */
	public abstract String toString();
	/**
	 * get score of an Expression according to keywords
	 * @param keywords
	 * @return
	 */
	public abstract BigDecimal getScore(List<String> keywords);
	/**
	 * get return type of this expression
	 * @return String
	 */
	public abstract String getReturnType();
	
	/**
	 * TODO is this needed??
	 */
	public BigDecimal getScore(String keywords) {
//		System.out.println(123 + this.toString());
//		return this.getScore(ScoreDef.splitKeyword(keywords));

		return this.getScore(ScoreDef.splitKeyword(keywords));
//				.multiply(this.getProbability());
	}
	
	
	public BigDecimal getProbability() {
		BigDecimal res = null;
		String serverName = "AochinoMacBook-Pro.local";
		int port = 4957;
//		
//		
//		Path currentRelativePath = Paths.get("");
//		String s = currentRelativePath.toAbsolutePath().toString();
//		
//		String python = "/Users/aochishu/anaconda3/bin/python";
//		String program = "/Users/aochishu/eclipse-workspace/pythonProgram/pythonProgram/PredictProbability.py";
		Socket client;
		try {
			client = new Socket(serverName, port);
			InputStream in = client.getInputStream();
			OutputStream out = client.getOutputStream();
			BufferedReader inRead = new BufferedReader(new InputStreamReader(in));
			out.write(this.toString().getBytes());
			String line = null;
			if ((line = inRead.readLine()) != null) {
				res = new BigDecimal(line);
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ArithmeticException e) {
			return new BigDecimal(0);
		}
		
		return res;
		
	}
	
}
