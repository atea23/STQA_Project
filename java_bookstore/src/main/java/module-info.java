module com.example.java_bookstore {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.junit.jupiter.api;
    requires junit;


    opens com.example.java_bookstore to javafx.fxml;
    opens Users;
    opens Model;
    exports com.example.java_bookstore;
}