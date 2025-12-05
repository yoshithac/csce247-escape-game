package com.escapegame.controller;

import com.escapegame.App;
import com.escapegame.util.DialogHelper;
import com.escapegame.util.ViewNavigator;
import com.model.*;
import com.model.AuthenticationService.LoginResult;
import com.model.AuthenticationService.RegistrationResult;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.image.*;

import java.util.*;

/**
 * GameContainerController - Main Container for Login/Registration/Main Menu
 * 
 * REFACTORED VERSION:
 * - Uses GameServiceManager for login/registration (delegates to AuthenticationService)
 * - Uses ViewNavigator for navigation
 * - Uses DialogHelper for alerts
 * - Cleaner state management
 * 
 * LEFT PANEL STATES:
 * 1. Welcome Buttons (Login/Register)
 * 2. Login Form
 * 3. Registration Form
 * 4. Main Menu (with difficulty submenu)
 * 
 * RIGHT PANEL:
 * - Static image (house_exterior_side.png) - always shown in this controller
 * - Progress/Leaderboard/Certificates views load on top of background
 */
public class GameContainerController {

    // ==================== LEFT PANEL COMPONENTS ====================
    
    @FXML private VBox leftPanel;
    
    // State 1: Welcome Buttons
    @FXML private VBox welcomeButtonsPanel;
    
    // State 2: Login Form
    @FXML private VBox loginFormPanel;
    @FXML private TextField loginUserId;
    @FXML private PasswordField loginPassword;
    @FXML private Label loginMessageLabel;
    
    // State 3: Registration Form
    @FXML private VBox registerFormPanel;
    @FXML private TextField registerUserId;
    @FXML private PasswordField registerPassword;
    @FXML private TextField registerFirstName;
    @FXML private TextField registerLastName;
    @FXML private TextField registerEmail;
    @FXML private Label registerMessageLabel;
    
    // State 4: Main Menu
    @FXML private VBox mainMenuPanel;
    @FXML private Label userNameLabel;
    @FXML private Label userScoreLabel;
    @FXML private Label userPuzzlesLabel;
    @FXML private VBox difficultySubmenu;
    @FXML private Button resumeGameButton;
    
    // ==================== RIGHT PANEL COMPONENTS ====================
    
    @FXML private StackPane rightPanel;
    @FXML private ImageView staticImageView;
    @FXML private BorderPane gamePlayPane;
    
    // ==================== SERVICES & DATA ====================
    
    private final GameServiceManager serviceManager = GameServiceManager.getInstance();
    private User currentUser;
    
    // Images
    private Image houseExteriorImage;
    private Image backgroundFrameImage;
    
    // Frame padding percentages
    private static final double FRAME_LEFT_PERCENT = 0.14;
    private static final double FRAME_RIGHT_PERCENT = 0.14;
    private static final double FRAME_TOP_PERCENT = 0.17;
    private static final double FRAME_BOTTOM_PERCENT = 0.15;
    
    // Panel states
    private enum LeftPanelState {
        WELCOME, LOGIN, REGISTER, MAIN_MENU
    }

    // ==================== INITIALIZATION ====================
    
    @FXML
    public void initialize() {
        System.out.println("GameContainerController initialized");
        
        loadImages();
        Platform.runLater(this::bindImageViewToPanel);
        
        // Check if user is already logged in (returning from GameSessionView)
        User loggedInUser = serviceManager.getCurrentUser();
        if (loggedInUser != null) {
            currentUser = loggedInUser;
            switchLeftPanel(LeftPanelState.MAIN_MENU);
            System.out.println("User already logged in: " + currentUser.getFullName());
        } else {
            switchLeftPanel(LeftPanelState.WELCOME);
        }
        showStaticImage("house_exterior_side");
    }
    
    private void loadImages() {
        try {
            houseExteriorImage = new Image(
                getClass().getResourceAsStream("/images/backgrounds/house_exterior_side.png"));
            backgroundFrameImage = new Image(
                getClass().getResourceAsStream("/images/backgrounds/background_frame.png"));
            System.out.println("Images loaded successfully");
        } catch (Exception e) {
            System.err.println("Error loading images: " + e.getMessage());
        }
    }
    
    private void bindImageViewToPanel() {
        staticImageView.fitWidthProperty().bind(rightPanel.widthProperty());
        staticImageView.fitHeightProperty().bind(rightPanel.heightProperty());
        staticImageView.setSmooth(true);
        staticImageView.setCache(true);
        
        rightPanel.widthProperty().addListener((obs, old, val) -> updateFramePadding());
        rightPanel.heightProperty().addListener((obs, old, val) -> updateFramePadding());
    }
    
    private void updateFramePadding() {
        double width = rightPanel.getWidth();
        double height = rightPanel.getHeight();
        
        if (width > 0 && height > 0) {
            double left = width * FRAME_LEFT_PERCENT;
            double right = width * FRAME_RIGHT_PERCENT;
            double top = height * FRAME_TOP_PERCENT;
            double bottom = height * FRAME_BOTTOM_PERCENT;
            
            gamePlayPane.setPadding(new javafx.geometry.Insets(top, right, bottom, left));
        }
    }
    
    // ==================== LEFT PANEL STATE MANAGEMENT ====================
    
    private void switchLeftPanel(LeftPanelState state) {
        // Hide all panels
        hideAllPanels();
        
        // Show selected panel
        switch (state) {
            case WELCOME:
                showPanel(welcomeButtonsPanel);
                clearAllForms();
                break;
            case LOGIN:
                showPanel(loginFormPanel);
                break;
            case REGISTER:
                showPanel(registerFormPanel);
                break;
            case MAIN_MENU:
                showPanel(mainMenuPanel);
                updateMainMenuInfo();
                break;
        }
        System.out.println("Left panel switched to: " + state);
    }
    
    private void hideAllPanels() {
        setVisibility(welcomeButtonsPanel, false);
        setVisibility(loginFormPanel, false);
        setVisibility(registerFormPanel, false);
        setVisibility(mainMenuPanel, false);
    }
    
    private void showPanel(VBox panel) {
        setVisibility(panel, true);
    }
    
    private void setVisibility(VBox panel, boolean visible) {
        if (panel != null) {
            panel.setVisible(visible);
            panel.setManaged(visible);
        }
    }
    
    private void showStaticImage(String imageName) {
        if (gamePlayPane != null) {
            gamePlayPane.setCenter(null);
            gamePlayPane.setVisible(false);
            gamePlayPane.setManaged(false);
        }
        
        staticImageView.setVisible(true);
        if ("house_exterior_side".equals(imageName)) {
            staticImageView.setImage(houseExteriorImage);
        } else if ("background_frame".equals(imageName)) {
            staticImageView.setImage(backgroundFrameImage);
        }
    }
    
    // ==================== WELCOME SCREEN HANDLERS ====================
    
    @FXML
    private void handleLoginClick() {
        switchLeftPanel(LeftPanelState.LOGIN);
    }
    
    @FXML
    private void handleRegisterClick() {
        switchLeftPanel(LeftPanelState.REGISTER);
    }
    
    @FXML
    private void handleCancelToWelcome() {
        switchLeftPanel(LeftPanelState.WELCOME);
        showStaticImage("house_exterior_side");
    }
    
    // ==================== LOGIN HANDLERS (Using GameServiceManager) ====================
    
    @FXML
    private void handleLoginSubmit() {
        String userId = loginUserId.getText().trim();
        String password = loginPassword.getText();
        
        // Use GameServiceManager for login attempt (delegates to AuthenticationService)
        LoginResult result = serviceManager.attemptLogin(userId, password);
        
        if (result.isSuccess()) {
            currentUser = result.getUser();
            showLoginMessage(result.getMessage(), false);
            
            // Delay then switch to main menu
            delayedAction(500, () -> switchLeftPanel(LeftPanelState.MAIN_MENU));
        } else {
            showLoginMessage(result.getMessage(), true);
            loginPassword.clear();
        }
    }
    
    private void showLoginMessage(String message, boolean isError) {
        if (loginMessageLabel != null) {
            loginMessageLabel.setText(message);
            loginMessageLabel.setStyle(isError ? 
                "-fx-text-fill: #ff4444;" : "-fx-text-fill: #44ff44;");
        }
    }
    
    // ==================== REGISTRATION HANDLERS (Using GameServiceManager) ====================
    
    @FXML
    private void handleRegisterSubmit() {
        String userId = registerUserId.getText().trim();
        String password = registerPassword.getText();
        String firstName = registerFirstName.getText().trim();
        String lastName = registerLastName.getText().trim();
        String email = registerEmail.getText().trim();
        
        // Use GameServiceManager for registration attempt (delegates to AuthenticationService)
        RegistrationResult result = serviceManager.attemptRegistration(
            userId, password, firstName, lastName, email);
        
        showRegisterMessage(result.getMessage(), !result.isSuccess());
        
        if (result.isSuccess()) {
            // Return to welcome after short delay
            delayedAction(1000, () -> {
                clearRegisterForm();
                switchLeftPanel(LeftPanelState.WELCOME);
            });
        }
    }
    
    private void showRegisterMessage(String message, boolean isError) {
        if (registerMessageLabel != null) {
            registerMessageLabel.setText(message);
            registerMessageLabel.setStyle(isError ? 
                "-fx-text-fill: #ff4444;" : "-fx-text-fill: #44ff44;");
        }
    }
    
    // ==================== MAIN MENU HANDLERS ====================
    
    @FXML
    private void handleStartNewGame() {
        // Toggle difficulty submenu
        boolean currentlyVisible = difficultySubmenu.isVisible();
        difficultySubmenu.setVisible(!currentlyVisible);
        difficultySubmenu.setManaged(!currentlyVisible);
    }
    
    @FXML
    private void handleDifficultyEasy() {
        startGameWithDifficulty("EASY");
    }
    
    @FXML
    private void handleDifficultyMedium() {
        startGameWithDifficulty("MEDIUM");
    }
    
    @FXML
    private void handleDifficultyHard() {
        startGameWithDifficulty("HARD");
    }
    
    private void startGameWithDifficulty(String difficulty) {
        System.out.println("Starting new game with difficulty: " + difficulty);
        
        // Clear any old session data
        serviceManager.clearSession();
        serviceManager.clearPausedPuzzle();
        SessionData.clearSession();
        SessionData.setDifficulty(difficulty);
        
        // Hide difficulty submenu
        difficultySubmenu.setVisible(false);
        difficultySubmenu.setManaged(false);
        
        // Navigate to IntroScreen1 (story introduction)
        ViewNavigator.navigateWithFallback(
            ViewNavigator.INTRO_SCREEN_1,
            ViewNavigator.GAME_SESSION,
            "Error starting game"
        );
    }
    
    @FXML
    private void handleResumeGame() {
        try {
            // Restore session if available
            if (serviceManager.hasSavedSession()) {
                System.out.println("Restoring saved session...");
                serviceManager.restoreSession();
            }
            
            // Check if there's a paused PUZZLE first
            if (serviceManager.hasPausedPuzzle()) {
                resumePausedPuzzle();
                return;
            }
            
            // Check if there's a saved SESSION (but no paused puzzle)
            if (serviceManager.hasSavedSession()) {
                resumeSavedSession();
                return;
            }
            
            DialogHelper.showInfo("No saved game found");
            updateMainMenuInfo();
            
        } catch (Exception e) {
            e.printStackTrace();
            DialogHelper.showError("Error resuming game: " + e.getMessage());
        }
    }
    
    private void resumePausedPuzzle() throws Exception {
        String pausedPuzzleId = serviceManager.getPausedPuzzleId();
        System.out.println("Resuming paused puzzle: " + pausedPuzzleId);
        
        Optional<Puzzle> puzzleOpt = serviceManager.getPuzzle(pausedPuzzleId);
        if (!puzzleOpt.isPresent()) {
            DialogHelper.showInfo("Saved puzzle not found!");
            serviceManager.clearPausedPuzzle();
            updateMainMenuInfo();
            return;
        }
        
        Puzzle puzzle = puzzleOpt.get();
        SessionData.setCurrentPuzzle(puzzle);
        SessionData.setResuming(true);
        
        // Find which door this puzzle belongs to
        for (int door = 1; door <= 5; door++) {
            Optional<Puzzle> doorPuzzle = serviceManager.getSessionPuzzleForDoor(door);
            if (doorPuzzle.isPresent() && doorPuzzle.get().getPuzzleId().equals(pausedPuzzleId)) {
                SessionData.setCurrentDoorNumber(door);
                break;
            }
        }
        
        // Navigate to puzzle view
        String viewName = ViewNavigator.getViewNameForPuzzleType(puzzle.getPuzzleType());
        ViewNavigator.navigateTo(viewName);
    }
    
    private void resumeSavedSession() throws Exception {
        Map<String, Object> savedSessionState = serviceManager.getSavedSessionState();
        String savedDifficulty = (String) savedSessionState.get("sessionDifficulty");
        SessionData.setDifficulty(savedDifficulty);
        
        System.out.println("Resuming session with difficulty: " + savedDifficulty);
        ViewNavigator.navigateToGameSession();
    }
    
    @FXML
    private void handleViewProgress() {
        System.out.println("Loading Progress View...");
        loadViewInRightPanel("ProgressView");
    }
    
    @FXML
    private void handleViewLeaderboard() {
        System.out.println("Loading Leaderboard View...");
        loadViewInRightPanel("LeaderboardView");
    }
    
    @FXML
    private void handleViewCertificates() {
        System.out.println("Loading Certificates View...");
        loadViewInRightPanel("CertificatesView");
    }
    
    private void loadViewInRightPanel(String viewName) {
        try {
            String fxmlPath = "fxml/" + viewName + ".fxml";
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                App.class.getResource(fxmlPath));
            javafx.scene.Parent view = loader.load();
            
            // Set background image
            staticImageView.setImage(backgroundFrameImage);
            staticImageView.setVisible(true);
            
            // Load view into gamePlayPane
            gamePlayPane.setCenter(view);
            gamePlayPane.setVisible(true);
            gamePlayPane.setManaged(true);
            gamePlayPane.setStyle("-fx-background-color: transparent;");
            
            StackPane.setAlignment(gamePlayPane, javafx.geometry.Pos.CENTER);
            updateFramePadding();
            
            System.out.println("View loaded: " + viewName);
            
        } catch (Exception e) {
            System.err.println("ERROR loading view: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleLogout() {
        currentUser = null;
        serviceManager.logout();
        SessionData.reset();
        
        switchLeftPanel(LeftPanelState.WELCOME);
        showStaticImage("house_exterior_side");
    }
    
    // ==================== HELPER METHODS ====================
    
    @SuppressWarnings("unchecked")
    private void updateMainMenuInfo() {
        if (currentUser != null) {
            userNameLabel.setText(currentUser.getFullName());
            
            String userId = currentUser.getUserId();
            int totalScore = serviceManager.getUserTotalScore(userId);
            int completedCount = serviceManager.getCompletedPuzzleCount(userId);
            
            userScoreLabel.setText("Score: " + totalScore);
            userPuzzlesLabel.setText("Puzzles: " + completedCount);
            
            // Check for resume button visibility
            boolean hasPausedPuzzle = serviceManager.hasPausedPuzzle();
            boolean hasSessionWithProgress = false;
            
            if (serviceManager.hasSavedSession()) {
                Map<String, Object> sessionState = serviceManager.getSavedSessionState();
                hasSessionWithProgress = sessionState != null && sessionState.containsKey("doorPuzzleMap");
            }
            
            boolean hasSavedGame = hasPausedPuzzle || hasSessionWithProgress;
            resumeGameButton.setVisible(hasSavedGame);
            resumeGameButton.setManaged(hasSavedGame);
            
            if (hasPausedPuzzle) {
                System.out.println("Found paused puzzle: " + serviceManager.getPausedPuzzleId());
            }
            if (hasSessionWithProgress) {
                System.out.println("Found saved session with progress");
            }
        }
        
        showStaticImage("house_exterior_side");
    }
    
    private void clearAllForms() {
        clearLoginForm();
        clearRegisterForm();
    }
    
    private void clearLoginForm() {
        if (loginUserId != null) loginUserId.clear();
        if (loginPassword != null) loginPassword.clear();
        if (loginMessageLabel != null) loginMessageLabel.setText("");
    }
    
    private void clearRegisterForm() {
        if (registerUserId != null) registerUserId.clear();
        if (registerPassword != null) registerPassword.clear();
        if (registerFirstName != null) registerFirstName.clear();
        if (registerLastName != null) registerLastName.clear();
        if (registerEmail != null) registerEmail.clear();
        if (registerMessageLabel != null) registerMessageLabel.setText("");
    }
    
    /**
     * Execute an action after a delay (on UI thread)
     */
    private void delayedAction(int delayMs, Runnable action) {
        new Thread(() -> {
            try {
                Thread.sleep(delayMs);
                Platform.runLater(action);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}