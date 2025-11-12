package com.escapegame;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.model.User;

/**
 * JavaFX launcher for Whispers of Hollow Manor.
 * This version automatically looks for a stylesheet in multiple common locations.
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = loadFXML("home");
        scene = new Scene(root, 1440, 900);

            try (InputStream is = getClass().getResourceAsStream("/fonts/Jersey10-Regular.ttf")) {
        if (is == null) {
            System.err.println("FONT FILE NOT FOUND AT: /fonts/Jersey10-Regular.ttf");
        } else {
            javafx.scene.text.Font loaded = javafx.scene.text.Font.loadFont(is, 12);
            if (loaded == null) {
                System.err.println("Font.loadFont(...) returned null (failed to parse font)");
            } else {
                System.out.println("FONT LOADED OK -> family='" + loaded.getFamily()
                    + "' name='" + loaded.getName() + "'");
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
        // Load stylesheet automatically from possible locations
        tryLoadStylesheet(scene);

        stage.setScene(scene);
        stage.setTitle("Whispers of Hollow Manor");
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Replace scene root with the specified FXML (from /library/{fxml}.fxml).
     */
    public static void setRoot(String fxml) throws IOException {
        if (scene == null) {
            Parent root = loadFXML("home");
            scene = new Scene(root, 1440, 900);
            tryLoadStylesheet(scene);
        }
        scene.setRoot(loadFXML(fxml));
    }

    /**
     * Attempts to load the stylesheet from several common paths.
     */
    private static void tryLoadStylesheet(Scene scene) {
        String[] candidates = {
            "/escapegame/styles.css",
            "/com/escapegame/styles.css",
            "/styles.css"
        };

        boolean loaded = false;
        for (String cssPath : candidates) {
            URL cssUrl = App.class.getResource(cssPath);
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
                System.out.println("Loaded stylesheet: " + cssPath);
                loaded = true;
                break;
            }
        }

        if (!loaded) {
            System.err.println("WARNING: stylesheet not found. Tried:");
            for (String path : candidates) System.err.println("  " + path);
            System.err.println("Expected file under src/main/resources matching one of those paths.");
        }
    }

    /**
     * Loads an FXML file from /library/{fxml}.fxml.
     */
    private static Parent loadFXML(String fxml) throws IOException {
        String res = "/library/" + fxml + ".fxml";
        URL url = App.class.getResource(res);
        if (url == null) {
            String msg = "FXML not found: " + res + " (expected in src/main/resources" + res + ")";
            System.err.println(msg);
            throw new IOException(msg);
        }
        FXMLLoader loader = new FXMLLoader(url);
        return loader.load();
    }

    public static FXMLLoader loadFXMLWithLoader(String fxmlName) throws IOException {
    FXMLLoader loader = new FXMLLoader(App.class.getResource("/" + fxmlName + ".fxml"));
    return loader;
}

    public static Parent loadRoot(String fxmlName) throws IOException {
    FXMLLoader loader = loadFXMLWithLoader(fxmlName);
    return loader.load();
}



    public static void main(String[] args) {
        launch();
    }
    private static User currentUser;
public static void setCurrentUser(User u) { currentUser = u; }
public static User getCurrentUser() { return currentUser; }
}