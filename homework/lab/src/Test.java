
public class Test {

	public static void main(String[] args) {
		String string="hello";
		char[] test=new char[100];
		System.arraycopy(string.toCharArray(), 0, test, 0, string.length());
		System.out.println(new String(test));
	}
}
