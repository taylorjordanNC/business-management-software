package controller;

import database.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Contacts;
import java.util.Optional;

/** This class controls the add contact window.
 This class and associated window are invoked when the 'New' button is pressed for a contact. */
public class AddContactController {
    @FXML
    private Label nameLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Button addButton;
    @FXML
    private Button cancelButton;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField emailTextField;

    private ObservableList<Contacts> contacts = FXCollections.observableArrayList();

    /** This method initializes the add contact window.
     This method ensures the add button will remain disabled until all information is filled in. The method also
     sets the list of contacts for the form to use when adding a new contact.
     Lambda: I implemented lambda expressions within both of the text property change listening methods. This allowed for fewer
     lines of code.
     @param contacts List of contacts within the database */
    public void initialize(ObservableList<Contacts> contacts){
        this.contacts = contacts;
        nameLabel.visibleProperty().bind(nameTextField.focusedProperty());
        emailLabel.visibleProperty().bind(emailTextField.focusedProperty());

        addButton.setDisable(true);
        nameTextField.textProperty().addListener((observableValue, old, newText) -> {
            if (newText.trim().isEmpty()) {
                addButton.setDisable(true);
                nameTextField.setStyle("-fx-border-color: red");
            }
            if (!newText.trim().isEmpty()) {
                nameTextField.setStyle("-fx-border-color: none");
                if (!emailTextField.getText().trim().isEmpty()) {
                    addButton.setDisable(false);
                } else {
                    addButton.setDisable(true);
                }
            }
        });
        emailTextField.textProperty().addListener((observableValue, old, newText) -> {
            if (newText.trim().isEmpty()) {
                addButton.setDisable(true);
                emailTextField.setStyle("-fx-border-color: red");
            }
            if (!newText.trim().isEmpty()) {
                emailTextField.setStyle("-fx-border-color: none");
                if (!nameTextField.getText().trim().isEmpty()) {
                    addButton.setDisable(false);
                } else {
                    addButton.setDisable(true);
                }
            }
        });
    }

    /** This method is invoked when the user presses the 'Add' button on the form and handles adding a contact to the database.
     Once the new contact is successfully added to the database, the method closes the form. */
    @FXML
    private void handleAddContact(){
        Contacts contactToAdd = new Contacts();
        contactToAdd.setContactID(null);
        contactToAdd.setContactName(nameTextField.getText().trim());
        contactToAdd.setEmail(emailTextField.getText().trim());

        DBConnection.getInstance().insertContact(contactToAdd);
        contacts.setAll(DBConnection.getInstance().queryContacts());

        Stage stage = (Stage) addButton.getScene().getWindow();
        stage.close();
    }

    /** This method is invoked when the user presses the 'Cancel' button on the form and handles closing down the window.
     Before the window is closed, the user is alerted to confirm if they want to close the window or not. */
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
