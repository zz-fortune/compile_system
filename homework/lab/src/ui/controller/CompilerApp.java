package ui.controller;

import java.io.File;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class CompilerApp extends Application {

	// 	词法分析窗口的一些参数
	private final String title = "简单的语法编译器";
	private final String lexicalParserWindowResource = "/ui/view/LexicalParserWindow.fxml";
	private final String rootLayoutResource = "/ui/view/RootLayout.fxml";
	private final String grammerParserView = "/ui/view/GrammerParserView.fxml";
	private final String semanticParserView="/ui/view/SemanticParserView.fxml";

	private Stage stage;
	private BorderPane rootLayout;

	@Override
	public void start(Stage primaryStage) {
		this.stage = primaryStage;
		this.stage.setTitle(title);
		initRootLayout();
		loadLexicalParserView();
//		loadGrammerParserView();
//		loadSemanticParserView();
	}

	/**
	 * 初始化 RootLayout
	 */
	private void initRootLayout() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(CompilerApp.class.getResource(this.rootLayoutResource));
		try {
			this.rootLayout = (BorderPane) loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		RootViewController controller=loader.getController();
		controller.setApp(this);
		Scene scene = new Scene(this.rootLayout);
		this.stage.setScene(scene);
		this.stage.setResizable(false);
		this.stage.show();

	}

	/**
	 * 在 RootLayout 上布置词法分析的窗口
	 */
	public FXMLLoader setView(String resource) {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(CompilerApp.class.getResource(resource));
		AnchorPane lexicalParserWindow;
		try {

			// 新建Pane，放置在RootLayout上
			lexicalParserWindow = (AnchorPane) loader.load();
			this.rootLayout.setCenter(lexicalParserWindow);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return loader;
	}

	public void loadLexicalParserView() {
		FXMLLoader loader = setView(lexicalParserWindowResource);
		//		获取词法分析窗口的控制器实例
		LexicalParserWindowController controller = loader.getController();
		controller.setMainApp(this);
	}
	
	public void loadGrammerParserView() {
		FXMLLoader loader=setView(grammerParserView);
		GrammerParserViewController controller=loader.getController();
		controller.setApp(this);
	}
	
	public void loadSemanticParserView() {
		FXMLLoader loader=setView(semanticParserView);
		SemanticParserViewController controller=loader.getController();
		controller.setApp(this);
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
	
	public void showDialog(String info, AlertType type) {
		Alert alert=new Alert(type);
		alert.setTitle("NOTE");
		alert.setHeaderText(null);
		alert.setContentText(info);
		alert.showAndWait();

	}

	public static void main(String[] args) {
		launch(args);
	}
}
