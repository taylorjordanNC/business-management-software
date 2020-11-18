package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import database.DBConnection;
import java.util.Locale;
import java.util.ResourceBundle;

/** This class loads the application, initiates the connection to the database, and sets the stage for the application login screen. */
public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        Locale userLocation = Locale.getDefault();
        Parent root = FXMLLoader.load(getClass().getResource("/view/LoginForm.fxml"), ResourceBundle.getBundle("/LanguageRB", userLocation));
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }

    public static void main(String[] args) {
        DBConnection.getInstance().getConnection();
        launch(args);
        DBConnection.getInstance().closeConnection();
    }
}
