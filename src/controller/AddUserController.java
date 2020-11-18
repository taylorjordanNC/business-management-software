package controller;

import database.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Users;
import java.util.Optional;

/** This class controls the add user window.
 This class and associated window are invoked when the user presses the 'Add' button associated with a list of users. */
public class AddUserController {
    @FXML
    private Label usernameLabel;
    @FXML
    private Label passwordLabel;
    @FXML
    private Label passwordLabel2;
    @FXML
    private TextField usernameTF;
    @FXML
    private PasswordField userPasswordTF;
    @FXML
    private PasswordField userPasswordTF2;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private ObservableList<Users> userList = FXCollections.observableArrayList();

    /** This method initializes the add user form upon loading.
     This method creates listeners that ensure the save button is not enabled unless all fields are filled out properly.
     Lambda: When adding change listeners to all of my text fields' text properties, I implemented a lambda expression in
     order to shorten the lines of code and create higher efficiency.
     @param users List of users from the database. */
    public void initialize(ObservableList<Users> users){
        this.userList = users;
        saveButton.setDisable(true);

        usernameTF.textProperty().addListener((observableValue, old, newString) -> {
            if(!newString.trim().isEmpty()){
                usernameTF.setStyle("-fx-border-color: none");
                if(checkFields()){
                    saveButton.setDisable(false);
                }
            } else {
                saveButton.setDisable(true);
            }
            if(newString.trim().isEmpty()){
                usernameTF.setStyle("-fx-border-color: red");
            }
        });
        userPasswordTF.textProperty().addListener((observableValue, old, newString) -> {
            if(!newString.trim().isEmpty()){
                userPasswordTF.setStyle("-fx-border-color: none");
                if(checkFields()){
                    saveButton.setDisable(false);
                }
            } else {
                saveButton.setDisable(true);
            }
            if(newString.trim().isEmpty()){
                userPasswordTF.setStyle("-fx-border-color: red");
            }
        });
        userPasswordTF2.textProperty().addListener((observableValue, old, newString) -> {
            if(!newString.trim().isEmpty()){
                userPasswordTF2.setStyle("-fx-border-color: none");
                if(checkFields()){
                    saveButton.setDisable(false);
                }
            } else {
                saveButton.setDisable(true);
            }
            if(newString.trim().isEmpty()){
                userPasswordTF2.setStyle("-fx-border-color: red");
            }
        });
        usernameLabel.visibleProperty().bind(usernameTF.focusedProperty());
        passwordLabel.visibleProperty().bind(userPasswordTF.focusedProperty());
        passwordLabel2.visibleProperty().bind(userPasswordTF2.focusedProperty());
    }

    /** This method checks that all the text fields are filled out and is called by the initialize method.
     @return boolean */
    private boolean checkFields(){
        if(!usernameTF.getText().trim().isEmpty() && !userPasswordTF.getText().trim().isEmpty() &&
            !userPasswordTF2.getText().trim().isEmpty()){
                return true;
        } else {
            return false;
        }
    }

    /** This method handles saving a new user to the database.
     This method is invoked when the user presses the 'Add' button of the form.
     When the user is successfully added, the window is closed. */
    @FXML
    private void handleSaveUser() {
        Users userToAdd = new Users();
        userToAdd.setUserID(null);
        userToAdd.setUserName(usernameTF.getText().trim());
        if(userPasswordTF.getText().trim().equals(userPasswordTF2.getText().trim())) {
            userToAdd.setPassword(userPasswordTF.getText().trim());

            DBConnection.getInstance().insertUser(userToAdd, MainWindowController.getUserLoggedIn());
            userList.setAll(DBConnection.getInstance().queryUsers());
            Stage stage = (Stage) saveButton.getScene().getWindow();
            stage.close();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Password");
            alert.setContentText("Please make sure both password fields match.");
            alert.showAndWait();
        }
    }

    /** This method closes the current window and is invoked when the user presses the 'Cancel' button on the form.
     Before closing, the user is prompted to confirm their action. If the user presses 'OK', the window is closed.
     If the user presses 'Cancel', the alert is closed and the window remains open. */
    @FXML
    private void handleCancel() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm action");
        alert.setContentText("Changes will not be saved. Are you sure you want to continue?");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK){
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.close();
        } else if(result.isPresent() && result.get() == ButtonType.CANCEL){
            alert.close();
        }
    }
}
