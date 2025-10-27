package com.model;
import com.speech.Speak;
import java.util.Scanner;

/**
 * Main entry point for Puzzle Game application
 * Initializes MVC components and starts the application
 */
public class GameUI {
    public static void main(String[] args) {
        // Initialize Scanner for console input
        Scanner scanner = new Scanner(System.in);
        
        // Initialize View layer
        GameView view = new ConsoleView(scanner);
        
        // Initialize Services
        AuthenticationService authService = new AuthenticationService();
        
        // Initialize Controllers
        AuthController authController = new AuthController(view, authService);
        GameController gameController = new GameController(view, authService);
        
        // Show welcome banner
        showWelcomeBanner(view);
        
        // Authentication flow
        boolean loggedIn = authController.showAuthMenu();
        
        // If logged in successfully, start game
        if (loggedIn) {
            gameController.start();
            authController.logout();
        }
        
        // Cleanup and exit
        view.showMessage("\n" + "=".repeat(50));
        view.showMessage("   Thanks for playing, goodbye!");
        view.showMessage("=".repeat(50) + "\n");
        
        scanner.close();
    }
    
    /**
     * Display welcome banner
     */
    private static void showWelcomeBanner(GameView view) {
        view.clear();
        view.showMessage("\n" + "=".repeat(75));
        System.out.println();
        System.out.println("                                HAUNTING OF HOLLOW MANOR                                 ");
        System.out.println();
        System.out.println("                                          /\\");
        System.out.println("       !__!          O   _    __         /LL\\         __    _    O");
        System.out.println("   /\\__(''')__/\\     /L\\  \\'._(oo)  _   /LLLL\\     _  (OO)_.'/   /L\\");
        System.out.println("  / _        _ \\   /LLL\\  `.   (_.'/   /LLLLLL\\   \\'._)   .'   /LLL\\");
        System.out.println("  \\/ \\/\\  /\\/ \\/  /LLLLL_.' _.'-..'     |.--.|     '..-'._ `'._LLLLL\\");
        System.out.println("        mm         |.-.'__.'____________||__||____________'. __'.-.|");
        System.out.println("     \\_  '\\/` \\_   ||_||\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\\||_||");
        System.out.println(",__/  /`  ,\\_ /'  [_____]\\_\\_\\/\\_\\_\\_\\_\\_\\/\\_\\_\\_\\_\\_\\/\\_\\_\\_\\[_____]");
        System.out.println("   \\\\/---./  \\\\   /LLLLL\\\\_\\_//\\\\\\_\\_\\_\\_//\\\\\\_\\_\\_\\_//\\\\_\\_\\_/LLLLL\\");
        System.out.println("  '.\\\\, // '. \\\\ /LLLLLLL\\==//__\\\\======//oo\\\\======//__\\\\===/LLLLLLL\\");
        System.out.println(" /   \\\\//    \\ \\/LLLLLLLLL\\__|__|________|__|________|__|__ /LLLLLLLLL\\");
        System.out.println(":     \\#\\   _ :[___________]_\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_[___________]");
        System.out.println("'   _//\\ (_// '\\|    _   |_\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_\\_|   _    |");
        System.out.println(" \\  \\ ( \\ \\/ / )|  .'|'.[__].=============================.[__].'|'.  |");
        System.out.println("  '. \\ \\) ).' / |  |-OO| || |          _________          | || |-+-|  |");
        System.out.println("    `-\\/#(`  / /|  |_|_| || |    _    [_________]    _    | || |_|_|  |");
        System.out.println("     __\\ ,\\ / / |        || |  .'_'.   |__    _|   .'_'.  | ||        |");
        System.out.println("    (OO.-----.% |    _   || |  | | |_  (oo)_.'/|   | | |  | ||   _    |");
        System.out.println("     %%|R.I.P|%%|  .'|'. || |  |-+-|\\'._)   .' |   |-+-|  | || .'|'.  |");
        System.out.println("    %%%|_____|%%|  |-+-| || |  |_|_| '..-'._ `'._  |_|_|  | || |OO-|  |");
        System.out.println("~^\"^~[_________]|  |_|_| || | [_____]  |    '.__.'[_____] | || |_|_|  |");
        System.out.println("    ''\"^\"^\"~~^`\"|        || |          |       |          | ||        |");
        System.out.println("                | /\\     || lc_________|_______|__________| ||        | _");
        System.out.println("                |_) )_   ||/                               \\||      _ | ))");
        System.out.println("  .-~^\"^-__    .' \"\"\" '._||_________________________________||______)\\.'\"\"\"}\"'.__.'");
        System.out.println("              / /\\   /\\ \\__]XXXXXXXXXX[_________]XXXXXXXXXX[__]~\"^.'\"\"}\"'.__.'");
        System.out.println("             |    /_\\    |~\"^~\"^~\"^~[_____________]~^\"~_________  '.__.'~^\"^");
        System.out.println("             |  _______  |                            /Keep Out/   -\"~\"-");
        System.out.println("              \\ \\W W W/ /                  _-        /________/");
        System.out.println("               '.\\M M/.'               __--             / /");
        System.out.println("              '~\"^\"~\"^~'.                              / /");
        System.out.println("   _-\"^~\"^\"-                    __--              _-^~\"^\"~^-_");
        view.showMessage("=".repeat(75) + "\n");
         String message = "Welcome to the haunting of hollow manor escape game!";
         Speak.speak(message);
        view.getUserInput("Press Enter to continue...");
    }
}
