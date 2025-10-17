module com.escapegame {
requires javafx.controls;
    requires javafx.fxml;
    requires json.simple;
    requires com.google.gson;

    opens com.escapegame to javafx.fxml;
    exports com.escapegame;
}
