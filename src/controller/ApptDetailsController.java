package controller;

import database.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Appointment;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/** This class controls the appointment details window.
 This class and associated window are invoked when the user chooses to open the associated details of a specified appointment. */
public class ApptDetailsController {
    @FXML
    public TableColumn<Appointment, LocalDateTime> startColumn;
    @FXML
    public TableColumn<Appointment, LocalDateTime> endColumn;
    @FXML
    private TableView<Appointment> table1;
    @FXML
    private TableView<Appointment> table2;
    @FXML
    private TableView<Appointment> table3;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;

    private Appointment selectedAppt;
    private ObservableList<Appointment> appointments = FXCollections.observableArrayList();

    /** This method initializes the appointment details window with the appropriate appointment information.
     The 'start' and 'end' time columns are formatted, and all columns in each table are set with the passed appointment's information.
     Lambda: Lambda expressions are used in my methods to set items in the three tables of the window. I used these
     expressions primarily to shorten the lines of code for readability.
     @param appointment Details shown in form is from this appointment.
     @param appointments List of all appointments from database. */
    public void initialize(Appointment appointment, ObservableList<Appointment> appointments){
        this.selectedAppt = appointment;
        this.appointments = appointments;

        table1.setItems(appointments.filtered(appointment1 -> {
            if(appointment1.getId() == selectedAppt.getId()){
                return true;
            } else return false;
        }));
        table2.setItems(appointments.filtered(appointment12 -> {
            if(appointment12.getId() == selectedAppt.getId()){
                return true;
            } else return false;
        }));
        table3.setItems(appointments.filtered(appointment13 -> {
            if(appointment13.getId() == selectedAppt.getId()){
                return true;
            } else return false;
        }));
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
                        }
                    }};
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
        table1.setMinHeight(75);
        table2.setMinHeight(75);
        table3.setMinHeight(75);
    }

    /** This method opens the update appointment window when the user presses the 'Update' button on the form.
     This method passes the appointment's information to the update appointment window to be updated. */
    @FXML
    private void handleUpdateButton() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(editButton.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/UpdateAppointment.fxml"));
        try{
            dialog.getDialogPane().setContent(fxmlLoader.load());
            UpdateAppointmentController controller = fxmlLoader.getController();
            controller.initialize(appointments, selectedAppt);
        } catch(Exception e){
            System.out.println("Could not load update appointment form.");
            e.printStackTrace();
        }
        dialog.setResizable(true);
        dialog.show();
    }

    /** This method handles deleting the appointment when the user presses the 'Delete' button.
     Before deleting, the user is prompted to confirm their action. If the user presses 'OK', the appointment is deleted from the database and the window is closed.
     If the user presses 'Cancel', the alert is closed and the window remains open. */
    @FXML
    private void handleDeleteButton() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Action");
        alert.setContentText("Appointment will be permanently deleted. Are you sure you want to continue?");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK){
            DBConnection.getInstance().deleteAppointment(selectedAppt);
        } else if(result.isPresent() && result.get() == ButtonType.CANCEL){
            alert.close();
        }

        appointments.setAll(DBConnection.getInstance().queryAppointments());
        Stage stage = (Stage) deleteButton.getScene().getWindow();
        stage.close();
    }
}
