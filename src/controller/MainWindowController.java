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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

/** This class controls the main/home page of the application and is loaded when the user logs in successfully,
 or when the user clicks the 'Home' button on the navigation menu. */
public class MainWindowController {
    @FXML
    private Label welcomeLabel;
    @FXML
    private Button homeButton;
    @FXML
    private GridPane calendarGridPane;
    @FXML
    private Button getCustomersButton;
    @FXML
    private Button getScheduleButton;
    @FXML
    private VBox adminControls;
    @FXML
    private Button adminButton;
    @FXML
    private ComboBox<Country> countryComboBox;
    @FXML
    private ListView<FirstLevelDivision> divisionList;
    @FXML
    private Label today;
    @FXML
    private ListView<Appointment> todaysAppts;
    @FXML
    private Label tomorrow;
    @FXML
    private ListView<Appointment> tomorrowsAppts;
    @FXML
    private Label day3;
    @FXML
    private ListView<Appointment> day3Appts;
    @FXML
    private Label day4;
    @FXML
    private ListView<Appointment> day4Appts;
    @FXML
    private Button calendarButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Button customersButton;
    @FXML
    private Button addContactButton;
    @FXML
    private Button updateContactButton;
    @FXML
    private Button deleteContactButton;
    @FXML
    private TableView<Contacts> contactsTable;
    @FXML
    private Button editUserButton;
    @FXML
    private AnchorPane buttonAP;

    private static AtomicBoolean atomicBoolean = new AtomicBoolean();

    private static Users userLoggedIn;
    private ObservableList<Appointment> appointments = FXCollections.observableArrayList();
    private FilteredList<Appointment> todayList = new FilteredList<>(appointments);
    private FilteredList<Appointment> tomorrowList = new FilteredList<>(appointments);
    private FilteredList<Appointment> day3List = new FilteredList<>(appointments);
    private FilteredList<Appointment> day4List = new FilteredList<>(appointments);

    private ObservableList<Contacts> contacts = FXCollections.observableArrayList();
    private ObservableList<ListView<Appointment>> listViews = FXCollections.observableArrayList();

    /** This method initializes the main/home page of the application.
     Whenever the main window is loaded, this method handles the sizing of various nodes within the page. The method
     also sets up an abbreviated calendar view so the user can see any upcoming appointments taking place in the next four days.

     The method also initializes a table view of contacts as well as a list of divisions that can be sorted by country.

     Lambda: The selected item property listener methods and a mouse event method within the initialize method implement
     a lambda expression. This reduces the code and repetition of the method calls and de-clutters the method overall. */
    public void initialize(){
        countryComboBox.setMaxWidth(Double.MAX_VALUE);
        calendarGridPane.setMaxWidth(Double.MAX_VALUE);
        homeButton.setMaxWidth(Double.MAX_VALUE);
        calendarButton.setMaxWidth(Double.MAX_VALUE);
        customersButton.setMaxWidth(Double.MAX_VALUE);
        logoutButton.setMaxWidth(Double.MAX_VALUE);
        contactsTable.setMaxWidth(Double.MAX_VALUE);
        buttonAP.setMaxWidth(Double.MAX_VALUE);

        userLoggedIn = LoginFormController.getUser();
        if(userLoggedIn.getUserName().equals("admin")){
            adminControls.setVisible(true);
        }
        welcomeLabel.setText("Welcome,  " + userLoggedIn.getUserName() + "!");
        this.appointments.setAll(DBConnection.getInstance().queryAppointments());

        setUpcomingCalendar();
        setCountryComboBoxAndDivisions();

        countryComboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, country, t1) -> {
            if(t1 != null){
                int id = t1.getId();
                divisionList.setItems(DBConnection.getInstance().queryFLDByCountryID(id));
            }});
        contacts.setAll(DBConnection.getInstance().queryContacts());
        contactsTable.setItems(contacts);
        updateContactButton.setDisable(true);
        deleteContactButton.setDisable(true);
        getScheduleButton.setDisable(true);

        contactsTable.getSelectionModel().selectedItemProperty().addListener((observableValue, contacts, t1) -> {
            if(t1 != null && contactsTable.isFocused()){
                updateContactButton.setDisable(false);
                deleteContactButton.setDisable(false);
                getScheduleButton.setDisable(false);
            } else if (t1 == null){
                updateContactButton.setDisable(true);
                deleteContactButton.setDisable(true);
                getScheduleButton.setDisable(true);
            }
        });
        contactsTable.focusedProperty().addListener((observableValue, aBoolean, t1) -> {
            if((t1 || updateContactButton.isFocused() || deleteContactButton.isFocused() || getScheduleButton.isFocused()) && (contactsTable.getSelectionModel().getSelectedItem() != null)){
                    updateContactButton.setDisable(false);
                    deleteContactButton.setDisable(false);
                    getScheduleButton.setDisable(false);
            } else {
                updateContactButton.setDisable(true);
                deleteContactButton.setDisable(true);
                getScheduleButton.setDisable(true);
            }
        });
        contactsTable.setOnMouseClicked(new EventHandler<>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if ((contactsTable.getSelectionModel().getSelectedItem() != null) && (mouseEvent.getClickCount() == 2)) {
                    Dialog<ButtonType> dialog = new Dialog<>();
                    dialog.initOwner(contactsTable.getScene().getWindow());
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("/view/ContactSchedule.fxml"));
                    try {
                        dialog.getDialogPane().setContent(fxmlLoader.load());
                        ContactScheduleController controller = fxmlLoader.getController();
                        controller.initialize(contactsTable.getSelectionModel().getSelectedItem());
                    } catch (Exception e) {
                        System.out.println("Could not load contact schedule.");
                        e.printStackTrace();
                    }
                    dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
                    dialog.setResizable(true);
                    dialog.showAndWait();
                }}
        });
        getCustomersButton.setDisable(true);
        divisionList.getSelectionModel().selectedItemProperty().addListener((observableValue, d1, d2) -> {
            if(d2 != null && (divisionList.isFocused() || getCustomersButton.isFocused())){
                getCustomersButton.setDisable(false);
            } else getCustomersButton.setDisable(true);
        });
        divisionList.focusedProperty().addListener((observableValue, aBoolean, t1) -> {
            if((t1 || getCustomersButton.isFocused()) && (divisionList.getSelectionModel().getSelectedItem() != null)){
                getCustomersButton.setDisable(false);
            } else getCustomersButton.setDisable(true);
        });
        divisionList.setOnMouseClicked(mouseEvent -> {
            if((divisionList.getSelectionModel().getSelectedItem() != null) && (mouseEvent.getClickCount() == 2)){
                handleGetCustomers();
            }
        });

        listViews.addAll(todaysAppts, tomorrowsAppts, day3Appts, day4Appts);
        for(ListView<Appointment> lv : listViews){
            lv.setOnMouseClicked(new EventHandler<>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if ((mouseEvent.getClickCount() == 2) && (lv.getSelectionModel().getSelectedItem() != null)) {
                        Dialog<ButtonType> dialog = new Dialog<>();
                        dialog.initOwner(lv.getScene().getWindow());
                        FXMLLoader fxmlLoader = new FXMLLoader();
                        fxmlLoader.setLocation(getClass().getResource("/view/ApptDetails.fxml"));
                        try {
                            dialog.getDialogPane().setContent(fxmlLoader.load());
                            ApptDetailsController controller = fxmlLoader.getController();
                            controller.initialize(lv.getSelectionModel().getSelectedItem(), appointments);
                        } catch (Exception e) {
                            System.out.println("Could not load appointment details form.");
                            e.printStackTrace();
                        }
                        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
                        dialog.setResizable(true);
                        dialog.showAndWait();
                    }
                }});
            if(!atomicBoolean.get()) {
                appointmentAlert();
            }
        }
    }

    /** This method is called by the initialize method in order to set the data for the combo box of countries and combo box of divisions.
     The combo boxes are formatted with readable information for the user from the database, and the selection of divisions is
     filtered based on the selection of country. */
    private void setCountryComboBoxAndDivisions() {
        countryComboBox.setItems(DBConnection.getInstance().queryCountries());
        countryComboBox.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Country> call(ListView<Country> countryListView) {
                return new ListCell<>() {
                    private final Label label;
                    { setContentDisplay(ContentDisplay.CENTER);
                        label = new Label(); }
                    @Override
                    protected void updateItem(Country item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            label.setText(item.getName());
                            setGraphic(label);
                        }
                    }};
            }});
        countryComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Country c, boolean b) {
                super.updateItem(c, b);
                if (b) {
                    setText("");
                } else {
                    setText(c.getName());
                }}
        });

        divisionList.setCellFactory(new Callback<>() {
            @Override
            public ListCell<FirstLevelDivision> call(ListView<FirstLevelDivision> FLDListView) {
                return new ListCell<>() {
                    private final Label label;
                    { setContentDisplay(ContentDisplay.CENTER);
                        label = new Label(); }
                    @Override
                    protected void updateItem(FirstLevelDivision item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            label.setText("ID: " + item.getId() + "  Name: " + item.getName());
                            setGraphic(label);
                        }}};
            }});
    }

    /** This method is called by the initialize method and alerts the user whether or not they have any appointments within the next fifteen minutes.
     If the user does have an appointment, the alert message will allow the user to see the details of the appointment if they wish.
     If the user has no appointment, the alert message tells the user they have no upcoming appointments. */
    private void appointmentAlert(){
        atomicBoolean.getAndSet(true);
        Appointment upcomingAppt = null;
        for(Appointment appt : appointments){
            LocalTime startTime = appt.getStart().toLocalTime();
            LocalTime now = LocalTime.now();
            LocalDate today = LocalDate.now();
            if((appt.getUser() == userLoggedIn.getUserID()) && (appt.getStart().toLocalDate().equals(today)) && (startTime.isAfter(now) || startTime.equals(now)) &&
                    (startTime.isBefore(now.plusMinutes(15)) || startTime.equals(now.plusMinutes(15)))){
                upcomingAppt = appt;
            }
        }
        if(upcomingAppt == null){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("");
            alert.setHeaderText("No Upcoming Appointments");
            alert.setContentText("You have no appointments within the next fifteen minutes.");
            alert.showAndWait();
        } else {
            Alert alert1 = new Alert(Alert.AlertType.CONFIRMATION);
            alert1.setTitle("Upcoming Appointment");
            alert1.setHeaderText("You have the following appointment in the next 15 minutes: ");
            alert1.setContentText(upcomingAppt.getId() + " on " + upcomingAppt.getStart().format(DateTimeFormatter.ofPattern("LLL d, yyyy @ h:mm a")) + ". Press 'OK' to see details.");
            Optional<ButtonType> result = alert1.showAndWait();

            if(result.isPresent() && result.get() == ButtonType.OK){
                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.initOwner(alert1.getOwner());
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/view/ApptDetails.fxml"));
                try{
                    dialog.getDialogPane().setContent(fxmlLoader.load());
                    ApptDetailsController controller = fxmlLoader.getController();
                    controller.initialize(upcomingAppt, appointments);
                } catch(Exception e){
                    System.out.println("Could not load appointment details.");
                    e.printStackTrace();
                }
                dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
                dialog.setResizable(true);
                dialog.showAndWait();
                alert1.close();
            } else if(result.isPresent() && result.get() == ButtonType.CANCEL){
                alert1.close();
            }
        }
    }

    /** This method is called by the initialize method and sets the data for the calendar view showing the appointments for the next four days.
     Each day has its own list view and is populated with a filtered list of appointments for that day from the database. If there are no appointments,
     a message is shown accordingly.

     Lambda: The methods setting the predicates for the filtered list of the calendar list views all use lambda expressions.
     This greatly reduced repetition and streamlined code. */
    private void setUpcomingCalendar() {
        today.setText("Today," + LocalDate.now().format(DateTimeFormatter.ofPattern(" MM/d")));
        tomorrow.setText("Tomorrow," + LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern(" MM/d")));
        day3.setText((LocalDate.now().plusDays(2).format(DateTimeFormatter.ofPattern("EEEE"))) + LocalDate.now().plusDays(2).format(DateTimeFormatter.ofPattern(" MM/d")));
        day4.setText((LocalDate.now().plusDays(3).format(DateTimeFormatter.ofPattern("EEEE"))) + LocalDate.now().plusDays(3).format(DateTimeFormatter.ofPattern(" MM/d")));
        todaysAppts.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Appointment> call(ListView<Appointment> apptListView) {
                return new ListCell<>() {
                    private final Label label;
                    { setContentDisplay(ContentDisplay.CENTER);
                        label = new Label(); }
                    @Override
                    protected void updateItem(Appointment item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            label.setText(item.getStart().format(DateTimeFormatter.ofPattern("h:mm a")) + " : " + item.getTitle());
                            setGraphic(label);
                        }}};
            }});
        tomorrowsAppts.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Appointment> call(ListView<Appointment> apptListView) {
                return new ListCell<>() {
                    private final Label label;
                    { setContentDisplay(ContentDisplay.CENTER);
                        label = new Label(); }
                    @Override
                    protected void updateItem(Appointment item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            label.setText(item.getStart().format(DateTimeFormatter.ofPattern("h:mm a")) + " : " + item.getTitle());
                            setGraphic(label);
                        }}};
            }});
        day3Appts.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Appointment> call(ListView<Appointment> apptListView) {
                return new ListCell<>() {
                    private final Label label;
                    { setContentDisplay(ContentDisplay.CENTER);
                        label = new Label(); }
                    @Override
                    protected void updateItem(Appointment item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            label.setText(item.getStart().format(DateTimeFormatter.ofPattern("h:mm a")) + " : " + item.getTitle());
                            setGraphic(label);
                        }}};
            }});
        day4Appts.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Appointment> call(ListView<Appointment> apptListView) {
                return new ListCell<>() {
                    private final Label label;
                    { setContentDisplay(ContentDisplay.CENTER);
                        label = new Label(); }
                    @Override
                    protected void updateItem(Appointment item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            label.setText(item.getStart().format(DateTimeFormatter.ofPattern("h:mm a")) + " : " + item.getTitle());
                            setGraphic(label);
                        }}};
            }});
        todayList.setPredicate(appointment -> appointment.getStart().toLocalDate().equals(LocalDate.now()));
        todaysAppts.setPlaceholder(new Label("No Appointments"));
        todaysAppts.setItems(todayList);

        tomorrowList.setPredicate(appointment -> appointment.getStart().toLocalDate().equals(LocalDate.now().plusDays(1)));
        tomorrowsAppts.setItems(tomorrowList);
        tomorrowsAppts.setPlaceholder(new Label("No Appointments"));

        day3List.setPredicate(appointment -> appointment.getStart().toLocalDate().equals(LocalDate.now().plusDays(2)));
        day3Appts.setItems(day3List);
        day3Appts.setPlaceholder(new Label("No Appointments"));

        day4List.setPredicate(appointment -> appointment.getStart().toLocalDate().equals(LocalDate.now().plusDays(3)));
        day4Appts.setItems(day4List);
        day4Appts.setPlaceholder(new Label("No Appointments"));
    }

    /** This method is called from anywhere in the application when the information of the current user logged in is required and returns the user.
     @return Current user logged in. */
    public static Users getUserLoggedIn() {
        return userLoggedIn;
    }

    /** This method opens the form to add a new contact.
     This method is called when the user clicks the 'New' button associated with the table of contacts. */
    @FXML
    private void handleAddContact() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(addContactButton.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/AddContact.fxml"));
        try{
            dialog.getDialogPane().setContent(fxmlLoader.load());
            AddContactController controller = fxmlLoader.getController();
            controller.initialize(contacts);
        } catch(Exception e){
            System.out.println("Could not load new contact form.");
            e.printStackTrace();
        }
        dialog.setResizable(true);
        dialog.showAndWait();
    }

    /** This method opens the form to update a contact.
     The method takes the selected item from the contact table and passes it to the update form to initialize the data.
     This method is invoked when the 'Update' button is clicked by the table of contacts. */
    @FXML
    private void handleUpdateContact() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(updateContactButton.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/UpdateContact.fxml"));
        try{
            dialog.getDialogPane().setContent(fxmlLoader.load());
            UpdateContactController controller = fxmlLoader.getController();
            controller.initialize(contacts, contactsTable.getSelectionModel().getSelectedItem());
        } catch(Exception e){
            System.out.println("Could not load update contact form.");
            e.printStackTrace();
        }
        dialog.setResizable(true);
        dialog.showAndWait();
    }

    /** This method deletes a selected contact from the database.
     When the 'Delete' button is clicked, this method is invoked and the contact selected in the table is deleted.
     Before deletion, the user is prompted to confirm the action. If the user clicks 'OK', the contact is deleted from the database.
     If the user clicks 'Cancel', the alert is closed and no action is taken. */
    @FXML
    private void handleDeleteContact() {
        Contacts contactToDelete = contactsTable.getSelectionModel().getSelectedItem();
        int count = 0;
        for(Appointment appt : appointments){
            if(appt.getContact() == contactToDelete.getContactID()){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("This contact is associated with existing appointments.");
                alert.setContentText("A contact with appointments cannot be deleted. Press 'OK' to see all appointments for this contact.");

                Optional<ButtonType> result = alert.showAndWait();
                if(result.isPresent() && result.get() == ButtonType.OK){
                    Dialog<ButtonType> dialog = new Dialog<>();
                    dialog.initOwner(deleteContactButton.getScene().getWindow());
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("/view/ContactSchedule.fxml"));
                    try{
                        dialog.getDialogPane().setContent(fxmlLoader.load());
                        ContactScheduleController controller = fxmlLoader.getController();
                        controller.initialize(contactToDelete);
                    } catch(Exception e){
                        System.out.println("Could not load contact schedule.");
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
        if(count == 0) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Action");
            alert.setContentText("Contact will be permanently deleted. Are you sure you want to continue?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                DBConnection.getInstance().deleteContact(contactToDelete);
            } else if (result.isPresent() && result.get() == ButtonType.CANCEL) {
                alert.close();
            }
            contacts.setAll(DBConnection.getInstance().queryContacts());
            contactsTable.refresh();
        }
    }

    /** This method updates a specific user's information.
     When the 'Edit Username/Password' button is clicked, this method is invoked.
     The current user logged in is passed into the dialog window to be edited. The user may only edit their own information if
     they are not the administrator. */
    @FXML
    private void handleUpdateUser() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(editUserButton.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/UpdateUser.fxml"));
        try{
            dialog.getDialogPane().setContent(fxmlLoader.load());
            UpdateUserController controller = fxmlLoader.getController();
            controller.initialize(userLoggedIn, DBConnection.getInstance().queryUsers());
        } catch(Exception e){
            System.out.println("Could not load edit user form.");
            e.printStackTrace();
        }
        dialog.setResizable(true);
        dialog.showAndWait();
    }

    /** This method is only available to a user with administrative privileges and opens a list of all users in the database with all of their information.
     When the user clicks the 'Manage Users' button, this method is invoked. */
    @FXML
    private void handleUserDialog(){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(adminButton.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/ManageUsers.fxml"));
        try{
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch(Exception e){
            System.out.println("Could not load edit user form.");
            e.printStackTrace();
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.setResizable(true);
        dialog.showAndWait();
    }

    /** This method opens the schedule window for the selected contact from the contact table.
     This method is invoked when a user selects a contact and clicks the 'Get Schedule' button. The selected contact
     is then passed to the new dialog window. */
    @FXML
    private void getContactSchedule() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(contactsTable.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/ContactSchedule.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
            ContactScheduleController controller = fxmlLoader.getController();
            controller.initialize(contactsTable.getSelectionModel().getSelectedItem());
        } catch (Exception e) {
            System.out.println("Could not load contact schedule.");
            e.printStackTrace();
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.setResizable(true);
        dialog.showAndWait();
    }

    /** This method opens a dialog showing the list of customers for the selected division.
     When the 'Customer List' button is clicked, this method is called and passes the selected division into the new window to filter the customers accordingly. */
    @FXML
    private void handleGetCustomers() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(divisionList.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/CustomersInDivision.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
            CustomersInDivisionController controller = fxmlLoader.getController();
            controller.initialize(divisionList.getSelectionModel().getSelectedItem());
        } catch (Exception e) {
            System.out.println("Could not load customer list.");
            e.printStackTrace();
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.setResizable(true);
        dialog.showAndWait();
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

    /** This method handles the opening of the customer window.
     When the user presses the 'Customers' button in the navigation menu, the scene is reset to show the customer window. */
    @FXML
    private void handleCustomersButton(){
        try{
            Parent root = FXMLLoader.load(getClass().getResource("/view/CustomersWindow.fxml"));
            customersButton.getScene().setRoot(root);
        } catch(Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Something went wrong. Unable to load customers page.");
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    /** This method logs the user out of the application and returns the user to the log in form.
     When the user presses the 'Log Out' button, an alert prompts the user to confirm the action of logging out.
     If the user presses 'OK', the user is logged out and login form is again shown. If the user presses 'Cancel',
     the alert closes. */
    @FXML
    private void handleLogout() {
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

                resetAppointmentAlert();
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

    public static void resetAppointmentAlert(){
        atomicBoolean.getAndSet(false);
    }
}
