module com.escapegame {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires java.base;

    requires java.desktop;

    opens com.escapegame to javafx.fxml;
    exports com.escapegame;

    opens com.model to com.google.gson;
    exports com.model;
}
