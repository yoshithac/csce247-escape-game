package com.escapegame;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.Window;

public class RiddlePuzzleController implements Initializable {

    @FXML private ImageView bgImage;
    @FXML private TextField answerField;
    @FXML private Button btnSubmit, btnHint, btnSave, btnQuit;
    @FXML private Label statusLabel, hintsLabel, categoryLabel, promptLabel;
    @FXML private HBox heartsBox;

    private int attemptsLeft = 3;
    private int hintsLeft = 3;
    private int nextHintIndex = 0;
    private boolean solved = false;

    private final String[] HINTS = new String[] {
            "It's something associated with being ill.",
            "People often get this in winter or when around sick people.",
            "It's a short, common word — one word answer."
    };

    private final String[] ACCEPTED_ANSWERS = new String[] { "cold", "a cold", "catch a cold" };

    private final File SAVE_FILE = new File(System.getProperty("user.home"), ".escapegame_riddle.properties");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hintsLabel.setText(hintsLeft + " hint(s) available");
        statusLabel.setText("");
        refreshHearts();

        loadSave();
    }

    private void refreshHearts() {
        heartsBox.getChildren().clear();
        for (int i = 0; i < attemptsLeft; i++) {
            Label heart = new Label("\u2764");
            heart.setStyle("-fx-text-fill: #ff4d6d; -fx-font-size: 20px;");
            heartsBox.getChildren().add(heart);
        }
        if (attemptsLeft <= 0) {
            btnSubmit.setDisable(true);
            answerField.setDisable(true);
        }
    }

    @FXML
    private void onSubmit() {
        if (solved) {
            statusLabel.setText("You already solved the riddle.");
            return;
        }
        String answer = answerField.getText();
        if (answer == null) answer = "";
        answer = answer.trim().toLowerCase();

        if (answer.isEmpty()) {
            statusLabel.setText("Please type an answer before submitting.");
            return;
        }

        boolean ok = false;
        for (String a : ACCEPTED_ANSWERS) {
            if (a.equals(answer)) {
                ok = true;
                break;
            }
        }

        if (ok) {
            solved = true;
            statusLabel.setText("Correct! You solved the riddle.");
            btnSubmit.setDisable(true);
            btnHint.setDisable(true);
            answerField.setDisable(true);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Correct!");
            alert.setHeaderText(null);
            alert.setContentText("Congratulations — you answered correctly and solved the riddle!");
            alert.showAndWait();

            saveProgress();
        } else {
            attemptsLeft--;
            refreshHearts();
            if (attemptsLeft <= 0) {
                statusLabel.setText("No attempts left. The correct answer was: \"cold\".");
                btnSubmit.setDisable(true);
                answerField.setDisable(true);

                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Out of attempts");
                alert.setHeaderText(null);
                alert.setContentText("You've used all attempts. Better luck next time!");
                alert.showAndWait();

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

        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Hint");
        a.setHeaderText(null);
        a.setContentText(hint);
        a.showAndWait();

        saveProgress();
    }

    @FXML
    private void onSave() {
        boolean ok = saveProgress();
        statusLabel.setText(ok ? "Progress saved." : "Save failed.");
    }

    @FXML
    private void onQuit() {
        Node node = btnQuit;
        Window w = node.getScene().getWindow();
        if (w instanceof Stage) {
            ((Stage) w).close();
        } else {
            w.hide();
        }
    }

    private boolean saveProgress() {
        try {
            Properties p = new Properties();
            p.setProperty("attemptsLeft", Integer.toString(attemptsLeft));
            p.setProperty("hintsLeft", Integer.toString(hintsLeft));
            p.setProperty("solved", Boolean.toString(solved));
            p.setProperty("nextHintIndex", Integer.toString(nextHintIndex));
            try (OutputStream os = new FileOutputStream(SAVE_FILE)) {
                p.store(os, "Riddle puzzle save");
            }
            return true;
        } catch (Exception e) {
            System.err.println("Save error: " + e.getMessage());
            return false;
        }
    }

    private void loadSave() {
        try {
            if (!SAVE_FILE.exists()) return;
            Properties p = new Properties();
            p.load(new java.io.FileInputStream(SAVE_FILE));
            attemptsLeft = Integer.parseInt(p.getProperty("attemptsLeft", Integer.toString(attemptsLeft)));
            hintsLeft = Integer.parseInt(p.getProperty("hintsLeft", Integer.toString(hintsLeft)));
            solved = Boolean.parseBoolean(p.getProperty("solved", Boolean.toString(solved)));
            nextHintIndex = Integer.parseInt(p.getProperty("nextHintIndex", Integer.toString(nextHintIndex)));

            hintsLabel.setText(hintsLeft + " hint(s) available");
            refreshHearts();

            if (solved) {
                btnSubmit.setDisable(true);
                answerField.setDisable(true);
                btnHint.setDisable(true);
                statusLabel.setText("Already solved.");
            }
        } catch (Exception e) {
            System.err.println("Load save failed: " + e.getMessage());
        }
    }
}
