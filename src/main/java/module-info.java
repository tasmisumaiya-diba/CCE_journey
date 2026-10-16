module com.crms39.crms39 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;

    opens com.crms39.crms39 to javafx.fxml;
    opens com.crms39.crms39.controller to javafx.fxml;
    opens com.crms39.crms39.model to javafx.base;

    exports com.crms39.crms39;
}