package com.escapegame;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import com.model.GameFactory;
import com.model.PuzzleGame;
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

public class CipherPuzzleController implements Initializable {

    @FXML private StackPane rootPane;
    @FXML private ImageView backgroundImage;
    @FXML private TextField answerField;
    @FXML private Button btnSubmit, btnHint, btnSave, btnQuit;
    @FXML private Label statusLabel, hintsLabel, categoryLabel, promptLabel, cipherLabel;
    @FXML private HBox heartsBox;

    private PuzzleGame game;
    private String puzzleType = "CIPHER";
    private String difficulty = "MEDIUM";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            URL res = getClass().getResource("/images/background.png");
            if (res != null && backgroundImage != null) {
                backgroundImage.setImage(new Image(res.toExternalForm()));
                backgroundImage.fitWidthProperty().bind(rootPane.widthProperty());
                backgroundImage.fitHeightProperty().bind(rootPane.heightProperty());
                backgroundImage.setPreserveRatio(false);
            }
        } catch (Throwable t) { }

        try {
            String chosen = com.escapegame.App.getChosenDifficulty();
            if (chosen != null) difficulty = chosen.toUpperCase(Locale.ROOT);
        } catch (Throwable t) { }

        game = GameFactory.createGame(puzzleType, difficulty);
        loadSave();
        refreshUI();
    }

    private File getSaveFileForCurrentUser() {
        String userId = "guest";
        try {
            User u = App.getCurrentUser();
            if (u != null && u.getUserId() != null && !u.getUserId().isBlank()) userId = u.getUserId().trim();
        } catch (Throwable t) { }
        String fileName = ".escapegame_cipher_" + userId.replaceAll("\\s+", "_").toLowerCase(Locale.ROOT) + ".ser";
        return new File(System.getProperty("user.home"), fileName);
    }

    private void refreshHearts() {
        if (heartsBox == null) return;
        heartsBox.getChildren().clear();
        Map<String, Object> state = game.getGameState();
        int remaining = ((Number) state.getOrDefault("remainingAttempts", 0)).intValue();
        for (int i = 0; i < remaining; i++) {
            Label heart = new Label("\u2764");
            heart.setStyle("-fx-text-fill: #ff4d6d; -fx-font-size: 20px;");
            heartsBox.getChildren().add(heart);
        }
        boolean noAttempts = remaining <= 0;
        if (btnSubmit != null) btnSubmit.setDisable(noAttempts);
        if (answerField != null) answerField.setDisable(noAttempts);
    }

    private void refreshUI() {
        Map<String, Object> state = game.getGameState();
        String prompt = state.getOrDefault("prompt", "").toString();
        String category = state.getOrDefault("category", "").toString();
        int availableHints = ((Number) state.getOrDefault("availableHintsCount", 0)).intValue();
        String extra = state.getOrDefault("extraText", "").toString();

        if (promptLabel != null) promptLabel.setText(prompt);
        if (categoryLabel != null) categoryLabel.setText(category);
        if (hintsLabel != null) hintsLabel.setText(availableHints + " hint(s) available");
        if (cipherLabel != null) {
            cipherLabel.setText("Cipher: " + (extra == null ? "" : extra));
        } else if (promptLabel != null) {
            promptLabel.setText((extra == null || extra.isBlank()) ? prompt : prompt + " (" + extra + ")");
        }
        refreshHearts();
        if (statusLabel != null) statusLabel.setText("");
    }

    @FXML
    private void onSubmit() {
        if (answerField == null) return;
        String raw = answerField.getText();
        if (raw == null || raw.trim().isEmpty()) {
            if (statusLabel != null) statusLabel.setText("Please enter an answer.");
            return;
        }
        game.processInput(raw.trim());
        Map<String, Object> state = game.getGameState();
        if (game.isGameOver()) {
            Map<String, Object> result = game.getResult();
            boolean won = Boolean.TRUE.equals(result.get("won"));
            if (won) {
                if (statusLabel != null) statusLabel.setText("Correct! You decoded it.");
                if (btnSubmit != null) btnSubmit.setDisable(true);
                if (btnHint != null) btnHint.setDisable(true);
                if (answerField != null) answerField.setDisable(true);
                new Alert(Alert.AlertType.INFORMATION, "Nice!").showAndWait();
                saveSave();
                Map<String, Object> cfg = com.model.WordPuzzleGame.configFor(puzzleType, difficulty);
                String nextScene = cfg == null ? "" : (String) cfg.getOrDefault("nextScene", "");
                try {
                    if (nextScene != null && !nextScene.isBlank()) App.setRoot(nextScene);
                    else App.setRoot("opened3");
                } catch (Exception ex) { ex.printStackTrace(); }
            } else {
                String ans = result.getOrDefault("answer", "").toString();
                if (statusLabel != null) statusLabel.setText("No attempts left. The correct answer was: \"" + ans + "\".");
                if (btnSubmit != null) btnSubmit.setDisable(true);
                if (answerField != null) answerField.setDisable(true);
                new Alert(Alert.AlertType.WARNING, "Out of attempts!").showAndWait();
                saveSave();
            }
        } else {
            int remaining = ((Number) state.getOrDefault("remainingAttempts", 0)).intValue();
            if (statusLabel != null) statusLabel.setText("Incorrect. Attempts left: " + remaining);
            saveSave();
        }
        refreshUI();
    }

    @FXML
    private void onHint() {
        game.processInput("HINT");
        Map<String, Object> state = game.getGameState();
        @SuppressWarnings("unchecked")
        List<String> revealed = (List<String>) state.getOrDefault("revealedHints", List.of());
        if (!revealed.isEmpty()) {
            String last = revealed.get(revealed.size() - 1);
            new Alert(Alert.AlertType.INFORMATION, last).showAndWait();
        } else {
            if (statusLabel != null) statusLabel.setText("No hints remaining.");
        }
        if (hintsLabel != null) hintsLabel.setText(((Number) state.getOrDefault("availableHintsCount", 0)).intValue() + " hint(s) available");
        saveSave();
        refreshUI();
    }

    @FXML
    private void onSave() {
        boolean ok = saveSave();
        if (statusLabel != null) statusLabel.setText(ok ? "Progress saved." : "Save failed.");
    }

    @FXML
    private void onQuit() {
        try {
            saveSave();
            App.setRoot("puzzlehome");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean saveSave() {
        try {
            File f = getSaveFileForCurrentUser();
            Map<String, Object> state = game.saveState();
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f))) {
                oos.writeObject(state);
            }
            return true;
        } catch (Exception e) {
            System.err.println("Save error: " + e);
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    private void loadSave() {
        try {
            File f = getSaveFileForCurrentUser();
            if (!f.exists()) return;
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
                Object o = ois.readObject();
                if (o instanceof Map) {
                    Map<String, Object> saved = (Map<String, Object>) o;
                    game.restoreState(saved);
                }
            }
        } catch (Throwable t) {
            System.err.println("Load save failed: " + t);
        }
    }
}