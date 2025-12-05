package com.escapegame.util;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

/**
 * BackgroundManager - Centralized background and frame management
 * 
 * Handles loading background images and calculating frame padding
 * for views that display content inside a decorative frame.
 */
public final class BackgroundManager {
    
    // Frame padding percentages (based on background_frame.png inner rectangle)
    public static final double FRAME_LEFT_PERCENT = 0.15;
    public static final double FRAME_RIGHT_PERCENT = 0.15;
    public static final double FRAME_TOP_PERCENT = 0.175;
    public static final double FRAME_BOTTOM_PERCENT = 0.165;
    
    // Background image paths
    public static final String BACKGROUND_FRAME = "/images/backgrounds/background_frame.png";
    public static final String HOUSE_EXTERIOR = "/images/backgrounds/house_exterior_side.png";
    public static final String HALLWAY_INTERIOR = "/images/backgrounds/hallway_interior.png";
    
    // Private constructor - utility class
    private BackgroundManager() {}
    
    /**
     * Setup background image with responsive binding
     * @param rootPane The root StackPane
     * @param backgroundImage The ImageView to set up
     * @param imagePath Path to the background image
     */
    public static void setupBackground(StackPane rootPane, ImageView backgroundImage, String imagePath) {
        try {
            Image bgImage = loadImage(imagePath);
            if (bgImage != null) {
                backgroundImage.setImage(bgImage);
            }
            
            // Bind image size to root pane
            backgroundImage.fitWidthProperty().bind(rootPane.widthProperty());
            backgroundImage.fitHeightProperty().bind(rootPane.heightProperty());
            backgroundImage.setPreserveRatio(false);
            backgroundImage.setSmooth(true);
            backgroundImage.setCache(true);
            
        } catch (Exception e) {
            System.err.println("Error loading background: " + e.getMessage());
        }
    }
    
    /**
     * Setup frame background with dynamic padding
     * @param rootPane The root StackPane
     * @param backgroundImage The ImageView
     * @param contentPane The content pane to apply padding to
     */
    public static void setupFrameBackground(StackPane rootPane, ImageView backgroundImage, Region contentPane) {
        setupBackground(rootPane, backgroundImage, BACKGROUND_FRAME);
        
        // Setup dynamic padding based on panel size
        rootPane.widthProperty().addListener((obs, old, newVal) -> 
            updateFramePadding(rootPane, contentPane));
        rootPane.heightProperty().addListener((obs, old, newVal) -> 
            updateFramePadding(rootPane, contentPane));
        
        // Initial padding update
        Platform.runLater(() -> updateFramePadding(rootPane, contentPane));
    }
    
    /**
     * Update content padding to keep content inside the frame's inner rectangle
     * @param rootPane The root pane to measure
     * @param contentPane The content pane to apply padding to
     */
    public static void updateFramePadding(Region rootPane, Region contentPane) {
        double width = rootPane.getWidth();
        double height = rootPane.getHeight();
        
        if (width > 0 && height > 0 && contentPane != null) {
            double left = width * FRAME_LEFT_PERCENT;
            double right = width * FRAME_RIGHT_PERCENT;
            double top = height * FRAME_TOP_PERCENT;
            double bottom = height * FRAME_BOTTOM_PERCENT;
            
            contentPane.setPadding(new Insets(top, right, bottom, left));
        }
    }
    
    /**
     * Calculate frame padding values
     * @param width Container width
     * @param height Container height
     * @return Insets with calculated padding
     */
    public static Insets calculateFramePadding(double width, double height) {
        if (width <= 0 || height <= 0) {
            return Insets.EMPTY;
        }
        
        double left = width * FRAME_LEFT_PERCENT;
        double right = width * FRAME_RIGHT_PERCENT;
        double top = height * FRAME_TOP_PERCENT;
        double bottom = height * FRAME_BOTTOM_PERCENT;
        
        return new Insets(top, right, bottom, left);
    }
    
    /**
     * Load an image from resources
     * @param path Resource path
     * @return Image object or null if not found
     */
    public static Image loadImage(String path) {
        try {
            var stream = BackgroundManager.class.getResourceAsStream(path);
            if (stream != null) {
                Image img = new Image(stream);
                stream.close();
                if (!img.isError()) {
                    return img;
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading image " + path + ": " + e.getMessage());
        }
        return null;
    }
}
