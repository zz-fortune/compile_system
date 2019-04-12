package lexical;

import java.io.FileNotFoundException;
import java.util.List;

import exception.TextOverlargeException;

public class LexicalController {
	public static void main(String[] args) throws FileNotFoundException, TextOverlargeException {
		LexicalBuffer buffer = new LexicalBuffer(
				"/*test note 1*/\n" + "while(x==1){y=2}\n" + "/*test note 2\n" + "test note 2*/\n" + "int m=6;\n"
						+ "float n=7.;\n" + "floa&t q = 8;\n" + "if(n==m&&m<>3 or m<=5){m=m*4}\n");
		LexicalScanner scanner = new LexicalScanner(buffer);
		scanner.start();
		List<LexicalToken> tokens = scanner.getTokens();
		System.out.println(tokens.size());
		for (LexicalToken token : tokens) {
			System.out.println(token.toString());
		}
	}
}
