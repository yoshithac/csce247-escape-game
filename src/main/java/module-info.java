module com.escapegame {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires java.base;
    requires javafx.graphics;
    requires javafx.base;
    requires freetts;  
    requires junit; 

    requires java.desktop;

    // Export main packages
    exports com.escapegame;
    exports com.escapegame.controller;
    exports com.escapegame.util;
    exports com.model;

    // Open packages for FXML injection and JSON serialization
    opens com.escapegame to javafx.fxml;
    opens com.escapegame.controller to javafx.fxml;
    opens com.escapegame.util to javafx.fxml;
    opens com.model to com.google.gson, javafx.base;
}
