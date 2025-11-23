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
 * CipherPuzzleController — easy Caesar cipher: DNWG -> BLUE (shift -2)
 * Save/load is per-user: ~/.escapegame_cipher_<userid>.properties
 */
public class CipherPuzzleController implements Initializable {

    @FXML private StackPane rootPane;
    @FXML private ImageView backgroundImage;
    @FXML private TextField answerField;
    @FXML private Button btnSubmit, btnHint, btnSave, btnQuit;
    @FXML private Label statusLabel, hintsLabel, categoryLabel, promptLabel, cipherLabel;
    @FXML private HBox heartsBox;

    // gameplay state
    private int attemptsLeft = 3;
    private int hintsLeft = 3;
    private int nextHintIndex = 0;
    private boolean solved = false;

    private final String[] HINTS = {
        "Think Caesar cipher (each letter shifted).",
        "Shift each letter back by 2 (D->B, N->L, W->U, G->E).",
        "The result is a 4-letter color that starts with B."
    };

    private final String[] ACCEPTED_ANSWERS = { "blue", "a blue", "the color blue", "the colour blue" };

    // resource + dev fallback (dev path from your workspace)
    private static final String RESOURCE_PATH = "/images/background.png";
    private static final String DEV_FALLBACK = "file:/mnt/data/Screenshot 2025-11-19 221015.png";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("CipherPuzzleController.initialize() start");

        boolean loaded = false;
        try {
            URL res = getClass().getResource(RESOURCE_PATH);
            if (res != null) {
                Image img = new Image(res.toExternalForm());
                backgroundImage.setImage(img);
                loaded = true;
                System.out.println("Loaded cipher background from resource.");
            }
        } catch (Exception ex) {
            System.err.println("Error loading resource image: " + ex.getMessage());
        }

        if (!loaded) {
            try {
                Image img = new Image(DEV_FALLBACK);
                backgroundImage.setImage(img);
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

        // initialize UI content (reset UI state on each initialize)
        attemptsLeft = 3;
        hintsLeft = 3;
        nextHintIndex = 0;
        solved = false;

        if (hintsLabel != null) hintsLabel.setText(hintsLeft + " hint(s) available");
        if (cipherLabel != null) cipherLabel.setText("Cipher: DNWG");
        if (categoryLabel != null) categoryLabel.setText("Category: Color");
        if (promptLabel != null) promptLabel.setText("Decode the cipher text and enter the color.");
        refreshHearts();

        // load per-user save (if any)
        loadSave();

        System.out.println("CipherPuzzleController.initialize() done");
    }

    // compute save file path depending on current user (if available)
    private File getSaveFile() {
        String userHome = System.getProperty("user.home");
        String filename = ".escapegame_cipher.properties";

        try {
            // try to call App.getCurrentUser() reflectively (keeps compatibility if App changes)
            java.lang.reflect.Method gu = com.escapegame.App.class.getMethod("getCurrentUser");
            Object user = gu.invoke(null);
            if (user != null) {
                try {
                    java.lang.reflect.Method getId = user.getClass().getMethod("getUserId");
                    Object idObj = getId.invoke(user);
                    if (idObj != null) {
                        String uid = idObj.toString().trim();
                        if (!uid.isEmpty()) {
                            // safe filename: lowercase and remove spaces
                            String clean = uid.replaceAll("\\s+", "_").toLowerCase();
                            filename = ".escapegame_cipher_" + clean + ".properties";
                        }
                    }
                } catch (NoSuchMethodException nsme) {
                    // user object doesn't expose getUserId; ignore and use default filename
                }
            }
        } catch (NoSuchMethodException nsme) {
            // App.getCurrentUser not available; keep default filename
        } catch (Throwable t) {
            System.err.println("getSaveFile(): reflection error: " + t);
        }

        return new File(userHome, filename);
    }

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

    @FXML
    private void onSubmit() {
        if (solved) {
            if (statusLabel != null) statusLabel.setText("You already solved the cipher.");
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
            if (statusLabel != null) statusLabel.setText("Correct! You decoded it.");
            if (btnSubmit != null) btnSubmit.setDisable(true);
            if (btnHint != null) btnHint.setDisable(true);
            if (answerField != null) answerField.setDisable(true);
            new Alert(Alert.AlertType.INFORMATION, "Nice! The cipher DNWG decodes to BLUE.").showAndWait();
            saveProgress();
            try { App.setRoot("opened3"); } catch (IOException e) { e.printStackTrace(); }
        } else {
            attemptsLeft--;
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

    @FXML
    private void onHint() {
        if (solved) {
            if (statusLabel != null) statusLabel.setText("You already solved the cipher.");
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

    @FXML
    private void onSave() {
        if (statusLabel != null) statusLabel.setText(saveProgress() ? "Progress saved." : "Save failed.");
    }

    @FXML
    private void onQuit() {
        try { App.setRoot("opened2"); } catch (IOException e) { e.printStackTrace(); }
    }

    private boolean saveProgress() {
        try {
            Properties p = new Properties();
            p.setProperty("attemptsLeft", String.valueOf(attemptsLeft));
            p.setProperty("hintsLeft", String.valueOf(hintsLeft));
            p.setProperty("solved", String.valueOf(solved));
            p.setProperty("nextHintIndex", String.valueOf(nextHintIndex));
            File f = getSaveFile();
            try (OutputStream os = new FileOutputStream(f)) {
                p.store(os, "Cipher puzzle save");
            }
            return true;
        } catch (Exception e) {
            System.err.println("Save error: " + e.getMessage());
            return false;
        }
    }

    private void loadSave() {
        try {
            File f = getSaveFile();
            if (!f.exists()) return;
            Properties p = new Properties();
            try (FileInputStream fis = new FileInputStream(f)) {
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
