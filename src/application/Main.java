package application;

import java.io.IOException;

import gui.AppSettings;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	
	private static Scene mainScene;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/LoginView.fxml"));
			Parent parent = loader.load();
			
			mainScene = new Scene(parent);
			mainScene.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());
			
			primaryStage.setScene(mainScene);
			primaryStage.setTitle("Login");
			primaryStage.setResizable(true);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Scene getMainScene() {
		return mainScene;
	}

	public static void setMainScene(Scene scene) {
		mainScene = scene;
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public static void updateTheme() {
	    mainScene.getStylesheets().clear();
	    if ("Escuro".equals(AppSettings.getTheme())) {
	        mainScene.getStylesheets().add(Main.class.getResource("/application/dark-theme.css").toExternalForm());
	    } else {
	        mainScene.getStylesheets().add(Main.class.getResource("/application/application.css").toExternalForm());
	    }
	}
}