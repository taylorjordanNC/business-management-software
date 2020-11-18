package controller;

import database.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Contacts;
import java.util.Optional;

/** This class controls the update contact window.
 When any 'Update' button is clicked that is associated with a contact, this class and associated window are loaded. */
public class UpdateContactController {
    @FXML
    private Label nameLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Button saveButton;
    @FXML
    private TextField idTextField;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField emailTextField;
    @FXML
    private Button cancelButton;

    private Contacts contact;
    private ObservableList<Contacts> contacts = FXCollections.observableArrayList();

    /** This method initializes the window and sets the local variables to the values of the variables passed to the method.
     Another method is called to initialize the data of the form.

     Lambda: A lambda expression is used when setting the text property listeners for both text fields on the form. This helped
     to shorten the code and reduce repetition of method calls.
     @param contacts List of contacts from the database.
     @param selectedContact The selected contact to be updated. */
    public void initialize(ObservableList<Contacts> contacts, Contacts selectedContact){
        this.contacts = contacts;
        this.contact = selectedContact;
        idTextField.setDisable(true);
        nameLabel.visibleProperty().bind(nameTextField.focusedProperty());
        emailLabel.visibleProperty().bind(emailTextField.focusedProperty());
        initializeForm();

        saveButton.setDisable(true);
        nameTextField.textProperty().addListener((observableValue, s, t1) -> {
            if (t1.trim().isEmpty()) {
                saveButton.setDisable(true);
                nameTextField.setStyle("-fx-border-color: red");
            } else {
                saveButton.setDisable(false);
                nameTextField.setStyle("-fx-border-color: none");
            }
        });
        emailTextField.textProperty().addListener((observableValue, s, t1) -> {
            if (t1.trim().isEmpty()) {
                saveButton.setDisable(true);
                emailTextField.setStyle("-fx-border-color: red");
            } else {
                saveButton.setDisable(false);
                emailTextField.setStyle("-fx-border-color: none");
            }
        });
    }

    /** This method initializes the form of the dialog window with the appropriate contact information to be updated.
     All form fields are initiated with the contact's data that is passed into this method and is invoked by the initialize method. */
    private void initializeForm(){
        idTextField.setText(String.valueOf(contact.getContactID()));
        nameTextField.setText(contact.getContactName());
        emailTextField.setText(contact.getEmail());
    }

    /** This method updates the contact in the database.
     When the 'Save' button is clicked, this method is called and gets the information from the text fields to create a new
     contact variable to be passed into the database as an update.

     After the contact is saved, the window is closed. */
    @FXML
    private void handleSaveUpdate() {
        Contacts updatedContact = new Contacts();
        updatedContact.setContactID(Integer.parseInt(idTextField.getText()));
        updatedContact.setContactName(nameTextField.getText().trim());
        updatedContact.setEmail(emailTextField.getText().trim());

        DBConnection.getInstance().updateContact(updatedContact);
        this.contacts.setAll(DBConnection.getInstance().queryContacts());
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    /** This method closes the window.
     When the user clicks the 'Cancel' button, this method is invoked. Before closing, the user is prompted to confirm their action.
     If the user clicks 'OK', the form is exited and the information is not saved. If the user clicks 'Cancel', the alert closes. */
    @FXML
    private void handleCancel() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Action");
        alert.setContentText("Changes will not be saved. Are you sure you want to continue?");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.CANCEL){
            alert.close();
        } else if(result.isPresent() && result.get() == ButtonType.OK){
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.close();
        }
    }
}
