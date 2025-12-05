package com.escapegame.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.util.Duration;

/**
 * SessionTimer - Centralized JavaFX Timer Utility
 * 
 * Manages the session countdown timer with:
 * - Start/stop/pause/resume functionality
 * - Listener pattern for UI updates
 * - Time-up callback
 * 
 * This is a singleton utility used by Controllers to manage timing.
 * Controllers bridge timer state to/from GameServiceManager (Model).
 * 
 * Usage pattern (in Controller):
 *   1. Get elapsed/limit from GameServiceManager
 *   2. Start SessionTimer with those values
 *   3. On pause/save, get elapsed from SessionTimer and save to GSM
 * 
 * @author Escape Game Team
 * @version 1.0
 */
public class SessionTimer {
    
    private static SessionTimer instance = null;
    
    private Timeline timer;
    private int elapsedSeconds;
    private int timeLimit;
    private List<Consumer<Integer>> listeners;  // Receive remaining seconds on each tick
    private Runnable timeUpCallback;            // Called when time runs out
    
    /**
     * Private constructor - use getInstance()
     */
    private SessionTimer() {
        this.listeners = new ArrayList<>();
        this.elapsedSeconds = 0;
        this.timeLimit = 0;
    }
    
    /**
     * Get singleton instance
     * @return SessionTimer instance
     */
    public static synchronized SessionTimer getInstance() {
        if (instance == null) {
            instance = new SessionTimer();
        }
        return instance;
    }
    
    /**
     * Reset singleton instance (for testing)
     */
    public static synchronized void resetInstance() {
        if (instance != null) {
            instance.stop();
        }
        instance = null;
    }
    
    // ================================================================
    // TIMER CONTROL METHODS
    // ================================================================
    
    /**
     * Start the timer
     * @param timeLimit Total time limit in seconds
     * @param startElapsed Starting elapsed seconds (for restored sessions)
     * @param onTimeUp Callback when time runs out
     */
    public void start(int timeLimit, int startElapsed, Runnable onTimeUp) {
        // Always update the callback (different views need different callbacks)
        this.timeUpCallback = onTimeUp;
        
        System.out.println("=== SessionTimer.start() ===");
        System.out.println("Requested - Time limit: " + timeLimit + "s, startElapsed: " + startElapsed + "s");
        System.out.println("Current timer state - elapsed: " + this.elapsedSeconds + "s, limit: " + this.timeLimit + "s");
        
        // If timer is already running, just update callback - DO NOT reset elapsed time
        if (timer != null && timer.getStatus() == Timeline.Status.RUNNING) {
            System.out.println("Timer already running at " + this.elapsedSeconds + "s - keeping current time, updated callback only");
            return;
        }
        
        // If timer exists and is paused, resume it - DO NOT reset elapsed time
        if (timer != null && timer.getStatus() == Timeline.Status.PAUSED) {
            System.out.println("Resuming paused timer at " + this.elapsedSeconds + "s");
            timer.play();
            return;
        }
        
        // Only set time values when starting a NEW timer (timer is null or stopped)
        this.timeLimit = timeLimit;
        this.elapsedSeconds = startElapsed;
        
        System.out.println("Creating new timer - elapsed: " + elapsedSeconds + "s, remaining: " + getRemainingSeconds() + "s");
        
        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            elapsedSeconds++;
            int remaining = getRemainingSeconds();
            
            // Notify all listeners
            notifyListeners(remaining);
            
            // Check time limit
            if (remaining <= 0) {
                stop();
                if (timeUpCallback != null) {
                    Platform.runLater(timeUpCallback);
                }
            }
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
        
        System.out.println("Timer started - elapsed: " + elapsedSeconds + "s, remaining: " + getRemainingSeconds() + "s");
    }
    
    /**
     * Stop the timer completely
     */
    public void stop() {
        if (timer != null) {
            timer.stop();
            timer = null;
            System.out.println("SessionTimer stopped");
        }
        clearListeners();
    }
    
    /**
     * Pause the timer (can be resumed)
     */
    public void pause() {
        if (timer != null) {
            timer.pause();
            System.out.println("SessionTimer paused at " + elapsedSeconds + "s");
        }
    }
    
    /**
     * Resume a paused timer
     */
    public void resume() {
        if (timer != null && timer.getStatus() == Timeline.Status.PAUSED) {
            timer.play();
            System.out.println("SessionTimer resumed at " + elapsedSeconds + "s");
        }
    }
    
    /**
     * Check if timer is currently running
     * @return true if running
     */
    public boolean isRunning() {
        return timer != null && timer.getStatus() == Timeline.Status.RUNNING;
    }
    
    /**
     * Check if timer is paused
     * @return true if paused
     */
    public boolean isPaused() {
        return timer != null && timer.getStatus() == Timeline.Status.PAUSED;
    }
    
    // ================================================================
    // TIME STATE METHODS
    // ================================================================
    
    /**
     * Get elapsed seconds
     * @return Elapsed seconds since timer started
     */
    public int getElapsedSeconds() {
        return elapsedSeconds;
    }
    
    /**
     * Set elapsed seconds (for restoring state)
     * @param seconds Elapsed seconds
     */
    public void setElapsedSeconds(int seconds) {
        this.elapsedSeconds = seconds;
    }
    
    /**
     * Get time limit
     * @return Time limit in seconds
     */
    public int getTimeLimit() {
        return timeLimit;
    }
    
    /**
     * Set time limit
     * @param seconds Time limit in seconds
     */
    public void setTimeLimit(int seconds) {
        this.timeLimit = seconds;
    }
    
    /**
     * Get remaining seconds
     * @return Remaining seconds (timeLimit - elapsed)
     */
    public int getRemainingSeconds() {
        return Math.max(0, timeLimit - elapsedSeconds);
    }
    
    /**
     * Check if time is up
     * @return true if elapsed >= timeLimit
     */
    public boolean isTimeUp() {
        return elapsedSeconds >= timeLimit;
    }
    
    /**
     * Get formatted time string (MM:SS)
     * @return Formatted remaining time
     */
    public String getFormattedRemainingTime() {
        int remaining = getRemainingSeconds();
        int minutes = remaining / 60;
        int seconds = remaining % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
    
    // ================================================================
    // LISTENER MANAGEMENT
    // ================================================================
    
    /**
     * Add a listener that receives remaining seconds on each tick
     * @param listener Consumer that receives remaining seconds
     */
    public void addListener(Consumer<Integer> listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    /**
     * Remove a listener
     * @param listener Listener to remove
     */
    public void removeListener(Consumer<Integer> listener) {
        listeners.remove(listener);
    }
    
    /**
     * Clear all listeners
     */
    public void clearListeners() {
        listeners.clear();
    }
    
    /**
     * Notify all listeners with remaining seconds
     * @param remainingSeconds Remaining seconds
     */
    private void notifyListeners(int remainingSeconds) {
        for (Consumer<Integer> listener : listeners) {
            try {
                Platform.runLater(() -> listener.accept(remainingSeconds));
            } catch (Exception e) {
                System.err.println("Error notifying timer listener: " + e.getMessage());
            }
        }
    }
    
    /**
     * Update the time-up callback
     * @param callback New callback
     */
    public void setTimeUpCallback(Runnable callback) {
        this.timeUpCallback = callback;
    }
}