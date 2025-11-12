package com.escapegame;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.model.AuthenticationService;
import com.model.User;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

/**
 * Controller for mainmenu.fxml. Exposes initData(User, AuthenticationService)
 * so it can receive the logged-in user and the service instance from LoginController.
 */
public class MainMenuController implements Initializable {

    @FXML private StackPane menuRoot;
    @FXML private ImageView backgroundImage;

    @FXML private Button btnNewGame;
    @FXML private Button btnViewProgress;
    @FXML private Button btnLeaderboard;
    @FXML private Button btnCertificates;
    @FXML private Button btnLogout;

    @FXML private Label lblUser;

    // these will be set from the initData(...) call
    private AuthenticationService authService;
    private User currentUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // bind background image sizing
        if (backgroundImage != null && menuRoot != null) {
            backgroundImage.fitWidthProperty().bind(menuRoot.widthProperty());
            backgroundImage.fitHeightProperty().bind(menuRoot.heightProperty());
            backgroundImage.setPreserveRatio(true);
            backgroundImage.setSmooth(true);
        }

        // default text until initData is called
        lblUser.setText("Welcome");
    }

    /**
     * Called by the loader after FXMLLoader.load() to pass runtime data.
     * @param user currently logged-in user
     * @param auth AuthenticationService instance that performed login
     */
    public void initData(User user, AuthenticationService auth) {
        this.currentUser = user;
        this.authService = auth;

        if (user != null) {
            String display = (user.getFirstName() != null && !user.getFirstName().isBlank())
                    ? user.getFirstName() : user.getUserId();
            lblUser.setText("Welcome, " + display.toUpperCase());
        } else {
            lblUser.setText("Welcome, guest");
        }

        // wire button handlers (keeps controller self-contained)
        btnNewGame.setOnAction(e -> {
            try { App.setRoot("home"); } catch (IOException ex) { ex.printStackTrace(); }
        });
        btnViewProgress.setOnAction(e -> {
            try { App.setRoot("progress"); } catch (IOException ex) { ex.printStackTrace(); }
        });
        btnLeaderboard.setOnAction(e -> {
            try { App.setRoot("leaderboard"); } catch (IOException ex) { ex.printStackTrace(); }
        });
        btnCertificates.setOnAction(e -> {
            try { App.setRoot("certificates"); } catch (IOException ex) { ex.printStackTrace(); }
        });
        btnLogout.setOnAction(e -> {
            // clear auth state in this instance and navigate to login
            if (authService != null) authService.logout();
            try { App.setRoot("login"); } catch (IOException ex) { ex.printStackTrace(); }
        });
    }
}