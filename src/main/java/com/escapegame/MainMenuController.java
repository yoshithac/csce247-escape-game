package com.escapegame;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.model.AuthenticationService;
import com.model.User;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.fxml.FXMLLoader;

/**
 * Main menu controller — wired to mainmenu.fxml
 */
public class MainMenuController implements Initializable {

    @FXML private StackPane menuRoot;
    @FXML private ImageView backgroundImage;

    @FXML private Button newGameButton;
    @FXML private Button progressButton;
    @FXML private Button leaderboardButton;
    @FXML private Button certificatesButton;
    @FXML private Button logoutButton;

    @FXML private Label lblUser;

    private AuthenticationService authService;
    private User currentUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        try {
            if (lblUser != null) lblUser.setText("Welcome");

            if (backgroundImage != null && menuRoot != null) {
                try {
                    backgroundImage.fitWidthProperty().bind(menuRoot.widthProperty());
                    backgroundImage.fitHeightProperty().bind(menuRoot.heightProperty());
                    backgroundImage.setPreserveRatio(true);
                    backgroundImage.setSmooth(true);
                } catch (Throwable t) {
                    System.err.println("Warning: background image binding failed: " + t.getMessage());
                    t.printStackTrace();
                }
            }

            if (newGameButton != null) {
                try {
                    App.setRoot("difficulty"); 
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (progressButton != null) {
                try {
                    App.setRoot("progress");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (leaderboardButton != null) {
                try {
                    App.setRoot("leaderboard");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (certificatesButton != null) {
                try {
                    App.setRoot("certificates");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (logoutButton != null) {
                logoutButton.setOnAction(e -> {
                    if (authService != null) {
                        try { authService.logout(); } catch (Throwable ignore) {}
                    }
                    loadAndSwitch("login");
                });
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * Called by the loader after FXMLLoader.load() to pass runtime data
     */
    public void initData(User user, AuthenticationService auth) {
        this.currentUser = user;
        this.authService = auth;
        try {
            if (lblUser != null) {
                if (user != null) {
                    String display = user.getUserId();
                    lblUser.setText("Welcome, " + display.toUpperCase());
                } else {
                    lblUser.setText("Welcome, Guest");
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * Helper to load a target FXML and switch the current Scene root.
     */
    private void loadAndSwitch(String baseName) {
        String[] candidates = new String[] {
            "/library/" + baseName + ".fxml",
            "/" + baseName + ".fxml",
            "/fxml/" + baseName + ".fxml",
            "/views/" + baseName + ".fxml"
        };

        FXMLLoader loader = null;
        java.net.URL found = null;
        String tried = "";
        for (String p : candidates) {
            tried += p + " ";
            java.net.URL u = getClass().getResource(p);
            if (u != null) {
                found = u;
                loader = new FXMLLoader(found);
                break;
            }
        }

        if (loader == null) {
            System.err.println("MainMenuController: could not find FXML for '" + baseName + "'. Tried: " + tried);
            return;
        }

        try {
            Parent root = loader.load();
            // try to switch scene using a known node (menuRoot or any button)
            if (menuRoot != null && menuRoot.getScene() != null) {
                menuRoot.getScene().setRoot(root);
            } else if (newGameButton != null && newGameButton.getScene() != null) {
                newGameButton.getScene().setRoot(root);
            } else {
                System.err.println("MainMenuController: no scene available to switch root to " + baseName);
            }
        } catch (Throwable t) {
            System.err.println("MainMenuController.loadAndSwitch failed for '" + baseName + "': " + t.getClass().getSimpleName() + " - " + t.getMessage());
            t.printStackTrace();
        }
    }
}