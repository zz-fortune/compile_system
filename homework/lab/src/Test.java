import java.io.FileNotFoundException;
import java.util.List;

import exception.TextOverlargeException;
import grammer.GrammerParser;
import lexical.LexicalBuffer;
import lexical.LexicalScanner;
import lexical.LexicalToken;

public class Test {

	public static void main(String[] args) {
		GrammerParser parser = new GrammerParser();
//		parser.buildAnalysisTable("SS->S\n" + "S->L = R\n" + "S->R\n" + "L->* R\n" + "L->id\n" + "R->L");
//		parser.buildAnalysisTable("SS->S\nS->A\nA->B A\nA->epsilon\nB->a B\nB->b");
//		parser.buildAnalysisTable("EE->E\nE->E + T\nE->T\nT->T * F\nT->F\nF->( E )\nF->id");
//		词法处理
			LexicalBuffer buffer = null;
			try {
				buffer = new LexicalBuffer("int i;\n" + 
						"int j;\n" + 
//						"i=4+i;\n" + 
						"j=10;\n" + 
						"if (i<j) then \n" + 
						"while(j<10) do \n" + 
						"j=j+1;");
			} catch (FileNotFoundException | TextOverlargeException e) {
				e.printStackTrace();
			}
			LexicalScanner scanner = new LexicalScanner(buffer);
			scanner.start();
			List<LexicalToken> tokens = scanner.getTokens();
		parser.buildAnalysisTable("PP->P\r\n" + 
				"P->D P\r\n" + 
				"P->S P\r\n" + 
				"D-D D"+
				"P->epsilon\r\n" + 
				"D->T id ;\r\n" + 
				"T->int\r\n" + 
				"T->float\r\n" + 
				"T->bool\r\n" + 
				"S->id = E ;\r\n" + 
				"E->E + EE\r\n" + 
				"E->E - EE\r\n" + 
				"E->EE\r\n" + 
				"EE->EE * EEE\r\n" + 
				"EE->EEE\r\n" + 
				"EEE->( E )\r\n" + 
				"EEE->consti\r\n" + 
				"EEE->constf\r\n" + 
				"EEE->id\r\n" + 
				"S->if ( B ) then M { S } N else M { S }\r\n" + 
				"N->epsilon\r\n" + 
				"S->while M ( B ) do M { S }\r\n" + 
				"B->B or M H\r\n" + 
				"M->epsilon\r\n" + 
				"B->H\r\n" + 
				"H->H and M I\r\n" + 
				"H->I\r\n" + 
				"I->not I\r\n" + 
				"I->( B )\r\n" + 
				"I->EEE RELOP EEE\r\n" + 
				"I->true\r\n" + 
				"I->false\r\n" + 
				"RELOP-><\r\n" + 
				"RELOP-><=\r\n" + 
				"RELOP->>\r\n" + 
				"RELOP->>=\r\n" + 
				"RELOP->==\r\n" + 
				"RELOP->!=");
//		List<LexicalToken> tokens = new ArrayList<LexicalToken>();
//		tokens.add(new LexicalToken("int", "INT", "id"));
//		tokens.add(new LexicalToken("i", "ID", "-"));
//		tokens.add(new LexicalToken(";", "SEMI", "id"));
//		tokens.add(new LexicalToken("i", "ID", "-"));
//		tokens.add(new LexicalToken("=", "E", "id"));
//		tokens.add(new LexicalToken("i", "ID", "id"));
//		tokens.add(new LexicalToken("+", "ADD", "id"));
//		tokens.add(new LexicalToken("1", "CONSTI", "id"));
////		tokens.add(new LexicalToken(";", "ID", "id"));
//		tokens.add(new LexicalToken("#", "#", "id"));
//		parser.parse(tokens);
	}
}
