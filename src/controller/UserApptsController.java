package controller;

import database.DBConnection;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import model.Appointment;
import model.Users;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/** This class controls the user appointments window.
 When the user clicks the 'Get Schedule' button associated with the table of users, this window and its controller is called. */
public class UserApptsController {
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
    @FXML
    private Label userApptsLabel;
    @FXML
    private TableView<Appointment> appointmentTable;

    private FilteredList<Appointment> userAppts = new FilteredList<>(DBConnection.getInstance().queryAppointments());
    private Users selectedUser;

    /** This method initializes the user schedule form.
     When invoked, this method sets up the table of appointments associated with the selected user passed into the method.
     This method sets cell factories for the start time and end time columns to properly format the times, and also
     adds listeners to ensure that the update and delete buttons remain disabled until there is an appointment selected.
     @param user The selected user from the users table.
     @param userAppts The filtered list of appointments for the selected user. */
    public void initialize(FilteredList<Appointment> userAppts, Users user){
        this.userAppts = userAppts;
        selectedUser = user;
        userApptsLabel.setText("Appointments for " + user.getUserName());
        startColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Appointment, LocalDateTime> call(TableColumn<Appointment, LocalDateTime> appointmentLocalDateTimeTableColumn) {
                return new TableCell<>(){
                    @Override
                    protected void updateItem(LocalDateTime localDateTime, boolean empty) {
                        super.updateItem(localDateTime, empty);
                        if(localDateTime == null || empty){
                            setText(null);
                            setStyle("");
                        } else {
                            setText(DateTimeFormatter.ofPattern("LLL d, yyyy @ h:mm a").format(localDateTime));
                        }}};
            }});
        endColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Appointment, LocalDateTime> call(TableColumn<Appointment, LocalDateTime> appointmentLocalDateTimeTableColumn) {
                return new TableCell<>(){
                    @Override
                    protected void updateItem(LocalDateTime localDateTime, boolean empty) {
                        super.updateItem(localDateTime, empty);
                        if(localDateTime == null || empty){
                            setText(null);
                            setStyle("");
                        } else {
                            setText(DateTimeFormatter.ofPattern("LLL d, yyyy @ h:mm a").format(localDateTime));
                        }}};
            }});
        appointmentTable.setItems(userAppts);
        appointmentTable.setOnMouseClicked(new EventHandler<>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if((appointmentTable.getSelectionModel().getSelectedItem() != null) && (mouseEvent.getClickCount() == 2)){
                    Dialog<ButtonType> dialog = new Dialog<>();
                    dialog.initOwner(appointmentTable.getScene().getWindow());
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("/view/ApptDetails.fxml"));
                    try {
                        dialog.getDialogPane().setContent(fxmlLoader.load());
                        ApptDetailsController controller = fxmlLoader.getController();
                        controller.initialize(appointmentTable.getSelectionModel().getSelectedItem(), userAppts);
                    } catch (Exception e) {
                        System.out.println("Could not load appointment details form.");
                        e.printStackTrace();
                    }
                    dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
                    dialog.setResizable(true);
                    dialog.showAndWait();
                }}
        });
        updateButton.disableProperty().bind(appointmentTable.getSelectionModel().selectedItemProperty().isNull());
        deleteButton.disableProperty().bind(appointmentTable.getSelectionModel().selectedItemProperty().isNull());
    }

    /** This method opens the add appointment dialog form window.
     When the user clicks the 'New' button, this dialog is loaded and the selected user is passed as the default user. */
    @FXML
    private void addNewAppt() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(newButton.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/AddAppointment.fxml"));
        try{
            dialog.getDialogPane().setContent(fxmlLoader.load());
            AddApptController controller = fxmlLoader.getController();
            controller.initialize((ObservableList<Appointment>) userAppts.getSource(), null, null, selectedUser);
        } catch(Exception e){
            System.out.println("Could not load add appointment form.");
            e.printStackTrace();
        }
        dialog.setResizable(true);
        dialog.showAndWait();
    }

    /** This method opens the update appointment dialog window.
     When the 'Update' button is clicked, this window is loaded and is passed the filtered list of appointments
     as well as the selected appointment to be updated. */
    @FXML
    private void updateAppt() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(updateButton.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/UpdateAppointment.fxml"));
        try{
            dialog.getDialogPane().setContent(fxmlLoader.load());
            UpdateAppointmentController controller = fxmlLoader.getController();
            controller.initialize((ObservableList<Appointment>) userAppts.getSource(), appointmentTable.getSelectionModel().getSelectedItem());
        } catch(Exception e){
            System.out.println("Could not load update appointment form.");
            e.printStackTrace();
        }
        dialog.setResizable(true);
        dialog.show();
    }

    /** This method deletes the selected appointment from the database.
     When the user clicks the 'Delete' button, an alert prompts the user to confirm their action.
     If the user presses 'OK', the appointment is then deleted and the window is closed.
     If the user presses 'Cancel', the alert is closed and the window remains open. */
    @FXML
    private void deleteAppt() {
        Appointment appointmentToDelete = appointmentTable.getSelectionModel().getSelectedItem();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Action");
        alert.setContentText("Appointment will be permanently deleted. Are you sure you want to continue?");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK){
            DBConnection.getInstance().deleteAppointment(appointmentToDelete);
        } else if(result.isPresent() && result.get() == ButtonType.CANCEL){
            alert.close();
        }

        ObservableList<Appointment> appts = (ObservableList<Appointment>) userAppts.getSource();
        appts.setAll(DBConnection.getInstance().queryAppointments());
        appointmentTable.refresh();
    }
}
