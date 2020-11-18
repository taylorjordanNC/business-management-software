package controller;

import database.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Users;
import java.util.Optional;

/** This class controls the update user window.
 When the 'Update' button is clicked that is associated with a user, or when a user clicks the 'Edit Username/Password' button,
 this class and associated window are loaded. */
public class UpdateUserController {
    @FXML
    private TextField userIDTF;
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
    @FXML
    private Label usernameLabel;
    @FXML
    private Label passwordLabel;
    @FXML
    private Label passwordLabel2;
    @FXML
    private Users user;

    private ObservableList<Users> users = FXCollections.observableArrayList();
    private ObservableList<TextField> textfields = FXCollections.observableArrayList();

    /** This method initializes the form of the dialog window with the appropriate user information to be updated.
     This method primarily handles the formatting and setting of local variables and calls a separate method to handle initiating form fields.

     Lambda: A lambda expression is used when setting the text property listeners for all text fields on the form. This helped
     to shorten the code and reduce repetition.
     @param allUsers List of users from the database.
     @param user The selected user to be updated. */
    public void initialize(Users user, ObservableList<Users> allUsers){
        this.user = user;
        users = allUsers;
        userIDTF.setDisable(true);
        usernameLabel.visibleProperty().bind(usernameTF.focusedProperty());
        passwordLabel.visibleProperty().bind(userPasswordTF.focusedProperty());
        passwordLabel2.visibleProperty().bind(userPasswordTF2.focusedProperty());
        initializeForm();

        saveButton.setDisable(true);
        textfields.addAll(usernameTF, userPasswordTF, userPasswordTF2);
        for(TextField tf : textfields) {
            tf.textProperty().addListener((observableValue, s, t1) -> {
                if (t1.trim().isEmpty()) {
                    saveButton.setDisable(true);
                    tf.setStyle("-fx-border-color: red");
                } else {
                    saveButton.setDisable(false);
                    tf.setStyle("-fx-border-color: none");
                }
            });
        }
    }

    /** This method initializes the form data and is called by the initialize method.
     All of the information from the selected user is filled into the text fields and formatted. */
    private void initializeForm(){
        userIDTF.setText(String.valueOf(user.getUserID()));
        usernameTF.setText(user.getUserName());
        userPasswordTF.setText(user.getPassword());
        userPasswordTF2.setText(user.getPassword());
    }

    /** This method handles updating the user in the database with the information from the form.
     When the 'Save' button is clicked, this method is invoked.
     All of the information within the form is grabbed from the text fields and is put into a new user variable to pass to the database for update.

     After the user is saved, the window is closed. */
    @FXML
    private void handleSaveUser() {
        Users updatedUser = new Users();
        updatedUser.setUserID(Integer.parseInt(userIDTF.getText()));
        updatedUser.setUserName(usernameTF.getText());
        if(userPasswordTF.getText().trim().equals(userPasswordTF2.getText().trim())) {
            updatedUser.setPassword(userPasswordTF.getText().trim());

            DBConnection.getInstance().updateUser(updatedUser, MainWindowController.getUserLoggedIn());
            users.setAll(DBConnection.getInstance().queryUsers());
            Stage stage = (Stage) saveButton.getScene().getWindow();
            stage.close();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Password");
            alert.setContentText("Please make sure both password fields match.");
            alert.showAndWait();
        }
    }

    /** This method closes the window.
     When the user clicks the 'Cancel' button, this method is invoked. Before closing, the user is prompted to confirm their action.
     If the user clicks 'OK', the form is exited and the information is not saved. If the user clicks 'Cancel', the alert closes. */
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
