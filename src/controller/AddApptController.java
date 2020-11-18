package controller;

import database.DBConnection;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/** This class controls the add appointment window.
 This class and associated window are invoked when the 'New' button is pressed for an appointment. */
public class AddApptController {
    @FXML
    private Label startTimeLabel;
    @FXML
    private Label endTimeLabel;
    @FXML
    private Button addAppointmentButton;
    @FXML
    private Button cancelNewApptButton;
    @FXML
    private ComboBox<Users> userComboBox;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private Label titleLabel;
    @FXML
    private Label descLabel;
    @FXML
    private Label locationLabel;
    @FXML
    private TextField appointmentTitle;
    @FXML
    private TextField appointmentDesc;
    @FXML
    private TextField apptLocation;
    @FXML
    private ComboBox<Contacts> contactsComboBox;
    @FXML
    private ComboBox<String> appointmentType;
    @FXML
    private ComboBox<String> timeComboBoxStart;
    @FXML
    private ComboBox<String> timeComboBoxEnd;
    @FXML
    private ComboBox<Customer> customersComboBox;

    private ObservableList<Appointment> appointments = FXCollections.observableArrayList();

    private ObservableList<Users> users = FXCollections.observableArrayList();
    private ObservableList<Customer> customers = FXCollections.observableArrayList();
    private ObservableList<Contacts> contacts = FXCollections.observableArrayList();
    private ObservableList<TextField> textfields = FXCollections.observableArrayList();
    private ObservableList<ComboBox> comboBoxes = FXCollections.observableArrayList();
    private ObservableList<DatePicker> datePickers = FXCollections.observableArrayList();

    private Customer defaultCustomer;
    private Contacts defaultContact;
    private Users defaultUser;

    /** This method initializes the add appointment window.
     This method is invoked when pressing any of the 'New' buttons associated with a table of appointments.

     This method initializes all of the combo boxes and date pickers with the appropriate information needed by the user.
     The default user/customer/contact is also selected when appropriate depending on where in the application this form is accessed.
     The method ensures that the Add button will not be enabled unless all appropriate information is filled in.

     Lambda: I used lambda expressions when adding listeners to text properties, value properties, and selected item properties
     of text fields, date pickers, and combo boxes respectively. Lambda expressions were primarily useful for me to clean up the lengthy
     code of my initialize method. Lambda expressions are shorter and often more efficient than the full code expressions.

     @param appointments List of appointments within the database that may or may not be filtered according to parameters
     @param selectedContact The contact associated with the list of appointments, if not null
     @param selectedCustomer The customer associated with the list of appointments, if not null
     @param selectedUser The user currently logged in OR the user associated with the list of appointments */
    public void initialize(ObservableList<Appointment> appointments, Customer selectedCustomer, Contacts selectedContact, Users selectedUser){
        this.defaultCustomer = selectedCustomer;
        this.defaultContact = selectedContact;
        this.defaultUser = selectedUser;
        this.appointments = appointments;

        users.setAll(DBConnection.getInstance().queryUsers());
        customers.setAll(DBConnection.getInstance().queryCustomers());
        contacts.setAll(DBConnection.getInstance().queryContacts());

        setAllComboBoxes();

        titleLabel.visibleProperty().bind(appointmentTitle.focusedProperty());
        descLabel.visibleProperty().bind(appointmentDesc.focusedProperty());
        locationLabel.visibleProperty().bind(apptLocation.focusedProperty());

        addAppointmentButton.setDisable(true);
        textfields.addAll(appointmentTitle, appointmentDesc, apptLocation);
        for(TextField TF : textfields){
            TF.textProperty().addListener((observableValue, old, newText) -> {
                if(newText.trim().isEmpty()){
                    addAppointmentButton.setDisable(true);
                    TF.setStyle("-fx-border-color: red");
                }
                if(!newText.trim().isEmpty()){
                    TF.setStyle("-fx-border-color: none");
                    boolean result = checkFields();
                    if(result){
                        addAppointmentButton.setDisable(false);
                    } else {
                        addAppointmentButton.setDisable(true);
                    }
                }});
        }
        comboBoxes.addAll(contactsComboBox, timeComboBoxEnd, timeComboBoxStart, customersComboBox, userComboBox, appointmentType);
        for(ComboBox cb : comboBoxes){
            cb.getSelectionModel().selectedItemProperty().addListener((observableValue, old, newSelection) -> {
                if(newSelection != null){
                    boolean result = checkFields();
                    if(result){
                        addAppointmentButton.setDisable(false);
                    } else {
                        addAppointmentButton.setDisable(true);
                    }
                }
            });
        }

        timeComboBoxStart.disableProperty().bind(Bindings.or(startDatePicker.valueProperty().isNull(), endDatePicker.valueProperty().isNull()));
        timeComboBoxEnd.disableProperty().bind(Bindings.or(startDatePicker.valueProperty().isNull(), endDatePicker.valueProperty().isNull()));
        startTimeLabel.setVisible(false);
        endTimeLabel.setVisible(false);
        timeComboBoxStart.getSelectionModel().selectedItemProperty().addListener((observableValue, s, newTime) -> {
            boolean bool = (timeComboBoxEnd.getSelectionModel().getSelectedItem() != null);
            if(bool) {
                if (LocalTime.parse(newTime, DateTimeFormatter.ofPattern("h:mm a")).isAfter(LocalTime.parse(timeComboBoxEnd.getSelectionModel().getSelectedItem(), DateTimeFormatter.ofPattern("h:mm a")))){
                    timeComboBoxStart.setStyle("-fx-border-color: red");
                    startTimeLabel.setVisible(true);
                    addAppointmentButton.setDisable(true);
                } else {
                    timeComboBoxStart.setStyle("-fx-border-color: none");
                    startTimeLabel.setVisible(false);
                    timeComboBoxEnd.setStyle("-fx-border-color: none");
                    endTimeLabel.setVisible(false);
                    if(checkFields()){
                        addAppointmentButton.setDisable(false);
                    }
                }
                if(startDatePicker.getValue().isBefore(endDatePicker.getValue())){
                    timeComboBoxEnd.setStyle("-fx-border-color: none");
                    endTimeLabel.setVisible(false);
                    timeComboBoxStart.setStyle("-fx-border-color: none");
                    startTimeLabel.setVisible(false);
                    if(checkFields()){
                        addAppointmentButton.setDisable(false);
                    }
                }
            }
        });
        timeComboBoxEnd.getSelectionModel().selectedItemProperty().addListener((observableValue, s, newTime) -> {
            boolean bool = (timeComboBoxStart.getSelectionModel().getSelectedItem() != null);
            if(bool) {
                if (LocalTime.parse(newTime, DateTimeFormatter.ofPattern("h:mm a")).isBefore(LocalTime.parse(timeComboBoxStart.getSelectionModel().getSelectedItem(), DateTimeFormatter.ofPattern("h:mm a")))){
                    timeComboBoxEnd.setStyle("-fx-border-color: red");
                    endTimeLabel.setVisible(true);
                    addAppointmentButton.setDisable(true);
                } else {
                    timeComboBoxEnd.setStyle("-fx-border-color: none");
                    endTimeLabel.setVisible(false);
                    timeComboBoxStart.setStyle("-fx-border-color: none");
                    startTimeLabel.setVisible(false);
                    if(checkFields()){
                        addAppointmentButton.setDisable(false);
                    }
                }
                if(startDatePicker.getValue().isBefore(endDatePicker.getValue())){
                    timeComboBoxEnd.setStyle("-fx-border-color: none");
                    endTimeLabel.setVisible(false);
                    timeComboBoxStart.setStyle("-fx-border-color: none");
                    startTimeLabel.setVisible(false);
                    if(checkFields()){
                        addAppointmentButton.setDisable(false);
                    }
                }
            }
        });
        endDatePicker.disableProperty().bind(startDatePicker.valueProperty().isNull());
        startDatePicker.setShowWeekNumbers(false);
        endDatePicker.setShowWeekNumbers(false);

        endDatePicker.setDayCellFactory(new Callback<>() {
            public DateCell call(DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate localDate, boolean b) {
                        super.updateItem(localDate, b);
                        if(localDate.isBefore(LocalDate.now())){
                            setDisable(true);
                        }
                        if(localDate.isBefore(startDatePicker.getValue())){
                            setDisable(true);
                        }}};
            }});
        startDatePicker.setDayCellFactory(new Callback<>() {
            public DateCell call(DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate localDate, boolean b) {
                        super.updateItem(localDate, b);
                        if(localDate.isBefore(LocalDate.now())){
                            setDisable(true);
                        }
                        if(endDatePicker.getValue() != null)
                            if(localDate.isAfter(endDatePicker.getValue())){
                                setDisable(true);
                            }}};
            }});
        startDatePicker.valueProperty().addListener((observableValue, localDate, newDate) -> {
                boolean result = false;
                if(newDate != null){
                    result = checkFields();
                }
                if(result){
                    addAppointmentButton.setDisable(false);
                } else {
                    addAppointmentButton.setDisable(true);
                }
        });
        endDatePicker.valueProperty().addListener((observableValue, localDate, newDate) -> {
                boolean result;
                result = checkFields();
                if(result){
                    addAppointmentButton.setDisable(false);
                } else {
                    addAppointmentButton.setDisable(true);
                }
                if(newDate.isEqual(startDatePicker.getValue()) && (timeComboBoxStart.getSelectionModel().getSelectedItem() != null) &&
                        (timeComboBoxEnd.getSelectionModel().getSelectedItem() != null) &&
                        (LocalTime.parse(timeComboBoxEnd.getSelectionModel().getSelectedItem(), DateTimeFormatter.ofPattern("h:mm a")).isBefore(LocalTime.parse(timeComboBoxStart.getSelectionModel().getSelectedItem(), DateTimeFormatter.ofPattern("h:mm a"))))){
                    timeComboBoxEnd.setStyle("-fx-border-color: red");
                    endTimeLabel.setVisible(true);
                    timeComboBoxStart.setStyle("-fx-border-color: red");
                    startTimeLabel.setVisible(true);
                    addAppointmentButton.setDisable(true);
                } else {
                    timeComboBoxEnd.setStyle("-fx-border-color: none");
                    endTimeLabel.setVisible(false);
                    timeComboBoxStart.setStyle("-fx-border-color: none");
                    startTimeLabel.setVisible(false);
                    if(result){
                        addAppointmentButton.setDisable(false);
                    }
                }
        });
    }

    /** This method checks all form fields to see if they are appropriately filled in.
     This method is called by the initialize method in order to determine whether or not the Add button should be enabled.
     @return boolean */
    private boolean checkFields(){
        if(!apptLocation.getText().trim().isEmpty() && (appointmentType.getSelectionModel().getSelectedItem() != null) &&
            !appointmentDesc.getText().trim().isEmpty() && !appointmentTitle.getText().trim().isEmpty() &&
                (startDatePicker.getValue() != null) && (endDatePicker.getValue() != null) &&
                (timeComboBoxStart.getSelectionModel().getSelectedItem() != null) &&
                (timeComboBoxEnd.getSelectionModel().getSelectedItem() != null) &&
                (contactsComboBox.getSelectionModel().getSelectedItem() != null) &&
                (customersComboBox.getSelectionModel().getSelectedItem() != null) &&
                (userComboBox.getSelectionModel().getSelectedItem() != null) &&
                (startDatePicker.getValue().isBefore(endDatePicker.getValue()) ||
                        startDatePicker.getValue().equals(endDatePicker.getValue())) &&
                (startDatePicker.getValue().isAfter(LocalDate.now().minusDays(1))) &&
                (endDatePicker.getValue().isAfter(LocalDate.now().minusDays(1)))){
            return true;
        } else {
            return false;
        }
    }

    /** This method is invoked by the initialize method in order to set up all the combo boxes in the form with the appropriate data.
     Within this method, the combo boxes are filled with the appropriate observable lists, and the data is formatted to show appropriate string information for the user. */
    private void setAllComboBoxes(){
        ObservableList<String> times = FXCollections.observableArrayList();
        times.addAll(AppTime.getTimeList(LocalDate.now()));
        timeComboBoxStart.setItems(times);
        timeComboBoxEnd.setItems(times);
        userComboBox.setItems(users);
        customersComboBox.setItems(customers);
        contactsComboBox.setItems(contacts);
        appointmentType.setItems(Type.getTypes());

        Callback<ListView<Users>, ListCell<Users>> cellFactoryUser = userListView -> new ListCell<>() {
            @Override
            protected void updateItem(Users u, boolean b) {
                super.updateItem(u, b);
                if (b) {
                    setText("");
                } else {
                    setText("(ID) " + u.getUserID() + " : " + u.getUserName());
                }
            }};
        Callback<ListView<Users>, ListCell<Users>> cellFactoryUserButton = userListView -> new ListCell<>() {
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

        Callback<ListView<Customer>, ListCell<Customer>> cellFactoryCustomer = customerListView -> new ListCell<>() {
            @Override
            protected void updateItem(Customer c, boolean b) {
                super.updateItem(c, b);
                if (b) {
                    setText("");
                } else {
                    setText("(ID) " + c.getCustomerID() + " : " + c.getName());
                }
            }};
        Callback<ListView<Customer>, ListCell<Customer>> cellFactoryCustomerButton = customerListView -> new ListCell<>() {
            @Override
            protected void updateItem(Customer c, boolean b) {
                super.updateItem(c, b);
                if (b) {
                    setText("");
                } else {
                    setText(c.getName());
                }
            }};
        customersComboBox.setCellFactory(cellFactoryCustomer);
        customersComboBox.setButtonCell(cellFactoryCustomerButton.call(null));

        Callback<ListView<Contacts>, ListCell<Contacts>> cellFactoryContact = contactListView -> new ListCell<>() {
            @Override
            protected void updateItem(Contacts c, boolean b) {
                super.updateItem(c, b);
                if (b) {
                    setText("");
                } else {
                    setText("(ID) " + c.getContactID() + " : " + c.getContactName());
                }
            }};
        Callback<ListView<Contacts>, ListCell<Contacts>> cellFactoryContactButton = contactListView -> new ListCell<>() {
            @Override
            protected void updateItem(Contacts c, boolean b) {
                super.updateItem(c, b);
                if (b) {
                    setText("");
                } else {
                    setText(c.getContactName());
                }
            }};
        contactsComboBox.setCellFactory(cellFactoryContact);
        contactsComboBox.setButtonCell(cellFactoryContactButton.call(null));

        if(defaultContact != null) {
            for (Contacts contact : contactsComboBox.getItems()) {
                if (contact.getContactID() == defaultContact.getContactID()) {
                    contactsComboBox.getSelectionModel().select(contact);
                }
            }
        }
        if(defaultUser == null){
            for (Users user : userComboBox.getItems()) {
                if (user.getUserID() == MainWindowController.getUserLoggedIn().getUserID()) {
                    userComboBox.getSelectionModel().select(user);
                }
            }
        } else if(defaultUser != null){
            for (Users user : userComboBox.getItems()) {
                if (user.getUserID() == defaultUser.getUserID()) {
                    userComboBox.getSelectionModel().select(user);
                }
            }
        }
        if(!MainWindowController.getUserLoggedIn().getUserName().equals("admin")) {
            userComboBox.setDisable(true);
        }
        if(defaultCustomer != null){
            for (Customer customer : customersComboBox.getItems()) {
                if (customer.getCustomerID() == defaultCustomer.getCustomerID()) {
                    customersComboBox.getSelectionModel().select(customer);
                }
            }
        }
    }

    /** This method is invoked when the 'Add' button is pressed on the form and handles adding the appointment to the database.
     This method validates the provided information and, if all is correct, inserts the appointment into the database. */
    @FXML
    public void handleAddAppointment(){
        LocalDate localDate1 = startDatePicker.getValue();
        LocalDate localDate2 = endDatePicker.getValue();
        LocalTime localTime1 = LocalTime.parse(timeComboBoxStart.getSelectionModel().getSelectedItem(), DateTimeFormatter.ofPattern("h:mm a"));
        LocalTime localTime2 = LocalTime.parse(timeComboBoxEnd.getSelectionModel().getSelectedItem(), DateTimeFormatter.ofPattern("h:mm a"));
        LocalDateTime start = LocalDateTime.of(localDate1, localTime1);
        LocalDateTime end = LocalDateTime.of(localDate2, localTime2);

            boolean conflict = false;
            for (Appointment appointment : appointments) {
                if (appointment.getCustomerId() == customersComboBox.getSelectionModel().getSelectedItem().getCustomerID()) {
                    if ((appointment.getStart().isAfter(start) && appointment.getStart().isBefore(end)) ||
                            appointment.getEnd().isAfter(start) && appointment.getEnd().isBefore(end)) {
                        conflict = true;

                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("");
                        alert.setHeaderText("Error Adding Appointment");
                        alert.setContentText("Customer has a conflict. Press 'OK' to see this contact's schedule.");

                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.isPresent() && result.get() == ButtonType.OK) {
                            Dialog<ButtonType> dialog = new Dialog<>();
                            dialog.initOwner(customersComboBox.getScene().getWindow());
                            FXMLLoader fxmlLoader = new FXMLLoader();
                            fxmlLoader.setLocation(getClass().getResource("/view/ContactSchedule.fxml"));
                            try {
                                dialog.getDialogPane().setContent(fxmlLoader.load());
                                ContactScheduleController controller = fxmlLoader.getController();
                                controller.initialize(contactsComboBox.getSelectionModel().getSelectedItem());
                            } catch (Exception e) {
                                System.out.println("Could not load contact schedule.");
                                e.printStackTrace();
                            }
                            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
                            dialog.setResizable(true);
                            dialog.showAndWait();
                        } else if (result.isPresent() && result.get() == ButtonType.CANCEL) {
                            alert.close();
                        }
                        break;
                    }
                }
            }
            if (!conflict) {
                Appointment appt = new Appointment();
                Users user1 = new Users();
                for (Users user : users) {
                    if (userComboBox.getSelectionModel().getSelectedItem().getUserID() == user.getUserID()) {
                        user1 = user;
                    }
                }
                appt.setId(null);
                appt.setTitle(appointmentTitle.getText());
                appt.setDescription(appointmentDesc.getText());
                appt.setLocation(apptLocation.getText());
                appt.setType(appointmentType.getSelectionModel().getSelectedItem());
                appt.setStart(start);
                appt.setEnd(end);
                appt.setUser(user1.getUserID());
                appt.setContact(contactsComboBox.getSelectionModel().getSelectedItem().getContactID());
                appt.setCustomerId(customersComboBox.getSelectionModel().getSelectedItem().getCustomerID());

                DBConnection.getInstance().insertAppointment(appt, user1);
                appointments.setAll(DBConnection.getInstance().queryAppointments());
                Stage stage = (Stage) addAppointmentButton.getScene().getWindow();
                stage.close();
            }
    }

    /** This method is invoked when the 'Cancel' button is pressed.
     This method shows the user an alert to confirm they want to close the window. If they press 'OK', the window is closed.
     If they press 'Cancel' on the alert, the alert is closed. */
    @FXML
    public void handleCancel(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm action");
        alert.setContentText("Changes will not be saved. Are you sure you want to continue?");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK){
            Stage stage = (Stage) cancelNewApptButton.getScene().getWindow();
            stage.close();
        } else if(result.isPresent() && result.get() == ButtonType.CANCEL){
            alert.close();
        }
    }
}
