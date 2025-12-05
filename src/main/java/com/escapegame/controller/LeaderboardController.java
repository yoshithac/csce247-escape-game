package com.escapegame.controller;

import com.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

/**
 * LeaderboardController - Leaderboard Display
 * Loads into RIGHT PANEL of GameContainerView
 * No back button needed - user navigates via left panel menu
 */
public class LeaderboardController {
    @FXML private TableView<LeaderboardEntry> leaderboardTable;
    @FXML private TableColumn<LeaderboardEntry, Integer> rankColumn;
    @FXML private TableColumn<LeaderboardEntry, String> nameColumn;
    @FXML private TableColumn<LeaderboardEntry, Integer> scoreColumn;
    @FXML private TableColumn<LeaderboardEntry, Integer> puzzlesColumn;
    @FXML private Label yourRankLabel;

    private final GameServiceManager serviceManager = GameServiceManager.getInstance();

    @FXML
    public void initialize() {
        User currentUser = serviceManager.getCurrentUser();
        if (currentUser == null) {
            return;
        }
        
        String userId = currentUser.getUserId();
        
        // Load data
        List<LeaderboardEntry> entries = serviceManager.getTopPlayers(100);
        ObservableList<LeaderboardEntry> items = FXCollections.observableArrayList(entries);
        leaderboardTable.setItems(items);
        
        // Setup table columns with proper competition ranking
        // Players with same score get same rank (delegated to LeaderboardService)
        rankColumn.setCellValueFactory(cellData -> {
            LeaderboardEntry entry = cellData.getValue();
            int rank = serviceManager.calculateRank(entry);
            return new javafx.beans.property.SimpleIntegerProperty(rank).asObject();
        });
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("totalScore"));
        puzzlesColumn.setCellValueFactory(new PropertyValueFactory<>("puzzlesCompleted"));

        // Show user's rank (competition ranking from service)
        int rank = serviceManager.getUserRank(userId);
        yourRankLabel.setText("#" + (rank > 0 ? rank : "N/A"));

        // Highlight current user's row
        leaderboardTable.setRowFactory(tv -> new TableRow<LeaderboardEntry>() {
            @Override
            protected void updateItem(LeaderboardEntry item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && item.getUserId().equals(userId)) {
                    setStyle("-fx-background-color: rgba(102, 126, 234, 0.6); -fx-text-fill: white;");
                } else {
                    setStyle("");
                }
            }
        });
    }
}