package com.escapegame;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.text.Normalizer;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import com.model.User;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

/**
 * Controller for the Cipher Puzzle scene — a simple Caesar cipher where "DNWG" decodes to "BLUE" (shift -2).
 * Includes per-user save files, safe state loading, and normalized input checking.
 * Also provides debug logging for saves and resource loading.
 */
public class CipherPuzzleController implements Initializable {

    @FXML private StackPane rootPane;
    @FXML private ImageView backgroundImage;
    @FXML private TextField answerField;
    @FXML private Button btnSubmit, btnHint, btnSave, btnQuit;
    @FXML private Label statusLabel, hintsLabel, categoryLabel, promptLabel, cipherLabel;
    @FXML private HBox heartsBox;

    // Game state
    private int attemptsLeft = 3;
    private int hintsLeft = 3;
    private int nextHintIndex = 0;
    private boolean solved = false;

    // Filled puzzle data (easy Caesar cipher)
    private final String[] HINTS = {
        "It's a Caesar-style cipher (letters shifted).",
        "Shift each letter back by 2 in the alphabet.",
        "The result is a 4-letter color that starts with B."
    };

    // Accepted answers (we normalize input before comparing)
    private final String[] ACCEPTED_ANSWERS = {
        "blue",
        "a blue",
        "the color blue",
        "the colour blue"
    };

    // resource and dev fallback paths
    private static final String RESOURCE_PATH = "/images/background.png";
    private static final String DEV_FALLBACK = "file:/mnt/data/Screenshot 2025-11-19 221015.png";
      /**
     * Initializes the cipher puzzle scene, setting up images, UI text, bindings, and save state handling.
     * It loads any saved progress, restores the player’s state, and prepares the interface.
     * @param location  the location used to resolve relative paths for the root object
     * @param resources the resources used to localize the root object
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    System.out.println("CipherPuzzleController.initialize() start");

    boolean loaded = false;
    try {
        URL res = getClass().getResource(RESOURCE_PATH);
        if (res != null) {
            Image img = new Image(res.toExternalForm());
            if (backgroundImage != null) backgroundImage.setImage(img);
            loaded = true;
            System.out.println("Loaded cipher background from resource.");
        }
    } catch (Exception ex) {
        System.err.println("Error loading resource image: " + ex.getMessage());
    }

    if (!loaded) {
        try {
            Image img = new Image(DEV_FALLBACK);
            if (backgroundImage != null) backgroundImage.setImage(img);
            loaded = true;
            System.out.println("Loaded cipher background from dev fallback.");
        } catch (Exception ex) {
            System.err.println("Error loading dev fallback image: " + ex.getMessage());
        }
    }

    if (!loaded) {
        if (statusLabel != null) statusLabel.setText("Background image not found (resource nor dev path).");
    }

    if (backgroundImage != null && rootPane != null) {
        backgroundImage.fitWidthProperty().bind(rootPane.widthProperty());
        backgroundImage.fitHeightProperty().bind(rootPane.heightProperty());
        backgroundImage.setPreserveRatio(false);
    }

    // initialize UI text for the puzzle
    if (hintsLabel != null) hintsLabel.setText(hintsLeft + " hint(s) available");
    if (cipherLabel != null) cipherLabel.setText("Cipher: DNWG"); // the cipher to decode
    if (categoryLabel != null) categoryLabel.setText("Category: Color");
    if (promptLabel != null) promptLabel.setText("Prompt: Decode DNWG (Caesar shift -2)");
    refreshHearts();

    // debug: show exactly which save file will be used
    System.out.println("DEBUG: Cipher save path -> " + getSaveFileForCurrentUser().getAbsolutePath());

    // load saved state from file
    loadSave();

    // *** AUTOMATIC RESET: if a persisted save marks the puzzle solved, clear it and reset UI ***
    if (solved) {
        System.out.println("DEBUG: Saved state indicates solved=true — clearing save to reset puzzle.");
        // delete the save so next launch is fresh
        clearSaveForCurrentUser();
        // reset in-memory state to defaults so UI is interactive
        solved = false;
        attemptsLeft = 3;
        hintsLeft = 3;
        nextHintIndex = 0;
        if (hintsLabel != null) hintsLabel.setText(hintsLeft + " hint(s) available");
        if (statusLabel != null) statusLabel.setText("");
        if (btnSubmit != null) btnSubmit.setDisable(false);
        if (btnHint != null) btnHint.setDisable(false);
        if (answerField != null) answerField.setDisable(false);
        refreshHearts();
    }

    System.out.println("CipherPuzzleController.initialize() done");
}


    /**
     * Returns the per user save file for this cipher puzzle, falling back to "guest" if no user is found.
     * Uses reflection as a backup to obtain the user ID if the App class methods differ.
     * @return File pointing to the save location for the current user
     */
    private File getSaveFileForCurrentUser() {
        String userId = "guest";
        try {
            User u = null;
            try {
                u = App.getCurrentUser();
            } catch (Throwable t) {
                // ignore — try reflection (defensive) or remain guest
                try {
                    java.lang.reflect.Method gu = App.class.getMethod("getCurrentUser");
                    Object userObj = gu.invoke(null);
                    if (userObj instanceof User) {
                        u = (User) userObj;
                    } else if (userObj != null) {
                        // try to call getUserId via reflection
                        try {
                            java.lang.reflect.Method getId = userObj.getClass().getMethod("getUserId");
                            Object idObj = getId.invoke(userObj);
                            if (idObj != null) {
                                final String idStr = idObj.toString();
                                // create a small temporary User-like object to pass the id
                                u = new User() {
                                    @Override public String getUserId() { return idStr; }
                                };
                            }
                        } catch (Throwable ignored) { }
                    }
                } catch (Throwable ignored) { }
            }
            if (u != null && u.getUserId() != null && !u.getUserId().trim().isEmpty()) {
                userId = u.getUserId().trim();
            }
        } catch (Throwable t) {
            // ignore; userId stays guest
        }
        String clean = userId.replaceAll("\\s+", "_").toLowerCase(Locale.ROOT);
        String filename = ".escapegame_cipher_" + clean + ".properties";
        return new File(System.getProperty("user.home"), filename);
    }
     /**
     * Refreshes the heart icons representing remaining attempts in the UI.
     * Disables input controls when no attempts remain.
     */
    private void refreshHearts() {
        if (heartsBox == null) return;
        heartsBox.getChildren().clear();
        for (int i = 0; i < Math.max(0, attemptsLeft); i++) {
            Label heart = new Label("\u2764");
            heart.setStyle("-fx-text-fill: #ff4d6d; -fx-font-size: 20px;");
            heartsBox.getChildren().add(heart);
        }
        if (attemptsLeft <= 0) {
            if (btnSubmit != null) btnSubmit.setDisable(true);
            if (answerField != null) answerField.setDisable(true);
        }
    }
     /**
     * Handles submission of the user’s answer, validates correctness, updates game state,
     * and provides feedback via alerts and status labels.
     */
    @FXML
    private void onSubmit() {
        if (solved) {
            if (statusLabel != null) statusLabel.setText("You already solved the cipher.");
            return;
        }

        String raw = (answerField == null || answerField.getText() == null) ? "" : answerField.getText();
        String answer = normalize(raw);
        if (answer.isEmpty()) {
            if (statusLabel != null) statusLabel.setText("Please enter an answer.");
            return;
        }

        boolean ok = false;
        for (String a : ACCEPTED_ANSWERS) {
            if (normalize(a).equals(answer)) { ok = true; break; }
        }

        if (ok) {
            solved = true;
            if (statusLabel != null) statusLabel.setText("Correct! You decoded it.");
            if (btnSubmit != null) btnSubmit.setDisable(true);
            if (btnHint != null) btnHint.setDisable(true);
            if (answerField != null) answerField.setDisable(true);
            new Alert(Alert.AlertType.INFORMATION, "Nice! The cipher DNWG decodes to BLUE.").showAndWait();
            saveProgress();
            try { App.setRoot("opened3"); } catch (IOException e) { e.printStackTrace(); }
        } else {
            attemptsLeft = Math.max(0, attemptsLeft - 1);
            refreshHearts();
            if (attemptsLeft <= 0) {
                if (statusLabel != null) statusLabel.setText("No attempts left. The correct answer was: \"blue\".");
                if (btnSubmit != null) btnSubmit.setDisable(true);
                if (answerField != null) answerField.setDisable(true);
                new Alert(Alert.AlertType.WARNING, "Out of attempts!").showAndWait();
                saveProgress();
            } else {
                if (statusLabel != null) statusLabel.setText("Incorrect. Attempts left: " + attemptsLeft);
            }
        }
    }
    /**
     * Handles hint button logic — shows the next hint and decreases the available hint count.
     */
    @FXML
    private void onHint() {
        if (solved) {
            if (statusLabel != null) statusLabel.setText("You already solved the puzzle.");
            return;
        }
        if (hintsLeft <= 0) {
            if (statusLabel != null) statusLabel.setText("No hints remaining.");
            return;
        }

        String hint = HINTS[Math.min(nextHintIndex, HINTS.length - 1)];
        nextHintIndex = Math.min(nextHintIndex + 1, HINTS.length);
        hintsLeft = Math.max(0, hintsLeft - 1);
        if (hintsLabel != null) hintsLabel.setText(hintsLeft + " hint(s) available");
        new Alert(Alert.AlertType.INFORMATION, hint).showAndWait();
        saveProgress();
    }
     /**
     * Saves the current progress manually when the player clicks the save button.
     */
    @FXML
    private void onSave() {
        if (statusLabel != null) statusLabel.setText(saveProgress() ? "Progress saved." : "Save failed.");
    }
    /**
     * Quits the current puzzle and returns to the previous scene.
     */
    @FXML
    private void onQuit() {
        try {
            App.setRoot("opened2");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
      /**
     * Saves puzzle state (attempts, hints, solved flag, etc.) to a per user properties file.
     * @return true if save succeeded, false otherwise
     */
    private boolean saveProgress() {
        try {
            Properties p = new Properties();
            p.setProperty("attemptsLeft", String.valueOf(attemptsLeft));
            p.setProperty("hintsLeft", String.valueOf(hintsLeft));
            p.setProperty("solved", String.valueOf(solved));
            p.setProperty("nextHintIndex", String.valueOf(nextHintIndex));
            File out = getSaveFileForCurrentUser();
            try (OutputStream os = new FileOutputStream(out)) {
                p.store(os, "Cipher puzzle save");
            }
            System.out.println("DEBUG saved -> " + out.getAbsolutePath());
            return true;
        } catch (Exception e) {
            System.err.println("Save error: " + e.getMessage());
            return false;
        }
    }
     /**
     * Loads previously saved puzzle state from the user's save file, restoring progress.
     * If no save file exists, the method returns silently.
     */
    private void loadSave() {
        try {
            File f = getSaveFileForCurrentUser();
            if (!f.exists()) {
                System.out.println("DEBUG: save file not found -> " + f.getAbsolutePath());
                return;
            }
            Properties p = new Properties();
            try (FileInputStream fis = new FileInputStream(f)) {
                p.load(fis);
            }
            attemptsLeft = getSafeInt(p.getProperty("attemptsLeft"), attemptsLeft);
            hintsLeft = getSafeInt(p.getProperty("hintsLeft"), hintsLeft);
            solved = Boolean.parseBoolean(p.getProperty("solved", String.valueOf(solved)));
            nextHintIndex = getSafeInt(p.getProperty("nextHintIndex"), nextHintIndex);
            if (hintsLabel != null) hintsLabel.setText(hintsLeft + " hint(s) available");
            refreshHearts();

            System.out.println("DEBUG loaded - attemptsLeft=" + attemptsLeft
                + " hintsLeft=" + hintsLeft + " nextHintIndex=" + nextHintIndex + " solved=" + solved
                + " (from " + f.getAbsolutePath() + ")");

            if (solved) {
                if (btnSubmit != null) btnSubmit.setDisable(true);
                if (answerField != null) answerField.setDisable(true);
                if (btnHint != null) btnHint.setDisable(true);
                if (statusLabel != null) statusLabel.setText("Already solved.");
            }
        } catch (Exception e) {
            System.err.println("Load save failed: " + e.getMessage());
        }
    }

     /**
     * Parses a string safely into an integer, falling back to a default value on failure.
     * @param s the string to parse
     * @param fallback  the default value to use if parsing fails
     * @return parsed integer or fallback
     */
    private int getSafeInt(String s, int fallback) {
        if (s == null) return fallback;
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException ex) {
            System.err.println("Invalid integer in save file: \"" + s + "\"; using fallback " + fallback);
            return fallback;
        }
    }

     /**
     * Normalizes a raw text input by removing punctuation, lowercasing,
     * and stripping leading articles for consistent answer comparison.
     * @param raw the original input string
     * @return normalized string
     */
    private static String normalize(String raw) {
        if (raw == null) return "";
        String n = Normalizer.normalize(raw, Normalizer.Form.NFKC)
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^\\p{L}\\p{N}\\s]", " ")
                .trim()
                .replaceAll("\\s+", " ");
        if (n.startsWith("a ")) n = n.substring(2).trim();
        else if (n.startsWith("an ")) n = n.substring(3).trim();
        else if (n.startsWith("the ")) n = n.substring(4).trim();
        return n;
    }

    /**
     * Helper used for debugging/testing to delete the current user's save file.
     * Typically called to reset progress between sessions or tests.
     * @return true if file deleted successfully or not found, false otherwise
     */
    private boolean clearSaveForCurrentUser() {
        File f = getSaveFileForCurrentUser();
        if (f.exists()) {
            boolean deleted = f.delete();
            System.out.println("DEBUG: clearSaveForCurrentUser() deleted=" + deleted + " -> " + f.getAbsolutePath());
            return deleted;
        } else {
            System.out.println("DEBUG: clearSaveForCurrentUser() not found -> " + f.getAbsolutePath());
            return true;
        }
    }

}
