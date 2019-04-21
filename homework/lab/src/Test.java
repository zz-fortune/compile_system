import java.util.ArrayList;
import java.util.List;

import grammer.GrammerParser;
import lexical.LexicalToken;

public class Test {

	public static void main(String[] args) {
		GrammerParser parser = new GrammerParser();
//		parser.buildAnalysisTable("SS->S\n" + "S->L = R\n" + "S->R\n" + "L->* R\n" + "L->id\n" + "R->L");
//		parser.buildAnalysisTable("SS->S\nS->A\nA->B A\nA->epsilon\nB->a B\nB->b");
//		parser.buildAnalysisTable("EE->E\nE->E + T\nE->T\nT->T * F\nT->F\nF->( E )\nF->id");
		parser.buildAnalysisTable("PP->P\nP->D ; S \n" + "D->T L\n" + "T->int\n" + "L->id\n" + "E->E + T\n"
				+ "E->T\n" + "T->F\n" + "F->id\n" + "F->consti\n" + "S->id = E");
		List<LexicalToken> tokens = new ArrayList<LexicalToken>();
		tokens.add(new LexicalToken("int", "INT", "id"));
		tokens.add(new LexicalToken("i", "ID", "-"));
		tokens.add(new LexicalToken(";", "SEMI", "id"));
		tokens.add(new LexicalToken("i", "ID", "-"));
		tokens.add(new LexicalToken("=", "E", "id"));
		tokens.add(new LexicalToken("i", "ID", "id"));
		tokens.add(new LexicalToken("+", "ADD", "id"));
		tokens.add(new LexicalToken("1", "CONSTI", "id"));
//		tokens.add(new LexicalToken(";", "ID", "id"));
		tokens.add(new LexicalToken("#", "#", "id"));
		parser.parse(tokens);
	}
}
