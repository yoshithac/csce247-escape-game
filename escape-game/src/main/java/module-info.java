module com.escapegame {
    requires javafx.controls;
    requires javafx.fxml;
    requires json.simple;

    opens com.escapegame to javafx.fxml;
    exports com.escapegame;
}
