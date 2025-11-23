package com.escapegame;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

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
 * Anagram puzzle controller — scrambled LPAEP -> APPLE (category Fruit)
 * Manages the anagram puzzle UI: background loading, attempts/hints state,
 * simple persistence to a properties file, and navigation on solve/quit.
 * UI references are checked for null to be defensive when used from FXML.
 */
public class AnagramPuzzleController implements Initializable {

    @FXML private StackPane rootPane;
    @FXML private ImageView backgroundImage;
    @FXML private TextField answerField;
    @FXML private Button btnSubmit, btnHint, btnSave, btnQuit;
    @FXML private Label statusLabel, hintsLabel, categoryLabel, promptLabel, scrambledLabel;
    @FXML private HBox heartsBox;

    // Game state
    private int attemptsLeft = 3;
    private int hintsLeft = 3;
    private int nextHintIndex = 0;
    private boolean solved = false;

    private final String[] HINTS = {
        "It's a common fruit.",
        "Kids are often told 'an ___ a day keeps the doctor away.'",
        "It has five letters and starts with A."
    };

    private final String[] ACCEPTED_ANSWERS = {
        "apple",
        "an apple",
        "the fruit apple",
        "apple fruit"
    };

    private final File SAVE_FILE = new File(System.getProperty("user.home"), ".escapegame_anagram.properties");

    private static final String RESOURCE_PATH = "/images/background.png";
    private static final String DEV_FALLBACK = "file:/mnt/data/Screenshot 2025-11-22 202825.png";
     /**
     * Initialize the puzzle screen: load background image (resource then fallback),
     * set initial UI labels, draw hearts and restore saved progress.
     * @param location  not used
     * @param resources not used
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("AnagramPuzzleController.initialize() start");

        boolean loaded = false;
        try {
            URL res = getClass().getResource(RESOURCE_PATH);
            if (res != null) {
                Image img = new Image(res.toExternalForm());
                backgroundImage.setImage(img);
                loaded = true;
            }
        } catch (Exception ex) {
            System.err.println("Error loading resource image: " + ex.getMessage());
        }

        if (!loaded) {
            try {
                Image img = new Image(DEV_FALLBACK);
                backgroundImage.setImage(img);
                loaded = true;
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

        // initialize UI labels for puzzle
        if (hintsLabel != null) hintsLabel.setText(hintsLeft + " hint(s) available");
        if (scrambledLabel != null) scrambledLabel.setText("Scrambled: LPAEP");
        if (categoryLabel != null) categoryLabel.setText("Category: Fruit");
        if (promptLabel != null) promptLabel.setText("Prompt: Unscramble the letters to find the word");
        refreshHearts();
        loadSave();

        System.out.println("AnagramPuzzleController.initialize() done");
    }
    /**
     * Draw hearts representing remaining attempts and disable inputs when none remain.
     */
    private void refreshHearts() {
        if (heartsBox == null) return;
        heartsBox.getChildren().clear();
        for (int i = 0; i < attemptsLeft; i++) {
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
     * Called when the user submits an answer. Validates input, checks accepted
     * answers, updates state, shows alerts, saves progress and navigates on success.
     */
    @FXML
    private void onSubmit() {
        if (solved) {
            if (statusLabel != null) statusLabel.setText("You already solved the puzzle.");
            return;
        }

        String answer = (answerField == null || answerField.getText() == null) ? "" : answerField.getText().trim().toLowerCase();
        if (answer.isEmpty()) {
            if (statusLabel != null) statusLabel.setText("Please enter an answer.");
            return;
        }

        boolean ok = false;
        for (String a : ACCEPTED_ANSWERS) {
            if (a.equals(answer)) { ok = true; break; }
        }

        if (ok) {
            solved = true;
            if (statusLabel != null) statusLabel.setText("Correct! It's APPLE.");
            if (btnSubmit != null) btnSubmit.setDisable(true);
            if (btnHint != null) btnHint.setDisable(true);
            if (answerField != null) answerField.setDisable(true);
            new Alert(Alert.AlertType.INFORMATION, "Nice — LPAEP unscrambles to APPLE.").showAndWait();
            saveProgress();
            try { App.setRoot("opened5"); } catch (IOException e) { e.printStackTrace(); }
        } else {
            attemptsLeft--;
            refreshHearts();
            if (attemptsLeft <= 0) {
                if (statusLabel != null) statusLabel.setText("No attempts left. The correct answer was: \"apple\".");
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
     * Shows the next hint (if available), updates hint counters and saves progress.
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
        nextHintIndex++;
        hintsLeft--;
        if (hintsLabel != null) hintsLabel.setText(hintsLeft + " hint(s) available");
        new Alert(Alert.AlertType.INFORMATION, hint).showAndWait();
        saveProgress();
    }
    /** Saves progress to the user's properties file and updates the status label. */
    @FXML
    private void onSave() {
        if (statusLabel != null) statusLabel.setText(saveProgress() ? "Progress saved." : "Save failed.");
    }
    /** Quit action: navigate back to the previous/opened4 screen. */
    @FXML
    private void onQuit() {
        try { App.setRoot("opened4");
        } catch (IOException e) { 
            e.printStackTrace(); 
        }
    }
    /**
     * Persist puzzle state to a properties file under the user's home directory.
     * @return true if save succeeded, false otherwise
     */
    private boolean saveProgress() {
        try {
            Properties p = new Properties();
            p.setProperty("attemptsLeft", String.valueOf(attemptsLeft));
            p.setProperty("hintsLeft", String.valueOf(hintsLeft));
            p.setProperty("solved", String.valueOf(solved));
            p.setProperty("nextHintIndex", String.valueOf(nextHintIndex));
            try (OutputStream os = new FileOutputStream(SAVE_FILE)) {
                p.store(os, "Anagram puzzle save");
            }
            return true;
        } catch (Exception e) {
            System.err.println("Save error: " + e.getMessage());
            return false;
        }
    }
    /**
     * Load saved puzzle state from properties file if present and update UI.
     */
    private void loadSave() {
        try {
            if (!SAVE_FILE.exists()) return;
            Properties p = new Properties();
            try (FileInputStream fis = new FileInputStream(SAVE_FILE)) {
                p.load(fis);
            }
            attemptsLeft = Integer.parseInt(p.getProperty("attemptsLeft", "3"));
            hintsLeft = Integer.parseInt(p.getProperty("hintsLeft", "3"));
            solved = Boolean.parseBoolean(p.getProperty("solved", "false"));
            nextHintIndex = Integer.parseInt(p.getProperty("nextHintIndex", "0"));
            if (hintsLabel != null) hintsLabel.setText(hintsLeft + " hint(s) available");
            refreshHearts();

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
}