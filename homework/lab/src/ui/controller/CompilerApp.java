package ui.controller;

import java.io.File;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class CompilerApp extends Application {

	//	词法分析窗口的一些参数
	private final String title = "词法分析";
	private final String lexicalParserWindowResource = "/ui/view/LexicalParserWindow.fxml";
	private final String rootLayoutResource = "/ui/view/RootLayout.fxml";

	private Stage stage;
	private BorderPane rootLayout;

	@Override
	public void start(Stage primaryStage) {
		this.stage = primaryStage;
		this.stage.setTitle(title);
		initRootLayout();
		showLexicalWindow();
	}
	
	/**
	 * 初始化 RootLayout
	 */
	private void initRootLayout() {
		FXMLLoader loader=new FXMLLoader();
		loader.setLocation(CompilerApp.class.getResource(this.rootLayoutResource));
		try {
			this.rootLayout = (BorderPane) loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Scene scene=new Scene(this.rootLayout);
		this.stage.setScene(scene);
		this.stage.setResizable(false);
		this.stage.show();

	}

	/**
	 * 在 RootLayout 上布置词法分析的窗口
	 */
	public void showLexicalWindow() {
		FXMLLoader loader=new FXMLLoader();
		loader.setLocation(CompilerApp.class.getResource(this.lexicalParserWindowResource));
		AnchorPane lexicalParserWindow;
		try {
			
			//	新建Pane，放置在RootLayout上
			lexicalParserWindow = (AnchorPane) loader.load();
			this.rootLayout.setCenter(lexicalParserWindow);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//	获取词法分析窗口的控制器实例
		LexicalParserWindowController controller = loader.getController();
		controller.setMainApp(this);

	}

	/**
	 * 通过文件选择框选择一个文件
	 * 
	 * @return 文件
	 */
	public File chooseInputFile() {
		FileChooser chooser = new FileChooser();
		File file;
		chooser.setTitle("选择文件");
		file = chooser.showOpenDialog(this.stage);
		return file;
	}
	
	/**
	 * 通过文件选择框选择一个文件
	 * 
	 * @return 文件
	 */
	public File chooseResultFile() {
		FileChooser chooser = new FileChooser();
		File file;
		chooser.setTitle("选择文件");
		file = chooser.showSaveDialog(stage);
		return file;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
