module com.escapegame {
    requires javafx.controls;
    requires javafx.fxml;
    requires json.simple;
    requires com.google.gson;
    requires java.base;

    requires java.desktop;

    opens com.escapegame to javafx.fxml;
    exports com.escapegame;
}
