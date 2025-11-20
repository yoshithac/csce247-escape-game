package com.escapegame;

import java.io.File;
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

public class CipherPuzzleController implements Initializable {

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

    String [] HINTS = {
        //get from json
    };
    String [] ACCEPTED_ANSWERS = {
        //get from json
    };

    private final File SAVE_FILE = new File(System.getProperty("user.home"), ".escapegame_cipher.properties");

    private static final String RESOURCE_PATH = "/images/background.png";
    private static final String DEV_FALLBACK = "file:/mnt/data/Screenshot 2025-11-19 204918.png";

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
                System.out.println("Loaded background from resource path.");
            }
        } catch (Exception ex) {
            System.err.println("Error loading resource image: " + ex.getMessage());
        }

        // Fallback to local file for development
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

        backgroundImage.fitWidthProperty().bind(rootPane.widthProperty());
        backgroundImage.fitHeightProperty().bind(rootPane.heightProperty());
        backgroundImage.setPreserveRatio(false);

        hintsLabel.setText(hintsLeft + " hint(s) available");
        refreshHearts();
        loadSave();

        System.out.println("CipherPuzzleController.initialize() done");
    }

    // Draw hearts based on remaining attempts
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
            statusLabel.setText("You already solved the cipher puzzle.");
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
            statusLabel.setText("Correct! You solved the puzzle.");
            btnSubmit.setDisable(true);
            btnHint.setDisable(true);
            answerField.setDisable(true);
            //new Alert(Alert.AlertType.INFORMATION, "Congratulations — you solved the  puzzle!").showAndWait();
            try {
                App.setRoot("opened3");
            } catch (IOException e) {
                  e.printStackTrace();
            }
            saveProgress();
        } else {
            attemptsLeft--;
            refreshHearts();
            if (attemptsLeft <= 0) {
                statusLabel.setText("No attempts left. The correct answer was: ");
                btnSubmit.setDisable(true);
                answerField.setDisable(true);
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
            statusLabel.setText("You already solved the puzzle.");
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
            App.setRoot("opened3");
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
            try (OutputStream os = new FileOutputStream(SAVE_FILE)) {
                p.store(os, "Matching puzzle save");
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
            attemptsLeft = Integer.parseInt(p.getProperty("attemptsLeft", "3"));
            hintsLeft = Integer.parseInt(p.getProperty("hintsLeft", "3"));
            solved = Boolean.parseBoolean(p.getProperty("solved", "false"));
            nextHintIndex = Integer.parseInt(p.getProperty("nextHintIndex", "0"));
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
