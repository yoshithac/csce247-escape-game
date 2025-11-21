package com.escapegame;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import com.model.AuthenticationService;
import com.model.User;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

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
            if (lblUser != null) {
            User user = App.getCurrentUser();
            if (user != null) {
                lblUser.setText("Welcome, " + user.getUserId().toUpperCase());
            } else {
                lblUser.setText("Welcome");
            }
        }

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
                newGameButton.setOnAction(e -> {
                    loadAndSwitch("difficulty");
                });
            }
            if (progressButton != null) {
                progressButton.setOnAction(e -> {
                    loadAndSwitch("progress");
                });
            }
            if (leaderboardButton != null) {
                leaderboardButton.setOnAction(e -> {
                    loadAndSwitch("leaderboard");
                });
            }
            if (certificatesButton != null) {
                certificatesButton.setOnAction(e -> {
                    loadAndSwitch("certificates");
                });
            }
            if (logoutButton != null) {
                logoutButton.setOnAction(e -> {
                    if (authService != null) {
                        try { authService.logout(); } catch (Throwable ignore) {}
                    }
                    // delete current user's riddle save on logout
                    try {
                        com.model.User appUser = App.getCurrentUser();
                        if (appUser != null && appUser.getUserId() != null && !appUser.getUserId().isEmpty()) {
                            String fn = ".escapegame_riddle_" + appUser.getUserId() + ".properties";
                            File f = new File(System.getProperty("user.home"), fn);
                            if (f.exists()) {
                                boolean deleted = f.delete();
                                System.out.println("Deleted riddle save for user " + appUser.getUserId() + "? " + deleted);
                            }
                        }
                    } catch (Throwable t) {
                        System.err.println("Failed to delete riddle save on logout: " + t.getMessage());
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

        Object controller = loader.getController();
        if (controller != null) {
            try {
                java.lang.reflect.Method m = controller.getClass().getMethod("initData", com.model.User.class, com.model.AuthenticationService.class);
                com.model.User appUser = null;
                com.model.AuthenticationService appAuth = null;
                try {
                    java.lang.reflect.Method gu = com.escapegame.App.class.getMethod("getCurrentUser");
                    appUser = (com.model.User) gu.invoke(null);
                } catch (NoSuchMethodException ignored) { }
                try {
                    java.lang.reflect.Method ga = com.escapegame.App.class.getMethod("getAuthService");
                    appAuth = (com.model.AuthenticationService) ga.invoke(null);
                } catch (NoSuchMethodException ignored) { }

                m.invoke(controller, appUser, appAuth);
            } catch (NoSuchMethodException nsme) {
            } catch (Throwable invokeEx) {
                System.err.println("Warning: failed to invoke initData on controller: " + invokeEx);
                invokeEx.printStackTrace();
            }
        }

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