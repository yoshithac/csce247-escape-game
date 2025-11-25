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

public class RiddlePuzzleController implements Initializable {

    @FXML private StackPane rootPane;
    @FXML private ImageView backgroundImage;
    @FXML private TextField answerField;
    @FXML private Button btnSubmit, btnHint, btnSave, btnQuit;
    @FXML private Label statusLabel, hintsLabel, categoryLabel, promptLabel;
    @FXML private HBox heartsBox;

    private int attemptsLeft = 3;
    private int hintsLeft = 3;
    private int nextHintIndex = 0;
    private boolean solved = false;

    private String[] HINTS = new String[0];
    private String[] ACCEPTED_ANSWERS = new String[0];
    private String PROMPT = "Solve the riddle";

    private static final String RESOURCE_PATH = "/images/background.png";

    private String chosenDifficulty = "MEDIUM";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("RiddlePuzzleController.initialize() start");

        try {
            String d = com.escapegame.App.getChosenDifficulty();
            if (d != null && !d.isEmpty()) chosenDifficulty = d.toUpperCase();
        } catch (Throwable t) {
            System.err.println("Warning reading chosen difficulty: " + t);
        }

        configureForDifficulty(chosenDifficulty);

        try {
            URL res = getClass().getResource(RESOURCE_PATH);
            if (res != null) {
                Image img = new Image(res.toExternalForm());
                backgroundImage.setImage(img);
                System.out.println("Loaded background from resource path.");
            }
        } catch (Exception ex) {
            System.err.println("Error loading resource image: " + ex.getMessage());
        }

        if (rootPane != null && backgroundImage != null) {
            backgroundImage.fitWidthProperty().bind(rootPane.widthProperty());
            backgroundImage.fitHeightProperty().bind(rootPane.heightProperty());
            backgroundImage.setPreserveRatio(false);
        }

        if (promptLabel != null) promptLabel.setText(PROMPT);
        if (hintsLabel != null) hintsLabel.setText(hintsLeft + " hint(s) available");
        refreshHearts();
        loadSave();
        
        if (attemptsLeft <= 0) {
            System.out.println("DEBUG: Saved state indicates attemptsLeft<=0 — clearing save to reset puzzle.");
            clearSaveForCurrentUser();
            solved = false;
            configureForDifficulty(chosenDifficulty);
            if (hintsLabel != null) hintsLabel.setText(hintsLeft + " hint(s) available");
            if (statusLabel != null) statusLabel.setText("");
            if (btnSubmit != null) btnSubmit.setDisable(false);
            if (btnHint != null) btnHint.setDisable(false);
            if (answerField != null) answerField.setDisable(false);
            refreshHearts();
        }

        if (solved) {
            System.out.println("DEBUG: Saved state indicates solved=true — clearing save to reset puzzle.");
            clearSaveForCurrentUser();
            solved = false;
            configureForDifficulty(chosenDifficulty);
            if (hintsLabel != null) hintsLabel.setText(hintsLeft + " hint(s) available");
            if (statusLabel != null) statusLabel.setText("");
            if (btnSubmit != null) btnSubmit.setDisable(false);
            if (btnHint != null) btnHint.setDisable(false);
            if (answerField != null) answerField.setDisable(false);
            refreshHearts();
        }

        System.out.println("RiddlePuzzleController.initialize() done (difficulty=" + chosenDifficulty + ")");
    }

    private void configureForDifficulty(String difficulty) {
        switch (String.valueOf(difficulty).toUpperCase()) {
            case "EASY":
                PROMPT = "I make you sneeze and sniffle; short, common — what am I?";
                HINTS = new String[] {
                    "It's something associated with being ill.",
                    "People often get this in winter or when around sick people.",
                    "It's a short, common word — one word answer."
                };
                ACCEPTED_ANSWERS = new String[] { "cold", "a cold", "catch a cold" };
                attemptsLeft = 3;
                hintsLeft = 3;
                break;
            case "MEDIUM":
                PROMPT = "I speak without a mouth and hear without ears. I have nobody, but I come alive with wind. What am I?";
                HINTS = new String[] {
                    "It's a sound that repeats what you say.",
                    "You often hear it in caves or wide empty spaces.",
                    "One-word answer; it repeats your voice."
                };
                ACCEPTED_ANSWERS = new String[] { "echo", "an echo" };
                attemptsLeft = 3;
                hintsLeft = 2;
                break;
            case "HARD":
                PROMPT = "I turn once, what is out will not get in. I turn again, what is in will not get out. What am I?";
                HINTS = new String[] {
                    "It's a small household object.",
                    "You often turn it to secure something.",
                    "One-word, metal object you might use on a door."
                };
                ACCEPTED_ANSWERS = new String[] { "key", "a key" };
                attemptsLeft = 2;
                hintsLeft = 1;
                break;
            default:
                PROMPT = "I speak without a mouth and hear without ears. What am I?";
                HINTS = new String[] {
                    "It repeats your sound.",
                    "You might hear it in a canyon."
                };
                ACCEPTED_ANSWERS = new String[] { "echo", "an echo" };
                attemptsLeft = 3;
                hintsLeft = 2;
                break;
        }
    }

    private File getSaveFileForCurrentUser() {
        String userId = "guest";
        try {
            User u = App.getCurrentUser();
            if (u != null && u.getUserId() != null && !u.getUserId().isEmpty()) {
                userId = u.getUserId();
            }
        } catch (Throwable t) { }
        String fileName = ".escapegame_riddle_" + userId + "_" + chosenDifficulty.toLowerCase() + ".properties";
        return new File(System.getProperty("user.home"), fileName);
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
            if (statusLabel != null) statusLabel.setText("You already solved the riddle.");
            return;
        }

        String answer = (answerField == null || answerField.getText() == null) ? "" : answerField.getText().trim().toLowerCase();
        if (answer.isEmpty()) {
            if (statusLabel != null) statusLabel.setText("Please type an answer before submitting.");
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
            if (statusLabel != null) statusLabel.setText("Correct! You solved the riddle.");
            if (btnSubmit != null) btnSubmit.setDisable(true);
            if (btnHint != null) btnHint.setDisable(true);
            if (answerField != null) answerField.setDisable(true);
            new Alert(Alert.AlertType.INFORMATION, "Congratulations — you solved the riddle!").showAndWait();
            saveProgress();
            try {
                App.setRoot("opened1");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            attemptsLeft--;
            refreshHearts();
            if (attemptsLeft <= 0) {
                if (statusLabel != null) statusLabel.setText("No attempts left. The correct answer was: \"" + (ACCEPTED_ANSWERS.length>0?ACCEPTED_ANSWERS[0]:"(answer)") + "\".");
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
            if (statusLabel != null) statusLabel.setText("You already solved the riddle.");
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
        try {
            App.setRoot("puzzlehome");
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                p.store(os, "Riddle puzzle save for user (" + chosenDifficulty + ")");
            }
            return true;
        } catch (Exception e) {
            System.err.println("Save error: " + e.getMessage());
            return false;
        }
    }

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
