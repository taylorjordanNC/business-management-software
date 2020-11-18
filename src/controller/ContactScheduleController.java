package controller;

import database.DBConnection;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.util.Callback;
import model.Appointment;
import model.Contacts;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/** This class controls the contact schedule window.
 When the user clicks the 'Get Schedule' button associated with the table of contacts, this window and its controller is called. */
public class ContactScheduleController {
    @FXML
    private Button newButton;
    @FXML
    private Label scheduleLabel;
    @FXML
    private Button updateButton;
    @FXML
    private Button deleteButton;
    @FXML
    private TableView<Appointment> contactSchedule;
    @FXML
    private TableColumn<Appointment, LocalDateTime> startColumn;
    @FXML
    private TableColumn<Appointment, LocalDateTime> endColumn;

    private Contacts contact;
    private FilteredList<Appointment> appointmentsForContact = new FilteredList<>(DBConnection.getInstance().queryAppointments());

    /** This method initializes the contact schedule form.
     When invoked, this method sets up the table of appointments associated with the selected contact passed into the method.
     This method sets cell factories for the start time and end time columns to properly format the times, and also
     adds listeners to ensure that the update and delete buttons remain disabled until there is an appointment selected.

     Lambda: When setting the predicate for the contact appointment filtered list, and when setting a listener for the selected item property
     of the contact schedule, a lambda expression is used to shorten code and reduce repetition.
     @param contact The selected contact from the contacts table. */
    public void initialize(Contacts contact){
        this.contact = contact;
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
                        }
                    }};
            }});
        appointmentsForContact.setPredicate(appointment -> {
            if(appointment.getContact() == contact.getContactID()){
                return true;
            } else return false;
        });

        contactSchedule.setItems(appointmentsForContact);
        updateButton.setDisable(true);
        deleteButton.setDisable(true);

        scheduleLabel.setText("Appointment Schedule for " + contact.getContactName());
        contactSchedule.getSelectionModel().selectedItemProperty().addListener((observableValue, appointment, appt2) -> {
            if(appt2 != null) {
                updateButton.setDisable(false);
                deleteButton.setDisable(false);
            } else{
                updateButton.setDisable(true);
                deleteButton.setDisable(true);
            }
        });
    }

    /** This method opens the update appointment dialog window.
     When the 'Update' button is clicked, this window is loaded and is passed the filtered list of appointments
     as well as the selected appointment to be updated. */
    @FXML
    private void handleUpdate() {
        if(contactSchedule.getSelectionModel().getSelectedItem() != null){
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.initOwner(updateButton.getScene().getWindow());
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/view/UpdateAppointment.fxml"));
            try{
                dialog.getDialogPane().setContent(fxmlLoader.load());
                UpdateAppointmentController controller = fxmlLoader.getController();
                controller.initialize((ObservableList<Appointment>) appointmentsForContact.getSource(), contactSchedule.getSelectionModel().getSelectedItem());
            } catch(Exception e){
                System.out.println("Could not load update appointment form.");
                e.printStackTrace();
            }
            dialog.setResizable(true);
            dialog.show();
            contactSchedule.refresh();
        }
    }

    /** This method deletes the selected appointment from the database.
     When the user clicks the 'Delete' button, an alert prompts the user to confirm their action.
     If the user presses 'OK', the appointment is then deleted and the window is closed.
     If the user presses 'Cancel', the alert is closed and the window remains open. */
    @FXML
    private void handleDelete() {
        Appointment appointmentToDelete = contactSchedule.getSelectionModel().getSelectedItem();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Action");
        alert.setContentText("Appointment will be permanently deleted. Are you sure you want to continue?");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK){
            DBConnection.getInstance().deleteAppointment(appointmentToDelete);
        } else if(result.isPresent() && result.get() == ButtonType.CANCEL){
            alert.close();
        }

        ObservableList<Appointment> appts = (ObservableList<Appointment>) appointmentsForContact.getSource();
        appts.setAll(DBConnection.getInstance().queryAppointments());
        contactSchedule.refresh();
    }

    /** This method opens the add appointment dialog form window.
     When the user clicks the 'New' button, this dialog is loaded and the selected contact is passed as the default contact. */
    @FXML
    private void handleNewAppt() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(newButton.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/AddAppointment.fxml"));
        try{
            dialog.getDialogPane().setContent(fxmlLoader.load());
            AddApptController controller = fxmlLoader.getController();
            controller.initialize((ObservableList<Appointment>) appointmentsForContact.getSource(), null, this.contact, null);
        } catch(Exception e){
            System.out.println("Could not load add appointment form.");
            e.printStackTrace();
        }
        dialog.setResizable(true);
        dialog.showAndWait();

        contactSchedule.refresh();
    }
}
