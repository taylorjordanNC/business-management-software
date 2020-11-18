module C195JavaFXProject {
    requires javafx.fxml;
    requires javafx.controls;
    requires java.sql;
    requires mysql.connector.java;
    requires javafx.graphics;

    opens main;
    opens controller;
    opens database;
    opens view;
    opens model;
}