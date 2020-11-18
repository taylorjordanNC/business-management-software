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

/** This class controls the update appointment window.
 When any 'Update' button is clicked that is associated with an appointment, this class and associated window are loaded. */
public class UpdateAppointmentController {
    @FXML
    private Label startTimeLabel;
    @FXML
    private Label endTimeLabel;
    @FXML
    private TextField appointmentID;
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
    private DatePicker startDatePicker;
    @FXML
    private ComboBox<String> timeComboBoxStart;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private ComboBox<String> timeComboBoxEnd;
    @FXML
    private ComboBox<Customer> customersComboBox;
    @FXML
    private ComboBox<Users> userComboBox;
    @FXML
    private Button saveAppointmentButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Label titleLabel;
    @FXML
    private Label descLabel;
    @FXML
    private Label locationLabel;

    private ObservableList<TextField> textfields = FXCollections.observableArrayList();
    private ObservableList<Appointment> appointments = FXCollections.observableArrayList();
    private Appointment selectedAppointment;

    /** This method initializes the form of the dialog window with the appropriate appointment information to be updated.
     All form fields and combo boxes are initiated with the appointment's data that is passed into this method.

     This method primarily handles the formatting of the combo boxes and calls separate methods to fill in data.

     Lambda: Nearly every selected item property, value property, and text property listener method is in a lambda expression form.
     I chose to implement this to reduce repetition when calling these methods and to clean up the code.
     @param appointments List of appointments from the database.
     @param selectedAppt The selected appointment to be updated. */
    public void initialize(ObservableList<Appointment> appointments, Appointment selectedAppt) {
        this.appointments = appointments;
        this.selectedAppointment = selectedAppt;
        this.appointmentID.setDisable(true);

        titleLabel.visibleProperty().bind(appointmentTitle.focusedProperty());
        descLabel.visibleProperty().bind(appointmentDesc.focusedProperty());
        locationLabel.visibleProperty().bind(apptLocation.focusedProperty());

        textfields.addAll(appointmentTitle, appointmentDesc, apptLocation);
        for (TextField TF : textfields) {
            TF.textProperty().addListener((observableValue, old, newText) -> {
                if(newText.trim().isEmpty()){
                    saveAppointmentButton.setDisable(true);
                    TF.setStyle("-fx-border-color: red");
                }
                if (!newText.trim().isEmpty()) {
                    TF.setStyle("-fx-border-color: none");
                    boolean result = checkFields();
                    if (result) {
                        saveAppointmentButton.setDisable(false);
                    } else {
                        saveAppointmentButton.setDisable(true);
                    }
                }});

            initializeForm(selectedAppt);
            setTimeComboBoxes();

            appointmentType.getSelectionModel().selectedItemProperty().addListener((observableValue, s, t1) -> {
                if(t1 != null){
                    if(checkFields()) {
                        saveAppointmentButton.setDisable(false);
                    } else {
                        saveAppointmentButton.setDisable(true);
                    }
                }});

            timeComboBoxStart.disableProperty().bind(Bindings.or(startDatePicker.valueProperty().isNull(), endDatePicker.valueProperty().isNull()));
            timeComboBoxEnd.disableProperty().bind(Bindings.or(startDatePicker.valueProperty().isNull(), endDatePicker.valueProperty().isNull()));
            startTimeLabel.setVisible(false);
            endTimeLabel.setVisible(false);
            timeComboBoxStart.getSelectionModel().selectedItemProperty().addListener((observableValue, s, newTime) -> {
                boolean bool = (timeComboBoxEnd.getSelectionModel().getSelectedItem() != null);
                if (bool) {
                    if (LocalTime.parse(newTime, DateTimeFormatter.ofPattern("h:mm a")).isAfter(LocalTime.parse(timeComboBoxEnd.getSelectionModel().getSelectedItem(), DateTimeFormatter.ofPattern("h:mm a")))) {
                        timeComboBoxStart.setStyle("-fx-border-color: red");
                        startTimeLabel.setVisible(true);
                        saveAppointmentButton.setDisable(true);
                    } else {
                        timeComboBoxStart.setStyle("-fx-border-color: none");
                        startTimeLabel.setVisible(false);
                        timeComboBoxEnd.setStyle("-fx-border-color: none");
                        endTimeLabel.setVisible(false);
                        if (checkFields()) {
                            saveAppointmentButton.setDisable(false);
                        }
                    }
                    if (startDatePicker.getValue().isBefore(endDatePicker.getValue())) {
                        timeComboBoxEnd.setStyle("-fx-border-color: none");
                        endTimeLabel.setVisible(false);
                        timeComboBoxStart.setStyle("-fx-border-color: none");
                        startTimeLabel.setVisible(false);
                        if (checkFields()) {
                            saveAppointmentButton.setDisable(false);
                        }
                    }}
            });
            timeComboBoxEnd.getSelectionModel().selectedItemProperty().addListener((observableValue, s, newTime) -> {
                boolean bool = (timeComboBoxStart.getSelectionModel().getSelectedItem() != null);
                if (bool) {
                    if (LocalTime.parse(newTime, DateTimeFormatter.ofPattern("h:mm a")).isBefore(LocalTime.parse(timeComboBoxStart.getSelectionModel().getSelectedItem(), DateTimeFormatter.ofPattern("h:mm a")))) {
                        timeComboBoxEnd.setStyle("-fx-border-color: red");
                        endTimeLabel.setVisible(true);
                        saveAppointmentButton.setDisable(true);
                    } else {
                        timeComboBoxEnd.setStyle("-fx-border-color: none");
                        endTimeLabel.setVisible(false);
                        timeComboBoxStart.setStyle("-fx-border-color: none");
                        startTimeLabel.setVisible(false);
                        if (checkFields()) {
                            saveAppointmentButton.setDisable(false);
                        }
                    }
                    if (startDatePicker.getValue().isBefore(endDatePicker.getValue())) {
                        timeComboBoxEnd.setStyle("-fx-border-color: none");
                        endTimeLabel.setVisible(false);
                        timeComboBoxStart.setStyle("-fx-border-color: none");
                        startTimeLabel.setVisible(false);
                        if (checkFields()) {
                            saveAppointmentButton.setDisable(false);
                        }
                    }}
            });
            endDatePicker.disableProperty().bind(startDatePicker.valueProperty().isNull());
            startDatePicker.setShowWeekNumbers(false);
            endDatePicker.setShowWeekNumbers(false);

            startDatePicker.valueProperty().addListener((observableValue, localDate, newDate) -> {
                boolean result = false;
                if (newDate != null) {
                    result = checkFields();
                }
                if (result) {
                    saveAppointmentButton.setDisable(false);
                } else {
                    saveAppointmentButton.setDisable(true);
                }
            });
            endDatePicker.valueProperty().addListener((observableValue, localDate, newDate) -> {
                boolean result;
                result = checkFields();
                if (result) {
                    saveAppointmentButton.setDisable(false);
                } else {
                    saveAppointmentButton.setDisable(true);
                }
                if (newDate.isEqual(startDatePicker.getValue()) && (timeComboBoxStart.getSelectionModel().getSelectedItem() != null) &&
                        (timeComboBoxEnd.getSelectionModel().getSelectedItem() != null) &&
                        (LocalTime.parse(timeComboBoxEnd.getSelectionModel().getSelectedItem(), DateTimeFormatter.ofPattern("h:mm a")).isBefore(LocalTime.parse(timeComboBoxStart.getSelectionModel().getSelectedItem(), DateTimeFormatter.ofPattern("h:mm a"))))) {
                    timeComboBoxEnd.setStyle("-fx-border-color: red");
                    endTimeLabel.setVisible(true);
                    timeComboBoxStart.setStyle("-fx-border-color: red");
                    startTimeLabel.setVisible(true);
                    saveAppointmentButton.setDisable(true);
                } else {
                    timeComboBoxEnd.setStyle("-fx-border-color: none");
                    endTimeLabel.setVisible(false);
                    timeComboBoxStart.setStyle("-fx-border-color: none");
                    startTimeLabel.setVisible(false);
                    if (result) {
                        saveAppointmentButton.setDisable(false);
                    }
                }});
        }
    }

    /** This method initializes the form data and is called by the initialize method.
     All of the information from the appointment parameter is filled into the combo boxes and text fields and formatted.
     @param appointment Selected appointment to be updated. */
    private void initializeForm(Appointment appointment){
        appointmentID.setText(String.valueOf(appointment.getId()));
        appointmentTitle.setText(appointment.getTitle());
        appointmentDesc.setText(appointment.getDescription());
        apptLocation.setText(appointment.getLocation());
        appointmentType.getSelectionModel().select(appointment.getType());

        timeComboBoxEnd.getSelectionModel().select(appointment.getEnd().toLocalTime().format(DateTimeFormatter.ofPattern("h:mm a")));
        timeComboBoxStart.getSelectionModel().select(appointment.getStart().toLocalTime().format(DateTimeFormatter.ofPattern("h:mm a")));
        startDatePicker.setValue(appointment.getStart().toLocalDate());
        endDatePicker.setValue(appointment.getEnd().toLocalDate());

        appointmentType.setItems(Type.getTypes());
        userComboBox.setItems(DBConnection.getInstance().queryUsers());
        customersComboBox.setItems(DBConnection.getInstance().queryCustomers());
        contactsComboBox.setItems(DBConnection.getInstance().queryContacts());
        if(!MainWindowController.getUserLoggedIn().getUserName().equals("admin")) {
            userComboBox.setDisable(true);
        }

        endDatePicker.setDayCellFactory(new Callback<>() {
            public DateCell call(DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate localDate, boolean b) {
                        super.updateItem(localDate, b);
                        if(localDate.isBefore(LocalDate.now())){
                            setDisable(true);
                        }
                        if (localDate.isBefore(startDatePicker.getValue())) {
                            setDisable(true);
                        }
                    }};
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
                        if (endDatePicker.getValue() != null)
                            if (localDate.isAfter(endDatePicker.getValue())) {
                                setDisable(true);
                            }
                    }};
            }});
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

        for (Users user : userComboBox.getItems()) {
            if (appointment.getUser() == user.getUserID()) {
                userComboBox.getSelectionModel().select(user);
            }
        }
        for (Customer customer : customersComboBox.getItems()) {
            if (appointment.getCustomerId() == customer.getCustomerID()) {
                customersComboBox.getSelectionModel().select(customer);
            }
        }
        for (Contacts contact : contactsComboBox.getItems()) {
            if (appointment.getContact() == contact.getContactID()) {
                contactsComboBox.getSelectionModel().select(contact);
            }
        }
    }

    /** This method specifically sets the combo boxes that hold the list of available appointment times.
     This method is called by the initialize method. */
    private void setTimeComboBoxes(){
        ObservableList<String> times = FXCollections.observableArrayList();
        times.addAll(AppTime.getTimeList(LocalDate.now()));
        timeComboBoxStart.setItems(times);
        timeComboBoxEnd.setItems(times);
    }

    private boolean checkFields(){
        if(!apptLocation.getText().trim().isEmpty() && (appointmentType.getSelectionModel().getSelectedItem() != null) &&
                !appointmentDesc.getText().trim().isEmpty() && !appointmentTitle.getText().trim().isEmpty() &&
                (startDatePicker.getValue() != null) && (endDatePicker.getValue() != null) &&
                (timeComboBoxStart.getSelectionModel().getSelectedItem() != null) &&
                (timeComboBoxEnd.getSelectionModel().getSelectedItem() != null) &&
                (contactsComboBox.getSelectionModel().getSelectedItem() != null) &&
                (customersComboBox.getSelectionModel().getSelectedItem() != null) &&
                (userComboBox.getSelectionModel().getSelectedItem() != null)){
            return true;
        } else {
            return false;
        }
    }

    /** This method handles updating the appointment in the database with the information from the form.
     When the 'Save' button is clicked, this method is invoked. All of the information within the form is grabbed from the combo boxes
     and text fields and is put into a new appointment variable to pass to the database for update.

     This method also checks to ensure the chosen appointment time falls within business hours, and the method also checks to see if the
     appointment conflicts with any other appointment for the customer. If the appointment fails either of these checks, the user will be
     alerted and the appointment will not be saved. If the customer does have an appointment conflict, the user will be given the option to open
     the dialog window that shows the customer's entire schedule.

     After the appointment is saved, the window is closed. */
    @FXML
    private void handleSaveAppointment(){
        LocalDate localDate1 = startDatePicker.getValue();
        LocalDate localDate2 = endDatePicker.getValue();
        LocalTime localTime1 = LocalTime.parse(timeComboBoxStart.getSelectionModel().getSelectedItem(), DateTimeFormatter.ofPattern("h:mm a"));
        LocalTime localTime2 = LocalTime.parse(timeComboBoxEnd.getSelectionModel().getSelectedItem(), DateTimeFormatter.ofPattern("h:mm a"));
        LocalDateTime start = LocalDateTime.of(localDate1, localTime1);
        LocalDateTime end = LocalDateTime.of(localDate2, localTime2);

            boolean conflict = false;
            for(Appointment appointment : appointments){
            if(appointment.getCustomerId() == customersComboBox.getSelectionModel().getSelectedItem().getCustomerID()){
                if(((appointment.getStart().isAfter(start) && appointment.getStart().isBefore(end)) ||
                        appointment.getEnd().isAfter(start) && appointment.getEnd().isBefore(end)) &&
                        (appointment.getId() != Integer.parseInt(appointmentID.getText().trim()))){
                    conflict = true;

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("");
                    alert.setHeaderText("Error Adding Appointment");
                    alert.setContentText("Customer has a conflict. Press 'OK' to see this contact's schedule.");

                    Optional<ButtonType> result = alert.showAndWait();
                    if(result.isPresent() && result.get() == ButtonType.OK){
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
                    } else if(result.isPresent() && result.get() == ButtonType.CANCEL){
                        alert.close();
                    }
                    break;
                }
            }
        }
        if(!conflict){
        Appointment updatedAppointment = new Appointment();
        Users creator = new Users();
        for(Users user : userComboBox.getItems()){
            if(user.getUserID() == userComboBox.getSelectionModel().getSelectedItem().getUserID()){
                updatedAppointment.setUser(user.getUserID());
                creator = user;
            }
        }
        updatedAppointment.setId(selectedAppointment.getId());
        updatedAppointment.setTitle(appointmentTitle.getText());
        updatedAppointment.setDescription(appointmentDesc.getText());
        updatedAppointment.setLocation(apptLocation.getText());
        updatedAppointment.setType(appointmentType.getSelectionModel().getSelectedItem());
        updatedAppointment.setStart(start);
        updatedAppointment.setEnd(end);
        updatedAppointment.setCustomerId(customersComboBox.getSelectionModel().getSelectedItem().getCustomerID());
        updatedAppointment.setContact(contactsComboBox.getSelectionModel().getSelectedItem().getContactID());

        DBConnection.getInstance().updateAppointment(updatedAppointment, creator);
        appointments.setAll(DBConnection.getInstance().queryAppointments());

        Stage stage = (Stage) saveAppointmentButton.getScene().getWindow();
        stage.close();
        }
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
