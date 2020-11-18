package controller;

import database.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Appointment;
import model.Type;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

/** This class controls the calendar window.
 This class and associated window are invoked when the user presses the 'Calendar' button within the navigation bar of the application. */
public class CalendarWindowController {
    @FXML
    private VBox centerContainer;
    @FXML
    private Button calendarButton;
    @FXML
    private Button searchByWeekButton;
    @FXML
    private TableView<Type> typeCount;
    @FXML
    private Button searchButton;
    @FXML
    private ComboBox<String> weekComboBox;
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
    private Label day5;
    @FXML
    private ListView<Appointment> day5Appts;
    @FXML
    private Label day6;
    @FXML
    private ListView<Appointment> day6Appts;
    @FXML
    private Label day7;
    @FXML
    private ListView<Appointment> day7Appts;
    @FXML
    private TableColumn<Appointment, LocalDateTime> startColumn;
    @FXML
    private TableColumn<Appointment, LocalDateTime> endColumn;
    @FXML
    private DatePicker apptDatePicker;
    @FXML
    private ComboBox<Month> monthComboBox;
    @FXML
    private ComboBox<Year> yearComboBox;
    @FXML
    private TableView<Appointment> allAppointments;
    @FXML
    private Button newApptButton;
    @FXML
    private Button updateApptButton;
    @FXML
    private Button deleteApptButton;
    @FXML
    private Button homeButton;
    @FXML
    private Button customersButton;
    @FXML
    private Button logoutButton;

    private ObservableList<ListView<Appointment>> listViews = FXCollections.observableArrayList();

    private final ObservableList<Month> months = FXCollections.observableArrayList();
    private final ObservableList<Year> years = FXCollections.observableArrayList();
    private final ObservableList<Appointment> appointments = FXCollections.observableArrayList();
    private final FilteredList<Appointment> filteredApptsByMonthYear = new FilteredList<>(appointments);

    private final FilteredList<Appointment> todayList = new FilteredList<>(appointments);
    private final FilteredList<Appointment> tomorrowList = new FilteredList<>(appointments);
    private final FilteredList<Appointment> day3List = new FilteredList<>(appointments);
    private final FilteredList<Appointment> day4List = new FilteredList<>(appointments);
    private final FilteredList<Appointment> day5List = new FilteredList<>(appointments);
    private final FilteredList<Appointment> day6List = new FilteredList<>(appointments);
    private final FilteredList<Appointment> day7List = new FilteredList<>(appointments);
    private ObservableList<Type> typesList = FXCollections.observableArrayList();

    /** This method initializes the calendar window and all of its elements when the window is loaded.
     This method handles node sizing, and also sets up the full table of appointments as well as a abbreviated calendar view of
     the upcoming week of appointments.

     Controls are set for the user to choose a specific day, month and week to filter the list of appointments accordingly.
     Listeners are also added to handle disabling the update and delete appointment buttons as well as listening to open
     a context menu that allows the user an option of opening the appointment details for the selected appointment.

     Lambda: When adding listeners and event handlers, I often choose to use lambda expressions to reduce amount of code and
     to increase efficiency. Lambda expressions are used in this method for value property listeners, key event handlers,
     selected item property listeners, and focused property listeners. */
    public void initialize(){
        homeButton.setMaxWidth(Double.MAX_VALUE);
        calendarButton.setMaxWidth(Double.MAX_VALUE);
        customersButton.setMaxWidth(Double.MAX_VALUE);
        logoutButton.setMaxWidth(Double.MAX_VALUE);
        centerContainer.setMaxWidth(Double.MAX_VALUE);

        typeCount.setItems(typesList);
        typeCount.setMaxWidth(150);
        typeCount.setMinHeight(150);
        setComboBoxes();

        appointments.setAll(DBConnection.getInstance().queryAppointments());
        allAppointments.setItems(appointments);
        allAppointments.setMinHeight(150);
        allAppointments.getSortOrder().add(startColumn);
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
                        }}};
            }});

        setWeekCalendar();
        apptDatePicker.valueProperty().addListener((observableValue, localDate, newDate) -> {
            if(newDate != null){
                filteredApptsByMonthYear.setPredicate(appointment -> {
                    if(appointment.getStart().toLocalDate().equals(newDate)){
                        return true;
                    } else return false;
                });
                allAppointments.setItems(filteredApptsByMonthYear);
                allAppointments.refresh();
                typesList.clear();
            }
        });

        weekComboBox.setDisable(true);
        searchByWeekButton.setDisable(true);
        monthComboBox.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                String userInput = monthComboBox.getEditor().getText().toUpperCase().trim();
                if (!userInput.isEmpty() && (yearComboBox.getValue() != null)) {
                    for (Month month : months) {
                        if (month.name().contains(userInput)) {
                            monthComboBox.getSelectionModel().select(month);
                        }
                    }
                    queryAppointmentsByMonthYear(monthComboBox.getValue(), yearComboBox.getValue());
                    weekComboBox.setDisable(false);
                    setWeekDates(monthComboBox.getSelectionModel().getSelectedItem(), yearComboBox.getSelectionModel().getSelectedItem());
                    searchByWeekButton.setDisable(false);
                    typesList.clear();
                }
            }
        });
        yearComboBox.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode() == KeyCode.ENTER){
                String userInput = monthComboBox.getEditor().getText().toUpperCase().trim();
                if(!userInput.isEmpty() && (yearComboBox.getValue() != null)){
                    for(Month month : months){
                        if(month.name().contains(userInput)){
                            monthComboBox.getSelectionModel().select(month);
                        }
                    }
                    queryAppointmentsByMonthYear(monthComboBox.getValue(), yearComboBox.getValue());
                    weekComboBox.setDisable(false);
                    setWeekDates(monthComboBox.getSelectionModel().getSelectedItem(), yearComboBox.getSelectionModel().getSelectedItem());
                    searchByWeekButton.setDisable(false);
                    typesList.clear();
                }
            }});

        updateApptButton.setDisable(true);
        deleteApptButton.setDisable(true);
        allAppointments.getSelectionModel().selectedItemProperty().addListener((observableValue, appointment, appt2) -> {
            if(appt2 != null) {
                updateApptButton.setDisable(false);
                deleteApptButton.setDisable(false);
            } else{
                updateApptButton.setDisable(true);
                deleteApptButton.setDisable(true);
            }
        });
        allAppointments.focusedProperty().addListener((observableValue, aBoolean, t1) -> {
            if((t1) && (allAppointments.getSelectionModel().getSelectedItem() != null)){
                updateApptButton.setDisable(false);
                deleteApptButton.setDisable(false);
            } else if(!t1 && (!updateApptButton.isFocused())){
                updateApptButton.setDisable(true);
            }
        });
        ContextMenu cm = new ContextMenu();
        allAppointments.setOnMouseClicked(new EventHandler<>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(((mouseEvent.getClickCount() == 2) || (mouseEvent.isShiftDown() && (mouseEvent.getButton() == MouseButton.PRIMARY)) ||
                        (mouseEvent.getButton() == MouseButton.SECONDARY)) && (allAppointments.getSelectionModel().getSelectedItem() != null)){
                    cm.getItems().clear();
                    cm.hide();
                    MenuItem details = new MenuItem("Details");
                    details.setOnAction(new EventHandler<>() {
                        @Override
                        public void handle(ActionEvent actionEvent) {
                            Dialog<ButtonType> dialog = new Dialog<>();
                            dialog.initOwner(allAppointments.getScene().getWindow());
                            FXMLLoader fxmlLoader = new FXMLLoader();
                            fxmlLoader.setLocation(getClass().getResource("/view/ApptDetails.fxml"));
                            try {
                                dialog.getDialogPane().setContent(fxmlLoader.load());
                                ApptDetailsController controller = fxmlLoader.getController();
                                controller.initialize(allAppointments.getSelectionModel().getSelectedItem(), appointments);
                            } catch (Exception e) {
                                System.out.println("Could not load appointment details form.");
                                e.printStackTrace();
                            }
                            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
                            dialog.setResizable(true);
                            dialog.showAndWait();
                        }
                    });
                    cm.getItems().add(details);
                    cm.show(allAppointments, mouseEvent.getScreenX(), mouseEvent.getScreenY());
                }
            }
        });
        listViews.addAll(todaysAppts, tomorrowsAppts, day3Appts, day4Appts, day5Appts, day6Appts, day7Appts);
        for(ListView<Appointment> lv : listViews) {
            lv.setCursor(Cursor.HAND);
            lv.setOnMouseClicked(new EventHandler<>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if (((mouseEvent.getClickCount() == 2) || (mouseEvent.isShiftDown() && (mouseEvent.getButton() == MouseButton.PRIMARY)) ||
                            (mouseEvent.getButton() == MouseButton.SECONDARY)) && (lv.getSelectionModel().getSelectedItem() != null)) {
                        ContextMenu cm = new ContextMenu();
                        MenuItem details = new MenuItem("Details");
                        details.setOnAction(new EventHandler<>() {
                            @Override
                            public void handle(ActionEvent actionEvent) {
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
                        });
                        cm.getItems().add(details);
                        cm.show(lv, mouseEvent.getScreenX(), mouseEvent.getScreenY());
                    }
                }});
        }
        Image img = new Image("/searchIcon.png");
        ImageView view = new ImageView(img);
        view.setFitHeight(18);
        view.setFitWidth(18);
        searchButton.setGraphic(view);
        searchButton.setMaxSize(25, 25);

        ImageView view2 = new ImageView(img);
        view2.setFitWidth(18);
        view2.setFitHeight(18);
        searchByWeekButton.setGraphic(view2);
        searchByWeekButton.setMaxSize(25, 25);

        apptDatePicker.setShowWeekNumbers(false);
    }

    /** This method is called by the initialize method and sets the list of week dates for the week combo box.
     Depending on the month and year provided, the week dates are filtered accordingly.
     Lambda: As in many other methods of the application, all predicate setting methods are shown in lambda form. I decided to use
     this approach to greatly reduce amount of code.
     @param month The month selected by the user.
     @param year The year selected by the user. */
    private void setWeekDates(Month month, Year year){
        ObservableList<String> weeksNotLeapYear = FXCollections.observableArrayList();
        weeksNotLeapYear.addAll("01/01 - 01/07", "01/08 - 01/14", "01/15 - 01/21", "01/22 - 01/28", "01/29 - 02/04",
                "02/05 - 02/11", "02/12 - 02/18", "02/19 - 02/25", "02-26 - 03/04",
                "03/05 - 03/11", "03/12 - 03/18", "03/19 - 03-25", "03/26 - 04/01",
                "04/02 - 04/08", "04/09 - 04/15", "04/16 - 04/22", "04/23 - 04/29", "04/30 - 05/06",
                "05/07 - 05/13", "05/14 - 05/20", "05/21 - 05/27", "05/28 - 06/03",
                "06/04 - 06/10", "06/11 - 06/17", "06/18 - 06/24", "06/25 - 07/01",
                "07/02 - 07/08", "07/09 - 07/15", "07/16 - 07/22", "07/23 - 07/29", "07/30 - 08/05",
                "08/06 - 08/12", "08/13 - 08/19", "08/20 - 08/26", "08/27 - 09/02",
                "09/03 - 09/09", "09/10 - 09/16", "09/17 - 09/23", "09/24 - 09/30",
                "10/01 - 10/07", "10/08 - 10/14", "10/15 - 10/21", "10/22 - 10/28", "10/29 - 11/04",
                "11/05 - 11/11", "11/12 - 11/18", "11/19 - 11/25", "11/26 - 12/02",
                "12/03 - 12/09", "12/10 - 12/16", "12/17 - 12/23", "12/24 - 12/30", "12/31 - 01/06");
        ObservableList<String> weeksLeapYear = FXCollections.observableArrayList();
        weeksLeapYear.addAll("01/01 - 01/07", "01/08 - 01/14", "01/15 - 01/21", "01/22 - 01/28", "01/29 - 02/04",
                "02/05 - 02/11", "02/12 - 02/18", "02/19 - 02/25", "02/26 - 03/03",
                "03/04 - 03/10", "03/11 - 03/17", "03/18 - 03-24", "03/25 - 03/31",
                "04/01 - 04/07", "04/08 - 04/14", "04/15 - 04/21", "04/22 - 04/28", "04/29 - 05/05",
                "05/06 - 05/12", "05/13 - 05/19", "05/20 - 05/26", "05/27 - 06/02",
                "06/03 - 06/09", "06/10 - 06/16", "06/17 - 06/23", "06/24 - 06/30",
                "07/01 - 07/07", "07/08 - 07/14", "07/15 - 07/21", "07/22 - 07/28", "07/29 - 08/04",
                "08/05 - 08/11", "08/12 - 08/18", "08/19 - 08/25", "08/26 - 09/01",
                "09/02 - 09/08", "09/09 - 09/15", "09/16 - 09/22", "09/23 - 09/29", "09/30 - 10/06",
                "10/07 - 10/13", "10/14 - 10/20", "10/21 - 10/27", "10/28 - 11/03",
                "11/04 - 11/10", "11/11 - 11/17", "11/18 - 11/24", "11/25 - 12/01",
                "12/02 - 12/08", "12/09 - 12/15", "12/16 - 12/22", "12/23 - 12/29", "12/30 - 01/05");

        FilteredList<String> filteredWeeksInLeap = new FilteredList<>(weeksLeapYear);
        FilteredList<String> filteredWeeksNotLeap = new FilteredList<>(weeksNotLeapYear);

        if(month.getValue() == 1){
            if(year.isLeap()){
                filteredWeeksInLeap.setPredicate(s -> {
                    if(s.equals("01/01 - 01/07") || s.equals("01/08 - 01/14") || s.equals("01/15 - 01/21") ||
                    s.equals("01/22 - 01/28") || s.equals("01/29 - 02/04")){
                        return true;
                    } else return false;
                });
                weekComboBox.setItems(filteredWeeksInLeap);
            }
            if(!year.isLeap()){
                filteredWeeksNotLeap.setPredicate(s -> {
                    if(s.equals("01/01 - 01/07") || s.equals("01/08 - 01/14") || s.equals("01/15 - 01/21") ||
                            s.equals("01/22 - 01/28") || s.equals("01/29 - 02/04")){
                        return true;
                    } else return false;
                });
                weekComboBox.setItems(filteredWeeksNotLeap);
            }
        } else if(month.getValue() == 2){
            if(year.isLeap()){
                filteredWeeksInLeap.setPredicate(s -> {
                    if(s.contains("02/")){
                        return true;
                    } else return false;
                });
                weekComboBox.setItems(filteredWeeksInLeap);
            }
            if(!year.isLeap()){
                filteredWeeksNotLeap.setPredicate(s -> {
                    if(s.contains("02/")){
                        return true;
                    } else return false;
                });
                weekComboBox.setItems(filteredWeeksNotLeap);
            }
        } else if(month.getValue() == 3){
            if(year.isLeap()){
                filteredWeeksInLeap.setPredicate(s -> {
                    if(s.contains("03/")){
                        return true;
                    } else return false;
                });
                weekComboBox.setItems(filteredWeeksInLeap);
            }
            if(!year.isLeap()){
                filteredWeeksNotLeap.setPredicate(s -> {
                    if(s.contains("03/")){
                        return true;
                    } else return false;
                });
                weekComboBox.setItems(filteredWeeksNotLeap);
            }
        }
        else if(month.getValue() == 4){
            if(year.isLeap()){
                filteredWeeksInLeap.setPredicate(s -> {
                    if(s.contains("04/")){
                        return true;
                    } else return false;
                });
                weekComboBox.setItems(filteredWeeksInLeap);
            }
            if(!year.isLeap()){
                filteredWeeksNotLeap.setPredicate(s -> {
                    if(s.contains("04/")){
                        return true;
                    } else return false;
                });
                weekComboBox.setItems(filteredWeeksNotLeap);
            }
        } else if(month.getValue() == 5){
            if(year.isLeap()){
                filteredWeeksInLeap.setPredicate(s -> {
                    if(s.contains("05/")){
                        return true;
                    } else return false;
                });
                weekComboBox.setItems(filteredWeeksInLeap);
            }
            if(!year.isLeap()){
                filteredWeeksNotLeap.setPredicate(s -> {
                    if(s.contains("05/")){
                        return true;
                    } else return false;
                });
                weekComboBox.setItems(filteredWeeksNotLeap);
            }
        } else if(month.getValue() == 6){
            if(year.isLeap()){
                filteredWeeksInLeap.setPredicate(s -> {
                    if(s.contains("06/")){
                        return true;
                    } else return false;
                });
                weekComboBox.setItems(filteredWeeksInLeap);
            }
            if(!year.isLeap()){
                filteredWeeksNotLeap.setPredicate(s -> {
                    if(s.contains("06/")){
                        return true;
                    } else return false;
                });
                weekComboBox.setItems(filteredWeeksNotLeap);
            }
        } else if(month.getValue() == 7){
            if(year.isLeap()){
                filteredWeeksInLeap.setPredicate(s -> {
                    if(s.contains("07/")){
                        return true;
                    } else return false;
                });
                weekComboBox.setItems(filteredWeeksInLeap);
            }
            if(!year.isLeap()){
                filteredWeeksNotLeap.setPredicate(s -> {
                    if(s.contains("07/")){
                        return true;
                    } else return false;
                });
                weekComboBox.setItems(filteredWeeksNotLeap);
            }
        } else if(month.getValue() == 8){
            if(year.isLeap()){
                filteredWeeksInLeap.setPredicate(s -> {
                    if(s.contains("08/")){
                        return true;
                    } else return false;
                });
                weekComboBox.setItems(filteredWeeksInLeap);
            }
            if(!year.isLeap()){
                filteredWeeksNotLeap.setPredicate(s -> {
                    if(s.contains("08/")){
                        return true;
                    } else return false;
                });
                weekComboBox.setItems(filteredWeeksNotLeap);
            }
        } else if(month.getValue() == 9){
            if(year.isLeap()){
                filteredWeeksInLeap.setPredicate(s -> {
                    if(s.contains("09/")){
                        return true;
                    } else return false;
                });
                weekComboBox.setItems(filteredWeeksInLeap);
            }
            if(!year.isLeap()){
                filteredWeeksNotLeap.setPredicate(s -> {
                    if(s.contains("09/")){
                        return true;
                    } else return false;
                });
                weekComboBox.setItems(filteredWeeksNotLeap);
            }
        } else if(month.getValue() == 10){
            if(year.isLeap()){
                filteredWeeksInLeap.setPredicate(s -> {
                    if(s.contains("10/")){
                        return true;
                    } else return false;
                });
                weekComboBox.setItems(filteredWeeksInLeap);
            }
            if(!year.isLeap()){
                filteredWeeksNotLeap.setPredicate(s -> {
                    if(s.contains("10/")){
                        return true;
                    } else return false;
                });
                weekComboBox.setItems(filteredWeeksNotLeap);
            }
        } else if(month.getValue() == 11){
            if(year.isLeap()){
                filteredWeeksInLeap.setPredicate(s -> {
                    if(s.contains("11/")){
                        return true;
                    } else return false;
                });
                weekComboBox.setItems(filteredWeeksInLeap);
            }
            if(!year.isLeap()){
                filteredWeeksNotLeap.setPredicate(s -> {
                    if(s.contains("11/")){
                        return true;
                    } else return false;
                });
                weekComboBox.setItems(filteredWeeksNotLeap);
            }
        } else if(month.getValue() == 12){
            if(year.isLeap()){
                filteredWeeksInLeap.setPredicate(s -> {
                    if(s.contains("12/")){
                        return true;
                    } else return false;
                });
                weekComboBox.setItems(filteredWeeksInLeap);
            }
            if(!year.isLeap()){
                filteredWeeksNotLeap.setPredicate(s -> {
                    if(s.contains("12/")){
                        return true;
                    } else return false;
                });
                weekComboBox.setItems(filteredWeeksNotLeap);
            }
        }
    }

    /** This method is called by the initialize method and sets the upcoming week's 'Calendar' view with the appropriate appointments.
     Each day of the week has a listview that is populated by any appointments associated with that date.
     Each appointment is formatted by the method to show the start time and title of the appointment.

     Lambda: As in many other methods of the application, all predicate setting methods are shown in lambda form. I decided to use
     this approach to greatly reduce amount of code. */
    private void setWeekCalendar(){
        today.setText("Today," + LocalDate.now().format(DateTimeFormatter.ofPattern(" MM/d")));
        tomorrow.setText("Tomorrow," + LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern(" MM/d")));
        day3.setText((LocalDate.now().plusDays(2).format(DateTimeFormatter.ofPattern("EEEE"))) + LocalDate.now().plusDays(2).format(DateTimeFormatter.ofPattern(" MM/d")));
        day4.setText((LocalDate.now().plusDays(3).format(DateTimeFormatter.ofPattern("EEEE"))) + LocalDate.now().plusDays(3).format(DateTimeFormatter.ofPattern(" MM/d")));
        day5.setText((LocalDate.now().plusDays(4).format(DateTimeFormatter.ofPattern("EEEE"))) + LocalDate.now().plusDays(4).format(DateTimeFormatter.ofPattern(" MM/d")));
        day6.setText((LocalDate.now().plusDays(5).format(DateTimeFormatter.ofPattern("EEEE"))) + LocalDate.now().plusDays(5).format(DateTimeFormatter.ofPattern(" MM/d")));
        day7.setText((LocalDate.now().plusDays(6).format(DateTimeFormatter.ofPattern("EEEE"))) + LocalDate.now().plusDays(6).format(DateTimeFormatter.ofPattern(" MM/d")));

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
                        }
                    }};
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
                        }
                    }};
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
                        }
                    }};
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
                        }
                    }};
            }});
        day5Appts.setCellFactory(new Callback<>() {
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
                        }
                    }};
            }});
        day6Appts.setCellFactory(new Callback<>() {
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
                        }
                    }};
            }});
        day7Appts.setCellFactory(new Callback<>() {
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
                        }
                    }};
            }});

        todayList.setPredicate(appointment -> appointment.getStart().toLocalDate().equals(LocalDate.now()));
        todaysAppts.setItems(todayList);
        todaysAppts.setPlaceholder(new Label("No Appointments"));

        tomorrowList.setPredicate(appointment -> appointment.getStart().toLocalDate().equals(LocalDate.now().plusDays(1)));
        tomorrowsAppts.setItems(tomorrowList);
        tomorrowsAppts.setPlaceholder(new Label("No Appointments"));

        day3List.setPredicate(appointment -> appointment.getStart().toLocalDate().equals(LocalDate.now().plusDays(2)));
        day3Appts.setItems(day3List);
        day3Appts.setPlaceholder(new Label("No Appointments"));

        day4List.setPredicate(appointment -> appointment.getStart().toLocalDate().equals(LocalDate.now().plusDays(3)));
        day4Appts.setItems(day4List);
        day4Appts.setPlaceholder(new Label("No Appointments"));

        day5List.setPredicate(appointment -> appointment.getStart().toLocalDate().equals(LocalDate.now().plusDays(4)));
        day5Appts.setItems(day5List);
        day5Appts.setPlaceholder(new Label("No Appointments"));

        day6List.setPredicate(appointment -> appointment.getStart().toLocalDate().equals(LocalDate.now().plusDays(5)));
        day6Appts.setItems(day6List);
        day6Appts.setPlaceholder(new Label("No Appointments"));

        day7List.setPredicate(appointment -> appointment.getStart().toLocalDate().equals(LocalDate.now().plusDays(6)));
        day7Appts.setItems(day7List);
        day7Appts.setPlaceholder(new Label("No Appointments"));
    }

    /** This method sets the month and year combo boxes with appropriate months and years and is called by the initialize method. */
    private void setComboBoxes(){
        months.setAll(Month.values());
        monthComboBox.setItems(months);
        monthComboBox.setEditable(true);
        Year year = Year.now();
        years.add(year);
        for(int i=0; i<9; i++){
            year = year.plusYears(1);
            years.add(year);
        }
        yearComboBox.setItems(years);
    }

    /** This method filters all appointments by the month and year passed into the method.
     This method is called by the handleSearch() method when the user inputs a month and year value and presses the search button or 'Enter' key.

     Lambda: When setting the predicate for the filtered list of appointments, a lambda expression is used to shorten code
     and to make the code more efficient.
     @param month The month chosen by the user.
     @param year The year chosen by the user. */
    private void queryAppointmentsByMonthYear(Month month, Year year){
        filteredApptsByMonthYear.setPredicate(appointment -> {
            if((month == appointment.getStart().getMonth()) && (year.getValue() == appointment.getStart().getYear())){
                return true;
            } else return false;
        });
        allAppointments.setItems(filteredApptsByMonthYear);
    }

    /** This method handles the reset of the table of appointments.
     When the user hits the 'Refresh' button, the appointments reset to the original full list. */
    @FXML
    private void handleRefreshTable(){
        appointments.setAll(DBConnection.getInstance().queryAppointments());
        allAppointments.setItems(appointments);
        typesList.clear();
    }

    /** This method handles opening the add appointment dialog window.
     When the user hits the 'New' button associated with the table of appointments, the dialog window is loaded. */
    @FXML
    private void handleNewAppt(){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(newApptButton.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/AddAppointment.fxml"));
        try{
            dialog.getDialogPane().setContent(fxmlLoader.load());
            AddApptController controller = fxmlLoader.getController();
            controller.initialize(appointments, null, null, null);
        } catch(Exception e){
            System.out.println("Could not load add appointment form.");
            e.printStackTrace();
        }
        dialog.setResizable(true);
        dialog.showAndWait();
    }

    /** This method handles opening the update appointment form dialog window.
     When the user presses the 'Update' button associated with the table of appointments, the dialog window is loaded. */
    @FXML
    private void handleUpdateAppt(){
        if(allAppointments.getSelectionModel().getSelectedItem() != null){
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.initOwner(updateApptButton.getScene().getWindow());
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/view/UpdateAppointment.fxml"));
            try{
                dialog.getDialogPane().setContent(fxmlLoader.load());
                UpdateAppointmentController controller = fxmlLoader.getController();
                controller.initialize(allAppointments.getItems(), allAppointments.getSelectionModel().getSelectedItem());
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
    private void handleDeleteAppt(){
        Appointment appointmentToDelete = allAppointments.getSelectionModel().getSelectedItem();

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
        allAppointments.refresh();
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
    private void handleLogout(){
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

    /** This method handles getting the month and year values from each combo box and enables the search by week combo box and button.
     This method, when the user presses 'Enter' or the search button is clicked, grabs the values from the combo boxes and passes them
     to the queryAppointmentsByMonthYear() and setWeekDates() methods.
     @return boolean */
    @FXML
    private boolean handleSearch() {
        String userInput = monthComboBox.getEditor().getText().toUpperCase().trim();
        if (!userInput.isEmpty() && (yearComboBox.getValue() != null)) {
            monthComboBox.setStyle("-fx-border-color: none;");
            yearComboBox.setStyle("-fx-border-color: none;");
            for (Month month : months) {
                if (month.name().contains(userInput)) {
                    monthComboBox.getSelectionModel().select(month);
                }
            }
            queryAppointmentsByMonthYear(monthComboBox.getValue(), yearComboBox.getValue());
            weekComboBox.setDisable(false);
            setWeekDates(monthComboBox.getSelectionModel().getSelectedItem(), yearComboBox.getSelectionModel().getSelectedItem());
            searchByWeekButton.setDisable(false);
            typesList.clear();
            return true;
        } else {
            if (userInput.isEmpty()) {
                monthComboBox.setStyle("-fx-border-color: red;");
            }
            if (yearComboBox.getSelectionModel().getSelectedItem() == null) {
                yearComboBox.setStyle("-fx-border-color: red;");
            }
            return false;
        }
    }

    /** This method handles counting each appointment type and filling the associated table with its results.
     When the 'Get Count By Type' button is pressed, this method counts the types of all the appointments that are currently
     in the appointment table and shows the data in a separate table underneath the button. */
    @FXML
    private void getTypeCount(){
        typesList.clear();
        int planningSession = 0;
        int deBriefing = 0;
        int networkingSession = 0;
        int salesMtg = 0;
        int other = 0;
        for(Appointment appt : allAppointments.getItems()){
            if(appt.getType().equals("Planning Session")){
                planningSession++;
            }
            if(appt.getType().equals("De-Briefing")){
                deBriefing++;
            }
            if(appt.getType().equals("Networking")){
                networkingSession++;
            }
            if(appt.getType().equals("Sales")){
                salesMtg++;
            }
            if(appt.getType().equals("Other")){
                other++;
            }
        }
        Type planning = new Type("Planning Session", planningSession);
        Type deBrief = new Type("De-Briefing", deBriefing);
        Type networking = new Type("Networking", networkingSession);
        Type sales = new Type("Sales", salesMtg);
        Type otherType = new Type("Other", other);

        typesList.addAll(planning, deBrief, networking, sales, otherType);
        typeCount.setItems(typesList);
        typeCount.refresh();
    }

    /** This method filteres the appointments according to the specific week date range selected by the user.
     This method is only available once the user has selected a month and year and a week date range from within the selected month.
     Once the user clicks the search button next to the Week combo box, the table is set with the filtered items accordingly.

     Lambda: A lambda expression is implemented in the method to set the predicate for the filtered list by week. I used this
     lambda to shorten the amount of code and make processing more efficient. */
    @FXML
    public void handleSearchByWeek() {
        String selectedWeek = weekComboBox.getSelectionModel().getSelectedItem();
        String[] stringArray = selectedWeek.split(" - ");
        String start = stringArray[0];
        String startWithYear = start.concat("/" + yearComboBox.getSelectionModel().getSelectedItem().toString());
        String end = stringArray[1];
        String endWithYear = end.concat("/" + yearComboBox.getSelectionModel().getSelectedItem().toString());

        LocalDate startDate = LocalDate.parse(startWithYear.trim(), DateTimeFormatter.ofPattern("M/dd/yyyy"));
        LocalDate endDate = LocalDate.parse(endWithYear.trim(), DateTimeFormatter.ofPattern("M/dd/yyyy"));

        FilteredList<Appointment> filteredApptsByWeek = new FilteredList<>(appointments);
        filteredApptsByWeek.setPredicate(appointment -> {
            LocalDate startDateAppt = appointment.getStart().toLocalDate();
            if(startDateAppt.equals(startDate) || startDateAppt.equals(endDate) ||
                    (startDateAppt.isAfter(startDate) && startDateAppt.isBefore(endDate))){
                return true;
            } else return false;
        });
        allAppointments.setItems(filteredApptsByWeek);
    }
}
