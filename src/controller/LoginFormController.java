package controller;

import database.DBConnection;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.Users;
import java.io.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

/** This class controls the login form window.
 This class and associated window are invoked when the Main application is started,
 or when the 'Log Out' button is clicked within the application. */
public class LoginFormController {
    @FXML
    private Label usernameLabel;
    @FXML
    private Label passwordLabel;
    @FXML
    private GridPane loginFormGP;
    @FXML
    private Button loginButton;
    @FXML
    private TextField usernameTextField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private Label locationLabel;

    private static Users user;

    /** This method initializes the login form for the business application.
     This method is invoked when the application is first started via the 'Main' class.

     This method handles the label that shows the location of the user. The method also binds the "Log In" button's disable
     property to the textfield inputs.

     Lambda: A lambda expression is used for a key event handler for the enter key. This expression reduced the amount of code here. */
    public void initialize(){
        loginButton.setDisable(true);
        String location = locationLabel.getText().concat(": " + Locale.getDefault().getCountry());
        locationLabel.setText(location);

        loginFormGP.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode() == KeyCode.ENTER){
                handleLoginButton();
            }
        });
        loginButton.disableProperty().bind(Bindings.or(
                usernameTextField.textProperty().isEmpty(),
                passwordTextField.textProperty().isEmpty()));
        usernameLabel.visibleProperty().bind(usernameTextField.focusedProperty());
        passwordLabel.visibleProperty().bind(passwordTextField.focusedProperty());
    }

    /** This method is invoked when the 'Log In' button is pressed or when the user presses the 'Enter' key after inputting login credentials.
     This method checks whether the user has entered a correct username and password combination.
     If the username and password are correct, the main window of the application will open. If not, an error alert
     message will open to notify the user one or more credentials is incorrect. */
    @FXML
    private void handleLoginButton(){
        List<Users> users = DBConnection.getInstance().queryUsers();
        String username = usernameTextField.getText();
        String pw = passwordTextField.getText();
        Boolean bool = false;
        for(Users u : users){
            if(username.equals(u.getUserName()) && pw.equals(u.getPassword())){
                System.out.println("Login successful");
                user = u;
                bool = true;
                try{
                    FXMLLoader loader = new FXMLLoader();
                    Parent root = loader.load(getClass().getResource("/view/MainWindow.fxml"));
                    Stage mainWindow = new Stage();
                    mainWindow.setScene(new Scene(root, 900, 600));
                    mainWindow.isResizable();
                    mainWindow.show();
                } catch(Exception e){
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("");
                    alert.setHeaderText(ResourceBundle.getBundle("/LanguageRB", Locale.getDefault()).getString("alertTitle"));
                    alert.setContentText(ResourceBundle.getBundle("/LanguageRB", Locale.getDefault()).getString("alertContent"));
                    alert.showAndWait();
                    break;
                }
                Stage stage = (Stage) loginButton.getScene().getWindow();
                stage.close();
            }
        }
        if(!bool){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("");
            alert.setHeaderText(ResourceBundle.getBundle("/LanguageRB", Locale.getDefault()).getString("errorLoginTitle"));
            alert.setContentText(ResourceBundle.getBundle("/LanguageRB", Locale.getDefault()).getString("errorLoginContent"));
            alert.showAndWait();
        }

        recordLogIn(bool, user);
    }

    /** This method returns the current user logged in.
     This method is used by the main window controller to get the information of the current user. */
    public static Users getUser(){
        return user;
    }

    private void recordLogIn(boolean success, Users userLoggingIn){
        String result;
        String user;
        String timestamp;
        if(success && (userLoggingIn != null)){
            result = "Successful Login Attempt by '";
            user = "User ID: " + userLoggingIn.getUserID() + ", Username: " + userLoggingIn.getUserName();
            timestamp = "' at " + Timestamp.valueOf(LocalDateTime.now()).toString();
        } else{
            result = "Failed Login Attempt at ";
            user = "";
            timestamp = Timestamp.valueOf(LocalDateTime.now()).toString();
        }

        try( FileWriter fwriter = new FileWriter("src/login_activity.txt", true);
        BufferedWriter bw = new BufferedWriter(fwriter);
        PrintWriter out = new PrintWriter(bw)) {
            out.println(result + user + timestamp);
        } catch(IOException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Unable to save login attempt to file.");
            alert.setContentText("");
            alert.showAndWait();
        }
    }
}
