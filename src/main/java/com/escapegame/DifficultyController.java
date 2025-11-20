package com.escapegame;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.application.Platform;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

import com.model.AuthenticationService;
import com.model.ConsoleView;
import com.model.GameController;  // this is the model GameController in com.model
import com.model.User;

/**
 * DifficultyController — wires difficulty buttons to start the model game.
 */
public class DifficultyController implements Initializable {

    @FXML private Button easyButton;
    @FXML private Button mediumButton;
    @FXML private Button hardButton;
    @FXML private Button backButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (easyButton != null) {
            easyButton.setOnAction(e -> {
                System.out.println("Difficulty: Easy clicked");
                App.setChosenDifficulty("EASY");
                try { App.setRoot("startgame"); } catch (IOException ex) { ex.printStackTrace(); }
                //startModelGameInBackground();
            });
        }

        if (mediumButton != null) {
            mediumButton.setOnAction(e -> {
                System.out.println("Difficulty: Medium clicked");
                App.setChosenDifficulty("MEDIUM");
                try { App.setRoot("startgame"); } catch (IOException ex) { ex.printStackTrace(); }
                //startModelGameInBackground();
            });
        }

        if (hardButton != null) {
            hardButton.setOnAction(e -> {
                System.out.println("Difficulty: Hard clicked");
                App.setChosenDifficulty("HARD");
                try { App.setRoot("startgame"); } catch (IOException ex) { ex.printStackTrace(); }
                //startModelGameInBackground();
            });
        }

        if (backButton != null) {
            backButton.setOnAction(e -> {
                try {
                    App.setRoot("mainmenu");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
        }
    }

    @FXML
        private void onEasy() {
            try {
                App.setRoot("startgame");
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Easy clicked");
            App.setChosenDifficulty("EASY");
            //startModelGameInBackground();
        }

        @FXML
        private void onMedium() {
            try {
                App.setRoot("startgame");
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Medium clicked");
            App.setChosenDifficulty("MEDIUM");
            //startModelGameInBackground();
        }

        @FXML
        private void onHard() {
            try {
                App.setRoot("startgame");
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Hard clicked");
            App.setChosenDifficulty("HARD");
            //startModelGameInBackground();
        }

        @FXML
        private void back() {
            System.out.println("Back clicked");
            try {
                App.setRoot("mainmenu");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    /**
     * This constructs model objects from com.model package (AuthenticationService, ConsoleView, GameController).
     */
    private void startModelGameInBackground() {
        new Thread(() -> {
            try {
                AuthenticationService authService = new AuthenticationService();

                try {
                    User fxUser = App.getCurrentUser();
                    if (fxUser != null) {
                        try {
                            // try a setter method first
                            java.lang.reflect.Method m = AuthenticationService.class.getMethod("setCurrentUser", User.class);
                            m.invoke(authService, fxUser);
                            System.out.println("Set model authService currentUser via setCurrentUser.");
                        } catch (NoSuchMethodException nsme) {
                            // fallback to reflective field set if no setter exists
                            try {
                                java.lang.reflect.Field f = AuthenticationService.class.getDeclaredField("currentUser");
                                f.setAccessible(true);
                                f.set(authService, fxUser);
                                System.out.println("Set model authService currentUser via reflection.");
                            } catch (NoSuchFieldException | IllegalAccessException fie) {
                                System.err.println("Could not inject currentUser into AuthenticationService: " + fie);
                            }
                        }
                    } else {
                        System.err.println("No App currentUser found; model will be unauthenticated.");
                    }
                } catch (Throwable t) {
                    System.err.println("Warning while attempting to set model currentUser: " + t);
                }

                Scanner scanner = new Scanner(System.in);
                ConsoleView view = new ConsoleView(scanner);

                GameController modelController = new GameController(view, authService);


                String chosen2 = com.escapegame.App.getChosenDifficulty();
                if (chosen2 != null) {
                    try {
                        modelController.setSessionDifficulty(chosen2); // requires public setter in GameController
                        System.out.println("DifficultyController: injected sessionDifficulty='" + chosen2 + "' into modelController");
                        // consume App chosen difficulty so it's not reused accidentally
                        com.escapegame.App.clearChosenDifficulty();
                    } catch (Throwable t) {
                        System.err.println("DifficultyController: failed to call setSessionDifficulty on modelController: " + t);
                        t.printStackTrace();
                    }
                } else {
                    System.out.println("DifficultyController: App.getChosenDifficulty() returned null before starting modelController.");
                }
                try {
                    java.lang.reflect.Method setDiff = GameController.class.getMethod("setSessionDifficulty", String.class);
                    String chosen = App.getChosenDifficulty();
                    if (chosen != null) {
                        setDiff.invoke(modelController, chosen);
                        System.out.println("Injected session difficulty into model via setSessionDifficulty: " + chosen);
                        App.clearChosenDifficulty();
                    }
                } catch (NoSuchMethodException nsme) {
                    
                } catch (Throwable t) {
                    System.err.println("Warning invoking setSessionDifficulty on model: " + t);
                }

                modelController.start();

                Platform.runLater(() -> {
                    try {
                        App.setRoot("mainmenu");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });

            } catch (Throwable t) {
                t.printStackTrace();
                Platform.runLater(() -> {
                    javafx.scene.control.Alert a = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                    a.setTitle("Game start error");
                    a.setHeaderText("Could not start game");
                    a.setContentText(t.toString());
                    a.showAndWait();
                });
            }
        }, "Model-Game-Thread").start();

    }
}