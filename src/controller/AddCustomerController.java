package controller;

import database.DBConnection;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Country;
import model.Customer;
import model.FirstLevelDivision;
import model.Users;
import java.util.Optional;

/** This class controls the add customer window.
 This class and associated window are invoked when the 'New' button is pressed for a customer. */
public class AddCustomerController {
    @FXML
    private Label nameLabel;
    @FXML
    private Label addressLabel;
    @FXML
    private Label postalCodeLabel;
    @FXML
    private Label phoneLabel;
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
    @FXML
    private Button addButton;
    @FXML
    private Button cancelButton;
    @FXML
    private ComboBox<Users> userComboBox;

    private FirstLevelDivision defaultDivision;

    private ObservableList<Customer> customers = FXCollections.observableArrayList();
    private ObservableList<FirstLevelDivision> divisions = FXCollections.observableArrayList();
    private ObservableList<Country> countries = FXCollections.observableArrayList();
    private ObservableList<Users> users = FXCollections.observableArrayList();
    private ObservableList<TextField> textFields = FXCollections.observableArrayList();

    /** This method initializes the add customer window.
     This method is invoked when pressing any of the 'New' buttons associated with a table of customers.

     This method handles setting each combo box with the appropriate formatted data from the database.
     If a division is passed into the method, that division is set as the default along with the associated country.

     Lambda: When adding text property change listeners to all of my text fields, I implemented a lambda expression in
     order to shorten the lines of code and create higher efficiency.
     @param customers List of customers from the database
     @param division Division associated with the list of customers. */
    public void initialize(ObservableList<Customer> customers, FirstLevelDivision division){
        this.customers = customers;
        this.defaultDivision = division;
        users.setAll(DBConnection.getInstance().queryUsers());
        userComboBox.setItems(users);
        setUserBox(MainWindowController.getUserLoggedIn());

        addButton.setDisable(true);
        userComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Users> observableValue, Users user1, Users user2) {
                if(checkFields()){
                    addButton.setDisable(false);
                } else {
                    addButton.setDisable(true);
                }
            }});
        countries.addAll(DBConnection.getInstance().queryCountries());
        divisions.addAll(DBConnection.getInstance().queryFLD());
        divisionComboBox.setItems(divisions);
        countryComboBox.setItems(countries);
        setDivisionAndCountryBoxes(division);

        divisionComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<>() {
                @Override
                public void changed(ObservableValue<? extends FirstLevelDivision> observableValue, FirstLevelDivision fld1, FirstLevelDivision fld2) {
                    if(checkFields()){
                        addButton.setDisable(false);
                    } else {
                        addButton.setDisable(true);
                    }
                }});
        countryComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<>() {
                @Override
                public void changed(ObservableValue<? extends Country> observableValue, Country country1, Country country2) {
                    if (country2 != null) {
                        divisionComboBox.setItems(DBConnection.getInstance().queryFLDByCountryID(country2.getId()));
                        divisionComboBox.getSelectionModel().select(0);
                    }
                    if(checkFields()){
                        addButton.setDisable(false);
                    } else {
                        addButton.setDisable(true);
                    }
                }});

        nameLabel.visibleProperty().bind(nameTF.focusedProperty());
        addressLabel.visibleProperty().bind(addressTF.focusedProperty());
        postalCodeLabel.visibleProperty().bind(postalCodeTF.focusedProperty());
        phoneLabel.visibleProperty().bind(phoneTF.focusedProperty());

        textFields.addAll(nameTF, addressTF, postalCodeTF, phoneTF);
        for(TextField TF : textFields){
            TF.textProperty().addListener((observableValue, old, newText) -> {
                if(newText.trim().isEmpty()){
                    addButton.setDisable(true);
                    TF.setStyle("-fx-border-color: red");
                }
                if(!newText.trim().isEmpty()){
                    TF.setStyle("-fx-border-color: none");
                    if(checkFields()){
                        addButton.setDisable(false);
                    } else {
                        addButton.setDisable(true);
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
            addButton.setDisable(false);
            return true;
        } else {
            return false;
        }
    }

    /** This method is called by the initialize method and sets the user combo box to the default user.
     The current user logged in is selected in the combo box. */
    private void setUserBox(Users defaultUser){
        Callback<ListView<Users>, ListCell<Users>> cellFactoryUser = userListView -> new ListCell<Users>() {
            @Override
            protected void updateItem(Users u, boolean b) {
                super.updateItem(u, b);
                if (b) {
                    setText("");
                } else {
                    setText("(ID) " + u.getUserID() + " : " + u.getUserName());
                }
            }};
        Callback<ListView<Users>, ListCell<Users>> cellFactoryUserButton = userListView -> new ListCell<Users>() {
            @Override
            protected void updateItem(Users u, boolean b) {
                super.updateItem(u, b);
                if (b) {
                    setText("");
                } else {
                    setText(u.getUserName());
                }
            }};
        userComboBox.setCellFactory(cellFactoryUser);
        userComboBox.setButtonCell(cellFactoryUserButton.call(null));

        if(defaultUser != null) {
            for (Users user : userComboBox.getItems()) {
                if (user.getUserID() == MainWindowController.getUserLoggedIn().getUserID()) {
                    userComboBox.getSelectionModel().select(user);
                }
            }
        }
        if(!MainWindowController.getUserLoggedIn().getUserName().equals("admin")) {
            userComboBox.setDisable(true);
        }
    }

    /** This method is called by the initialize method and sets the division and country combo boxes if a division is passed into the initialize method.
     The division is selected in the combo box and whichever country is associated with that division is also pre-selected. */
    private void setDivisionAndCountryBoxes(FirstLevelDivision defaultDivision){
        Callback<ListView<FirstLevelDivision>, ListCell<FirstLevelDivision>> cellFactory = divisionListView -> new ListCell<FirstLevelDivision>() {
            @Override
            protected void updateItem(FirstLevelDivision fld, boolean b) {
                super.updateItem(fld, b);
                if (b) {
                    setText("");
                } else {
                    setText(fld.getName());
                }
            }};
        divisionComboBox.setCellFactory(cellFactory);
        divisionComboBox.setButtonCell(cellFactory.call(null));
        if(defaultDivision != null) {
            for (FirstLevelDivision fld : divisionComboBox.getItems()) {
                if (fld.getId() == defaultDivision.getId()) {
                    divisionComboBox.getSelectionModel().select(fld);
                }
            }
        }
        Callback<ListView<Country>, ListCell<Country>> cellFactory2 = countryListView -> new ListCell<Country>() {
            @Override
            protected void updateItem(Country c, boolean b) {
                super.updateItem(c, b);
                if (b) {
                    setText("");
                } else {
                    setText(c.getName());
                }
            }};
        if(defaultDivision != null) {
            for (Country country : countryComboBox.getItems()) {
                if (country.getId() == defaultDivision.getCountry_id()) {
                    countryComboBox.getSelectionModel().select(country);
                }
            }
        }
        countryComboBox.setCellFactory(cellFactory2);
        countryComboBox.setButtonCell(cellFactory2.call(null));
    }

    /** This method adds a new customer into the database when the user presses the 'Add' button.
     The method gets all information set in the text fields and combo boxes and creates a new customer variable to add to the database.
     When the customer is successfully added, the window is closed. */
    @FXML
    private void handleAddCustomer() {
        Customer customerToAdd = new Customer();
        customerToAdd.setCustomerID(null);
        customerToAdd.setName(nameTF.getText().trim());
        customerToAdd.setAddress(addressTF.getText().trim());
        customerToAdd.setPostalCode(postalCodeTF.getText().trim());
        customerToAdd.setPhoneNumber(phoneTF.getText().trim());
        customerToAdd.setDivisionID(divisionComboBox.getSelectionModel().getSelectedItem().getId());
        Users user = userComboBox.getSelectionModel().getSelectedItem();

        DBConnection.getInstance().insertCustomer(customerToAdd, user);
        customers.setAll(DBConnection.getInstance().queryCustomers());

        Stage stage = (Stage) addButton.getScene().getWindow();
        stage.close();
    }

    /** This method closes the window when the user presses the 'Cancel' button.
     Before, closing, the user is prompted to confirm the action. If the user presses 'OK' the window is closed.
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
