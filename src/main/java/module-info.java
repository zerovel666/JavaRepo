module com.aziz.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jbcrypt;
    requires jdk.httpserver;


    opens com.aziz.demo to javafx.fxml;
    exports com.aziz.demo;
}