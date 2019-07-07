package parsingTest;

import org.eclipse.jdt.core.Signature;

/**
* @author Archer Shu
* @date 2019/07/05
*/
public class SignatureTest {
	public static void main(String[] args){
		String str = "String";
		
		String sig = Signature.createTypeSignature(str, false);
		sig = "Z";
		String res = Signature.getSignatureSimpleName(sig);
		String res2 = Signature.getSignatureQualifier(sig);
		System.out.println("Type signature is : " + sig);
		System.out.println("Simple Name is : " + res);
		System.out.println("Qualifier is : " + res2);
	}
}
