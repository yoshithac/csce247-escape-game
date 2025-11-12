package com.escapegame;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws Exception {
        // load initial view (library/home.fxml)
        Parent root = loadFXML("home");
        scene = new Scene(root, 1280, 720);

        // NOTE: your styles.css lives in resources/escapegame/styles.css
        // we add it from the classpath root as "/escapegame/styles.css"
        scene.getStylesheets().add(App.class.getResource("/escapegame/styles.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Whispers of Hollow Manor");
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Replace the root of the current scene with the requested FXML (from resources/library/).
     * Example: App.setRoot("login") -> loads /library/login.fxml
     */
    public static void setRoot(String fxml) throws IOException {
        if (scene == null) {
            // defensive fallback: create a scene if start() hasn't initialized it
            Parent root = loadFXML("home");
            scene = new Scene(root, 1280, 720);
            scene.getStylesheets().add(App.class.getResource("/escapegame/styles.css").toExternalForm());
        }
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/library/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}