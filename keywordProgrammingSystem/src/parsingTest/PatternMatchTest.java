package parsingTest;

public class PatternMatchTest {

	public static void main(String[] args) {
		String s = "asda.sda";
		System.out.println(s.endsWith(".sda"));
		
	}
}

class A{
	public void funA() {
		
	}
}

class B extends A{
	public void funB(){
		funA();
	}
}