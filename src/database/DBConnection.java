package database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import model.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Properties;

/** This class handles all database connections and queries for the application. */
public class DBConnection {

    private static final String protocol = "jdbc";
    private static final String vendorName = ":mysql:";
    private static final String ipAddress = "//wgudb.ucertify.com:3306/WJ076IR";
    private static final String jdbcURL = protocol + vendorName + ipAddress;
    private static final String username = "U076IR";
    private static final String password = "53688952492";

    public static final String TABLE_APPOINTMENTS = "appointments";
    public static final String COLUMN_APPOINTMENT_ID = "Appointment_ID";
    public static final String COLUMN_TITLE = "Title";
    public static final String COLUMN_DESCRIPTION = "Description";
    public static final String COLUMN_LOCATION = "Location";
    public static final String COLUMN_TYPE = "Type";
    public static final String COLUMN_START = "Start";
    public static final String COLUMN_END = "End";
    public static final String COLUMN_APPT_LAST_UPDATE = "Last_Update";
    public static final String COLUMN_APPT_LAST_UPDATED_BY = "Last_Updated_By";

    public static final String TABLE_CONTACTS = "contacts";
    public static final String COLUMN_CONTACT_ID = "Contact_ID";
    public static final String COLUMN_CONTACT_NAME = "Contact_Name";
    public static final String COLUMN_CONTACT_EMAIL = "Email";

    public static final String TABLE_COUNTRIES = "countries";
    public static final String COLUMN_COUNTRY_ID = "Country_ID";
    public static final String COLUMN_COUNTRY = "Country";

    public static final String TABLE_CUSTOMERS = "customers";
    public static final String COLUMN_CUSTOMER_ID = "Customer_ID";
    public static final String COLUMN_CUSTOMER_NAME = "Customer_Name";
    public static final String COLUMN_CUSTOMER_ADDRESS = "Address";
    public static final String COLUMN_CUSTOMER_ZIPCODE = "Postal_Code";
    public static final String COLUMN_CUSTOMER_PHONE = "Phone";
    public static final String COLUMN_CUSTOMER_LAST_UPDATE = "Last_Update";
    public static final String COLUMN_CUSTOMER_LAST_UPDATED_BY = "Last_Updated_By";

    public static final String TABLE_FIRST_LEVEL_DIVISIONS = "first_level_divisions";
    public static final String COLUMN_DIVISION_ID = "Division_ID";
    public static final String COLUMN_DIVISION_NAME = "Division";

    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "User_ID";
    public static final String COLUMN_USER_NAME = "User_Name";
    public static final String COLUMN_PASSWORD = "Password";
    public static final String COLUMN_USER_LAST_UPDATE = "Last_Update";
    public static final String COLUMN_USER_LAST_UPDATED_BY = "Last_Updated_By";

    public static final String INSERT_APPOINTMENT = "INSERT INTO " + TABLE_APPOINTMENTS +
            " VALUES (NULL, ?, ?, ?, ?, ?, ?, NOW(), ?, NOW(), ?, ?, ?, ?)";
    public static final String INSERT_CUSTOMER = "INSERT INTO " + TABLE_CUSTOMERS +
            " VALUES(NULL, ?, ?, ?, ?, NOW(), ?, NOW(), ?, ?)";
    public static final String INSERT_USER = "INSERT INTO " + TABLE_USERS +
            " VALUES(NULL, ?, ?, NOW(), ?, NOW(), ?)";
    public static final String INSERT_CONTACT = "INSERT INTO " + TABLE_CONTACTS +
            " VALUES(NULL, ?, ?)";

    public static final String UPDATE_APPOINTMENT = "UPDATE " + TABLE_APPOINTMENTS + " SET " + COLUMN_APPOINTMENT_ID + " = ?, " +
            COLUMN_TITLE + " = ?, " + COLUMN_DESCRIPTION + " = ?, " + COLUMN_LOCATION + " = ?, " +
            COLUMN_TYPE + " = ?, " + COLUMN_START + " = ?, " + COLUMN_END + " = ?, " + COLUMN_APPT_LAST_UPDATE +
            " = NOW(), " + COLUMN_APPT_LAST_UPDATED_BY + " = ?, " +
            COLUMN_CUSTOMER_ID + " = ?, " + COLUMN_USER_ID + " = ?, " + COLUMN_CONTACT_ID + " = ? WHERE " + COLUMN_APPOINTMENT_ID + " = ?";
    public static final String UPDATE_CUSTOMER = "UPDATE " + TABLE_CUSTOMERS + " SET " + COLUMN_CUSTOMER_ID + " = ?, " +
            COLUMN_CUSTOMER_NAME + " = ?, " + COLUMN_CUSTOMER_ADDRESS + " = ?, " + COLUMN_CUSTOMER_ZIPCODE + " = ?, " +
            COLUMN_CUSTOMER_PHONE + " = ?, " + COLUMN_CUSTOMER_LAST_UPDATE + " = NOW(), " + COLUMN_CUSTOMER_LAST_UPDATED_BY + " = ?, " +
            COLUMN_DIVISION_ID + " = ? WHERE " + COLUMN_CUSTOMER_ID + " = ?";
    public static final String UPDATE_CONTACT = "UPDATE " + TABLE_CONTACTS + " SET " + COLUMN_CONTACT_ID +
            " = ?, " + COLUMN_CONTACT_NAME + " = ?, " + COLUMN_CONTACT_EMAIL + " = ? WHERE " + COLUMN_CONTACT_ID + " = ?";
    public static final String UPDATE_USER = "UPDATE " + TABLE_USERS + " SET " + COLUMN_USER_ID + " = ?, " +
            COLUMN_USER_NAME + " = ?, " + COLUMN_PASSWORD + " = ?, " + COLUMN_USER_LAST_UPDATE + " = NOW(), " +
            COLUMN_USER_LAST_UPDATED_BY + " = ? WHERE " + COLUMN_USER_ID + " = ?";

    public static final String DELETE_APPOINTMENT = "DELETE FROM " + TABLE_APPOINTMENTS + " WHERE " + COLUMN_APPOINTMENT_ID + " = ?";
    public static final String DELETE_CUSTOMER = "DELETE FROM " + TABLE_CUSTOMERS + " WHERE " + COLUMN_CUSTOMER_ID + " = ?";
    public static final String DELETE_CONTACT = "DELETE FROM " + TABLE_CONTACTS + " WHERE " + COLUMN_CONTACT_ID + " = ?";
    public static final String DELETE_USER = "DELETE FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_ID + " = ?";

    public static final String QUERY_APPOINTMENTS = "SELECT " + COLUMN_APPOINTMENT_ID + ", " + COLUMN_TITLE + ", " +
            COLUMN_DESCRIPTION + ", " + COLUMN_LOCATION + ", " + COLUMN_TYPE + ", " +
            COLUMN_START + ", " + COLUMN_END + ", " + COLUMN_CUSTOMER_ID + ", " + COLUMN_USER_ID + ", " +
            COLUMN_CONTACT_ID + " FROM " + TABLE_APPOINTMENTS;
    public static final String QUERY_CUSTOMERS = "SELECT " + COLUMN_CUSTOMER_ID + ", " + COLUMN_CUSTOMER_NAME + ", " +
            COLUMN_CUSTOMER_ADDRESS + ", " + COLUMN_CUSTOMER_ZIPCODE + ", " + COLUMN_CUSTOMER_PHONE + ", " +
            COLUMN_DIVISION_ID + " FROM " + TABLE_CUSTOMERS;
    public static final String QUERY_USERS = "SELECT " + COLUMN_USER_ID + ", " + COLUMN_USER_NAME + ", " + COLUMN_PASSWORD +
            " FROM " + TABLE_USERS;
    public static final String QUERY_FLD = "SELECT " + COLUMN_DIVISION_ID + ", " + COLUMN_DIVISION_NAME +
            ", " + COLUMN_COUNTRY_ID + " FROM " + TABLE_FIRST_LEVEL_DIVISIONS;
    public static final String QUERY_COUNTRIES = "SELECT " + COLUMN_COUNTRY_ID + ", " + COLUMN_COUNTRY + " FROM " + TABLE_COUNTRIES;
    public static final String QUERY_CONTACTS = "SELECT * FROM " + TABLE_CONTACTS;

    public static final String QUERY_FLD_BY_COUNTRY_ID = "SELECT * FROM " + TABLE_FIRST_LEVEL_DIVISIONS +
            " WHERE " + COLUMN_COUNTRY_ID + " = ?";

    private Connection conn;

    private PreparedStatement insertIntoUsers;
    private PreparedStatement insertIntoAppointments;
    private PreparedStatement insertIntoContacts;
    private PreparedStatement insertIntoCustomers;

    private PreparedStatement updateAppointment;
    private PreparedStatement updateCustomer;
    private PreparedStatement updateUser;
    private PreparedStatement updateContact;

    private PreparedStatement deleteAppointment;
    private PreparedStatement deleteCustomer;
    private PreparedStatement deleteContact;
    private PreparedStatement deleteUser;

    private PreparedStatement queryFLDByCountry;

    private static DBConnection instance = new DBConnection();

    private DBConnection(){}

    /** This method allows the user to call methods from anywhere in the application.
     @return Instance of the database connection class. */
    public static DBConnection getInstance(){
        return instance;
    }

    /** This method initiates the connection to the database.
     This method is called only by the main method at the start of the application.
     @return boolean */
    public boolean getConnection(){
        Properties connectionProps = new Properties();
        connectionProps.put("user", username);
        connectionProps.put("password", password);
        try {
            conn = DriverManager.getConnection(jdbcURL, connectionProps);
            System.out.println("Connection to database successful");
            insertIntoAppointments = conn.prepareStatement(INSERT_APPOINTMENT, Statement.RETURN_GENERATED_KEYS);
            insertIntoUsers = conn.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS);
            insertIntoContacts = conn.prepareStatement(INSERT_CONTACT, Statement.RETURN_GENERATED_KEYS);
            insertIntoCustomers = conn.prepareStatement(INSERT_CUSTOMER, Statement.RETURN_GENERATED_KEYS);
            updateAppointment = conn.prepareStatement(UPDATE_APPOINTMENT);
            updateCustomer = conn.prepareStatement(UPDATE_CUSTOMER);
            updateContact = conn.prepareStatement(UPDATE_CONTACT);
            updateUser = conn.prepareStatement(UPDATE_USER);
            deleteAppointment = conn.prepareStatement(DELETE_APPOINTMENT);
            deleteCustomer = conn.prepareStatement(DELETE_CUSTOMER);
            deleteContact = conn.prepareStatement(DELETE_CONTACT);
            deleteUser = conn.prepareStatement(DELETE_USER);
            queryFLDByCountry = conn.prepareStatement(QUERY_FLD_BY_COUNTRY_ID);
            return true;
        }
        catch(SQLException e){
            System.out.println("Failed to connect to database: " + e.getMessage());
            return false;
        }
    }

    /** This method closes the database connection when the user exits the application.
     The method closes all of the prepared statements including the connection. */
    public void closeConnection(){
        try {
            if(insertIntoAppointments != null){
                insertIntoAppointments.close();
            }
            if(insertIntoUsers != null){
                insertIntoUsers.close();
            }
            if(insertIntoCustomers != null){
                insertIntoCustomers.close();
            }
            if(insertIntoContacts != null){
                insertIntoContacts.close();
            }
            if(updateAppointment != null){
                updateAppointment.close();
            }
            if(updateCustomer != null){
                updateCustomer.close();
            }
            if(updateContact != null){
                updateContact.close();
            }
            if(updateUser != null){
                updateUser.close();
            }
            if(deleteAppointment != null){
                deleteAppointment.close();
            }
            if(deleteCustomer != null){
                deleteCustomer.close();
            }
            if(deleteContact != null){
                deleteContact.close();
            }
            if(deleteUser != null){
                deleteUser.close();
            }
            if(queryFLDByCountry != null){
                queryFLDByCountry.close();
            }
            if(conn != null){
                conn.close();
            }
            System.out.println("Database closed successfully.");
        } catch(SQLException e){
            System.out.println("Failed to close connection to database: " + e.getMessage());
        }
    }

    /** This method creates an observable list of type 'Users' from the database.
     The method queries the database for all user data and creates a list that is called throughout the application.
     @return Observable List of type 'Users' */
    public ObservableList<Users> queryUsers(){
        try(Statement statement = conn.createStatement();
            ResultSet results = statement.executeQuery(QUERY_USERS)){
            ObservableList<Users> users = FXCollections.observableArrayList();
            while(results.next()){
                Users user = new Users();
                user.setUserID(results.getInt(COLUMN_USER_ID));
                user.setUserName(results.getString(COLUMN_USER_NAME));
                user.setPassword(results.getString(COLUMN_PASSWORD));
                users.add(user);
            }
            return users;
        } catch(Exception e){
            System.out.println("Exception: " + e.getMessage());
            return null;
        }
    }

    /** This method creates an observable list of type 'Customer' from the database.
     The method queries the database for all customer data and creates a list that is called throughout the application.
     @return Observable List of type 'Customer' */
    public ObservableList<Customer> queryCustomers(){
        try(Statement statement = conn.createStatement();
            ResultSet results = statement.executeQuery(QUERY_CUSTOMERS)){
            ObservableList<Customer> customers = FXCollections.observableArrayList();
            while(results.next()){
                Customer customer = new Customer();
                customer.setCustomerID(results.getInt(COLUMN_CUSTOMER_ID));
                customer.setName(results.getString(COLUMN_CUSTOMER_NAME));
                customer.setAddress(results.getString(COLUMN_CUSTOMER_ADDRESS));
                customer.setPostalCode(results.getString(COLUMN_CUSTOMER_ZIPCODE));
                customer.setPhoneNumber(results.getString(COLUMN_CUSTOMER_PHONE));
                customer.setDivisionID(results.getInt(COLUMN_DIVISION_ID));
                customers.add(customer);
            }
            return customers;
        } catch(Exception e){
            System.out.println("Exception: " + e.getMessage());
            return null;
        }
    }

    /** This method creates an observable list of type 'Appointment' from the database.
     The method queries the database for all appointment data and creates a list that is called throughout the application.
     @return Observable List of type 'Appointment' */
    public ObservableList<Appointment> queryAppointments(){
        try(Statement statement = conn.createStatement();
            ResultSet results = statement.executeQuery(QUERY_APPOINTMENTS)){
            ObservableList<Appointment> appointments = FXCollections.observableArrayList();
            while(results.next()){
                Appointment appointment = new Appointment();
                appointment.setId(results.getInt(COLUMN_APPOINTMENT_ID));
                appointment.setTitle(results.getString(COLUMN_TITLE));
                appointment.setDescription(results.getString(COLUMN_DESCRIPTION));
                appointment.setLocation(results.getString(COLUMN_LOCATION));
                appointment.setType(results.getString(COLUMN_TYPE));
                appointment.setStart(results.getTimestamp(COLUMN_START).toLocalDateTime());
                appointment.setEnd(results.getTimestamp(COLUMN_END).toLocalDateTime());
                appointment.setCustomerId(results.getInt(COLUMN_CUSTOMER_ID));
                appointment.setUser(results.getInt(COLUMN_USER_ID));
                appointment.setContact(results.getInt(COLUMN_CONTACT_ID));
                appointments.add(appointment);
            }
            return appointments;
        } catch(Exception e){
            System.out.println("Exception: " + e.getMessage());
            return null;
        }
    }

    /** This method creates an observable list of type 'FirstLevelDivision' from the database.
     The method queries the database for all division data and creates a list that is called throughout the application.
     @return Observable List of type 'FirstLevelDivision' */
    public ObservableList<FirstLevelDivision> queryFLD(){
        try(Statement statement = conn.createStatement();
            ResultSet results = statement.executeQuery(QUERY_FLD)){
            ObservableList<FirstLevelDivision> firstLevelDivisions = FXCollections.observableArrayList();
            while(results.next()){
                FirstLevelDivision firstLevelDivision = new FirstLevelDivision();
                firstLevelDivision.setId(results.getInt(COLUMN_DIVISION_ID));
                firstLevelDivision.setName(results.getString(COLUMN_DIVISION_NAME));
                firstLevelDivision.setCountry_id(results.getInt(COLUMN_COUNTRY_ID));
                firstLevelDivisions.add(firstLevelDivision);
            }
            return firstLevelDivisions;
        } catch(Exception e){
            System.out.println("Exception: " + e.getMessage());
            return null;
        }
    }

    /** This method creates an observable list of type 'Country' from the database.
     The method queries the database for all country data and creates a list that is called throughout the application.
     @return Observable List of type 'Country' */
    public ObservableList<Country> queryCountries(){
        try(Statement statement = conn.createStatement();
            ResultSet results = statement.executeQuery(QUERY_COUNTRIES)){
            ObservableList<Country> countries= FXCollections.observableArrayList();
            while(results.next()){
                Country country = new Country();
                country.setId(results.getInt(COLUMN_COUNTRY_ID));
                country.setName(results.getString(COLUMN_COUNTRY));
                countries.add(country);
            }
            return countries;
        } catch(Exception e){
            System.out.println("Exception: " + e.getMessage());
            return null;
        }
    }

    /** This method creates an observable list of type 'Contacts' from the database.
     The method queries the database for all contact data and creates a list that is called throughout the application.
     @return Observable List of type 'Contacts' */
    public ObservableList<Contacts> queryContacts(){
        try(Statement statement = conn.createStatement();
            ResultSet results = statement.executeQuery(QUERY_CONTACTS)){
            ObservableList<Contacts> contacts = FXCollections.observableArrayList();
            while(results.next()){
                Contacts contact = new Contacts();
                contact.setContactID(results.getInt(COLUMN_CONTACT_ID));
                contact.setContactName(results.getString(COLUMN_CONTACT_NAME));
                contact.setEmail(results.getString(COLUMN_CONTACT_EMAIL));
                contacts.add(contact);
            }
            return contacts;
        } catch(Exception e){
            System.out.println("Exception: " + e.getMessage());
            return null;
        }
    }

    /** This method handles inserting a new appointment into the database.
     Using a prepared statement, this method takes the parameter information and inserts it into the database.
     If the add is successful, the method alerts the user of the success. If it fails, the user is alerted that the insert has failed.

     @param appointment Appointment to be added to the database.
     @param user The user creating the new appointment. */
    public void insertAppointment(Appointment appointment, Users user){
        String title = appointment.getTitle();
        String desc = appointment.getDescription();
        String location = appointment.getLocation();
        String type = appointment.getType();
        LocalDateTime start = appointment.getStart();
        LocalDateTime end = appointment.getEnd();
        String userName = user.getUserName();
        int userID = appointment.getUser();
        int customerID = appointment.getCustomerId();
        int contactID = appointment.getContact();
        try {
            conn.setAutoCommit(false);
            insertIntoAppointments.setString(1, title);
            insertIntoAppointments.setString(2, desc);
            insertIntoAppointments.setString(3, location);
            insertIntoAppointments.setString(4, type);
            insertIntoAppointments.setTimestamp(5, Timestamp.valueOf(start));
            insertIntoAppointments.setTimestamp(6, Timestamp.valueOf(end));
            insertIntoAppointments.setString(7, userName);
            insertIntoAppointments.setString(8, userName);
            insertIntoAppointments.setInt(9, customerID);
            insertIntoAppointments.setInt(10, userID);
            insertIntoAppointments.setInt(11, contactID);
            insertIntoAppointments.executeUpdate();
            conn.commit();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setContentText("Appointment added successfully");
            alert.showAndWait();
        } catch(Exception e){
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Error adding appointment.");
            alert.showAndWait();
        }
    }

    /** This method handles inserting a new customer into the database.
     Using a prepared statement, this method takes the parameter information and inserts it into the database.
     If the add is successful, the method alerts the user of the success. If it fails, the user is alerted that the insert has failed.

     @param customer Customer to be added to the database.
     @param user The user creating the new customer. */
    public void insertCustomer(Customer customer, Users user){
        try {
            conn.setAutoCommit(false);
            insertIntoCustomers.setString(1, customer.getName());
            insertIntoCustomers.setString(2, customer.getAddress());
            insertIntoCustomers.setString(3, customer.getPostalCode());
            insertIntoCustomers.setString(4, customer.getPhoneNumber());
            insertIntoCustomers.setString(5, user.getUserName());
            insertIntoCustomers.setString(6, user.getUserName());
            insertIntoCustomers.setInt(7, customer.getDivisionID());
            insertIntoCustomers.executeUpdate();
            conn.commit();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setContentText("Customer added successfully");
            alert.showAndWait();
        } catch(Exception e){
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Error adding customer.");
            alert.showAndWait();
        }
    }

    /** This method handles inserting a new contact into the database.
     Using a prepared statement, this method takes the parameter information and inserts it into the database.
     If the add is successful, the method alerts the user of the success. If it fails, the user is alerted that the insert has failed.

     @param contact Contact to be added to the database. */
    public void insertContact(Contacts contact){
        try {
            conn.setAutoCommit(false);
            insertIntoContacts.setString(1, contact.getContactName());
            insertIntoContacts.setString(2, contact.getEmail());
            insertIntoContacts.executeUpdate();
            conn.commit();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setContentText("Contact added successfully");
            alert.showAndWait();
        } catch(Exception e){
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Error adding contact.");
            alert.showAndWait();
        }
    }

    /** This method handles inserting a new user into the database.
     Using a prepared statement, this method takes the parameter information and inserts it into the database.
     If the add is successful, the method alerts the user of the success. If it fails, the user is alerted that the insert has failed.

     @param user User to be added to the database.
     @param creator The user creating the new user. */
    public void insertUser(Users user, Users creator){
        try{
            conn.setAutoCommit(false);
            insertIntoUsers.setString(1, user.getUserName());
            insertIntoUsers.setString(2, user.getPassword());
            insertIntoUsers.setString(3, creator.getUserName());
            insertIntoUsers.setString(4, creator.getUserName());
            insertIntoUsers.executeUpdate();
            conn.commit();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setContentText("User added successfully");
            alert.showAndWait();
        } catch(Exception e){
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Error adding user");
            alert.showAndWait();
        }
    }

    /** This method updates an existing appointment within the database using a prepared statement.
     If the update is successful, the user is alerted appropriately. If not, the user is also alerted.

     @param user The user updating the appointment.
     @param appointment The appointment to be updated. */
    public void updateAppointment(Appointment appointment, Users user){
        try {
            conn.setAutoCommit(false);
            updateAppointment.setInt(1, appointment.getId());
            updateAppointment.setString(2, appointment.getTitle());
            updateAppointment.setString(3, appointment.getDescription());
            updateAppointment.setString(4, appointment.getLocation());
            updateAppointment.setString(5, appointment.getType());
            updateAppointment.setTimestamp(6, Timestamp.valueOf(appointment.getStart()));
            updateAppointment.setTimestamp(7, Timestamp.valueOf(appointment.getEnd()));
            updateAppointment.setString(8, user.getUserName());
            updateAppointment.setInt(9, appointment.getCustomerId());
            updateAppointment.setInt(10, appointment.getUser());
            updateAppointment.setInt(11, appointment.getContact());
            updateAppointment.setInt(12, appointment.getId());
            updateAppointment.executeUpdate();
            conn.commit();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setContentText("Appointment updated successfully");
            alert.showAndWait();
        } catch(Exception e){
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Error updating appointment.");
            alert.showAndWait();
        }
    }

    /** This method updates an existing customer within the database using a prepared statement.
     If the update is successful, the user is alerted appropriately. If not, the user is also alerted.

     @param user The user updating the appointment.
     @param customer The customer to be updated. */
    public void updateCustomer(Customer customer, Users user){
        try{
            conn.setAutoCommit(false);
            updateCustomer.setInt(1, customer.getCustomerID());
            updateCustomer.setString(2, customer.getName());
            updateCustomer.setString(3, customer.getAddress());
            updateCustomer.setString(4, customer.getPostalCode());
            updateCustomer.setString(5, customer.getPhoneNumber());
            updateCustomer.setString(6, user.getUserName());
            updateCustomer.setInt(7, customer.getDivisionID());
            updateCustomer.setInt(8, customer.getCustomerID());
            updateCustomer.executeUpdate();
            conn.commit();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setContentText("Customer updated successfully");
            alert.showAndWait();
        } catch(Exception e){
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Error updating customer.");
            alert.showAndWait();
        }
    }

    /** This method updates an existing contact within the database using a prepared statement.
     If the update is successful, the user is alerted appropriately. If not, the user is also alerted.

     @param contact The contactto be updated. */
    public void updateContact(Contacts contact){
        try{
            conn.setAutoCommit(false);
            updateContact.setInt(1, contact.getContactID());
            updateContact.setString(2, contact.getContactName());
            updateContact.setString(3, contact.getEmail());
            updateContact.setInt(4, contact.getContactID());
            updateContact.executeUpdate();
            conn.commit();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setContentText("Contact updated successfully");
            alert.showAndWait();
        } catch(Exception e){
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Error updating contact.");
            alert.showAndWait();
        }
    }

    /** This method updates an existing user within the database using a prepared statement.
     If the update is successful, the user is alerted appropriately. If not, the user is also alerted.

     @param updater The user updating the appointment.
     @param user The user to be updated. */
    public void updateUser(Users user, Users updater){
        try{
            conn.setAutoCommit(false);
            updateUser.setInt(1, user.getUserID());
            updateUser.setString(2, user.getUserName());
            updateUser.setString(3, user.getPassword());
            updateUser.setString(4, updater.getUserName());
            updateUser.setInt(5, user.getUserID());
            updateUser.executeUpdate();
            conn.commit();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setContentText("User updated successfully");
            alert.showAndWait();
        } catch(Exception e){
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Error updating user.");
            alert.showAndWait();
        }
    }

    /** This method deletes an appointment from the database using a prepared statement.
     The prepared statement deletes the appointment according to the ID number. If the delete was a success,
     the user is alerted accordingly. If not, the user is also alerted.

     @param appointment The appointment to be deleted. */
    public void deleteAppointment(Appointment appointment){
        try{
            conn.setAutoCommit(false);
            deleteAppointment.setInt(1, appointment.getId());
            deleteAppointment.executeUpdate();
            conn.commit();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setContentText("Appointment with ID of " + appointment.getId() + ", of type '" + appointment.getType() + "' deleted successfully");
            alert.showAndWait();
        } catch(Exception e){
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Error deleting appointment.");
            alert.showAndWait();
        }
    }

    /** This method deletes a customer from the database using a prepared statement.
     The prepared statement deletes the customer according to the ID number. If the delete was a success,
     the user is alerted accordingly. If not, the user is also alerted.

     @param customer The customer to be deleted. */
    public void deleteCustomer(Customer customer){
        try{
            conn.setAutoCommit(false);
            deleteCustomer.setInt(1, customer.getCustomerID());
            deleteCustomer.executeUpdate();
            conn.commit();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setContentText("Customer deleted successfully");
            alert.showAndWait();
        } catch(Exception e){
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Error deleting customer.");
            alert.showAndWait();
        }
    }

    /** This method deletes a contact from the database using a prepared statement.
     The prepared statement deletes the contact according to the ID number. If the delete was a success,
     the user is alerted accordingly. If not, the user is also alerted.

     @param contact The contact to be deleted. */
    public void deleteContact(Contacts contact){
        try{
            conn.setAutoCommit(false);
            deleteContact.setInt(1, contact.getContactID());
            deleteContact.executeUpdate();
            conn.commit();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setContentText("Contact deleted successfully");
            alert.showAndWait();
        } catch(Exception e){
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Error deleting contact.");
            alert.showAndWait();
        }
    }

    /** This method deletes a user from the database using a prepared statement.
     The prepared statement deletes the user according to the ID number. If the delete was a success,
     the user is alerted accordingly. If not, the user is also alerted.

     @param user The user to be deleted. */
    public void deleteUser(Users user){
        try{
            conn.setAutoCommit(false);
            deleteUser.setInt(1, user.getUserID());
            deleteUser.executeUpdate();
            conn.commit();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setContentText("User deleted successfully");
            alert.showAndWait();
        } catch(Exception e){
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Error deleting user.");
            alert.showAndWait();
        }
    }

    /** This method queries the database for all divisions according to the country ID.
     The method creates an observable list of type 'FirstLevelDivision' using the country ID parameter to be used
     throughout the application.

     @param countryId The country ID to be used to filter the first level divisions.
     @return Observable List of type 'FirstLevelDivision' */
    public ObservableList<FirstLevelDivision> queryFLDByCountryID(int countryId){
        try{
            queryFLDByCountry.setInt(1, countryId);
            ResultSet results = queryFLDByCountry.executeQuery();

            ObservableList<FirstLevelDivision> divisionsByCountry = FXCollections.observableArrayList();
            while(results.next()){
                FirstLevelDivision fld = new FirstLevelDivision();
                fld.setId(results.getInt(COLUMN_DIVISION_ID));
                fld.setName(results.getString(COLUMN_DIVISION_NAME));
                fld.setCountry_id(results.getInt(COLUMN_COUNTRY_ID));
                divisionsByCountry.add(fld);
            }
            return divisionsByCountry;
        } catch(Exception e){
            e.printStackTrace();
            System.out.println("Failed query");
            return null;
        }
    }

}
