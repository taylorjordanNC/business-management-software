package controller;

import database.DBConnection;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import model.Appointment;
import model.Customer;
import model.FirstLevelDivision;
import java.util.Optional;
import java.util.function.Predicate;

/** This class controls the customers in division window.
 When the user clicks the 'Customer List' button underneath the list of divisions, this class and its associated window is loaded. */
public class CustomersInDivisionController {
    @FXML
    private Button scheduleButton;
    @FXML
    private Button newButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Label divisionLabel;
    @FXML
    private TableView<Customer> customerTable;
    @FXML
    private Customer selectedCustomer;

    private FirstLevelDivision division;
    private FilteredList<Customer> customersInDivision = new FilteredList<>(DBConnection.getInstance().queryCustomers());
    private FilteredList<Appointment> customerAppointments = new FilteredList<>(DBConnection.getInstance().queryAppointments());

    /** This method initializes the customers in division window.
     When the page is loaded, this window sets up a table of customers for the division selected from the previous window and passed into the method.

     The 'Update', 'Delete', and 'Get Schedule' buttons are disabled until a customer is selected from the list.
     This method handles creating a filtered list of customers from the division passed to the method, and also
     handles filtering the appointments depending on which customer is selected.

     Lambda: Lambda expressions are used for setting predicates of filtered lists, as well as setting listeners for
     selected item properties and focused properties within this method. Lambdas shorten length of code and aid in creating more efficient programs.
     @param selectedDivision The division selected by the user from the previous window. */
    public void initialize(FirstLevelDivision selectedDivision){
        this.division = selectedDivision;
        customersInDivision.setPredicate(customer -> {
            if(customer.getDivisionID() == selectedDivision.getId()){
                return true;
            } else return false;
        });
        divisionLabel.setText("Customers in " + selectedDivision.getName());
        customerTable.setItems(customersInDivision);

        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        scheduleButton.setDisable(true);
        customerTable.getSelectionModel().selectedItemProperty().addListener((observableValue, customer1, t1) -> {
            if(t1 != null && customerTable.isFocused()){
                updateButton.setDisable(false);
                deleteButton.setDisable(false);
                scheduleButton.setDisable(false);
            } else if (t1 == null){
                updateButton.setDisable(true);
                deleteButton.setDisable(true);
                scheduleButton.setDisable(true);
            }});
        customerTable.focusedProperty().addListener((observableValue, aBoolean, t1) -> {
            if((t1 || updateButton.isFocused() || deleteButton.isFocused()) && (customerTable.getSelectionModel().getSelectedItem() != null)){
                updateButton.setDisable(false);
                deleteButton.setDisable(false);
                scheduleButton.setDisable(false);
            } else {
                updateButton.setDisable(true);
                deleteButton.setDisable(true);
                scheduleButton.setDisable(true);
            }});

        customerTable.getSelectionModel().selectedItemProperty().addListener((observableValue, customer, t1) -> {
            if(t1 != null){
                selectedCustomer = t1;
                customerAppointments.setPredicate(appointment -> {
                    if(appointment.getCustomerId() == selectedCustomer.getCustomerID()){
                        return true;
                    } else return false;
                });
            }});
    }

    /** This method opens the dialog to add a new customer and is invoked when the user clicks the 'New' button.
     The method passes the division selected to be used as the default division in the form. */
    @FXML
    private void handleNewCustomer() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(newButton.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/AddCustomer.fxml"));
        try{
            dialog.getDialogPane().setContent(fxmlLoader.load());
            AddCustomerController controller = fxmlLoader.getController();
            controller.initialize((ObservableList<Customer>) customersInDivision.getSource(), division);
        } catch(Exception e){
            System.out.println("Could not load add customer form.");
            e.printStackTrace();
        }
        dialog.setResizable(true);
        dialog.showAndWait();
    }

    /** This method opens the dialog form to update a customer and is invoked when the user clicks the 'Update' button.
     The selected customer is passed to the update form. */
    @FXML
    private void handleUpdateCustomer() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(updateButton.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/UpdateCustomer.fxml"));
        try{
            dialog.getDialogPane().setContent(fxmlLoader.load());
            UpdateCustomerController controller = fxmlLoader.getController();
            controller.initialize((ObservableList<Customer>) customersInDivision.getSource(), customerTable.getSelectionModel().getSelectedItem());
        } catch(Exception e){
            System.out.println("Could not load update customer form.");
            e.printStackTrace();
        }
        dialog.setResizable(true);
        dialog.showAndWait();
    }

    /** This method deletes the selected customer and is invoked when the user clicks the 'Delete' button.
     Before deleting the customer, the user is prompted to confirm their action. If the user clicks 'OK', the customer
     is deleted from the database. If the user clicks 'Cancel', the alert is closed.

     This method also checks to see if the customer has any scheduled appointments in the database. If yes, the user
     is prompted with an alert that the customer cannot be deleted until all appointments are deleted. */
    @FXML
    private void handleDeleteCustomer() {
        Customer toDelete = customerTable.getSelectionModel().getSelectedItem();
        int count = 0;
        for(Appointment appt : DBConnection.getInstance().queryAppointments()){
            if(toDelete.getCustomerID() == appt.getCustomerId()){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Cannot Delete Customer");
                alert.setHeaderText("This customer is associated with existing appointments.");
                alert.setContentText("A customer with appointments cannot be deleted. Press 'OK' to see all appointments for this customer.");
                Optional<ButtonType> result = alert.showAndWait();
                if(result.isPresent() && result.get() == ButtonType.OK){
                    Dialog<ButtonType> dialog = new Dialog<>();
                    dialog.initOwner(deleteButton.getScene().getWindow());
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("/view/CustomerSchedule.fxml"));
                    try{
                        dialog.getDialogPane().setContent(fxmlLoader.load());
                        CustomerScheduleController controller = fxmlLoader.getController();
                        controller.initialize(customerAppointments, customerTable.getSelectionModel().getSelectedItem());
                    } catch(Exception e){
                        System.out.println("Could not load customer schedule.");
                        e.printStackTrace();
                    }
                    dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
                    dialog.setResizable(true);
                    dialog.showAndWait();
                } else if(result.isPresent() && result.get() == ButtonType.CANCEL){
                    alert.close();
                }
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
                ObservableList<Customer> customers = (ObservableList<Customer>) customersInDivision.getSource();
                customers.setAll(DBConnection.getInstance().queryCustomers());
                customerTable.refresh();
            } else if(result.isPresent() && result.get() == ButtonType.CANCEL){
                alert.close();
            }
        }
    }

    /** This method opens the schedule window for the selected customer and is invoked when the user clicks the 'Get Schedule' button.
     The selected customer from the table and the filtered list of appointments for the customer are passed into the schedule window. */
    @FXML
    private void openSchedule() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(scheduleButton.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/CustomerSchedule.fxml"));
        try{
            dialog.getDialogPane().setContent(fxmlLoader.load());
            CustomerScheduleController controller = fxmlLoader.getController();
            controller.initialize(customerAppointments, selectedCustomer);
        } catch(Exception e){
            System.out.println("Could not load user appointments.");
            e.printStackTrace();
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.setResizable(true);
        dialog.showAndWait();
    }
}
