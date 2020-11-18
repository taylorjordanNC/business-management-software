package model;

/** This class creates and manages objects of type 'Users' and all associated variables. */
public class Users {
    private Integer userID;
    private String userName;
    private String password;

    public Users() {
    }
    public int getUserID() {
        return userID;
    }
    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
