package ui.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import exception.TextOverlargeException;
import grammer.GrammerParser;
import grammer.LR1Item;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import lexical.LexicalBuffer;
import lexical.LexicalScanner;
import lexical.LexicalToken;

/**
 * 语法分析器窗口的控制器
 * 
 * @author zhang heng
 *
 */
public class GrammerParserViewController {

	@FXML
	private TextArea sourcecode;

	@FXML
	private TextArea grammer;

	@FXML
	private TextArea lexicalresult;

	@FXML
	private TextArea lrtable;

	@FXML
	private TextArea grammerresult;

	@FXML
	private TextField sourceinput;

	@FXML
	private TextField grammerinput;

	private CompilerApp app;

	public void setApp(CompilerApp app) {
		this.app = app;
	}

	/**
	 * 从文件输入源代码文件
	 */
	@FXML
	private void chooseSource() {
		File file = this.app.chooseInputFile();
		if (file != null && file.exists()) {
			this.sourceinput.setText(file.getPath());
		}else {
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
			this.sourcecode.setText(buffer.toString());
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
	}

	/**
	 * 从文件输入语法文件
	 */
	@FXML
	private void chooseGrammer() {
		File file = this.app.chooseInputFile();
		if (file != null && file.exists()) {
			this.grammerinput.setText(file.getPath());
		}else {
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
			this.grammer.setText(buffer.toString());
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
	}

	/**
	 * 开始分析
	 */
	@FXML
	private void start() {
		
		//	处理输入
		this.grammerresult.setText("");
		this.lexicalresult.setText("");
		this.lrtable.setText("");
		String grammer_txt = this.grammer.getText();
		String source_txt = this.sourcecode.getText();
		if (grammer_txt.equals("") || source_txt.equals("")) {
			app.showDialog("输入不能为空", AlertType.ERROR);
		}
		
		//	词法处理
		LexicalBuffer buffer = null;
		try {
			buffer = new LexicalBuffer(source_txt);
		} catch (FileNotFoundException | TextOverlargeException e) {
			e.printStackTrace();
		}
		LexicalScanner scanner = new LexicalScanner(buffer);
		scanner.start();
		List<LexicalToken> tokens = scanner.getTokens();
		
		//	打印词法结果
		StringBuffer stringBuffer = new StringBuffer();
		for (LexicalToken token : tokens) {
			stringBuffer.append(token.toString() + "\n");
		}
		this.lexicalresult.setText(stringBuffer.toString());
		
		//	语法处理
		GrammerParser parser = new GrammerParser();
		parser.buildAnalysisTable(grammer_txt);
		parser.parse(tokens);
		List<String> errors = parser.getErrors();
		
		//	打印语法结果
		stringBuffer.delete(0, stringBuffer.length());
		if (!errors.isEmpty()) {
			stringBuffer.append("分析失败.....\n");
			for (String e : errors) {
				stringBuffer.append(e+"\n");
			}
			this.grammerresult.setText(stringBuffer.toString());
		}else {
			stringBuffer.append("分析成功......\n");
			List<LR1Item> rules=parser.getRules();
			for (int i=0; i<rules.size();i++) {
				if (rules.get(i)!=null) {
					stringBuffer.append(rules.get(i).getGramer()+"\n");
				}
			}
			this.grammerresult.setText(stringBuffer.toString());
		}
		
		//	打印分析表
		List<String> terminal=parser.getTerminals();
		List<String> varible=parser.getVaribles();
		int[][] actionT=parser.getActionTable();
		int[][] gotoT=parser.getGotoTable();
		stringBuffer.delete(0, stringBuffer.length());
		for (int i = 0; i < terminal.size(); i++) {
			stringBuffer.append("\t"+terminal.get(i));
		}
		for (int i = 0; i < varible.size(); i++) {
			stringBuffer.append("\t"+varible.get(i));
		}
		stringBuffer.append("\n");
		for(int i=0;i<parser.numOfState();i++) {
			stringBuffer.append(i);
			for (int j = 0; j < terminal.size(); j++) {
				if (actionT[i][j]==GrammerParser.acc) {
					stringBuffer.append("\tacc");
				}else if (actionT[i][j]<0) {
					stringBuffer.append("\tr"+(-actionT[i][j]-1));
				}else if (actionT[i][j]>0) {
					stringBuffer.append("\ts"+(actionT[i][j]-1));
				}else {
					stringBuffer.append("\terr");
				}
			}
			for (int j = 0; j < varible.size(); j++) {
				if (gotoT[i][j]>0) {
					stringBuffer.append("\ts"+(gotoT[i][j]-1));
				}else {
					stringBuffer.append("\terr");
				}
			}
			stringBuffer.append("\n");
		}
		this.lrtable.setText(stringBuffer.toString());
		// TODO
	}

}
