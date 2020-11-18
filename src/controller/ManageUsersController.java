package controller;

import database.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import model.Appointment;
import model.Users;
import java.util.Optional;

/** This class controls the manage users window of the application.
 When the 'Manage Users' button is clicked by a user with administrative priveleges, this class and associated window are loaded. */
public class ManageUsersController {
    @FXML
    private Button scheduleButton;
    @FXML
    private TableView<Users> userTable;
    @FXML
    private Button newButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button deleteButton;
    private FilteredList<Appointment> userAppointments = new FilteredList<>(DBConnection.getInstance().queryAppointments());
    private Users selectedUser;
    private ObservableList<Users> users = FXCollections.observableArrayList();

    /** This method initializes the window with the user data from the database.
     A table of users is populated with all username and password data.
     The method also ensures the update and delete buttons remain disabled until a user is selected.

     Lambda: The listener for the selected item property of the user table uses a lambda expression. This shortens code
     and improves efficiency. */
    public void initialize(){
        users.setAll(DBConnection.getInstance().queryUsers());
        userTable.setItems(users);

        userTable.getSelectionModel().selectedItemProperty().addListener((observableValue, users, t1) -> {
            if(t1 != null) {
                selectedUser = t1;
                userAppointments.setPredicate(appointment -> {
                    if (appointment.getUser() == selectedUser.getUserID()) {
                        return true;
                    } else return false;
                });
            }});
        updateButton.disableProperty().bind(userTable.getSelectionModel().selectedItemProperty().isNull());
        deleteButton.disableProperty().bind(userTable.getSelectionModel().selectedItemProperty().isNull());
        scheduleButton.disableProperty().bind(userTable.getSelectionModel().selectedItemProperty().isNull());
    }

    /** This method opens the dialog form to add a new user.
     When the user clicks the 'New' button, this method is invoked and loads the new dialog window. */
    @FXML
    private void handleAddUser() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(newButton.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/AddUser.fxml"));
        try{
            dialog.getDialogPane().setContent(fxmlLoader.load());
            AddUserController controller = fxmlLoader.getController();
            controller.initialize(users);
        } catch(Exception e){
            System.out.println("Could not load new user form.");
            e.printStackTrace();
        }
        dialog.setResizable(true);
        dialog.showAndWait();
    }

    /** This method opens the dialog form to update a selected user's information.
     When a user clicks the 'Update' button, the selected user is passed into the new dialog window to initialize the form data. */
    @FXML
    private void handleUpdateUser() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(updateButton.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/UpdateUser.fxml"));
        try{
            dialog.getDialogPane().setContent(fxmlLoader.load());
            UpdateUserController controller = fxmlLoader.getController();
            controller.initialize(userTable.getSelectionModel().getSelectedItem(), users);
        } catch(Exception e){
            System.out.println("Could not load update user form.");
            e.printStackTrace();
        }
        dialog.setResizable(true);
        dialog.showAndWait();
    }

    /** This method deletes a selected user from the database.
     When a user clicks the 'Delete' button, the user is then deleted. Before deletion, the user is prompted to confirm
     their action. If the user clicks 'OK', the user is deleted from the database. If the user clicks 'Cancel', the alert is closed
     and no action is taken.

     This method also checks for any appointments associated with the user. If they have any, the user is alerted that any
     appointments must be deleted before the user can be deleted. */
    @FXML
    private void handleDeleteUser() {
        if(!userAppointments.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Cannot delete user");
            alert.setContentText("All appointments associated with this user must be deleted first. Press 'OK' to see all appointments.");
            Optional<ButtonType> result = alert.showAndWait();
            if(result.isPresent() && result.get() == ButtonType.OK){
                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.initOwner(deleteButton.getScene().getWindow());
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/view/UserAppointments.fxml"));
                try{
                    dialog.getDialogPane().setContent(fxmlLoader.load());
                    UserApptsController controller = fxmlLoader.getController();
                    controller.initialize(userAppointments, selectedUser);
                } catch(Exception e){
                    System.out.println("Could not load user appointments.");
                    e.printStackTrace();
                }
                dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
                dialog.setResizable(true);
                dialog.showAndWait();
                alert.close();
            } else if(result.isPresent() && result.get() == ButtonType.CANCEL){
                alert.close();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Action");
            alert.setContentText("User will be permanently deleted. Are you sure you want to continue?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                DBConnection.getInstance().deleteUser(userTable.getSelectionModel().getSelectedItem());
            } else if (result.isPresent() && result.get() == ButtonType.CANCEL) {
                alert.close();
            }
            users.setAll(DBConnection.getInstance().queryUsers());
            userTable.refresh();
        }
    }

    /** This method opens a dialog window showing the appointment schedule for the selected user.
     When the user clicks the 'Get Schedule' button, this method is invoked and the window is loaded with the filtered appointments and the selected user. */
    @FXML
    private void openSchedule() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(scheduleButton.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/UserAppointments.fxml"));
        try{
            dialog.getDialogPane().setContent(fxmlLoader.load());
            UserApptsController controller = fxmlLoader.getController();
            controller.initialize(userAppointments, selectedUser);
        } catch(Exception e){
            System.out.println("Could not load user appointments.");
            e.printStackTrace();
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.setResizable(true);
        dialog.showAndWait();
    }
}
