package controller;

import database.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.util.Callback;
import model.Appointment;
import model.Customer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/** This class controls the customer schedule window.
 When the user clicks the 'Get Schedule' button associated with the table of contacts, this window and its controller is called. */
public class CustomerScheduleController {
    @FXML
    private Label scheduleLabel;
    @FXML
    private TableView<Appointment> customerSchedule;
    @FXML
    private TableColumn<Appointment, LocalDateTime> startColumn;
    @FXML
    private TableColumn<Appointment, LocalDateTime> endColumn;
    @FXML
    private Button newButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button deleteButton;

    private Customer customer;
    private ObservableList<Appointment> appts = FXCollections.observableArrayList(DBConnection.getInstance().queryAppointments());
    private FilteredList<Appointment> customerAppointments = new FilteredList<>(appts);

    /** This method initializes the customer schedule window.
     When the page is loaded, this method sets up the window with a table of appointments associated with the customer passed to the method.

     The update/delete buttons are disabled until an appointment is selected in the table.
     This method also handles formatting of the table columns to show the start/end times in a readable format to the user.

     Lambda: A lambda expression is used when setting a listener on the selected item property of the customer schedule. I used
     this to shorten the length of code and reduce repetition between various classes implementing and calling the same methods for the listeners.
     @param customer The customer with which the appointments shown are associated.
     @param customerAppts The filtered list of appointments for the selected customer. */
    public void initialize(FilteredList<Appointment> customerAppts, Customer customer){
        this.customer = customer;
        customerAppointments = customerAppts;
        scheduleLabel.setText("Appointments for " + customer.getName());

        startColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Appointment, LocalDateTime> call(TableColumn<Appointment, LocalDateTime> appointmentLocalDateTimeTableColumn) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(LocalDateTime localDateTime, boolean empty) {
                        super.updateItem(localDateTime, empty);
                        if (localDateTime == null || empty) {
                            setText(null);
                            setStyle("");
                        } else {
                            setText(DateTimeFormatter.ofPattern("LLL d, yyyy @ h:mm a").format(localDateTime));
                        }}};
            }});
        endColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Appointment, LocalDateTime> call(TableColumn<Appointment, LocalDateTime> appointmentLocalDateTimeTableColumn) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(LocalDateTime localDateTime, boolean empty) {
                        super.updateItem(localDateTime, empty);
                        if (localDateTime == null || empty) {
                            setText(null);
                            setStyle("");
                        } else {
                            setText(DateTimeFormatter.ofPattern("LLL d, yyyy @ h:mm a").format(localDateTime));
                        }}};
            }});
        customerSchedule.setItems(customerAppointments);
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        customerSchedule.getSelectionModel().selectedItemProperty().addListener((observableValue, appointment, appt2) -> {
            if(appt2 != null) {
                updateButton.setDisable(false);
                deleteButton.setDisable(false);
            } else{
                updateButton.setDisable(true);
                deleteButton.setDisable(true);
            }});
    }

    /** This method opens the add appointment dialog form window.
     When the user clicks the 'New' button, this dialog is loaded and the customer is passed as the default customer. */
    @FXML
    private void handleNewAppt() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(newButton.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/AddAppointment.fxml"));
        try{
            dialog.getDialogPane().setContent(fxmlLoader.load());
            AddApptController controller = fxmlLoader.getController();
            controller.initialize(appts, customer, null, null);
        } catch(Exception e){
            System.out.println("Could not load add appointment form.");
            e.printStackTrace();
        }
        dialog.setResizable(true);
        dialog.showAndWait();
        customerSchedule.refresh();
    }

    /** This method opens the update appointment dialog window.
     When the 'Update' button is clicked, this window is loaded and is passed the filtered list of appointments
     as well as the selected appointment to be updated. */
    @FXML
    private void handleUpdate() {
        if(customerSchedule.getSelectionModel().getSelectedItem() != null){
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.initOwner(updateButton.getScene().getWindow());
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/view/UpdateAppointment.fxml"));
            try{
                dialog.getDialogPane().setContent(fxmlLoader.load());
                UpdateAppointmentController controller = fxmlLoader.getController();
                controller.initialize(appts, customerSchedule.getSelectionModel().getSelectedItem());
            } catch(Exception e){
                System.out.println("Could not load update appointment form.");
                e.printStackTrace();
            }
            dialog.setResizable(true);
            dialog.show();
            customerSchedule.refresh();
        }
    }

    /** This method deletes the selected appointment from the database.
     When the user clicks the 'Delete' button, an alert prompts the user to confirm their action.
     If the user presses 'OK', the appointment is then deleted and the window is closed.
     If the user presses 'Cancel', the alert is closed and the window remains open. */
    @FXML
    private void handleDelete() {
        Appointment appointmentToDelete = customerSchedule.getSelectionModel().getSelectedItem();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Action");
        alert.setContentText("Appointment will be permanently deleted. Are you sure you want to continue?");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK){
            DBConnection.getInstance().deleteAppointment(appointmentToDelete);
        } else if(result.isPresent() && result.get() == ButtonType.CANCEL){
            alert.close();
        }
        appts.setAll(DBConnection.getInstance().queryAppointments());
        customerSchedule.refresh();
    }
}
