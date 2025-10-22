package com.model;

/**
 * Puzzle class for the escape game
 * @author We're Getting an A
 */
public class Puzzle {

    private String puzzleId;
    private String type; // RIDDLE, ANAGRAM, CIPHER
    private String prompt;
    private String answer;
    private String description;
    private boolean isCompleted = false;
    private int maxAttempts = Integer.MAX_VALUE;
    private int attemptsUsed = 0;
    private DifficultyLevel difficulty;

    public Puzzle() {
    }

    public Puzzle(String puzzleId, String type, String prompt, String answer, String description, DifficultyLevel difficulty, int maxAttempts) {
        this.puzzleId = puzzleId;
        this.type = type;
        this.prompt = prompt;
        this.answer = answer;
        this.description = description;
        this.difficulty = difficulty;
        this.maxAttempts = maxAttempts;
    }

    public String getPuzzleId() {
        return puzzleId;
    }

    public void setPuzzleId(String puzzleId) {
        this.puzzleId = puzzleId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public int getAttemptsUsed() {
        return attemptsUsed;
    }

    public void setAttemptsUsed(int attemptsUsed) {
        this.attemptsUsed = attemptsUsed;
    }

    public DifficultyLevel getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(DifficultyLevel difficulty) {
        this.difficulty = difficulty;
    }

    public boolean tryAnswer(String candidate) {
        // Defensive checks
        if (candidate == null) {
            attemptsUsed++;
            return false;
        }

        // If already completed, treat as correct (or you could return false to avoid re-checking)
        if (isCompleted) {
            return true;
        }

        // Normalize both strings: trim and lower-case for simple comparison
        String cleanCandidate = candidate.trim().toLowerCase();
        String correct = (answer == null) ? "" : answer.trim().toLowerCase();

        // Count attempt
        attemptsUsed++;

        // Check if we've exceeded max attempts - still allow the check, but you can alter behavior if desired
        if (attemptsUsed > maxAttempts) {
            // The game might disallow further tries; here we still check but caller can inspect attemptsUsed/maxAttempts
            // If you'd rather block checking after maxAttempts, uncomment next lines:
            // return false;
        }

        // Compare
        boolean correctAnswer = cleanCandidate.equals(correct);
        if (correctAnswer) {
            this.isCompleted = true;
        }
        return correctAnswer;
    }

    /**
     * Return a short, helpful hint depending on puzzle type.
     * Beginner-friendly and small: not a full solution, just a nudge.
     * 
     *  - RIDDLE  -> returns the first letter of the answer if available
     *  - ANAGRAM -> reveals first letter
     *  - CIPHER  -> looks for "KEY:<n>" or "KEY=<n>" in description and suggests a Caesar shift if found
     *
     * @return a short hint string
     */
    public String getHint() {
        // If type is missing, fallback to showing the prompt
        if (type == null) {
            return safePromptHint();
        }

        String t = type.trim().toUpperCase();
        String ans = (answer == null) ? "" : answer.trim();

        try {
            switch (t) {
                case "RIDDLE":
                    if (!ans.isEmpty()) {
                        // Show first letter, but don't reveal too much
                        return "Hint: the answer starts with '" + ans.charAt(0) + "'";
                    } else {
                        return "Hint: think about the riddle carefully.";
                    }
                case "ANAGRAM":
                    if (!ans.isEmpty()) {
                        // Reveal a single letter (first) as a small clue
                        return "Hint: one letter in the answer is '" + ans.charAt(0) + "'";
                    } else {
                        return "Hint: try rearranging the letters.";
                    }
                case "CIPHER":
                    Integer key = parseCipherKeyFromDescription();
                    if (key != null) {
                        return "Hint: try a Caesar shift of " + key + ".";
                    } else {
                        // If there's no explicit key, give a gentle nudge
                        return "Hint: think about letter shifts or substitution.";
                    }
                default:
                    return safePromptHint();
            }
        } catch (Exception e) {
            // In case anything unexpected happens, return a safe fallback
            return safePromptHint();
        }
    }

    /**
     * Small helper that returns a safe hint based on the prompt (used as fallback).
     */
    private String safePromptHint() {
        if (prompt == null || prompt.trim().isEmpty()) {
            return "Hint: try thinking about the puzzle again.";
        }
        return "Hint: " + prompt;
    }

    /**
     * Attempt to read a simple integer cipher key from the description.
     * Looks for patterns like "KEY:3" or "KEY=3" (ignores case, spaces).
     *
     * @return Integer key if found, otherwise null
     */
    private Integer parseCipherKeyFromDescription() {
        if (description == null) return null;

        String desc = description.toUpperCase();

        // Look for "KEY:" pattern
        int idx = desc.indexOf("KEY:");
        if (idx >= 0) {
            String rest = desc.substring(idx + 4).trim();
            return parseLeadingInteger(rest);
        }

        // Look for "KEY=" pattern
        idx = desc.indexOf("KEY=");
        if (idx >= 0) {
            String rest = desc.substring(idx + 4).trim();
            return parseLeadingInteger(rest);
        }

        // Nothing found
        return null;
    }

    /**
     * Parse an integer from the start of the string (handles optional leading - sign).
     * Returns null if the beginning of the string is not an integer.
     */
    private Integer parseLeadingInteger(String s) {
        if (s == null || s.isEmpty()) return null;

        StringBuilder num = new StringBuilder();
        int i = 0;

        // allow optional negative sign
        if (s.charAt(0) == '-') {
            num.append('-');
            i = 1;
            if (s.length() == 1) return null; // only a minus sign, no digits
        }

        // collect digits
        for (; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isDigit(c)) {
                num.append(c);
            } else {
                break;
            }
        }

        if (num.length() == 0 || (num.length() == 1 && num.charAt(0) == '-')) {
            return null;
        }

        try {
            return Integer.parseInt(num.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return "Puzzle{" +
                "puzzleId='" + puzzleId + '\'' +
                ", type='" + type + '\'' +
                ", prompt='" + prompt + '\'' +
                ", answer='" + (answer == null ? "" : "[hidden]") + '\'' +
                ", isCompleted=" + isCompleted +
                ", attemptsUsed=" + attemptsUsed +
                ", maxAttempts=" + maxAttempts +
                '}';
    }
}
