package ui.controller;

import javafx.fxml.FXML;

public class RootViewController {
	
	private CompilerApp app;
	
	public void setApp(CompilerApp app) {
		this.app = app;
	}
	
	@FXML
	private void exit() {
		System.exit(0);
	}
	
	@FXML
	private void loadLexicalParser() {
		app.loadLexicalParserView();
	}
	
	@FXML
	private void loadGrammerParser() {
		app.loadGrammerParserView();
	}
	
	@FXML
	private void loadSemanticsParser() {
		app.loadSemanticParserView();
	}
}
