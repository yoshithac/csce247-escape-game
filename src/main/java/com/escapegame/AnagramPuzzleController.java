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
 * Anagram puzzle controller — scrambled LPAEP -> APPLE (category Fruit)
 * Improvements:
 *  - per-user save file (.escapegame_anagram_<userid>.properties)
 *  - debug prints showing the save path and loaded state
 *  - safe parsing of integer properties
 *  - normalization of user input for robust matching
 *  - clearSaveForCurrentUser() helper for testing
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

    private static final String RESOURCE_PATH = "/images/background.png";
    private static final String DEV_FALLBACK = "file:/mnt/data/Screenshot 2025-11-22 202825.png";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("AnagramPuzzleController.initialize() start");

        boolean loaded = false;
        try {
            URL res = getClass().getResource(RESOURCE_PATH);
            if (res != null) {
                Image img = new Image(res.toExternalForm());
                if (backgroundImage != null) backgroundImage.setImage(img);
                loaded = true;
            }
        } catch (Exception ex) {
            System.err.println("Error loading resource image: " + ex.getMessage());
        }

        if (!loaded) {
            try {
                Image img = new Image(DEV_FALLBACK);
                if (backgroundImage != null) backgroundImage.setImage(img);
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
        if (promptLabel != null) promptLabel.setText("Prompt: Unscramble the letters to find the word: LPAEP");
        refreshHearts();

        // debug: show exactly which save file will be used
        System.out.println("DEBUG: Anagram save path -> " + getSaveFileForCurrentUser().getAbsolutePath());

        loadSave();

        // If save marked solved=true, automatically clear it (one-time reset) and re-enable UI
        if (solved) {
            System.out.println("DEBUG: Saved state indicates solved=true — clearing save to reset puzzle.");
            clearSaveForCurrentUser();
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

        System.out.println("AnagramPuzzleController.initialize() done");
    }

    private File getSaveFileForCurrentUser() {
        String userId = "guest";
        try {
            User u = null;
            try {
                u = App.getCurrentUser();
            } catch (Throwable t) {
                // fallback to reflection if needed
                try {
                    java.lang.reflect.Method gu = App.class.getMethod("getCurrentUser");
                    Object userObj = gu.invoke(null);
                    if (userObj instanceof User) {
                        u = (User) userObj;
                    } else if (userObj != null) {
                        try {
                            java.lang.reflect.Method getId = userObj.getClass().getMethod("getUserId");
                            Object idObj = getId.invoke(userObj);
                            if (idObj != null) {
                                final String idStr = idObj.toString();
                                u = new User() { @Override public String getUserId() { return idStr; } };
                            }
                        } catch (Throwable ignored) { }
                    }
                } catch (Throwable ignored) { }
            }
            if (u != null && u.getUserId() != null && !u.getUserId().trim().isEmpty()) {
                userId = u.getUserId().trim();
            }
        } catch (Throwable t) {
            // ignore, use guest
        }
        String clean = userId.replaceAll("\\s+", "_").toLowerCase(Locale.ROOT);
        String filename = ".escapegame_anagram_" + clean + ".properties";
        return new File(System.getProperty("user.home"), filename);
    }

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

    @FXML
    private void onSubmit() {
        if (solved) {
            if (statusLabel != null) statusLabel.setText("You already solved the puzzle.");
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
            if (statusLabel != null) statusLabel.setText("Correct! It's APPLE.");
            if (btnSubmit != null) btnSubmit.setDisable(true);
            if (btnHint != null) btnHint.setDisable(true);
            if (answerField != null) answerField.setDisable(true);
            new Alert(Alert.AlertType.INFORMATION, "Nice — LPAEP unscrambles to APPLE.").showAndWait();
            saveProgress();
            try { App.setRoot("opened5"); } catch (IOException e) { e.printStackTrace(); }
        } else {
            attemptsLeft = Math.max(0, attemptsLeft - 1);
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

    @FXML
    private void onSave() {
        if (statusLabel != null) statusLabel.setText(saveProgress() ? "Progress saved." : "Save failed.");
    }

    @FXML
    private void onQuit() {
        try { App.setRoot("opened4"); } catch (IOException e) { e.printStackTrace(); }
    }

    private boolean saveProgress() {
        try {
            Properties p = new Properties();
            p.setProperty("attemptsLeft", String.valueOf(attemptsLeft));
            p.setProperty("hintsLeft", String.valueOf(hintsLeft));
            p.setProperty("solved", String.valueOf(solved));
            p.setProperty("nextHintIndex", String.valueOf(nextHintIndex));
            File out = getSaveFileForCurrentUser();
            try (OutputStream os = new FileOutputStream(out)) {
                p.store(os, "Anagram puzzle save");
            }
            System.out.println("DEBUG saved -> " + out.getAbsolutePath());
            return true;
        } catch (Exception e) {
            System.err.println("Save error: " + e.getMessage());
            return false;
        }
    }

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

    private int getSafeInt(String s, int fallback) {
        if (s == null) return fallback;
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException ex) {
            System.err.println("Invalid integer in save file: \"" + s + "\"; using fallback " + fallback);
            return fallback;
        }
    }

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