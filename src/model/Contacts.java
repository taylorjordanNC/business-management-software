package model;

/** This class creates and manages objects of type 'Contacts' and all associated variables. */
public class Contacts {
    private Integer contactID;
    private String contactName;
    private String email;

    public Contacts() {
    }

    public int getContactID() {
        return contactID;
    }

    public void setContactID(Integer contactID) {
        this.contactID = contactID;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
