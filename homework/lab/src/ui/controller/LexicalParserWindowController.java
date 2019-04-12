package ui.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import exception.TextOverlargeException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import lexical.LexicalBuffer;
import lexical.LexicalScanner;
import lexical.LexicalToken;

public class LexicalParserWindowController {

	//	词法分析窗口上的一些控件
	@FXML
	private RadioButton selecttext;

	@FXML
	private RadioButton selectfile;

	@FXML
	private TextField filepath;

	@FXML
	private Button lookup;

	@FXML
	private Label errtips;

	@FXML
	private TextArea inputsourcecode;

	@FXML
	private TextArea showresult;
	
	//	单选框的分组
	private ToggleGroup group;
	
	private CompilerApp mainApp;	//	主类的实例
	private List<LexicalToken> tokens;	//	词法分析结果

	/**
	 * 获得主类的实例
	 * 
	 * @param mainApp 主类的实例
	 */
	public void setMainApp(CompilerApp mainApp) {
		this.mainApp = mainApp;
	}

	/**
	 * 初始化
	 */
	@FXML
	private void initialize() {
		this.group = new ToggleGroup();
		this.selecttext.setToggleGroup(group);
		this.selectfile.setToggleGroup(group);
		this.selecttext.setSelected(true);
	}

	/**
	 * 选择一个输入文件
	 */
	@FXML
	private void chooseFile() {
		File file = this.mainApp.chooseInputFile();
		if (file != null && file.exists()) {
			this.filepath.setText(file.getPath());
			this.errtips.setVisible(false);
		}
	}

	/**
	 * 选择文件输入选项
	 */
	@FXML
	private void chooseFileOption() {
		this.filepath.setVisible(true);
		this.lookup.setVisible(true);
	}

	/**
	 * 选择在界面中输入文本的选项
	 */
	@FXML
	private void chooseTextOption() {
		this.filepath.setVisible(false);
		this.lookup.setVisible(false);
		this.errtips.setVisible(false);
	}

	/**
	 * 开启词法分析，并展示结果
	 */
	@FXML
	private void startParse() {
		LexicalBuffer buffer = null;
		if (this.selecttext.isSelected()) {	//	选择从文本中直接输入
			try {
				buffer = new LexicalBuffer(this.inputsourcecode.getText());
			} catch (FileNotFoundException | TextOverlargeException e) {
				this.errtips.setVisible(true);
				this.errtips.setText("输入的文本过大，请采用文件输入！");
				return;
			}
		} else if (this.selectfile.isSelected()) {	//	选择以文件方式输入
			String filepath = this.filepath.getText();
			File file = new File(filepath);
			if (!file.exists()) {	//	判断文件是否存在
				this.errtips.setVisible(true);
				this.errtips.setText("文件不存在!");
				return;
			} else {	//	将文件部分内容展示在输入框
				showInputFile(file);
				buffer = new LexicalBuffer(file);
			}
		} else {	//	没有选择输入方式
			this.errtips.setVisible(true);
			this.errtips.setText("请选择输入方式！");
			return;
		}
		
		//	实例化扫描器开始进行词法分析
		LexicalScanner scanner = new LexicalScanner(buffer);
		scanner.start();
		tokens = scanner.getTokens();
		StringBuffer stringBuffer = new StringBuffer();
		
		//	展示结果
		for (LexicalToken token : tokens) {
			stringBuffer.append(token.toString() + "\n");
		}
		this.showresult.setText(stringBuffer.toString());
	}

	/**
	 * 保存分析结果
	 */
	@FXML
	private void saveResult() {
		File file = mainApp.chooseResultFile();
		
		//	没有选择结果文件
		if (file == null) {
			this.errtips.setVisible(true);
			this.errtips.setText("保存失败！");
			return;
		}
		
		BufferedWriter writer = null;
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			writer = new BufferedWriter(new FileWriter(file));
			for (LexicalToken token : tokens) {
				writer.write(token.toString() + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	/**
	 * 将输入文件的内容的部分展示在输入框中
	 * 
	 * @param file 进行词法分析的文件
	 */
	private void showInputFile(File file) {
		char[] buf = new char[1024];
		int len;
		BufferedReader reader = null;
		try {
			
			//	读取文件中至多 1024 个字符并展示
			reader = new BufferedReader(new FileReader(file));
			len = reader.read(buf, 0, 1024);
			if (len < 1024) {
				char[] re = new char[len];
				System.arraycopy(buf, 0, re, 0, len);
				this.inputsourcecode.setText(new String(re));
			} else {
				this.inputsourcecode.setText(new String(buf) + "\n...");
			}
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
}
