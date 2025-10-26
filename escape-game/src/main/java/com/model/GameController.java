package com.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.speech.Speak;

/**
 * Main game controller - handles all game flow after login
 * Uses a single difficulty selection for all puzzles in the session
 */
public class GameController {
    private final AuthenticationService authService;
    private final GameProgressService progressService;
    private final LeaderboardService leaderboardService;
    private final CertificateService certificateService;
    private final GameDataFacade dataFacade;
    private final GameView view;
    private String sessionDifficulty = null; // Difficulty for session


    public GameController(GameView view, AuthenticationService authService) {
        this.view = view;
        this.authService = authService;
        this.progressService = new GameProgressService();
        this.leaderboardService = new LeaderboardService();
        this.certificateService = new CertificateService();
        this.dataFacade = GameDataFacade.getInstance();
    }

    public void start() {
        while (authService.isLoggedIn()) {
            // Prompt for difficulty if not set, or allow changing it explicitly
      //      if (sessionDifficulty == null) {
       //         sessionDifficulty = selectDifficulty();
       //     }

            view.clear();
            String userId = authService.getCurrentUser().getUserId();
            String userName = authService.getCurrentUser().getFullName();
            

            view.showMessage("\n" + "=".repeat(50));
            view.showMessage("     Welcome, " + userName + "!");
            view.showMessage("=".repeat(50));
            view.showMessage("Current Session Difficulty: " + sessionDifficulty);

            // Check for saved game
            UserProgress progress = progressService.getUserProgress(userId);
            boolean hasSavedGame = progress.hasGameInProgress();
            

         // Build menu dynamically
            List<String> menuOptions = new ArrayList<>();
            menuOptions.add("Start New Game");
            if (hasSavedGame) {
                menuOptions.add("Resume Saved Game");
            }
            menuOptions.add("View Progress");
            menuOptions.add("View Leaderboard");
            menuOptions.add("View Certificates");
            menuOptions.add("Logout");

            String choice = view.showMenu(menuOptions.toArray(new String[0]));
            int choiceNum = parseChoice(choice);

            // Build clean index map
            int menuIndex = 1;
            int START_NEW = menuIndex++;
            int RESUME = hasSavedGame ? menuIndex++ : -1;
            int VIEW_PROGRESS = menuIndex++;
            int VIEW_LEADERBOARD = menuIndex++;
            int VIEW_CERTS = menuIndex++;
            int LOGOUT = menuIndex++;

            // Handle menu selection
            if (choiceNum == START_NEW) {
                startNewPuzzle();
                progress.resetTimer();  
                progress.startTimer();
            } else if (choiceNum == RESUME && hasSavedGame) {
                resumeSavedGame();
            } else if (choiceNum == VIEW_PROGRESS) {
                viewProgress();
            } else if (choiceNum == VIEW_LEADERBOARD) {
                viewLeaderboard();
            } else if (choiceNum == VIEW_CERTS) {
                viewCertificates();
            } else if (choiceNum == LOGOUT) {
                return; // Logout
            } else {
                view.showMessage("Invalid choice.");
                waitForUser();
            }

        }
    }

    private String selectDifficulty() {
        view.clear();
        view.showMessage("\nSelect difficulty for this session:");
        String diffChoice = view.showMenu(new String[]{"EASY", "MEDIUM", "HARD"});
        String[] difficulties = {"EASY", "MEDIUM", "HARD"};
        int diffIndex = parseChoice(diffChoice) - 1;
        if (diffIndex < 0 || diffIndex >= difficulties.length) {
            view.showMessage("Invalid choice. Defaulting to EASY.");
            waitForUser();
            return "EASY";
        }
        view.showMessage("Difficulty set to " + difficulties[diffIndex] + ".");
        waitForUser();
        return difficulties[diffIndex];
    }
    
    private void startNewPuzzle() {
        // Prompt for difficulty if not set
        if (sessionDifficulty == null) {
            sessionDifficulty = selectDifficulty();
        }
        
        String userId = authService.getCurrentUser().getUserId();
        String difficulty = sessionDifficulty;
        //Game intro starts here
        view.clear();
        String intro = "Rain pours as you stand before the infamous Hollow Manor, the house everyone in town swears is haunted."
                     + "\n"
                     + " You came to prove the legends wrong but the moment you step inside, the door slams shut behind you."
                     + "\n"
                     + " A whisper echos through the halls: Find the keys.. before it finds you.."
                     + "\n"
                     + "Each room hides a puzzle. Each puzzle guards a key. Solve them all to unlock to door.. and escape the manor before it's too late.";             
        System.out.println(intro);
        Speak.speak(intro);
        waitForUser();
        view.clear();
        //Game intro ends here
        // Step 1: Get all puzzle types
        Set<String> allTypes = dataFacade.getAvailablePuzzleTypes();
        
        // Step 2: Select one random uncompleted puzzle per type
        Map<String, Puzzle> sessionPuzzles = new HashMap<>();
        for (String type : allTypes) {
            List<Puzzle> puzzles = dataFacade.getPuzzlesByDifficulty(type, difficulty);
            // Filter to only uncompleted puzzles
            puzzles = puzzles.stream()
                .filter(p -> !progressService.isPuzzleCompleted(userId, p.getPuzzleId()))
                .collect(java.util.stream.Collectors.toList());
            
            if (!puzzles.isEmpty()) {
                // Randomly select one
                Collections.shuffle(puzzles);
                sessionPuzzles.put(type, puzzles.get(0));
            }
        }
        
        if (sessionPuzzles.isEmpty()) {
            view.showMessage("No uncompleted puzzles available for this difficulty!");
            waitForUser();
            return;
        }
        
        // Step 3: Show session menu and let user play puzzles one by one
        Set<String> completedTypes = new HashSet<>();
        
        while (completedTypes.size() < sessionPuzzles.size()) {
            view.clear();
            view.showMessage("=".repeat(50));
            view.showMessage("GAME SESSION - " + difficulty);
            view.showMessage("Completed: " + completedTypes.size() + "/" + sessionPuzzles.size());
            view.showMessage("=".repeat(50));
            view.showMessage("\nSelect a puzzle to play:");
            
            // Build menu of uncompleted puzzles
            List<String> menuOptions = new ArrayList<>();
            List<String> typesList = new ArrayList<>();
            
            for (String type : sessionPuzzles.keySet()) {
                if (!completedTypes.contains(type)) {
                    Puzzle puzzle = sessionPuzzles.get(type);
                    menuOptions.add(String.format("%s - %s", type, puzzle.getTitle()));
                    typesList.add(type);
                }
            }
            
            menuOptions.add("Exit Session");
            
            //String choice = view.showMenu(menuOptions.toArray(new String[0]));
            String choice = view.showPuzzlesMenu(menuOptions.toArray(new String[0]));
            int choiceNum = parseChoice(choice);
            
            if (choiceNum < 1 || choiceNum > menuOptions.size()) {
                view.showMessage("Invalid choice.");
                waitForUser();
                continue;
            }
            
            // Check if user wants to exit
            if (choiceNum == menuOptions.size()) {
                view.showMessage("Exiting game session...");
                waitForUser();
                return;
            }
            
            // Play selected puzzle
            String selectedType = typesList.get(choiceNum - 1);
            Puzzle selectedPuzzle = sessionPuzzles.get(selectedType);
            
            playGame(selectedPuzzle, null);
            
            // Check if puzzle was completed
            if (progressService.isPuzzleCompleted(userId, selectedPuzzle.getPuzzleId())) {
                completedTypes.add(selectedType);
            }
        }
        // Final key message 
        String msg = "You found all the keys needed to escape the manor .. Press ENTER to craft the final key and escape!";
        System.out.println(msg);
        Speak.speak(msg);
        // End of final key message 
        // All puzzles completed!
        view.clear();
        view.showMessage("=".repeat(50));
        view.showMessage(" CONGRATULATIONS!");
        view.showMessage("You completed all puzzles in this session!");
        view.showMessage("=".repeat(50));
        waitForUser();
    }


    private void resumeSavedGame() {
        String userId = authService.getCurrentUser().getUserId();
        UserProgress progress = progressService.getUserProgress(userId);
        progress.startTimer();

        if (!progress.hasGameInProgress()) {
            view.showMessage("\nNo saved game found!");
            waitForUser();
            return;
        }
        String puzzleId = progress.getCurrentPuzzleId();
        Optional<Puzzle> puzzleOpt = dataFacade.getPuzzle(puzzleId);
        if (!puzzleOpt.isPresent()) {
            view.showMessage("\nSaved puzzle not found!");
            progress.clearGameState();
            dataFacade.saveUserProgress(progress);
            waitForUser();
            return;
        }
        Puzzle puzzle = puzzleOpt.get();
        Map<String, Object> savedState = progress.getGameState();
        view.showMessage("\nResuming: " + puzzle.getTitle());
        waitForUser();
        playGame(puzzle, savedState);
    }

    private void playGame(Puzzle puzzle, Map<String, Object> savedState) {
        String userId = authService.getCurrentUser().getUserId();
        PuzzleGame game = GameFactory.createGame(puzzle.getPuzzleType());

        if (savedState != null) {
            game.restoreState(savedState);
        } else {
            game.initialize(puzzle.getData());
        }
        if (game instanceof WordPuzzleGame) {
            List<Hint> hints = dataFacade.getHintsForPuzzle(puzzle.getPuzzleId());
            ((WordPuzzleGame) game).setHints(hints);
            ((WordPuzzleGame) game).setPuzzleId(puzzle.getPuzzleId());
        }

        while (!game.isGameOver()) {
            view.clear();
            view.displayGame(game.getGameState(), game.getGameType());
            String input = view.getUserInput("\nYour move: ");

            if (input.equalsIgnoreCase("save")) {
                saveGame(puzzle, game);
                return;
            }
            if (input.equalsIgnoreCase("quit")) {
                view.showMessage("Game abandoned.");
                UserProgress progress = progressService.getUserProgress(userId);
                progress.clearGameState();
                dataFacade.saveUserProgress(progress);
                progress.resetTimer();
                waitForUser();
                return;
            }

            boolean validInput = game.processInput(input);
            if (!validInput) {
                view.showMessage("Invalid input. Try again.");
                waitForUser();
            }

            if (game instanceof MatchingGame && game.getGameState().get("secondCard") != null) {
                view.clear();
                view.displayGame(game.getGameState(), game.getGameType());
                
                //waitForUser();
                
                // Pause briefly to let user see both cards
                try {
                    Thread.sleep(1000); // 1 second delay
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                ((MatchingGame) game).clearSelection();
            }
        }

        view.clear();
        Map<String, Object> result = game.getResult();
        view.showResult(result);
        boolean won = (boolean) result.get("won");
        if (won) {
            int score = calculateScore(result);
            progressService.completePuzzle(userId, puzzle.getPuzzleId(), score);
            certificateService.awardCertificate(userId, puzzle, score);
            view.showMessage("* Certificate earned!");
        }
        UserProgress progress = progressService.getUserProgress(userId);
        progress.clearGameState();
        dataFacade.saveUserProgress(progress);
        waitForUser();
    }

    private void saveGame(Puzzle puzzle, PuzzleGame game) {
        String userId = authService.getCurrentUser().getUserId();
        UserProgress progress = progressService.getUserProgress(userId);
        Map<String, Object> gameState = game.saveState();
        progress.saveGameState(puzzle.getPuzzleId(), gameState);
        dataFacade.saveUserProgress(progress);
        progress.pauseTimer();

        view.showMessage("\nGame saved! You can resume later.");
        waitForUser();
    }

    private int calculateScore(Map<String, Object> result) {
        long timeMs = (long) result.get("time");
        int moves = (int) result.get("moves");
        int baseScore = 100;
        int timeBonus = Math.max(0, 100 - (int)(timeMs / 1000));
        int moveBonus = Math.max(0, 50 - moves);
        return baseScore + timeBonus + moveBonus;
    }

    private void viewProgress() {
        String userId = authService.getCurrentUser().getUserId();
        UserProgress progress = progressService.getUserProgress(userId);
        Map<String, Integer> stats = progressService.getProgressStats(userId);

        view.clear();
        view.showMessage("\n=== YOUR PROGRESS ===\n");
        view.showMessage("Total Score: " + progress.getTotalScore());
        view.showMessage("Puzzles Completed: " + stats.get("completed") + "/" + stats.get("totalPuzzles"));
        view.showMessage("Completion: " + stats.get("completionPercentage") + "%");
        view.showMessage("Remaining: " + stats.get("remaining"));
        view.showMessage("Time Elapsed: " + progress.getTimer()/60 + " minutes, " + progress.getTimer()%60 + " seconds");
        waitForUser();
    }

    private void viewLeaderboard() {
        List<LeaderboardEntry> top10 = leaderboardService.getTopPlayers(10);
        String userId = authService.getCurrentUser().getUserId();
        int userRank = leaderboardService.getUserRank(userId);

        view.clear();
        view.showMessage("\n=== LEADERBOARD - TOP 10 ===\n");

        for (int i = 0; i < top10.size(); i++) {
            LeaderboardEntry entry = top10.get(i);
            String marker = entry.getUserId().equals(userId) ? " *" : "";
            view.showMessage(String.format("%2d. %-20s %5d pts (%d puzzles)%s",
                i + 1, entry.getUserName(), entry.getTotalScore(),
                entry.getPuzzlesCompleted(), marker));
        }
        if (userRank > 10) {
            view.showMessage("\n...");
            view.showMessage("Your rank: #" + userRank);
        }
        waitForUser();
    }

    private void viewCertificates() {
        String userId = authService.getCurrentUser().getUserId();
        List<Certificate> certs = certificateService.getUserCertificates(userId);
        Map<String, Integer> stats = certificateService.getCertificateStats(userId);

        view.clear();
        view.showMessage("\n=== YOUR CERTIFICATES ===\n");
        view.showMessage("Total: " + certs.size());
        view.showMessage("Easy: " + stats.get("EASY") + " | Medium: " + stats.get("MEDIUM") + " | Hard: " + stats.get("HARD"));
        view.showMessage("\nRecent certificates:");
        int limit = Math.min(10, certs.size());
        for (int i = 0; i < limit; i++) {
            Certificate cert = certs.get(i);
            view.showMessage(String.format("* %s (%s) - %d pts",
                cert.getDescription(), cert.getDifficulty(), cert.getScoreAchieved()));
        }
        waitForUser();
    }

    private int parseChoice(String choice) {
        try {
            return Integer.parseInt(choice.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void waitForUser() {
        view.getUserInput("\nPress Enter to continue...");
    }
}

