package com.escapegame;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.ArrayList;
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
 * RiddlePuzzleController that reads puzzles from /GameData.json using Gson,
 * normalizes answers, stores them under multiple keys, and uppercases user input.
 */
public class RiddlePuzzleController implements Initializable {

    @FXML private StackPane rootPane;
    @FXML private ImageView backgroundImage;
    @FXML private TextField answerField;
    @FXML private Button btnSubmit, btnHint, btnSave, btnQuit;
    @FXML private Label statusLabel, hintsLabel, categoryLabel, promptLabel;
    @FXML private HBox heartsBox;

    private PuzzleGame game;
    private String puzzleType = "RIDDLE";
    private String difficulty = "MEDIUM";

    private String canonicalAnswerFromJson = "";

    private static final String GAME_DATA_CLASSPATH = "/GameData.json";
    private final Gson gson = new Gson();

    // Hints cache and selected puzzle id
    private Map<String, List<String>> controllerRevealedHints = new HashMap<>();
    private Map<String, List<String>> hintsCache = null;
    private String selectedPuzzleId = "";

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
        } catch (Throwable t) { /* ignore UI image load issues */ }

        try {
            String chosen = com.escapegame.App.getChosenDifficulty();
            if (chosen != null) difficulty = chosen.toUpperCase(Locale.ROOT);
        } catch (Throwable t) { /* ignore */ }

        game = GameFactory.createGame(puzzleType, difficulty);

        // load JSON puzzle into game state (then loadSave can override)
        try {
            Optional<Map<String, Object>> chosenPuzzle = loadAndSelectRiddlePuzzleFromJson(puzzleType, difficulty);
            if (chosenPuzzle.isPresent()) {
                Map<String, Object> puzzle = chosenPuzzle.get();

                // store selected puzzle id for hint lookup
                Object pidObj = puzzle.get("puzzleId");
                selectedPuzzleId = pidObj == null ? "" : pidObj.toString();

                // ensure hints from top-level "hints" array are read & cached
                ensureHintsLoaded();

                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) puzzle.getOrDefault("data", Map.of());
                Map<String, Object> initialState = buildInitialGameStateFromPuzzleData(data);

                // Override availableHintsCount & revealedHints from top-level hints array
                applyTopLevelHintsToState(puzzle, initialState);

                try {
                    game.restoreState(initialState);
                    System.out.println("DEBUG: after initial restore, gameState = " + safeToString(game.getGameState()));
                } catch (Throwable t) {
                    System.err.println("restoreState failed: " + t);
                }
            } else {
                System.err.println("No riddle puzzle found for type=" + puzzleType + " difficulty=" + difficulty);
            }
        } catch (Throwable t) {
            System.err.println("Failed to load GameData.json: " + t);
        }

        // load any saved progress (overrides the JSON-based initial state)
        loadSave();
        // final check
        try {
            System.out.println("DEBUG: final gameState after loadSave() = " + safeToString(game.getGameState()) + " canonicalAnswerFromJson=" + canonicalAnswerFromJson);
        } catch (Throwable t) { /* ignore */ }
        refreshUI();
    }

    @SuppressWarnings("unchecked")
    private Optional<Map<String, Object>> loadAndSelectRiddlePuzzleFromJson(String pType, String diff) {
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
                // fallback: pick first puzzle of the same type
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
     * Build initial game state from puzzle data.
     * Uppercases canonical answer and writes it under multiple likely keys so different model impls find it.
     */
    private Map<String, Object> buildInitialGameStateFromPuzzleData(Map<String, Object> data) {
        Map<String, Object> state = new HashMap<>();
        if (data == null) return state;

        Object prompt = data.get("prompt");
        if (prompt != null) state.put("prompt", prompt.toString());

        // --- robust extraction of canonical answer ---
        Object ansObj = data.get("answer");
        if (ansObj == null) ansObj = data.get("correctAnswer");
        if (ansObj == null) ansObj = data.get("solution");
        if (ansObj == null) ansObj = data.get("expectedAnswer");
        if (ansObj == null) ansObj = data.get("solutions"); // maybe an array
        if (ansObj == null) ansObj = data.get("answers");   // maybe an array

        // if we have an array of answers, pick the first non-blank entry
        if (ansObj instanceof List) {
            @SuppressWarnings("unchecked")
            List<Object> arr = (List<Object>) ansObj;
            for (Object o : arr) {
                if (o != null && !o.toString().isBlank()) {
                    ansObj = o;
                    break;
                }
            }
        }

        // final normalization + put into state under many keys
        if (ansObj != null) {
            String canonical = ansObj.toString().trim().toUpperCase(Locale.ROOT);
            canonicalAnswerFromJson = canonical;
            state.put("answer", canonical);
            state.put("solution", canonical);
            state.put("correctAnswer", canonical);
            state.put("expectedAnswer", canonical);
        } else {
            System.out.println("WARN: buildInitialGameStateFromPuzzleData: no answer found in data=" + safeToString(data));
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

        int hints = 3;
        Object availHints = data.getOrDefault("availableHintsCount", data.get("hints"));
        if (availHints instanceof Number) hints = ((Number) availHints).intValue();
        else if (availHints != null) {
            try { hints = Integer.parseInt(String.valueOf(availHints)); } catch (Exception e) { /* ignore */ }
        }
        state.put("availableHintsCount", hints);

        state.put("revealedHints", List.of());

        Object extra = data.getOrDefault("extra", data.get("note"));
        if (extra != null) state.put("extraText", extra.toString());
        else state.put("extraText", "");

        // copy other keys from data if not already present
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
        String fileName = ".escapegame_riddle_" + userId.replaceAll("\\s+", "_").toLowerCase(Locale.ROOT) + ".ser";
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
        try {
            Map<String, Object> state = game.getGameState();
            String prompt = state.getOrDefault("prompt", "").toString();
            String category = state.getOrDefault("category", "").toString();

            // Compute total hints from top-level hints array (never null)
            List<String> topHints = List.of();
            if (selectedPuzzleId != null && !selectedPuzzleId.isBlank()) {
                topHints = getHintsForPuzzleId(selectedPuzzleId);
            }

            // Determine how many hints have been revealed already:
            // Prefer controllerRevealedHints, else fall back to engine's revealedHints
            List<String> revealed = controllerRevealedHints.get(selectedPuzzleId);
            if (revealed == null) {
                @SuppressWarnings("unchecked")
                List<String> engineRevealed = (List<String>) state.getOrDefault("revealedHints", new ArrayList<String>());
                revealed = engineRevealed == null ? new ArrayList<>() : new ArrayList<>(engineRevealed);
            }

            int total = topHints.size();
            int revealedCount = revealed == null ? 0 : revealed.size();
            int remaining = Math.max(0, total - revealedCount);

            // Update UI elements
            promptLabel.setText(prompt);
            categoryLabel.setText(category);
            hintsLabel.setText(remaining + " hint(s) available");

            // keep hearts/attempts UI unchanged
            refreshHearts();
            statusLabel.setText("");

            // Debugging info
            System.out.println("DEBUG: refreshUI selectedPuzzleId=" + selectedPuzzleId +
                            " totalHints=" + total + " revealed=" + revealedCount +
                            " remaining=" + remaining + " (engineAvailable=" +
                            state.getOrDefault("availableHintsCount", "<none>") + ")");

        } catch (Throwable t) {
            System.err.println("DEBUG: refreshUI failed: " + t);
        }
    }

    @FXML
    private void onSubmit() {
        if (answerField == null) return;
        String raw = answerField.getText();
        if (raw == null || raw.trim().isEmpty()) {
            statusLabel.setText("Please type an answer before submitting.");
            return;
        }

        // Normalize input to uppercase
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

           try {
        Map<String, Object> result = game.getResult();
        boolean engineWon = Boolean.TRUE.equals(result.get("won"));

        if (!engineWon && canonicalAnswerFromJson != null && !canonicalAnswerFromJson.isBlank()) {
            if (normalized.equals(canonicalAnswerFromJson)) {
                System.out.println("DEBUG: FALLBACK TRIGGERED — canonical answer matched");

                // treat as win (copying your existing win logic)
                statusLabel.setText("Correct! You solved the riddle.");
                if (btnSubmit != null) btnSubmit.setDisable(true);
                if (btnHint != null) btnHint.setDisable(true);
                if (answerField != null) answerField.setDisable(true);
                new Alert(Alert.AlertType.INFORMATION, "Congratulations — you solved the riddle!").showAndWait();

                saveSave();

                Map<String, Object> cfg = com.model.WordPuzzleGame.configFor(puzzleType, difficulty);
                String nextScene = cfg == null ? "" : (String) cfg.getOrDefault("nextScene", "");
                if (nextScene != null && !nextScene.isBlank()) {
                    try { App.setRoot(nextScene); } catch (Exception ex) { ex.printStackTrace(); }
                } else {
                    try { App.setRoot("opened1"); } catch (Exception ex) { ex.printStackTrace(); }
                }

                return; // IMPORTANT — stop further processing
            }
        }
    } catch (Throwable t) {
        System.err.println("DEBUG: fallback win-check failed: " + t);
    }

        if (game.isGameOver()) {
            Map<String, Object> result = game.getResult();
            boolean won = Boolean.TRUE.equals(result.get("won"));
            if (won) {
                statusLabel.setText("Correct! You solved the riddle.");
                if (btnSubmit != null) btnSubmit.setDisable(true);
                if (btnHint != null) btnHint.setDisable(true);
                if (answerField != null) answerField.setDisable(true);
                new Alert(Alert.AlertType.INFORMATION, "Congratulations — you solved the riddle!").showAndWait();
                saveSave();
                Map<String, Object> cfg = com.model.WordPuzzleGame.configFor(puzzleType, difficulty);
                String nextScene = cfg == null ? "" : (String) cfg.getOrDefault("nextScene", "");
                if (nextScene != null && !nextScene.isBlank()) {
                    try { App.setRoot(nextScene); } catch (Exception ex) { ex.printStackTrace(); }
                } else {
                    try { App.setRoot("opened1"); } catch (Exception ex) { ex.printStackTrace(); }
                }
            } else {
                // show correct answer with fallbacks
                String ans = safeGetString(result, "answer");
                if (ans.isBlank()) ans = safeGetString(state, "answer");
                if (ans.isBlank()) ans = safeGetString(state, "solution");
                if (ans.isBlank()) ans = safeGetString(state, "correctAnswer");
                if (ans.isBlank()) ans = safeGetString(state, "expectedAnswer");
                if (ans.isBlank()) ans = (canonicalAnswerFromJson == null ? "" : canonicalAnswerFromJson);
                statusLabel.setText("Out of attempts. The correct answer was: \"" + ans + "\".");
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
        try {
            // Let engine handle hint if it knows how
            game.processInput("HINT");
        } catch (Throwable t) {
            System.err.println("DEBUG: hint processInput threw: " + t);
        }

        Map<String, Object> state = game.getGameState();
        @SuppressWarnings("unchecked")
        List<String> revealed = (List<String>) state.getOrDefault("revealedHints", List.of());

        // If engine returned a revealed hint, show it
        if (!revealed.isEmpty()) {
            String last = revealed.get(revealed.size() - 1);
            new Alert(Alert.AlertType.INFORMATION, last).showAndWait();
            // update UI/hint label (refreshUI will do this, but do it explicitly too)
            hintsLabel.setText(((Number) state.getOrDefault("availableHintsCount", 0)).intValue() + " hint(s) available");
            saveSave();
            refreshUI();
            return;
        }

        // Otherwise, supply next hint from the top-level hints array (fallback)
        if (selectedPuzzleId == null || selectedPuzzleId.isBlank()) {
            statusLabel.setText("No hints available.");
            refreshUI();
            return;
        }

        // get hints and how many have been revealed so far (from state)
        @SuppressWarnings("unchecked")
        List<String> revealedMutable = (List<String>) state.getOrDefault("revealedHints", new ArrayList<String>());
        List<String> topHints = getHintsForPuzzleId(selectedPuzzleId);
        int alreadyRevealed = revealedMutable.size();
        if (alreadyRevealed < topHints.size()) {
            String next = topHints.get(alreadyRevealed);
            // create a new list copy (saved lists were often immutable via List.of())
            ArrayList<String> newRevealed = new ArrayList<>(revealedMutable);
            newRevealed.add(next);
            state.put("revealedHints", newRevealed);
            state.put("availableHintsCount", Math.max(0, topHints.size() - newRevealed.size()));

            // persist updated state into engine so save/other logic keeps working
            try {
                game.restoreState(state);
            } catch (Throwable t) {
                System.err.println("DEBUG: restoreState after providing hint failed: " + t);
            }

            // show the hint
            new Alert(Alert.AlertType.INFORMATION, next).showAndWait();
        } else {
            // no hints left
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
            App.setRoot("puzzlehome");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean saveSave() {
        try {
            File f = getSaveFileForCurrentUser();
            Map<String, Object> state = game.saveState();
            try (java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(new java.io.FileOutputStream(f))) {
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

                    // If an initial JSON state was loaded earlier, compare prompts and reject stale saves
                    Map<String,Object> currentState = game.getGameState();
                    String initialPrompt = (String) currentState.getOrDefault("prompt", "");
                    String savedPrompt = (String) saved.getOrDefault("prompt", "");
                    if (savedPrompt == null) savedPrompt = "";
                    if (!savedPrompt.equals(initialPrompt)) {
                        // mismatch -> delete stale save
                        if (!f.delete()) {
                            System.err.println("Could not delete stale save: " + f.getAbsolutePath());
                        } else {
                            System.out.println("Deleted stale save file: " + f.getAbsolutePath());
                        }
                        return;
                    }

                    // Defensive: ignore save files that don't include a canonical answer
                    String savedAns = safeGetString(saved, "answer");
                    if (savedAns.isBlank()) savedAns = safeGetString(saved, "correctAnswer");
                    if (savedAns.isBlank()) savedAns = safeGetString(saved, "solution");
                    if (savedAns.isBlank()) savedAns = safeGetString(saved, "expectedAnswer");

                    if (savedAns.isBlank() && !canonicalAnswerFromJson.isBlank()) {
                        System.out.println("DEBUG: save has no canonical answer — removing answer keys and restoring (keeping JSON answer).");
                        saved.remove("answer");
                        saved.remove("solution");
                        saved.remove("correctAnswer");
                        saved.remove("expectedAnswer");
                    }

                    // restore if matching
                    game.restoreState(saved);
                    System.out.println("DEBUG: after restore from save, gameState = " + safeToString(game.getGameState()));
                }
            }
        } catch (Throwable t) {
            System.err.println("Load save failed: " + t);
        }
    }

    // helpers for debug-safe stringification

    private static String safeToString(Object o) {
        try { return String.valueOf(o); } catch (Throwable t) { return "<unprintable>"; }
    }

    private static String safeGetString(Map<String,Object> m, String key) {
        if (m == null) return "";
        Object v = m.getOrDefault(key, "");
        return v == null ? "" : v.toString();
    }

    /**
     * Load & cache top-level hints from GameData.json grouped by puzzleId and ordered by hintPriority.
     * Uses the same resource path constant GAME_DATA_CLASSPATH you already have.
     */
    private void ensureHintsLoaded() {
        if (hintsCache != null) return;
        hintsCache = new HashMap<>();
        try (InputStream is = getClass().getResourceAsStream(GAME_DATA_CLASSPATH)) {
            if (is == null) {
                System.err.println("GameData.json not found for hints at " + GAME_DATA_CLASSPATH);
                return;
            }
            JsonElement rootElem = JsonParser.parseReader(new InputStreamReader(is));
            if (rootElem == null || !rootElem.isJsonObject()) return;
            JsonObject root = rootElem.getAsJsonObject();
            JsonElement hintsElem = root.get("hints");
            if (hintsElem == null || !hintsElem.isJsonArray()) return;
            JsonArray hintsArr = hintsElem.getAsJsonArray();

            // Collect entries into a temporary structure that keeps priority
            Map<String, List<HintHolder>> tmp = new HashMap<>();
            for (JsonElement he : hintsArr) {
                if (!he.isJsonObject()) continue;
                JsonObject ho = he.getAsJsonObject();
                String pid = ho.has("puzzleId") ? ho.get("puzzleId").getAsString() : "";
                String text = ho.has("hintText") ? ho.get("hintText").getAsString() : "";
                int pr = ho.has("hintPriority") ? ho.get("hintPriority").getAsInt() : Integer.MAX_VALUE;
                if (pid.isBlank()) continue;
                tmp.computeIfAbsent(pid, k -> new ArrayList<>()).add(new HintHolder(pr, text));
            }

            // Sort by priority & copy into hintsCache
            for (Map.Entry<String, List<HintHolder>> e : tmp.entrySet()) {
                List<HintHolder> list = e.getValue();
                list.sort((a, b) -> Integer.compare(a.priority, b.priority));
                List<String> texts = new ArrayList<>();
                for (HintHolder hh : list) texts.add(hh.text);
                hintsCache.put(e.getKey(), texts);
            }
        } catch (Throwable t) {
            System.err.println("Failed to load hints from GameData.json: " + t);
            // keep hintsCache as empty map (already set)
        }
    }

    private static class HintHolder {
        int priority;
        String text;
        HintHolder(int p, String t) { priority = p; text = t; }
    }

    /**
     * Return hints list for a puzzle id (never null).
     */
    private List<String> getHintsForPuzzleId(String puzzleId) {
        ensureHintsLoaded();
        List<String> l = hintsCache.get(puzzleId);
        return l == null ? List.of() : l;
    }

    /**
     * Apply top-level hints info into initial state map:
     * - sets availableHintsCount to number of hints found for this puzzle
     * - ensures revealedHints exists (empty list)
     *
     * This overrides values in the earlier per-puzzle data block so top-level hints are authoritative.
     */
    @SuppressWarnings("unchecked")
    private void applyTopLevelHintsToState(Map<String, Object> puzzle, Map<String, Object> state) {
        if (puzzle == null || state == null) return;
        Object pidObj = puzzle.get("puzzleId");
        String pid = pidObj == null ? "" : pidObj.toString();
        List<String> topHints = getHintsForPuzzleId(pid);
        state.put("availableHintsCount", topHints.size());
        // ensure revealedHints is an ArrayList so it can be mutated later
        state.put("revealedHints", new ArrayList<String>());
    }
}
