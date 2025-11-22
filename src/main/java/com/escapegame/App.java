package com.escapegame;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.model.User;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * JavaFX launcher for Whispers of Hollow Manor.
 */
public class App extends Application {

    private static Scene scene;

    /**
     * Starts the JavaFX app.
     * Loads fonts, the "home" FXML, applies styles, and sets up the main window.
     * @param stage main application window
     * @throws Exception if loading fails
     */
    @Override
    public void start(Stage stage) throws Exception {
        Font.loadFont(App.class.getResourceAsStream("/com/escapegame/fonts/Jersey10-Regular.ttf"), 12);
        Parent root = loadFXML("home");
        scene = new Scene(root, 1440, 900);

        try (InputStream is = getClass().getResourceAsStream("/fonts/Jersey10-Regular.ttf")) {
            if (is == null) {
                System.err.println("Font file not found");
            } else {
                javafx.scene.text.Font loaded = javafx.scene.text.Font.loadFont(is, 12);
                if (loaded == null) {
                    System.err.println("Font returned null");
                } else {
                    System.out.println("FONT LOADED OK -> family='" + loaded.getFamily()
                            + "' name='" + loaded.getName() + "'");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Load stylesheet from possible locations
        tryLoadStylesheet(scene);

        stage.setScene(scene);
        stage.setTitle("Whispers of Hollow Manor");
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Replace scene root with the specified FXML.
     * @param fxml FXML name (without extension)
     * @throws IOException if loading fails
     */
    public static void setRoot(String fxml) throws IOException {
        if (scene == null) {
            Parent root = loadFXML("home");
            scene = new Scene(root, 1440, 900);
            tryLoadStylesheet(scene);
        }
        Parent newRoot = loadFXML(fxml);
        try {
            scene.setRoot(newRoot);
        } catch (Exception e) {
            System.err.println("Failed to set root to " + fxml + ": " + e);
            throw e;
        }

        String[] candidates = {
                "/escapegame/styles.css",
                "/com/escapegame/styles.css",
                "/styles.css"
        };
        for (String cssPath : candidates) {
            URL cssUrl = App.class.getResource(cssPath);
            if (cssUrl != null) {
                String cssToAdd = cssUrl.toExternalForm();
                if (!scene.getStylesheets().contains(cssToAdd)) {
                    scene.getStylesheets().add(cssToAdd);
                }
                break;
            }
        }
    }

    /**
     * Tries to load a stylesheet from known paths.
     * @param scene scene to apply styles to
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
            System.err.println("Expected file under src/main/resources.");
        }
    }

    /**
     * Loads an FXML file from /library/{fxml}.fxml.
     * @param fxml FXML file name
     * @return loaded root node
     * @throws IOException if not found
     */
    private static Parent loadFXML(String fxml) throws IOException {
        String res = "/library/" + fxml + ".fxml";
        URL url = App.class.getResource(res);
        if (url == null) {
            String msg = "FXML not found: " + res;
            System.err.println(msg);
            throw new IOException(msg);
        }
        FXMLLoader loader = new FXMLLoader(url);
        return loader.load();
    }

    /**
     * Returns an FXMLLoader for the given FXML file.
     * @param fxmlName name of FXML file
     * @return FXMLLoader
     * @throws IOException if not found
     */
    public static FXMLLoader loadFXMLWithLoader(String fxmlName) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/" + fxmlName + ".fxml"));
        return loader;
    }

    /**
     * Loads and returns the root node for a given FXML.
     * @param fxmlName name of FXML file
     * @return root node
     * @throws IOException if loading fails
     */
    public static Parent loadRoot(String fxmlName) throws IOException {
        FXMLLoader loader = loadFXMLWithLoader(fxmlName);
        return loader.load();
    }

    /**
     * Launches the JavaFX app.
     * @param args CLI arguments
     */
    public static void main(String[] args) {
        launch();
    }

    private static User currentUser;

    /**
     * Sets the current user.
     * @param u user to set
     */
    public static void setCurrentUser(User u) {
        currentUser = u;
    }

    /**
     * Gets the current user.
     * @return current user or null
     */
    public static User getCurrentUser() {
        return currentUser;
    }

    private static String chosenDifficulty = null;

    /**
     * Set the chosen difficulty
     * @param difficulty
     */
    public static void setChosenDifficulty(String difficulty) {
        chosenDifficulty = difficulty;
        System.out.println("App: chosenDifficulty set: " + difficulty);
    }

    /**
     * Get the chosen difficulty
     */
    public static String getChosenDifficulty() {
        return chosenDifficulty;
    }

    /**
     * Clear the chosen difficulty
     */
    public static void clearChosenDifficulty() {
        chosenDifficulty = null;
    }
}
