package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/** This class handles type objects for appointments within the application. */
public class Type {
    private String typeName;
    private int count;

    private static ObservableList<String> types = FXCollections.observableArrayList();

    /** This method is the constructor for the type object and allows the class to be instantiated.
     @param count The count of a particular type object.
     @param typeName The name of the type object. */
    public Type(String typeName, int count) {
        this.typeName = typeName;
        this.count = count;
    }

    /** This method gets the name of a particular object 'Type'.
     @return String variable */
    public String getTypeName() {
        return typeName;
    }

    /** This method gets the count of a particular object 'Type'.
     @return int variable */
    public int getCount() {
        return count;
    }

    /** This method returns a list of type objects to be used by the application.
     This method returns a pre-set list of types the user may choose from when selecting the type of a appointment
     they are creating or updating.

     @return Observable list of type 'String' */
    public static ObservableList<String> getTypes() {
        types.setAll("Planning Session", "De-Briefing", "Networking", "Sales", "Other");
        return types;
    }
}
