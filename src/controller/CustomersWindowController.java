package controller;

import database.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Appointment;
import model.Customer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

/** This class controls the customers window.
 When the user clicks the 'Customers' button within the navigation menu, this window is loaded. */
public class CustomersWindowController {
    @FXML
    private Button newButtonAppt;
    @FXML
    private Button customersButton;
    @FXML
    private Button updateApptButton;
    @FXML
    private Button deleteApptButton;
    @FXML
    private TableColumn<Appointment, LocalDateTime> startColumn;
    @FXML
    private TableColumn<Appointment, LocalDateTime> endColumn;
    @FXML
    private TableView<Appointment> customerSchedule;
    @FXML
    private TableView<Customer> customerTable;
    @FXML
    private Button calendarButton;
    @FXML
    private Button homeButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Button newButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button deleteButton;

    private ObservableList<Customer> customersList = FXCollections.observableArrayList();
    private ObservableList<Appointment> appointments = FXCollections.observableArrayList();
    private FilteredList<Appointment> customerAppointments = new FilteredList<>(appointments);

    /** This method initializes the customers window.
     When the page is loaded, this method handles setting up a table of customers and their associated appointments.
     The method ensures that the update and delete buttons are disabled unless there is an appropriate item selected.

     Lambda: Lambda expressions are used when setting selected item property and focused property listeners for the tables in the window.
     I used lambda expressions within the method to shorten code and reduce repetition of method calls. */
    public void initialize(){
        homeButton.setMaxWidth(Double.MAX_VALUE);
        calendarButton.setMaxWidth(Double.MAX_VALUE);
        customersButton.setMaxWidth(Double.MAX_VALUE);
        logoutButton.setMaxWidth(Double.MAX_VALUE);

        customersList.addAll(DBConnection.getInstance().queryCustomers());
        customerTable.setItems(customersList);
        customerTable.refresh();
        appointments.setAll(DBConnection.getInstance().queryAppointments());

        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        customerTable.getSelectionModel().selectedItemProperty().addListener((observableValue, customer1, t1) -> {
            if(t1 != null && customerTable.isFocused()){
                updateButton.setDisable(false);
                deleteButton.setDisable(false);
            } else if (t1 == null){
                updateButton.setDisable(true);
                deleteButton.setDisable(true);
            }
        });
        customerTable.focusedProperty().addListener((observableValue, aBoolean, t1) -> {
            if((t1 || updateButton.isFocused() || deleteButton.isFocused()) && (customerTable.getSelectionModel().getSelectedItem() != null)){
                updateButton.setDisable(false);
                deleteButton.setDisable(false);
            } else {
                updateButton.setDisable(true);
                deleteButton.setDisable(true);
            }
        });
        updateApptButton.setDisable(true);
        deleteApptButton.setDisable(true);
        customerSchedule.getSelectionModel().selectedItemProperty().addListener((observableValue, appt1, t1) -> {
            if(t1 != null && customerSchedule.isFocused()){
                updateButton.setDisable(false);
                deleteButton.setDisable(false);
            } else if (t1 == null){
                updateButton.setDisable(true);
                deleteButton.setDisable(true);
            }
        });
        customerSchedule.focusedProperty().addListener((observableValue, aBoolean, t1) -> {
            if(t1 || updateApptButton.isFocused() || deleteApptButton.isFocused()){
                updateApptButton.setDisable(false);
                deleteApptButton.setDisable(false);
            } else {
                updateApptButton.setDisable(true);
                deleteApptButton.setDisable(true);
            }
        });
        customerTable.getSelectionModel().selectedItemProperty().addListener((observableValue, customer1, cust2) -> {
            if(cust2 != null){
                customerAppointments.setPredicate(appointment -> {
                    for(Customer customer : customersList){
                        if(cust2 == customer){
                            int id = customer.getCustomerID();
                            if(id == appointment.getCustomerId()){
                                    return true;
                            } else return false;
                        }
                    }
                    return false;
                });
            }
            customerSchedule.setItems(customerAppointments);
        });

        customerSchedule.setOnMouseClicked(new EventHandler<>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if ((customerSchedule.getSelectionModel().getSelectedItem() != null) &&
                        (mouseEvent.getClickCount() == 2)) {
                    Dialog<ButtonType> dialog = new Dialog<>();
                    dialog.initOwner(customerSchedule.getScene().getWindow());
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("/view/ApptDetails.fxml"));
                    try {
                        dialog.getDialogPane().setContent(fxmlLoader.load());
                        ApptDetailsController controller = fxmlLoader.getController();
                        controller.initialize(customerSchedule.getSelectionModel().getSelectedItem(), appointments);
                    } catch (Exception e) {
                        System.out.println("Could not load appointment details form.");
                        e.printStackTrace();
                    }
                    dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
                    dialog.setResizable(true);
                    dialog.showAndWait();
                }}});

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
    }

    /** This method opens the dialog window to add a new customer.
     When the user clicks the 'New' button associated with the table of customers, this method is invoked. */
    @FXML
    private void openNewCustomerDialog(){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(newButton.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/AddCustomer.fxml"));
        try{
            dialog.getDialogPane().setContent(fxmlLoader.load());
            AddCustomerController controller = fxmlLoader.getController();
            controller.initialize(customersList, null);
        } catch(Exception e){
            System.out.println("Could not load add customer form.");
            e.printStackTrace();
        }
        dialog.setResizable(true);
        dialog.showAndWait();
    }

    /** This method opens the dialog window to update a customer.
     When the user clicks the 'Update' button associated with the table of customers,
     this method is invoked and the selected customer is passed to the new window. */
    @FXML
    private void openUpdateCustomerDialog(){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(updateButton.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/UpdateCustomer.fxml"));
        try{
            dialog.getDialogPane().setContent(fxmlLoader.load());
            UpdateCustomerController controller = fxmlLoader.getController();
            controller.initialize(customersList, customerTable.getSelectionModel().getSelectedItem());
        } catch(Exception e){
            System.out.println("Could not load update customer form.");
            e.printStackTrace();
        }
        dialog.setResizable(true);
        dialog.showAndWait();
    }

    /** This method deletes a customer from the database.
     When the user clicks the 'Delete' button associated with the table of customers,
     this method is invoked. Before deletion, the user is prompted to confirm their action. If the user clicks 'OK',
     the customer is deleted from the database. If the user clicks 'Cancel', the alert is closed.

     The method also checks if the customer has any scheduled appointments. If yes, the user is alerted that the customer
     may not be deleted until all associated appointments are deleted. */
    @FXML
    private void deleteCustomer(){
        Customer toDelete = customerTable.getSelectionModel().getSelectedItem();
        int count = 0;
        for(Appointment appt : appointments){
            if(toDelete.getCustomerID() == appt.getCustomerId()){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Cannot Delete Customer");
                alert.setHeaderText("This customer is associated with existing appointments.");
                alert.setContentText("A customer with appointments cannot be deleted.");
                alert.showAndWait();
                count++;
                break;
            }
        }
        if(count == 0){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Action");
            alert.setContentText("Your deletion will be permanent. Are you sure you wish to proceed?");
            Optional<ButtonType> result = alert.showAndWait();
            if(result.isPresent() && result.get() == ButtonType.OK){
                DBConnection.getInstance().deleteCustomer(toDelete);
                customersList.setAll(DBConnection.getInstance().queryCustomers());
            } else if(result.isPresent() && result.get() == ButtonType.CANCEL){
                alert.close();
            }
        }
    }

    /** This method handles opening the update appointment form dialog window.
     When the user presses the 'Update' button associated with the table of customer appointments, the dialog window is loaded and
     the selected appointment and full list of appointments are passed to the new window. */
    @FXML
    private void handleUpdateAppt() {
        if(customerSchedule.getSelectionModel().getSelectedItem() != null){
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.initOwner(updateApptButton.getScene().getWindow());
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/view/UpdateAppointment.fxml"));
            try{
                dialog.getDialogPane().setContent(fxmlLoader.load());
                UpdateAppointmentController controller = fxmlLoader.getController();
                controller.initialize(appointments, customerSchedule.getSelectionModel().getSelectedItem());
            } catch(Exception e){
                System.out.println("Could not load update appointment form.");
                e.printStackTrace();
            }
            dialog.setResizable(true);
            dialog.show();
        }
    }

    /** This method handles deleting a selected appointment.
     When the user presses the 'Delete' button, an alert opens prompting the user to confirm action. If the user
     presses 'OK', the appointment is deleted from the database. If the user presses 'Cancel', the alert closes. */
    @FXML
    private void handleDeleteAppt() {
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
        appointments.setAll(DBConnection.getInstance().queryAppointments());
        customerSchedule.refresh();
    }

    /** This method handles opening the add appointment dialog window.
     When the user hits the 'New' button associated with the table of customer appointments, the dialog window is loaded,
     and the selected customer is passed into the new window as the default customer selection. */
    @FXML
    private void handleAddAppt() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(newButtonAppt.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/AddAppointment.fxml"));
        try{
            dialog.getDialogPane().setContent(fxmlLoader.load());
            AddApptController controller = fxmlLoader.getController();
            controller.initialize(appointments, customerTable.getSelectionModel().getSelectedItem(), null, null);
        } catch(Exception e){
            System.out.println("Could not load add appointment form.");
            e.printStackTrace();
        }
        dialog.setResizable(true);
        dialog.showAndWait();
    }

    /** This method handles the opening of the main/home window.
     When the user presses the 'Home' button in the navigation menu, the scene is reset to show the main window. */
    @FXML
    private void handleHomeButton(){
        try{
            Parent root = FXMLLoader.load(getClass().getResource("/view/MainWindow.fxml"));
            homeButton.getScene().setRoot(root);
        } catch(Exception e){
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Could not load main page.");
            alert.showAndWait();
        }
    }

    /** This method handles the opening of the calendar window.
     When the user presses the 'Calendar' button in the navigation menu, the scene is reset to show the calendar window. */
    @FXML
    private void handleCalendarButton(){
        try{
            Parent root = FXMLLoader.load(getClass().getResource("/view/CalendarWindow.fxml"));
            calendarButton.getScene().setRoot(root);
        } catch(Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Something went wrong. Unable to load calendar page.");
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    /** This method logs the user out of the application and returns the user to the log in form.
     When the user presses the 'Log Out' button, an alert prompts the user to confirm the action of logging out.
     If the user presses 'OK', the user is logged out and login form is again shown. If the user presses 'Cancel',
     the alert closes. */
    @FXML
    private void handleLogoutButton(){
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Logging Out");
        confirm.setContentText("Are you sure you want to log out?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Stage primaryStage = new Stage();
                Parent root = FXMLLoader.load(getClass().getResource("/view/LoginForm.fxml"), ResourceBundle.getBundle("/LanguageRB", Locale.getDefault()));
                primaryStage.setScene(new Scene(root, 300, 275));
                primaryStage.show();
                Stage stage = (Stage) logoutButton.getScene().getWindow();
                stage.close();

                MainWindowController.resetAppointmentAlert();
            } catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Could not log out of application.");
                alert.showAndWait();
            }
        } else if(result.isPresent() && result.get() == ButtonType.CANCEL){
            confirm.close();
        }
    }
}
