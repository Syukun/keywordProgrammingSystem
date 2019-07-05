package parsingTest;

import org.eclipse.jdt.core.Signature;

/**
* @author Archer Shu
* @date 2019/07/05
*/
public class SignatureTest {
	public static void main(String[] args){
		String str = "Byte";
		
		String sig = Signature.createTypeSignature(str, false);
		String res = Signature.getSignatureSimpleName("B");
		System.out.println(res);
//		System.out.println(sig);
	}
}
