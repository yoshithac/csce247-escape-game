package com.model;

import java.util.List;

import static com.model.GameDataFacade.gameDataFacade;

/*
 * This class manages the game flow and player interactions
 * @author We're Getting an A
 */
public class GameManager{
    private User currentPlayer;
    private List<User> players;
    private DifficultyLevel difficulty;
    private int startTime;
    private boolean isActive = false;
    private GameProgress currentProgress;
    private List<Puzzle> currentPuzzles;
    private List<Hint> hints;
    private int timer;

    /**
     * Constructor for GameManager
     */
    public GameManager() {
        currentPlayer = null;
        players = null;
        difficulty = DifficultyLevel.EASY;
        startTime = 0;
        isActive = false;
        currentProgress = null;
        currentPuzzles = null;
        hints = null;
        gameDataFacade = GameDataFacade.getInstance();
    }

    /**
     * Starts a new game session.
     */
    public void startGame() {
        isActive = true;
        startTime = (int) (System.currentTimeMillis() / 1000L);
        currentPuzzles = gameDataFacade.getPuzzles();
        currentProgress = new GameProgress(currentPuzzles);
        startTimer();
    }

    /**
     * Loads a saved game for a user.
     * @param userId The ID of the user whose game is to be loaded
     */
    public void loadGame(String userId) {
        User u = gameDataFacade.getUser(userId);
        if (u == null) {
            throw new IllegalArgumentException("User not found: " + userId);
        }
        currentPlayer = u;
        GameData gd = gameDataFacade.getGameData();
        if (gd != null && gd.getGameProgress() != null) {
            currentProgress = gd.getGameProgress();
        } else {
            currentProgress = new GameProgress(gameDataFacade.getPuzzles());
            setCurrentPlayer(u);
        }
    }

    /**
     * Saves the current game state for a user.
     * @param userId The ID of the user whose game is to be saved
     */
    public void saveGame(String userId) {
        /* if (userId == null) return;
        if (currentPlayer != null && userId.equals(currentPlayer.getUserId())) {
            gameDataFacade.saveUser(currentPlayer);
        }
        if (gameDataFacade.loadGameData() != null) {
            GameData gd = gameDataFacade.loadGameData();
            try {
                gd.setGameProgress(this.currentProgress);
                gameDataFacade.saveGameData(gd);
            } catch (Exception e) {
                
            }
        } */
    }

    /**
     * Pauses the current game session.
     */
    public void pauseGame() {
        /* 
        this.isActive = false; */
    }

    /**
     * Gets the leaderboard.
     */
    public Leaderboard getLeaderboard() {
        return gameDataFacade.getGameData().getLeaderboard();
        
    }

    /**
     * Ends the current game session.
     */
    public void endGame() {
        /*
        this.isActive = false;
            */
    }

    /**
     * Registers a new user.
     * @param newUser The data of the user to be registered
     */
    public String registerUser(String userId, String passWord, String firstName, String lastName, String email) {
        
        if (gameDataFacade.getUsers() != null) {
            for (User user : gameDataFacade.getUsers()) {
                if (user.getUserId().equals(userId)){
                    return "userId already used. Please register with different UserId";
                }
            }
            User newUser = new User(userId,passWord,firstName,lastName,email,"user",0,0);
            gameDataFacade.addUser(newUser);
            this.currentPlayer = newUser;
            return "Successfull registered";
        }
        
        return "User Database not available. Please comeback";
    }

    /**
     * Starts the next puzzle for the current player.
     */
    public void startNextPuzzle() {
        if (currentProgress == null) return;
        currentProgress.checkProgress();
        Puzzle next = currentProgress.getCurrentPuzzle();
        if (next == null) {
            handleGameEnd();
        } else {
            //ui to display puzzle
        }
    }

    /**
     * Moves the player in the specified direction.
     * @param direction The direction to move the player
     */
    public boolean loginUser(String userId, String password) {
        User user = gameDataFacade.getUser(userId);
        if (user == null) return false;
        if(password != null && password.equals(user.getPassword())){
            this.currentPlayer = user;
            return true;
        }else{
            return false;
        }
        
    }

    /**
     * Deletes the current user.
     */
    public void deleteUser(String userId) {
        
    }

    /**
     * Recovers the account for a user.
     * @param userId The ID of the user whose account is to be recovered
     */
    public void recoverAccount (String userId) {
        User u = gameDataFacade.getUser(userId);
        if (u == null) return;
        //ui to display recovery options
    }

    /*
     * Loads users
     */
    public boolean saveUsers() {
        return gameDataFacade.saveUsers();
    }

    /**
     * 
     */
    public boolean saveGameData() {
            return gameDataFacade.saveGameData();
    }

    public List<User> getUsers() {
        return gameDataFacade.getUsers();
    }

    /*
     * Loads game data
     */
    public GameData loadGameData() {
        return gameDataFacade.getGameData();
    }

    /**
     * Checks if the game is over
     * @return True if the game is over, false otherwise
     */
    public boolean checkGameOver() {
        if (currentProgress == null) return true;
        return currentProgress.getCurrentPuzzle() == null && currentProgress.getToDoPuzzles().isEmpty();
    }

    /**
     * Handles the end of the game
     */
    public void handleGameEnd() {
        isActive = false;
        //ui to display end game
    }

    /**
     * Starts the timer for the current game session
     */
    public void startTimer() {
        if(!isActive) {
            isActive = true;
        }
        startTime = (int) (System.currentTimeMillis() / 1000L);
    }

    /**
     * Gets remaining time in the current game session
     * @return Remaining time in seconds
     */
    public int getRemainingTime() {
        return 1800 - ((int) (System.currentTimeMillis() / 1000L) - startTime);
    }

    /**
     * Pauses the timer for the current game session
     */
    public void pauseTimer() {
        isActive = false;
        timer += (int) (System.currentTimeMillis() / 1000L) - startTime;
        startTime = 0;
    }

    /**
     * Gets elapsed time in the current game session
     * @return Elapsed time in seconds
     */
    public int getElapsedTime() {
        return timer;
    }

    public void clearTimer() {
        timer = 0;
        startTime = 0;  
        isActive = false;
    }

    /**
     * Checks if the time is up for the current game session
     * @return True if time is up, false otherwise
     */
    public boolean isTimeUp() {
        return getRemainingTime() <= 0;
    }

    /**
     * Gets the difficulty level of the game
     * @return The difficulty level
     */
    public DifficultyLevel getDifficulty() {
        return difficulty;
    }

    /**
     * Gets the current player
     * @return The current player
     */
    public User getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Sets the current player
     * @param user The user to be set as the current player
     */    
    public void setCurrentPlayer(User user) {
        this.currentPlayer = user;
    }

    /**
     * Gets the start time of the game session
     * @return The start time in seconds since epoch
     */
    public int getStartTime() {
        return startTime;
    }   
}

