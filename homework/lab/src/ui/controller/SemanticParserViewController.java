package ui.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import exception.TextOverlargeException;
import grammer.GrammerParser;
import grammer.LR1Item;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import lexical.LexicalBuffer;
import lexical.LexicalScanner;
import lexical.LexicalToken;
import lexical.SignTableItem;
import semantics.CodeItem;

public class SemanticParserViewController {

	private final String grammerText = "P->S\r\n" + 
			"S->D\r\n" + 
			"D->D D\r\n" + 
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
			"S->S S\r\n" + 
			"S->if N1 ( B ) then N2 { S } else N3 { S }\r\n" + 
			"N1->epsilon\r\n" + 
			"N2->epsilon\r\n" + 
			"N3->epsilon\r\n" + 
			"S->while M1 ( B ) do M2 { S }\r\n" + 
			"M1->epsilon\r\n" + 
			"M2->epsilon\r\n" + 
			"B->B or H\r\n" + 
			"B->H\r\n" + 
			"H->H and I\r\n" + 
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
			"RELOP->!=";

	@FXML
	private TextArea textarea;
	
	@FXML
	private TextArea tips;
	
	@FXML 
	private TableView<CodeItem> semaresult;
	
	@FXML 
	private TableColumn<CodeItem, Integer> address;
	
	@FXML 
	private TableColumn<CodeItem, String> action;
	
	@FXML 
	private TableColumn<CodeItem, String> arg1;
	
	@FXML 
	private TableColumn<CodeItem, String> arg2;
	
	@FXML 
	private TableColumn<CodeItem, String> result;
	
	@FXML
	private TableView<SignTableItem> signtable;
	
	@FXML 
	private TableColumn<SignTableItem, String> name;
	@FXML 
	private TableColumn<SignTableItem, String> category;
	@FXML 
	private TableColumn<SignTableItem, String> type;
	@FXML 
	private TableColumn<SignTableItem, Integer> offset;

	private boolean button1 = true;
	private boolean button2 = false;
	private boolean button3 = false;
	private boolean button4 = false;

	private CompilerApp app;

	private List<LexicalToken> tokens;
	private List<CodeItem> codes;
	private List<String> warning;
	private List<String> error;
	private Map<String, SignTableItem> signT;

	public void setApp(CompilerApp app) {
		this.app = app;
	}
	
	@FXML
	private void initialize() {
		this.address.setCellValueFactory(cellData -> cellData.getValue().offsetProperty().asObject());
		this.action.setCellValueFactory(cellData->cellData.getValue().actionProperty());
		this.arg1.setCellValueFactory(cellData->cellData.getValue().arg1Property());
		this.arg2.setCellValueFactory(cellData->cellData.getValue().arg2Property());
		this.result.setCellValueFactory(cellData->cellData.getValue().resultProperty());
		
		this.name.setCellValueFactory(cellData->cellData.getValue().nameProperty());
		this.category.setCellValueFactory(cellData->cellData.getValue().categoryProperty());
		this.type.setCellValueFactory(cellData->cellData.getValue().typeProperty());
		this.offset.setCellValueFactory(cellData->cellData.getValue().offsetProperty().asObject());
		this.semaresult.setVisible(false);
		this.tips.setVisible(false);
		this.signtable.setVisible(false);
//		this.textarea.setVisible(false);
	}

	@FXML
	private void importCode() {
		this.textarea.setVisible(true);
		this.semaresult.setVisible(false);
		this.tips.setVisible(false);
		this.signtable.setVisible(false);
		if (!button1) {
			return;
		}
		File file = app.chooseInputFile();
		if (file == null || !file.exists()) {
			return;
		}
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = null;
			StringBuffer buffer = new StringBuffer();
			while ((line = reader.readLine()) != null) {
				buffer.append(line + "\n");
			}
			this.textarea.setText(buffer.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		button2 = true;
		button3 = false;
		button4 = false;
	}

	@FXML
	private void lexicalParse() {
		if (!button2) {
			return;
		}
		String sourcecode = this.textarea.getText();
		if (sourcecode.equals("")) {
			return;
		}

		LexicalBuffer buffer;
		try {
			buffer = new LexicalBuffer(sourcecode);
			LexicalScanner scanner = new LexicalScanner(buffer);
			scanner.start();
			this.tokens = scanner.getTokens();
			StringBuffer stringBuffer = new StringBuffer();
			for (LexicalToken token : tokens) {
				stringBuffer.append(token.toString() + "\n");
			}
			this.textarea.setText(stringBuffer.toString());
		} catch (FileNotFoundException | TextOverlargeException e) {
			e.printStackTrace();
		}
		button2 = false;
		button3 = true;
	}

	@FXML
	private void grammerParse() {
		if (!button3) {
			return;
		}
		GrammerParser parser = new GrammerParser();
		parser.buildAnalysisTable(grammerText);
		parser.parse(tokens);
		error = parser.getErrors();
		warning = parser.getWarnings();
		codes = parser.getCodes();
		signT=parser.getSignTable();

		// 打印语法结果
		StringBuffer buffer = new StringBuffer();
		if (parser.getSuccess()) {
			buffer.append("语法分析成功......\n");
			List<LR1Item> rules = parser.getRules();
			for (int i = 0; i < rules.size(); i++) {
				if (rules.get(i) != null) {
					buffer.append(rules.get(i).getGramer() + "\n");
				}
			}
			button3 = false;
			button4 = true;
		} else {
			buffer.append("语法分析失败.....\n");
			String pre = "";
			for (String e : error) {
				if (!pre.equals(e)) {
					buffer.append(e + "\n");
				}
				pre = e;
			}
			button3 = false;
		}
		textarea.setText(buffer.toString());
		button3 = false;
		button4 = true;
	}

	@FXML
	private void semanticParse() {
		if (!button4) {
			return;
		}

		StringBuffer buffer = new StringBuffer();
		if (error.size() == 0) {
			this.textarea.setVisible(false);
			this.semaresult.setVisible(true);
			this.tips.setVisible(true);
			this.signtable.setVisible(true);
			buffer.append("语义分析成功......\n");
			String pre = "";
			for (String w : warning) {
				if (!pre.equals(w)) {
					buffer.append(w + "\n");
				}
				pre = w;
			}
			ObservableList<CodeItem> codeitems = FXCollections.observableArrayList();
			for (CodeItem item : codes) {
				codeitems.add(item);
			}
			
			ObservableList<SignTableItem> signitems = FXCollections.observableArrayList();
			for(SignTableItem item: signT.values()) {
				signitems.add(item);
			}
			this.tips.setText(buffer.toString());
			this.semaresult.setItems(codeitems);
			this.signtable.setItems(signitems);
		} else {
			buffer.append("语义分析失败......\n");
			String pre = "";
			for (String e : error) {
				if (!pre.equals(e)) {
					buffer.append(e + "\n");
				}
				pre = e;
			}
			textarea.setText(buffer.toString());
		}
		button4 = false;
	}

}
