package com.escapegame;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
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

/**
 * AnagramPuzzleController that reads puzzles from /GameData.json using Gson,
 * initializes the PuzzleGame with the chosen puzzle data, then loads any saved progress.
 *
 * This controller now mirrors the other controllers' behavior:
 * - extracts a canonical uppercase answer from JSON and stores it under multiple keys
 * - keeps a controller-level canonicalAnswerFromJson fallback
 * - protects against stale/partial saves that would overwrite the canonical answer
 * - accepts the canonical answer at the controller level if the PuzzleGame engine doesn't mark a win
 */
public class AnagramPuzzleController implements Initializable {

    @FXML private StackPane rootPane;
    @FXML private ImageView backgroundImage;
    @FXML private TextField answerField;
    @FXML private Button btnSubmit, btnHint, btnSave, btnQuit;
    @FXML private Label statusLabel, hintsLabel, categoryLabel, promptLabel, scrambledLabel;
    @FXML private HBox heartsBox;

    private PuzzleGame game;
    private String puzzleType = "ANAGRAM";
    private String difficulty = "MEDIUM";

    // controller-level fallback for the canonical JSON answer
    private String canonicalAnswerFromJson = "";

    private static final String GAME_DATA_CLASSPATH = "/GameData.json";
    private final Gson gson = new Gson();

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
        } catch (Throwable t) { /* ignore UI load issues */ }

        try {
            String chosen = com.escapegame.App.getChosenDifficulty();
            if (chosen != null) difficulty = chosen.toUpperCase(Locale.ROOT);
        } catch (Throwable t) { /* ignore */ }

        // create game object
        game = GameFactory.createGame(puzzleType, difficulty);

        // load GameData.json and inject a selected anagram puzzle into the game state
        try {
            Optional<Map<String, Object>> chosenPuzzle = loadAndSelectAnagramPuzzleFromJson(puzzleType, difficulty);
            if (chosenPuzzle.isPresent()) {
                Map<String, Object> puzzle = chosenPuzzle.get();
                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) puzzle.getOrDefault("data", Map.of());
                Map<String, Object> initialState = buildInitialGameStateFromPuzzleData(data);
                try {
                    game.restoreState(initialState);
                    System.out.println("DEBUG: after initial restore, gameState = " + safeToString(game.getGameState()));
                } catch (Throwable t) {
                    System.err.println("restoreState failed: " + t);
                }
            } else {
                System.err.println("No anagram puzzle found for type=" + puzzleType + " difficulty=" + difficulty);
            }
        } catch (Throwable t) {
            System.err.println("Failed to load GameData.json: " + t);
        }

        // load user save (overrides JSON-loaded state if present)
        loadSave();
        try {
            System.out.println("DEBUG: final gameState after loadSave() = " + safeToString(game.getGameState()) + " canonicalAnswerFromJson=" + canonicalAnswerFromJson);
        } catch (Throwable t) { /* ignore */ }
        refreshUI();
    }

    @SuppressWarnings("unchecked")
    private Optional<Map<String, Object>> loadAndSelectAnagramPuzzleFromJson(String pType, String diff) {
        try (InputStream is = getClass().getResourceAsStream(GAME_DATA_CLASSPATH)) {
            if (is == null) {
                System.err.println("GameData.json not found on classpath at " + GAME_DATA_CLASSPATH);
                return Optional.empty();
            }
            JsonElement rootElem = JsonParser.parseReader(new InputStreamReader(is));
            if (rootElem == null || !rootElem.isJsonObject()) return Optional.empty();
            JsonObject root = rootElem.getAsJsonObject();

            JsonElement puzzlesElem = root.get("puzzles");
            if (puzzlesElem == null || !puzzlesElem.isJsonArray()) return Optional.empty();
            JsonArray puzzles = puzzlesElem.getAsJsonArray();

            String diffUpper = (diff == null) ? "" : diff.toUpperCase(Locale.ROOT);

            // collect matching anagrams
            List<JsonObject> matches = new java.util.ArrayList<>();
            for (JsonElement pe : puzzles) {
                if (!pe.isJsonObject()) continue;
                JsonObject p = pe.getAsJsonObject();
                String type = p.has("puzzleType") ? p.get("puzzleType").getAsString() : "";
                String pdiff = p.has("difficulty") ? p.get("difficulty").getAsString() : "";
                if (pType.equalsIgnoreCase(type) && diffUpper.equalsIgnoreCase(pdiff.toUpperCase(Locale.ROOT))) {
                    matches.add(p);
                }
            }

            JsonObject chosenJson = null;
            if (!matches.isEmpty()) {
                int idx = new Random().nextInt(matches.size());
                chosenJson = matches.get(idx);
            } else {
                // fallback: first puzzle of same type regardless of difficulty
                for (JsonElement pe : puzzles) {
                    if (!pe.isJsonObject()) continue;
                    JsonObject p = pe.getAsJsonObject();
                    String type = p.has("puzzleType") ? p.get("puzzleType").getAsString() : "";
                    if (pType.equalsIgnoreCase(type)) {
                        chosenJson = p;
                        break;
                    }
                }
            }

            if (chosenJson == null) return Optional.empty();
            Map<String, Object> map = gson.fromJson(chosenJson, new TypeToken<Map<String, Object>>() {}.getType());
            return Optional.of(map);
        } catch (Throwable t) {
            System.err.println("Error reading/parsing GameData.json: " + t);
            return Optional.empty();
        }
    }

    /**
     * Build a conservative initial game-state map from the puzzle's data block.
     * Keys used: prompt, answer, category, remainingAttempts, availableHintsCount, revealedHints, extraText.
     *
     * Robust answer extraction: tries several possible key names and normalizes answer to uppercase.
     */
    private Map<String, Object> buildInitialGameStateFromPuzzleData(Map<String, Object> data) {
        Map<String, Object> state = new HashMap<>();
        if (data == null) return state;

        Object prompt = data.get("prompt");
        if (prompt != null) state.put("prompt", prompt.toString());

        // robust extraction of canonical answer
        Object ansObj = data.get("answer");
        if (ansObj == null) ansObj = data.get("correctAnswer");
        if (ansObj == null) ansObj = data.get("solution");
        if (ansObj == null) ansObj = data.get("expectedAnswer");
        if (ansObj == null) ansObj = data.get("solutions");
        if (ansObj == null) ansObj = data.get("answers");

        if (ansObj instanceof List) {
            @SuppressWarnings("unchecked")
            List<Object> arr = (List<Object>) ansObj;
            Object picked = null;
            for (Object o : arr) {
                if (o != null && !o.toString().isBlank()) { picked = o; break; }
            }
            ansObj = picked;
        }

        if (ansObj != null) {
            String canonical = ansObj.toString().trim().toUpperCase(Locale.ROOT);
            canonicalAnswerFromJson = canonical;
            state.put("answer", canonical);
            state.put("solution", canonical);
            state.put("correctAnswer", canonical);
            state.put("expectedAnswer", canonical);
        }

        Object cat = data.get("category");
        if (cat != null) state.put("category", cat.toString());
        else state.put("category", "");

        int maxAttempts = 3;
        Object maxAttObj = data.get("maxAttempts");
        if (maxAttObj instanceof Number) maxAttempts = ((Number) maxAttObj).intValue();
        else if (maxAttObj != null) {
            try { maxAttempts = Integer.parseInt(String.valueOf(maxAttObj)); } catch (Exception e) { /* ignore */ }
        }
        state.put("remainingAttempts", maxAttempts);

        int hints = 0;
        Object availHints = data.getOrDefault("availableHintsCount", data.get("hints"));
        if (availHints instanceof Number) {
            hints = ((Number) availHints).intValue();
        } else if (availHints != null) {
            try { hints = Integer.parseInt(String.valueOf(availHints)); } catch (Exception e) { /* ignore */ }
        }

        // If JSON didn't provide hints or provided 0/negative, use sensible defaults by difficulty
        if (hints <= 0) {
            String diffUpper = (difficulty == null) ? "" : difficulty.toUpperCase(Locale.ROOT);
            switch (diffUpper) {
                case "EASY":   hints = 3; break;
                case "MEDIUM": hints = 2; break;
                case "HARD":   hints = 1; break;
                default:       hints = 3; break;
            }
        }

        state.put("availableHintsCount", hints);


        state.put("revealedHints", List.of());

        Object extra = data.getOrDefault("extra", data.get("note"));
        if (extra != null) state.put("extraText", extra.toString());
        else state.put("extraText", "");

        // copy other keys from data (non-destructive)
        for (Map.Entry<String, Object> e : data.entrySet()) {
            String k = e.getKey();
            if (!state.containsKey(k)) state.put(k, e.getValue());
        }

        return state;
    }

    private File getSaveFileForCurrentUser() {
        String userId = "guest";
        try {
            User u = App.getCurrentUser();
            if (u != null && u.getUserId() != null && !u.getUserId().isBlank()) userId = u.getUserId().trim();
        } catch (Throwable t) { }
        String fileName = ".escapegame_anagram_" + userId.replaceAll("\\s+", "_").toLowerCase(Locale.ROOT) + ".ser";
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

        // show scrambled text if the label exists; otherwise append to the prompt as a fallback
        if (scrambledLabel != null) {
            scrambledLabel.setText("Scrambled: " + (extra == null ? "" : extra));
        } else if (promptLabel != null) {
            // keep prompt readable if scrambledLabel isn't present
            promptLabel.setText((extra == null || extra.isBlank()) ? prompt : (prompt + "  [Scrambled: " + extra + "]"));
        }

        refreshHearts();
        if (statusLabel != null) statusLabel.setText("");
    }

    @FXML
    private void onSubmit() {
        if (answerField == null) return;
        String raw = answerField.getText();
        if (raw == null || raw.trim().isEmpty()) {
            statusLabel.setText("Please enter an answer.");
            return;
        }

        // normalize and uppercase input so single-letter or case differences don't matter
        String normalized = raw.trim().toUpperCase(Locale.ROOT);
        System.out.println("DEBUG: user submitted (normalized) = '" + normalized + "'");

        try {
            System.out.println("DEBUG: gameState BEFORE processInput = " + safeToString(game.getGameState()));
        } catch (Throwable t) { System.out.println("DEBUG: failed to read game state before processInput: " + t); }

        try {
            game.processInput(normalized);
        } catch (Throwable t) {
            System.err.println("DEBUG: processInput threw: " + t);
        }

        Map<String, Object> state = game.getGameState();

        try {
            System.out.println("DEBUG: gameState AFTER processInput = " + safeToString(state));
            System.out.println("DEBUG: gameResult AFTER processInput = " + safeToString(game.getResult()));
        } catch (Throwable t) { System.out.println("DEBUG: failed to read game result after processInput: " + t); }

        // controller-level fallback: if engine didn't mark a win but our JSON canonical answer matches, accept it
        try {
            Map<String,Object> result = game.getResult();
            boolean engineWon = Boolean.TRUE.equals(result.get("won"));
            if (!engineWon && canonicalAnswerFromJson != null && !canonicalAnswerFromJson.isBlank()) {
                if (normalized.equals(canonicalAnswerFromJson)) {
                    System.out.println("DEBUG: controller fallback accepting canonicalAnswerFromJson as correct (\"" + canonicalAnswerFromJson + "\")");
                    statusLabel.setText("Correct! You solved it.");
                    if (btnSubmit != null) btnSubmit.setDisable(true);
                    if (btnHint != null) btnHint.setDisable(true);
                    if (answerField != null) answerField.setDisable(true);
                    new Alert(Alert.AlertType.INFORMATION, "Nice!").showAndWait();
                    saveSave();
                    Map<String, Object> cfg = com.model.WordPuzzleGame.configFor(puzzleType, difficulty);
                    String nextScene = cfg == null ? "" : (String) cfg.getOrDefault("nextScene", "");
                    try {
                        if (nextScene != null && !nextScene.isBlank()) App.setRoot(nextScene);
                        else App.setRoot("opened5");
                    } catch (Exception ex) { ex.printStackTrace(); }
                    return; // stop further processing
                }
            }
        } catch (Throwable t) {
            System.err.println("DEBUG: controller fallback check threw: " + t);
        }

        if (game.isGameOver()) {
            Map<String, Object> result = game.getResult();
            boolean won = Boolean.TRUE.equals(result.get("won"));
            if (won) {
                statusLabel.setText("Correct! You solved it.");
                if (btnSubmit != null) btnSubmit.setDisable(true);
                if (btnHint != null) btnHint.setDisable(true);
                if (answerField != null) answerField.setDisable(true);
                new Alert(Alert.AlertType.INFORMATION, "Nice!").showAndWait();
                saveSave();
                Map<String, Object> cfg = com.model.WordPuzzleGame.configFor(puzzleType, difficulty);
                String nextScene = cfg == null ? "" : (String) cfg.getOrDefault("nextScene", "");
                try {
                    if (nextScene != null && !nextScene.isBlank()) App.setRoot(nextScene);
                    else App.setRoot("opened5");
                } catch (Exception ex) { ex.printStackTrace(); }
            } else {
                // show correct answer with fallbacks
                String ans = safeGetString(result, "answer");
                if (ans.isBlank()) ans = safeGetString(state, "answer");
                if (ans.isBlank()) ans = safeGetString(state, "solution");
                if (ans.isBlank()) ans = safeGetString(state, "correctAnswer");
                if (ans.isBlank()) ans = safeGetString(state, "expectedAnswer");
                if (ans.isBlank()) ans = (canonicalAnswerFromJson == null ? "" : canonicalAnswerFromJson);
                statusLabel.setText("No attempts left. The correct answer was: \"" + ans + "\".");
                if (btnSubmit != null) btnSubmit.setDisable(true);
                if (answerField != null) answerField.setDisable(true);
                new Alert(Alert.AlertType.WARNING, "Out of attempts!").showAndWait();
                saveSave();
            }
        } else {
            int remaining = ((Number) state.getOrDefault("remainingAttempts", 0)).intValue();
            statusLabel.setText("Incorrect. Attempts left: " + remaining);
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
            statusLabel.setText("No hints remaining.");
        }
        hintsLabel.setText(((Number) state.getOrDefault("availableHintsCount", 0)).intValue() + " hint(s) available");
        saveSave();
        refreshUI();
    }

    @FXML
    private void onSave() {
        boolean ok = saveSave();
        statusLabel.setText(ok ? "Progress saved." : "Save failed.");
    }

    @FXML
    private void onQuit() {
        try {
            saveSave();
            App.setRoot("opened4");
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

                    // If we loaded an initial state earlier from JSON, compare prompt values:
                    Map<String,Object> currentState = game.getGameState();
                    String initialPrompt = (String) currentState.getOrDefault("prompt", "");
                    String savedPrompt = (String) saved.getOrDefault("prompt", "");

                    if (savedPrompt == null) savedPrompt = "";
                    if (!savedPrompt.equals(initialPrompt)) {
                        // mismatch -> delete the stale save and don't restore
                        if (!f.delete()) {
                            System.err.println("Could not delete stale save: " + f.getAbsolutePath());
                        } else {
                            System.out.println("Deleted stale save file: " + f.getAbsolutePath());
                        }
                        return;
                    }

                    // Defensive: if the saved file contains empty answer-like keys, prefer the JSON canonical answer
                    String savedAns = safeGetString(saved, "answer");
                    if (savedAns.isBlank()) savedAns = safeGetString(saved, "correctAnswer");
                    if (savedAns.isBlank()) savedAns = safeGetString(saved, "solution");
                    if (savedAns.isBlank()) savedAns = safeGetString(saved, "expectedAnswer");

                    if (savedAns.isBlank() && !canonicalAnswerFromJson.isBlank()) {
                        System.out.println("DEBUG: save has no canonical answer â€” removing answer keys and restoring (keeping JSON answer).");
                        saved.remove("answer");
                        saved.remove("solution");
                        saved.remove("correctAnswer");
                        saved.remove("expectedAnswer");
                    }

                    game.restoreState(saved);
                }
            }
        } catch (Throwable t) {
            System.err.println("Load save failed: " + t);
        }
    }

    // small helpers

    private static String safeToString(Object o) {
        try {
            return String.valueOf(o);
        } catch (Throwable t) {
            return "<unprintable>";
        }
    }

    private static String safeGetString(Map<String,Object> m, String key) {
        if (m == null) return "";
        Object v = m.getOrDefault(key, "");
        return v == null ? "" : v.toString();
    }
}