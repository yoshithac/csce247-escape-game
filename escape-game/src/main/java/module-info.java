module com.escapegame {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.escapegame to javafx.fxml;
    exports com.escapegame;
}
