package com.escapegame;

import java.io.IOException;
import java.io.InputStream;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * JavaFX App - Main Entry Point
 * Launches the Whispers of Hollow Manor Escape Game
 * 
 * Uses GameContainerView as the main container
 * CSS Files:
 *   - style.css - Global styles for puzzle views
 *   - GameContainerStyles.css - Container/menu specific styles
 *   - GameSessionStyles.css - Hallway game session styles
 * Fonts:
 *   - Jersey10-Regular.ttf - Custom game font
 */
public class App extends Application {

    private static Scene scene;
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        
        // Load custom fonts first (before any UI)
        loadFonts();
        
        // Load GameContainerView as the starting view
        scene = new Scene(loadFXML("GameContainerView"), 1000, 700);
        
        // Load all CSS files
        loadStylesheets();
        
        primaryStage.setScene(scene);
        primaryStage.setTitle("Whispers of Hollow Manor");
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(700);
        
        primaryStage.show();
        
        System.out.println("Application started with GameContainerView");
    }
    
    // Store loaded font for use by controllers
    private static Font jerseyFont = null;
    
    /**
     * Get the Jersey font with specified size
     * @param size Font size
     * @return Font with specified size, or system font if not loaded
     */
    public static Font getJerseyFont(double size) {
        if (jerseyFont != null) {
            return Font.font(jerseyFont.getFamily(), size);
        }
        return Font.font("System", size);
    }
    
    /**
     * Check if custom font is loaded
     */
    public static boolean isCustomFontLoaded() {
        return jerseyFont != null;
    }
    
    /**
     * Load custom fonts
     */
    private void loadFonts() {
        String[] fontFiles = {
            "Jersey10-Regular.ttf"
        };
        
        for (String fontFile : fontFiles) {
            loadFont(fontFile);
        }
        
        // Debug: Print all available font families
        System.out.println("Available font families containing 'Jersey': ");
        Font.getFamilies().stream()
            .filter(f -> f.toLowerCase().contains("jersey"))
            .forEach(f -> System.out.println("  - " + f));
    }
    
    /**
     * Load a font file from multiple possible locations
     */
    private void loadFont(String filename) {
        String[] possiblePaths = {
            "/fonts/" + filename,
            "fonts/" + filename,
            "/" + filename,
            filename
        };
        
        for (String path : possiblePaths) {
            try {
                InputStream fontStream = getClass().getResourceAsStream(path);
                if (fontStream != null) {
                    Font font = Font.loadFont(fontStream, 12);
                    if (font != null) {
                        // Store the font for later use
                        jerseyFont = font;
                        System.out.println("Loaded Font: " + font.getName() + " (family: " + font.getFamily() + ") from " + path);
                        fontStream.close();
                        return;
                    }
                    fontStream.close();
                }
            } catch (Exception e) {
                // Continue to next path
            }
        }
        
        System.err.println("Warning: Could not load font file: " + filename);
    }
    
    /**
     * Load all stylesheets
     */
    private void loadStylesheets() {
        // CSS files to load (in order of priority - later ones override earlier)
        String[] cssFiles = {
            "style.css",                    // Global styles (puzzles, tables, etc.)
            "GameContainerStyles.css",      // Container view styles
            "GameSessionStyles.css",        // Session view styles
            "CertificatesStyles.css"        // Certificates view styles
        };
        
        for (String cssFile : cssFiles) {
            loadCSS(cssFile);
        }
    }
    
    /**
     * Attempt to load a CSS file from multiple possible locations
     */
    private void loadCSS(String filename) {
        String[] possiblePaths = {
            filename,                       // Root of resources
            "/" + filename,                 // Root with leading slash
            "css/" + filename,              // css subfolder
            "/css/" + filename              // css subfolder with leading slash
        };
        
        for (String path : possiblePaths) {
            try {
                var resource = getClass().getResource(path);
                if (resource != null) {
                    scene.getStylesheets().add(resource.toExternalForm());
                    System.out.println("✓ Loaded CSS: " + path);
                    return;
                }
            } catch (Exception e) {
                // Continue to next path
            }
        }
        
        System.err.println("Warning: Could not load CSS file: " + filename);
    }

    /**
     * Switch to a different view
     * @param fxml FXML file name (without .fxml extension)
     * @throws IOException if FXML cannot be loaded
     */
    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    /**
     * Load FXML file
     * @param fxml FXML file name (without .fxml extension)
     * @return Parent node
     * @throws IOException if FXML cannot be loaded
     */
    private static Parent loadFXML(String fxml) throws IOException {
        // Try multiple possible locations for FXML files
        String[] possiblePaths = {
            fxml + ".fxml",                  // Root of resources (most common)
            "/" + fxml + ".fxml",            // Root with leading slash
            "fxml/" + fxml + ".fxml",        // fxml subfolder
            "/fxml/" + fxml + ".fxml",       // fxml subfolder with leading slash
            "views/" + fxml + ".fxml",       // views subfolder
            "/views/" + fxml + ".fxml"       // views subfolder with leading slash
        };
        
        Exception lastException = null;
        
        for (String path : possiblePaths) {
            try {
                var resource = App.class.getResource(path);
                if (resource != null) {
                    FXMLLoader fxmlLoader = new FXMLLoader(resource);
                    Parent parent = fxmlLoader.load();
                    System.out.println("✓ Loaded FXML: " + path);
                    return parent;
                }
            } catch (Exception e) {
                lastException = e;
                // Log the actual error - this is usually the real problem
                System.err.println("  Error loading " + path + ": " + e.getMessage());
                if (e.getCause() != null) {
                    System.err.println("    Caused by: " + e.getCause().getMessage());
                }
            }
        }
        
        // If all paths fail, throw error with helpful message and root cause
        String errorMsg = "Could not find/load FXML file: " + fxml + ".fxml\n" +
            "Tried paths:\n" +
            "  - " + fxml + ".fxml\n" +
            "  - /" + fxml + ".fxml\n" +
            "  - fxml/" + fxml + ".fxml\n" +
            "  - /fxml/" + fxml + ".fxml\n\n" +
            "Make sure FXML files are in: src/main/resources/ or src/main/resources/fxml/";
        
        if (lastException != null) {
            errorMsg += "\n\nActual error: " + lastException.getMessage();
            if (lastException.getCause() != null) {
                errorMsg += "\nCaused by: " + lastException.getCause().getMessage();
            }
            // Print full stack trace for debugging
            lastException.printStackTrace();
        }
        
        throw new IOException(errorMsg);
    }

    /**
     * Get the primary stage
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Main method
     */
    public static void main(String[] args) {
        launch(args);
    }
}