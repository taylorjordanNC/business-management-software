package controller;

import database.DBConnection;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.*;
import java.util.Optional;

/** This class controls the update customer window.
 When any 'Update' button is clicked that is associated with a customer, this class and associated window are loaded. */
public class UpdateCustomerController {
    @FXML
    private Label nameLabel;
    @FXML
    private Label addressLabel;
    @FXML
    private Label postalCodeLabel;
    @FXML
    private Label phoneLabel;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;
    @FXML
    private TextField customerIDTF;
    @FXML
    private TextField nameTF;
    @FXML
    private TextField addressTF;
    @FXML
    private TextField postalCodeTF;
    @FXML
    private TextField phoneTF;
    @FXML
    private ComboBox<FirstLevelDivision> divisionComboBox;
    @FXML
    private ComboBox<Country> countryComboBox;

    private Customer customer;
    private ObservableList<Customer> customers = FXCollections.observableArrayList();
    private ObservableList<FirstLevelDivision> divisions = FXCollections.observableArrayList();
    private ObservableList<Country> countries = FXCollections.observableArrayList();
    private ObservableList<TextField> textfields = FXCollections.observableArrayList();

    /** This method initializes the form of the dialog window with the appropriate customer information to be updated.
     All form fields and combo boxes are initiated with the customer's data that is passed into this method.

     This method primarily handles the formatting of the combo boxes and calls separate methods to fill in data.

     Lambda: A lambda expression is used when setting the text property listeners for all text fields on the form. This helped
     to shorten the code and reduce repetition.
     @param customers List of customers from the database.
     @param selectedCustomer The selected customer to be updated. */
    public void initialize(ObservableList<Customer> customers, Customer selectedCustomer){
        this.customers = customers;
        this.customer = selectedCustomer;
        saveButton.setDisable(true);

        countries.addAll(DBConnection.getInstance().queryCountries());
        divisions.addAll(DBConnection.getInstance().queryFLD());
        divisionComboBox.setItems(divisions);
        countryComboBox.setItems(countries);
        initializeForm();

        countryComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Country> observableValue, Country country, Country t1) {
                if (t1 != null) {
                    divisionComboBox.setItems(DBConnection.getInstance().queryFLDByCountryID(t1.getId()));
                    divisionComboBox.getSelectionModel().select(0);
                }
                if(checkFields()){
                    saveButton.setDisable(false);
                } else {
                    saveButton.setDisable(true);
                }
            }});
        divisionComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends FirstLevelDivision> observableValue, FirstLevelDivision fld1, FirstLevelDivision fld2) {
                if(checkFields()){
                    saveButton.setDisable(false);
                } else {
                    saveButton.setDisable(true);
                }
            }});
        customerIDTF.setDisable(true);
        nameLabel.visibleProperty().bind(nameTF.focusedProperty());
        addressLabel.visibleProperty().bind(addressTF.focusedProperty());
        postalCodeLabel.visibleProperty().bind(postalCodeTF.focusedProperty());
        phoneLabel.visibleProperty().bind(phoneTF.focusedProperty());

        textfields.addAll(nameTF, addressTF, postalCodeTF, phoneTF);
        for(TextField tf : textfields){
            tf.textProperty().addListener((observableValue, s, t1) -> {
                if (t1.trim().isEmpty()) {
                    saveButton.setDisable(true);
                    tf.setStyle("-fx-border-color: red");
                } else {
                    tf.setStyle("-fx-border-color: none");
                    if(checkFields()) {
                        saveButton.setDisable(false);
                    } else {
                        saveButton.setDisable(true);
                    }
                }});
        }
    }

    /** This method checks all form fields to see if they are appropriately filled in.
     This method is called by the initialize method in order to determine whether or not the Add button should be enabled.
     @return boolean */
    private boolean checkFields(){
        if(!nameTF.getText().trim().isEmpty() &&
                !phoneTF.getText().trim().isEmpty() && !addressTF.getText().trim().isEmpty() &&
                !postalCodeTF.getText().trim().isEmpty() && (divisionComboBox.getValue() != null) &&
                (countryComboBox.getSelectionModel().getSelectedItem() != null)){
            saveButton.setDisable(false);
            return true;
        } else {
            return false;
        }
    }

    /** This method initializes the form data and is called by the initialize method.
     All of the information from the selected customer is filled into the combo boxes and text fields and formatted. */
    private void initializeForm(){
        customerIDTF.setText(String.valueOf(customer.getCustomerID()));
        nameTF.setText(customer.getName());
        addressTF.setText(customer.getAddress());
        postalCodeTF.setText(customer.getPostalCode());
        phoneTF.setText(customer.getPhoneNumber());
        for(FirstLevelDivision fld : divisionComboBox.getItems()){
            if(customer.getDivisionID() == fld.getId()){
                for(Country country : countryComboBox.getItems()){
                    if(country.getId() == fld.getCountry_id()){
                        countryComboBox.getSelectionModel().select(country);
                    }}
            }
        }
        for (FirstLevelDivision div : divisionComboBox.getItems()) {
            if (div.getId() == customer.getDivisionID()) {
                divisionComboBox.getSelectionModel().select(div);
            }
        }

        Callback<ListView<Country>, ListCell<Country>> cellFactoryCountry = countryListView -> new ListCell<Country>() {
            @Override
            protected void updateItem(Country c, boolean b) {
                super.updateItem(c, b);
                if (b) {
                    setText("");
                } else {
                    setText(c.getName());
                }
            }};
        countryComboBox.setCellFactory(cellFactoryCountry);
        countryComboBox.setButtonCell(cellFactoryCountry.call(null));
        Callback<ListView<FirstLevelDivision>, ListCell<FirstLevelDivision>> cellFactoryDivision = divisionListView -> new ListCell<FirstLevelDivision>() {
            @Override
            protected void updateItem(FirstLevelDivision fld, boolean b) {
                super.updateItem(fld, b);
                if (b) {
                    setText("");
                } else {
                    setText(fld.getName());
                }
            }};
        divisionComboBox.setCellFactory(cellFactoryDivision);
        divisionComboBox.setButtonCell(cellFactoryDivision.call(null));
    }

    /** This method handles updating the customer in the database with the information from the form.
     When the 'Save' button is clicked, this method is invoked. All of the information within the form is grabbed from the combo boxes
     and text fields and is put into a new customer variable to pass to the database for update.

     After the customer is saved, the window is closed. */
    @FXML
    private void handleSaveCustomer() {
        Customer updatedCustomer = new Customer();
        updatedCustomer.setCustomerID(Integer.parseInt(customerIDTF.getText().trim()));
        updatedCustomer.setName(nameTF.getText().trim());
        updatedCustomer.setAddress(addressTF.getText().trim());
        updatedCustomer.setPostalCode(postalCodeTF.getText().trim());
        updatedCustomer.setPhoneNumber(phoneTF.getText().trim());
        updatedCustomer.setDivisionID(divisionComboBox.getSelectionModel().getSelectedItem().getId());

        DBConnection.getInstance().updateCustomer(updatedCustomer, MainWindowController.getUserLoggedIn());
        customers.setAll(DBConnection.getInstance().queryCustomers());

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
