package com.escapegame;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
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
 * Riddle puzzle controller with per-user save files (Option C).
 * Save file name: .escapegame_riddle_<userId>.properties in user.home
 */
public class RiddlePuzzleController implements Initializable {

    @FXML private StackPane rootPane;
    @FXML private ImageView backgroundImage;
    @FXML private TextField answerField;
    @FXML private Button btnSubmit, btnHint, btnSave, btnQuit;
    @FXML private Label statusLabel, hintsLabel, categoryLabel, promptLabel;
    @FXML private HBox heartsBox;

    // Game state
    private int attemptsLeft = 3;
    private int hintsLeft = 3;
    private int nextHintIndex = 0;
    private boolean solved = false;

    // Hints and accepted answers
    private final String[] HINTS = {
        "It's something associated with being ill.",
        "People often get this in winter or when around sick people.",
        "It's a short, common word — one word answer."
    };

    private final String[] ACCEPTED_ANSWERS = { "cold", "a cold", "catch a cold" };

    // Resource and dev fallback
    private static final String RESOURCE_PATH = "/images/background.png";
    // keep your dev fallback if you still use it while developing
    private static final String DEV_FALLBACK = "file:/mnt/data/Screenshot 2025-11-19 204918.png";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("RiddlePuzzleController.initialize() start");

        boolean loaded = false;
        try {
            URL res = getClass().getResource(RESOURCE_PATH);
            if (res != null) {
                Image img = new Image(res.toExternalForm());
                backgroundImage.setImage(img);
                loaded = true;
                System.out.println("Loaded background from resource path.");
            }
        } catch (Exception ex) {
            System.err.println("Error loading resource image: " + ex.getMessage());
        }

        if (!loaded) {
            try {
                Image img = new Image(DEV_FALLBACK);
                backgroundImage.setImage(img);
                System.out.println("Loaded background from dev fallback.");
                loaded = true;
            } catch (Exception ex) {
                System.err.println("Error loading dev fallback image: " + ex.getMessage());
            }
        }

        if (!loaded) {
            statusLabel.setText("Background image not found (resource nor dev path).");
        }

        // Bind background to full root size (no white edges)
        if (rootPane != null && backgroundImage != null) {
            backgroundImage.fitWidthProperty().bind(rootPane.widthProperty());
            backgroundImage.fitHeightProperty().bind(rootPane.heightProperty());
            backgroundImage.setPreserveRatio(false);
        }

        hintsLabel.setText(hintsLeft + " hint(s) available");
        refreshHearts();
        loadSave(); // loads per-user save if present

        System.out.println("RiddlePuzzleController.initialize() done");
    }

    // Helper: returns the per-user save file
    private File getSaveFileForCurrentUser() {
        String userId = "guest";
        try {
            User u = App.getCurrentUser(); // App.getCurrentUser() used elsewhere in your project
            if (u != null && u.getUserId() != null && !u.getUserId().isEmpty()) {
                userId = u.getUserId();
            }
        } catch (Throwable t) {
            // ignore and use "guest"
        }
        String fileName = ".escapegame_riddle_" + userId + ".properties";
        return new File(System.getProperty("user.home"), fileName);
    }

    private void refreshHearts() {
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
            statusLabel.setText("You already solved the riddle.");
            return;
        }

        String answer = (answerField.getText() == null) ? "" : answerField.getText().trim().toLowerCase();
        if (answer.isEmpty()) {
            statusLabel.setText("Please type an answer before submitting.");
            return;
        }

        boolean correct = false;
        for (String a : ACCEPTED_ANSWERS) {
            if (a.equals(answer)) {
                correct = true;
                break;
            }
        }

        if (correct) {
            solved = true;
            statusLabel.setText("Correct! You solved the riddle.");
            if (btnSubmit != null) btnSubmit.setDisable(true);
            if (btnHint != null) btnHint.setDisable(true);
            if (answerField != null) answerField.setDisable(true);
            new Alert(Alert.AlertType.INFORMATION, "Congratulations — you solved the riddle!").showAndWait();
            saveProgress();
            // optional: navigate to another screen (your existing code used opened2)
            try {
                App.setRoot("opened1");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            attemptsLeft--;
            refreshHearts();
            if (attemptsLeft <= 0) {
                statusLabel.setText("No attempts left. The correct answer was: \"cold\".");
                if (btnSubmit != null) btnSubmit.setDisable(true);
                if (answerField != null) answerField.setDisable(true);
                new Alert(Alert.AlertType.WARNING, "Out of attempts!").showAndWait();
                saveProgress();
            } else {
                statusLabel.setText("Incorrect. Attempts left: " + attemptsLeft);
            }
        }
    }

    @FXML
    private void onHint() {
        if (solved) {
            statusLabel.setText("You already solved the riddle.");
            return;
        }
        if (hintsLeft <= 0) {
            statusLabel.setText("No hints remaining.");
            return;
        }

        String hint = HINTS[Math.min(nextHintIndex, HINTS.length - 1)];
        nextHintIndex++;
        hintsLeft--;
        hintsLabel.setText(hintsLeft + " hint(s) available");

        new Alert(Alert.AlertType.INFORMATION, hint).showAndWait();
        saveProgress();
    }

    @FXML
    private void onSave() {
        statusLabel.setText(saveProgress() ? "Progress saved." : "Save failed.");
    }

    @FXML
    private void onQuit() {
        try {
            App.setRoot("opened1");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Save to per-user file
    private boolean saveProgress() {
        try {
            Properties p = new Properties();
            p.setProperty("attemptsLeft", String.valueOf(attemptsLeft));
            p.setProperty("hintsLeft", String.valueOf(hintsLeft));
            p.setProperty("solved", String.valueOf(solved));
            p.setProperty("nextHintIndex", String.valueOf(nextHintIndex));
            File out = getSaveFileForCurrentUser();
            try (OutputStream os = new FileOutputStream(out)) {
                p.store(os, "Riddle puzzle save for user");
            }
            return true;
        } catch (Exception e) {
            System.err.println("Save error: " + e.getMessage());
            return false;
        }
    }

    // Load from per-user file
    private void loadSave() {
        try {
            File f = getSaveFileForCurrentUser();
            if (!f.exists()) return;
            Properties p = new Properties();
            try (FileInputStream fis = new FileInputStream(f)) {
                p.load(fis);
            }
            attemptsLeft = Integer.parseInt(p.getProperty("attemptsLeft", Integer.toString(attemptsLeft)));
            hintsLeft = Integer.parseInt(p.getProperty("hintsLeft", Integer.toString(hintsLeft)));
            solved = Boolean.parseBoolean(p.getProperty("solved", Boolean.toString(solved)));
            nextHintIndex = Integer.parseInt(p.getProperty("nextHintIndex", Integer.toString(nextHintIndex)));
            hintsLabel.setText(hintsLeft + " hint(s) available");
            refreshHearts();

            if (solved) {
                if (btnSubmit != null) btnSubmit.setDisable(true);
                if (answerField != null) answerField.setDisable(true);
                if (btnHint != null) btnHint.setDisable(true);
                statusLabel.setText("Already solved.");
            }
        } catch (Exception e) {
            System.err.println("Load save failed: " + e.getMessage());
        }
    }
}
