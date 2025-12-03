package com.model;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class WordPuzzleGame implements PuzzleGame {
    private String puzzleType;
    private String puzzleId;
    private String prompt;
    private List<String> acceptedAnswers;
    private String canonicalAnswer;
    private String category;
    private int maxAttempts;
    private int attemptsUsed;
    private List<String> guesses;
    private List<Hint> hints;
    private List<String> revealedHints;
    private boolean won;
    private long startTime;
    private String extraText;

    private static final Map<String, Map<String, Map<String, Object>>> BUILTIN_CONFIGS = new HashMap<>();

    static {
        try {
            InputStream in = WordPuzzleGame.class.getResourceAsStream("/GameData.json");
            if (in == null) {
                System.err.println("WordPuzzleGame: /GameData.json not found on classpath.");
            } else {
                StringBuilder sb = new StringBuilder();
                try (BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append('\n');
                    }
                }
                Object parsed = SimpleJson.parse(sb.toString());
                if (!(parsed instanceof Map)) {
                    System.err.println("WordPuzzleGame: GameData.json did not parse to an object.");
                } else {
                    Map<String, Object> top = (Map<String, Object>) parsed;
                    Map<String, Map<String, Object>> puzzleIdToEntry = new HashMap<>();
                    Object puzzlesObj = top.get("puzzles");
                    if (puzzlesObj instanceof List) {
                        for (Object pObj : (List<?>) puzzlesObj) {
                            if (!(pObj instanceof Map)) continue;
                            Map<String, Object> pmap = (Map<String, Object>) pObj;
                            Object pid = pmap.get("puzzleId");
                            if (pid == null) continue;
                            puzzleIdToEntry.put(pid.toString(), pmap);
                        }
                    }
                    Map<String, List<Map<String, Object>>> hintsByPuzzle = new HashMap<>();
                    Object hintsObj = top.get("hints");
                    if (hintsObj instanceof List) {
                        for (Object hObj : (List<?>) hintsObj) {
                            if (!(hObj instanceof Map)) continue;
                            Map<String, Object> hmap = (Map<String, Object>) hObj;
                            Object pid = hmap.get("puzzleId");
                            if (pid == null) continue;
                            hintsByPuzzle.computeIfAbsent(pid.toString(), k -> new ArrayList<>()).add(hmap);
                        }
                    }
                    Map<String, List<String>> orderedHints = new HashMap<>();
                    for (Map.Entry<String, List<Map<String, Object>>> e : hintsByPuzzle.entrySet()) {
                        List<Map<String, Object>> rawList = e.getValue();
                        rawList.sort(Comparator.comparingInt(m -> {
                            Object pr = m.get("hintPriority");
                            if (pr instanceof Number) return ((Number) pr).intValue();
                            try { return Integer.parseInt(String.valueOf(pr)); } catch (Exception ex) { return 0; }
                        }));
                        List<String> texts = new ArrayList<>();
                        for (Map<String, Object> hm : rawList) {
                            Object ht = hm.get("hintText");
                            if (ht != null) texts.add(ht.toString());
                        }
                        orderedHints.put(e.getKey(), texts);
                    }
                    for (Map.Entry<String, Map<String, Object>> entry : puzzleIdToEntry.entrySet()) {
                        String pid = entry.getKey();
                        Map<String, Object> p = entry.getValue();
                        String pType = (p.get("puzzleType") == null) ? "UNKNOWN" : p.get("puzzleType").toString().toUpperCase();
                        String difficulty = (p.get("difficulty") == null) ? "MEDIUM" : p.get("difficulty").toString().toUpperCase();
                        Map<String, Object> data = (Map<String, Object>) p.get("data");
                        if (data == null) data = new HashMap<>();
                        Map<String, Object> cfg = new HashMap<>();
                        Object prm = data.get("prompt");
                        if (prm == null) prm = p.getOrDefault("description", p.get("title"));
                        cfg.put("prompt", prm == null ? "" : prm.toString());
                        List<String> answers = new ArrayList<>();
                        Object ans = data.get("answer");
                        if (ans != null) answers.add(ans.toString());
                        Object accAns = data.get("acceptedAnswers");
                        if (accAns instanceof List) {
                            for (Object a : (List<?>) accAns) if (a != null) answers.add(a.toString());
                        }
                        cfg.put("acceptedAnswers", answers);
                        cfg.put("category", data.getOrDefault("category", p.getOrDefault("puzzleType", "")));
                        int maxA = 3;
                        Object ma = data.get("maxAttempts");
                        if (ma instanceof Number) maxA = ((Number) ma).intValue();
                        else if (ma != null) {
                            try { maxA = Integer.parseInt(ma.toString()); } catch (Exception ignored) {}
                        }
                        cfg.put("maxAttempts", Integer.valueOf(Math.max(1, maxA)));
                        List<String> hlist = orderedHints.getOrDefault(pid, new ArrayList<>());
                        cfg.put("hints", hlist);
                        cfg.put("extraText", data.getOrDefault("prompt", ""));
                        cfg.put("nextScene", p.getOrDefault("nextScene", ""));
                        BUILTIN_CONFIGS.computeIfAbsent(pType, k -> new HashMap<>()).put(difficulty, cfg);
                    }
                    System.out.println("WordPuzzleGame: loaded GameData.json (" + BUILTIN_CONFIGS.size() + " puzzle types)");
                }
            }
        } catch (Throwable t) {
            System.err.println("WordPuzzleGame: failed to load GameData.json -> " + t);
        }
    }

    public WordPuzzleGame() {
    }

    @Override
    public void initialize(Map<String, Object> puzzleData) {
        this.prompt = (String) puzzleData.getOrDefault("prompt", "");
        Object answers = puzzleData.get("acceptedAnswers");
        this.acceptedAnswers = new ArrayList<>();
        if (answers instanceof List) {
            for (Object o : (List<?>) answers) {
                if (o != null) acceptedAnswers.add(o.toString().trim().toUpperCase(Locale.ROOT));
            }
        } else if (puzzleData.get("answer") != null) {
            acceptedAnswers.add(puzzleData.get("answer").toString().trim().toUpperCase(Locale.ROOT));
        }
        this.canonicalAnswer = acceptedAnswers.isEmpty() ? "" : acceptedAnswers.get(0);

        this.category = (String) puzzleData.getOrDefault("category", "");
        Number ma = (Number) puzzleData.getOrDefault("maxAttempts", Integer.valueOf(3));
        this.maxAttempts = Math.max(1, ma.intValue());
        this.attemptsUsed = 0;
        this.guesses = new ArrayList<>();
        this.revealedHints = new ArrayList<>();
        this.won = false;
        this.startTime = System.currentTimeMillis();
        this.extraText = (String) puzzleData.getOrDefault("extraText", "");

        this.hints = new ArrayList<>();
        Object hintsObj = puzzleData.get("hints");
        if (hintsObj instanceof List) {
            List<String> htexts = (List<String>) hintsObj;
            for (int i = 0; i < htexts.size(); i++) {
                this.hints.add(new Hint(htexts.get(i), this.puzzleId, i + 1));
            }
        }
    }


    @Override
    public boolean processInput(String input) {
        if (input == null) return false;
        input = input.trim().toUpperCase(Locale.ROOT);
        if (input.equals("HINT")) {
            revealNextHint();
            return true;
        }
        for (String a : acceptedAnswers) {
            if (a.equals(input)) {
                won = true;
                return true;
            }
        }
        if (!guesses.contains(input)) guesses.add(input);
        attemptsUsed++;
        return true;
    }

    @Override
    public boolean isGameOver() {
        return won || attemptsUsed >= maxAttempts;
    }

    @Override
    public Map<String, Object> getGameState() {
        Map<String, Object> state = new HashMap<>();
        state.put("puzzleType", puzzleType);
        state.put("prompt", prompt);
        state.put("category", category);
        state.put("attemptsUsed", attemptsUsed);
        state.put("maxAttempts", maxAttempts);
        state.put("remainingAttempts", Math.max(0, maxAttempts - attemptsUsed));
        state.put("guesses", new ArrayList<>(guesses));
        state.put("revealedHints", new ArrayList<>(revealedHints));
        state.put("availableHintsCount", Math.max(0, hints.size() - revealedHints.size()));
        state.put("extraText", extraText);
        return state;
    }

    @Override
    public String getGameType() {
        return puzzleType;
    }

    @Override
    public Map<String, Object> getResult() {
        Map<String, Object> result = new HashMap<>();
        result.put("won", won);
        result.put("time", System.currentTimeMillis() - startTime);
        result.put("moves", attemptsUsed);
        result.put("hintsUsed", revealedHints.size());
        if (!won) result.put("answer", canonicalAnswer);
        return result;
    }

    @Override
    public void reset() {
        attemptsUsed = 0;
        guesses.clear();
        revealedHints.clear();
        won = false;
        startTime = System.currentTimeMillis();
    }

    @Override
    public Map<String, Object> saveState() {
        Map<String, Object> state = new HashMap<>();
        state.put("puzzleType", puzzleType);
        state.put("puzzleId", puzzleId);
        state.put("prompt", prompt);
        state.put("acceptedAnswers", new ArrayList<>(acceptedAnswers));
        state.put("category", category);
        state.put("maxAttempts", maxAttempts);
        state.put("attemptsUsed", attemptsUsed);
        state.put("guesses", new ArrayList<>(guesses));
        state.put("revealedHints", new ArrayList<>(revealedHints));
        state.put("won", won);
        state.put("startTime", startTime);
        state.put("extraText", extraText);
        return state;
    }

    @Override
    public void restoreState(Map<String, Object> savedState) {
        this.puzzleType = (String) savedState.get("puzzleType");
        this.puzzleId = (String) savedState.get("puzzleId");
        this.prompt = (String) savedState.get("prompt");
        Object answers = savedState.get("acceptedAnswers");
        this.acceptedAnswers = new ArrayList<>();
        if (answers instanceof List) {
            for (Object o : (List<?>) answers) {
                if (o != null) this.acceptedAnswers.add(o.toString().trim().toUpperCase(Locale.ROOT));
            }
        }
        this.canonicalAnswer = acceptedAnswers.isEmpty() ? "" : acceptedAnswers.get(0);
        this.category = (String) savedState.get("category");
        Number ma = (Number) savedState.getOrDefault("maxAttempts", Integer.valueOf(3));
        this.maxAttempts = Math.max(1, ma.intValue());
        this.attemptsUsed = ((Number) savedState.getOrDefault("attemptsUsed", Integer.valueOf(0))).intValue();
        Object guessesObj = savedState.get("guesses");
        this.guesses = guessesObj instanceof List ? new ArrayList<>((List<String>) guessesObj) : new ArrayList<>();
        Object revealedObj = savedState.get("revealedHints");
        this.revealedHints = revealedObj instanceof List ? new ArrayList<>((List<String>) revealedObj) : new ArrayList<>();
        this.won = (Boolean) savedState.getOrDefault("won", Boolean.FALSE);
        this.startTime = ((Number) savedState.getOrDefault("startTime", System.currentTimeMillis())).longValue();
        this.hints = new ArrayList<>();
        this.extraText = (String) savedState.getOrDefault("extraText", "");
    }

    public void setHints(List<Hint> hints) {
        this.hints = new ArrayList<>(hints);
    }

    public void setPuzzleType(String puzzleType) {
        this.puzzleType = puzzleType;
    }

    public void setPuzzleId(String puzzleId) {
        this.puzzleId = puzzleId;
    }

    private void revealNextHint() {
        int revealedCount = revealedHints.size();
        if (revealedCount < hints.size()) {
            Hint nextHint = hints.get(revealedCount);
            revealedHints.add(nextHint.getHintText());
        }
    }

    public static Map<String, Object> configFor(String puzzleType, String difficulty) {
        if (puzzleType == null) return null;
        Map<String, Map<String, Object>> byType = BUILTIN_CONFIGS.get(puzzleType.toUpperCase(Locale.ROOT));
        if (byType == null) return null;
        Map<String, Object> cfg = byType.get(difficulty == null ? "MEDIUM" : difficulty.toUpperCase(Locale.ROOT));
        return cfg;
    }

    static final class SimpleJson {
        private final String s;
        private int pos;

        private SimpleJson(String s) {
            this.s = s;
            this.pos = 0;
        }

        static Object parse(String s) {
            SimpleJson p = new SimpleJson(s);
            p.skipWhitespace();
            Object v = p.parseValue();
            p.skipWhitespace();
            return v;
        }

        private void skipWhitespace() {
            while (pos < s.length()) {
                char c = s.charAt(pos);
                if (c == ' ' || c == '\n' || c == '\r' || c == '\t' || c == '\f') pos++;
                else break;
            }
        }

        private Object parseValue() {
            skipWhitespace();
            if (pos >= s.length()) return null;
            char c = s.charAt(pos);
            if (c == '{') return parseObject();
            if (c == '[') return parseArray();
            if (c == '"') return parseString();
            if (c == 't' || c == 'f' || c == 'n') return parseLiteral();
            return parseNumber();
        }

        private Map<String, Object> parseObject() {
            Map<String, Object> map = new HashMap<>();
            pos++; // skip '{'
            skipWhitespace();
            if (pos < s.length() && s.charAt(pos) == '}') { pos++; return map; }
            while (pos < s.length()) {
                skipWhitespace();
                String key = parseString();
                skipWhitespace();
                if (pos < s.length() && s.charAt(pos) == ':') pos++;
                skipWhitespace();
                Object value = parseValue();
                map.put(key, value);
                skipWhitespace();
                if (pos < s.length() && s.charAt(pos) == ',') { pos++; continue; }
                if (pos < s.length() && s.charAt(pos) == '}') { pos++; break; }
                break;
            }
            return map;
        }

        private List<Object> parseArray() {
            List<Object> list = new ArrayList<>();
            pos++; // skip '['
            skipWhitespace();
            if (pos < s.length() && s.charAt(pos) == ']') { pos++; return list; }
            while (pos < s.length()) {
                skipWhitespace();
                Object v = parseValue();
                list.add(v);
                skipWhitespace();
                if (pos < s.length() && s.charAt(pos) == ',') { pos++; continue; }
                if (pos < s.length() && s.charAt(pos) == ']') { pos++; break; }
                break;
            }
            return list;
        }

        private String parseString() {
            StringBuilder sb = new StringBuilder();
            if (pos >= s.length() || s.charAt(pos) != '"') return "";
            pos++;
            while (pos < s.length()) {
                char c = s.charAt(pos++);
                if (c == '"') break;
                if (c == '\\') {
                    if (pos >= s.length()) break;
                    char e = s.charAt(pos++);
                    switch (e) {
                        case '"': sb.append('"'); break;
                        case '\\': sb.append('\\'); break;
                        case '/': sb.append('/'); break;
                        case 'b': sb.append('\b'); break;
                        case 'f': sb.append('\f'); break;
                        case 'n': sb.append('\n'); break;
                        case 'r': sb.append('\r'); break;
                        case 't': sb.append('\t'); break;
                        case 'u':
                            if (pos + 3 < s.length()) {
                                String hex = s.substring(pos, pos + 4);
                                try {
                                    int code = Integer.parseInt(hex, 16);
                                    sb.append((char) code);
                                } catch (Exception ex) { }
                                pos += 4;
                            }
                            break;
                        default:
                            sb.append(e);
                    }
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        }

        private Object parseLiteral() {
            if (s.startsWith("true", pos)) { pos += 4; return Boolean.TRUE; }
            if (s.startsWith("false", pos)) { pos += 5; return Boolean.FALSE; }
            if (s.startsWith("null", pos)) { pos += 4; return null; }
            return null;
        }

        private Number parseNumber() {
            int start = pos;
            boolean isFloat = false;
            if (pos < s.length() && (s.charAt(pos) == '-' || s.charAt(pos) == '+')) pos++;
            while (pos < s.length()) {
                char c = s.charAt(pos);
                if (c >= '0' && c <= '9') pos++;
                else break;
            }
            if (pos < s.length() && s.charAt(pos) == '.') {
                isFloat = true;
                pos++;
                while (pos < s.length()) {
                    char c = s.charAt(pos);
                    if (c >= '0' && c <= '9') pos++;
                    else break;
                }
            }
            if (pos < s.length() && (s.charAt(pos) == 'e' || s.charAt(pos) == 'E')) {
                isFloat = true;
                pos++;
                if (pos < s.length() && (s.charAt(pos) == '+' || s.charAt(pos) == '-')) pos++;
                while (pos < s.length()) {
                    char c = s.charAt(pos);
                    if (c >= '0' && c <= '9') pos++;
                    else break;
                }
            }
            String num = s.substring(start, pos);
            try {
                if (isFloat) return Double.parseDouble(num);
                else {
                    long v = Long.parseLong(num);
                    if (v <= Integer.MAX_VALUE && v >= Integer.MIN_VALUE) return (int) v;
                    return v;
                }
            } catch (Exception ex) {
                return 0;
            }
        }
    }
}
