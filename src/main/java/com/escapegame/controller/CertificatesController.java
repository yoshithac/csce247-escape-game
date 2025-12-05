package com.escapegame.controller;

import com.model.*;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * CertificatesController - Achievement Showcase Controller
 * 
 * Displays user certificates in beautiful card format
 * Loads into RIGHT PANEL of GameContainerView
 */
public class CertificatesController {
    
    @FXML private Label totalLabel;
    @FXML private Label easyCountLabel;
    @FXML private Label mediumCountLabel;
    @FXML private Label hardCountLabel;
    @FXML private Label totalScoreLabel;
    @FXML private FlowPane certificatesPane;
    @FXML private ScrollPane certificatesScroll;
    @FXML private Label emptyMessage;

    private final GameServiceManager serviceManager = GameServiceManager.getInstance();
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMM dd, yyyy");

    @FXML
    public void initialize() {
        User currentUser = serviceManager.getCurrentUser();
        if (currentUser == null) {
            showEmptyState();
            return;
        }
        
        String userId = currentUser.getUserId();
        
        // Get certificates and stats
        List<Certificate> certs = serviceManager.getUserCertificates(userId);
        Map<String, Integer> stats = serviceManager.getCertificateStats(userId);
        
        // Calculate total score
        int totalScore = certs.stream()
            .mapToInt(Certificate::getScoreAchieved)
            .sum();

        // Update stats display
        totalLabel.setText(String.valueOf(certs.size()));
        easyCountLabel.setText(String.valueOf(stats.getOrDefault("EASY", 0)));
        mediumCountLabel.setText(String.valueOf(stats.getOrDefault("MEDIUM", 0)));
        hardCountLabel.setText(String.valueOf(stats.getOrDefault("HARD", 0)));
        totalScoreLabel.setText(String.valueOf(totalScore));

        // Display certificates as cards
        if (certs.isEmpty()) {
            showEmptyState();
        } else {
            hideEmptyState();
            // Sort by date (newest first)
            certs.sort((a, b) -> b.getEarnedAt().compareTo(a.getEarnedAt()));
            
            for (Certificate cert : certs) {
                VBox card = createCertificateCard(cert);
                certificatesPane.getChildren().add(card);
            }
        }
    }
    
    /**
     * Create a beautiful certificate card
     */
    private VBox createCertificateCard(Certificate cert) {
        VBox card = new VBox();
        card.setSpacing(6);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(12, 15, 12, 15));
        card.setPrefWidth(200);
        card.setMinWidth(200);
        card.setMaxWidth(200);
        
        // Apply style class based on difficulty
        String difficulty = cert.getDifficulty();
        card.getStyleClass().add("certificate-card");
        card.getStyleClass().add("cert-" + difficulty.toLowerCase());
        
        // Medal/Trophy icon based on difficulty
        Label medalIcon = new Label(getMedalEmoji(difficulty));
        medalIcon.getStyleClass().add("cert-medal");
        
        // Puzzle title - use session title for session certificates
        String title = cert.isSessionCertificate() 
            ? difficulty + " Escape" 
            : getPuzzleTitle(cert.getDescription());
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("cert-title");
        titleLabel.setWrapText(true);
        titleLabel.setAlignment(Pos.CENTER);
        
        // Difficulty badge
        Label diffBadge = new Label(difficulty);
        diffBadge.getStyleClass().addAll("cert-badge", "badge-" + difficulty.toLowerCase());
        
        // Score
        HBox scoreBox = new HBox(4);
        scoreBox.setAlignment(Pos.CENTER);
        Label starIcon = new Label("⭐");
        starIcon.setStyle("-fx-font-size: 12px;");
        Label scoreLabel = new Label(cert.getScoreAchieved() + " pts");
        scoreLabel.getStyleClass().add("cert-score");
        scoreBox.getChildren().addAll(starIcon, scoreLabel);
        
        // Completion time (for session certificates)
        HBox timeBox = null;
        if (cert.getCompletionTimeSeconds() > 0) {
            timeBox = new HBox(4);
            timeBox.setAlignment(Pos.CENTER);
            Label clockIcon = new Label("⏱");
            clockIcon.setStyle("-fx-font-size: 12px;");
            Label timeLabel = new Label(cert.getFormattedTime());
            timeLabel.getStyleClass().add("cert-time");
            timeBox.getChildren().addAll(clockIcon, timeLabel);
        }
        
        // Date
        Label dateLabel = new Label(cert.getEarnedAt().format(DATE_FORMAT));
        dateLabel.getStyleClass().add("cert-date");
        
        // Decorative line
        Region line = new Region();
        line.getStyleClass().add("cert-line");
        line.setPrefWidth(150);
        line.setPrefHeight(1);
        
        // Add children
        card.getChildren().addAll(medalIcon, titleLabel, line, diffBadge, scoreBox);
        if (timeBox != null) {
            card.getChildren().add(timeBox);
        }
        card.getChildren().add(dateLabel);
        
        // Add hover effect
        card.setOnMouseEntered(e -> card.getStyleClass().add("cert-hover"));
        card.setOnMouseExited(e -> card.getStyleClass().remove("cert-hover"));
        
        return card;
    }
    
    /**
     * Get medal emoji based on difficulty
     */
    private String getMedalEmoji(String difficulty) {
        switch (difficulty.toUpperCase()) {
            case "HARD": return "🥇";
            case "MEDIUM": return "🥈";
            case "EASY": return "🥉";
            default: return "🎖️";
        }
    }
    
    /**
     * Extract puzzle title from description
     */
    private String getPuzzleTitle(String description) {
        // Description format: "Completed 'Puzzle Name' puzzle"
        if (description != null && description.contains("'")) {
            int start = description.indexOf("'") + 1;
            int end = description.lastIndexOf("'");
            if (end > start) {
                return description.substring(start, end);
            }
        }
        return description != null ? description : "Unknown Puzzle";
    }
    
    private void showEmptyState() {
        if (emptyMessage != null) {
            emptyMessage.setVisible(true);
            emptyMessage.setManaged(true);
        }
        if (certificatesScroll != null) {
            certificatesScroll.setVisible(false);
            certificatesScroll.setManaged(false);
        }
    }
    
    private void hideEmptyState() {
        if (emptyMessage != null) {
            emptyMessage.setVisible(false);
            emptyMessage.setManaged(false);
        }
        if (certificatesScroll != null) {
            certificatesScroll.setVisible(true);
            certificatesScroll.setManaged(true);
        }
    }
}