package com.model;


import java.util.List;
import java.util.Scanner;


public class GameUI {

private String[] mainMenuOptions = {"Register Account", "Login", "Play Game","Continue Game","Show Leader Board","Show all users","Logout"};
private Scanner scanner;
private GameManager gameManager;

public GameUI(){
		scanner = new Scanner(System.in);
    gameManager = new GameManager();
	}

  public void run() {
		System.out.println("*******************************************");
		System.out.println("*****     Welcome to Escape Games     *****");
		System.out.println("*******************************************");
		
		//Loop as long as we want to keep interacting with the library
    boolean showMainMenu  = true;
		while(showMainMenu) {
			displayMainMenu();
			
			int userCommand = getUserCommand(mainMenuOptions.length);
			
			if(userCommand == -1) {
				System.out.println("Not a valid command");
				continue;
			}			
			
			switch(userCommand) {
				case(0):
					registerAccount();
					break;
				case(1):
					login();
					break;
				case(2):
					playGame();
					break;
				case(3):
					continueGame();
					break;
				case(4):
					showLeaderBoard();
					break;
				case(5):
					showAllUsers();
					break;
        case(6):
					logout();
          showMainMenu = false;
					break;
			}
		}
		
		System.out.println("Good bye, and have a nice day");
		
	}

  // Display MainMenu of the Escape Games
  private void displayMainMenu() {
		System.out.println("\n************ Main Menu *************");
		for(int i=0; i< mainMenuOptions.length; i++) {
			System.out.println((i+1) + ". " + mainMenuOptions[i]);
		}
		System.out.println("\n");
	}

  //get the users command number, if it's not valid, return -1
	private int getUserCommand(int numCommands) {
		System.out.print("What would you like to do?: ");
		
		String input = scanner.nextLine();
		int command = Integer.parseInt(input) - 1;
		
		if(command >= 0 && command <= numCommands -1) return command;
		
		return -1;
	}
  
  public static void main(String[] args) throws Exception {

    GameUI gameUI = new GameUI();
    gameUI.run();

    System.out.println();
    
  }

    private void registerAccount() {
  
    // User Registration process
				
		String firstName = getField("First Name");
		String lastName = getField("Last Name");
		String email = getField("email");
		String userId = getField("userId");
		String password = getField("password");
		
		String status = gameManager.registerUser(userId, password, firstName, lastName, email);
		
		
			System.out.println(status);

    }
    
    private void login() {

			String userId = getField("userId");
		  String password = getField("password");
			if (gameManager.loginUser(userId, password)){
				String player = gameManager.getCurrentPlayer().getFirstName();
				System.out.println("Login successful. Welecome to Escape Games, " + player);
			}else{
				System.out.println("user not found. Please register first");
			}
		}    

    private void playGame() {
        //GameData gameData = gameDataFacade.getGameData();
				GameData gameData = gameManager.loadGameData();
    List<Puzzle> puzzles = gameData.getPuzzles();
    for (Puzzle puzzle : puzzles) {
      System.out.println("Puzzle ID: " + puzzle.getPuzzleId() + ", prompt: " + puzzle.getType() + "\nanswer:" + puzzle.getAnswer());
    }
    }

    private void continueGame() {
        System.out.println("Functionality will be available soon");
    }

    private void showLeaderBoard() {
			System.out.println("|-----------------------------------------------|");
			System.out.println("|-------------Leader board----------------------|");
			System.out.println("|-----------------------------------------------|");
        for (LeaderboardEntry entry : gameManager.getLeaderboard().sortByScore()){
					System.out.println(entry);
					System.out.println("|-----------------------------------------------|");
				}
    }

    private void showAllUsers() {
    List<User> users = gameManager.getUsers();
    for (User user : users) {
      System.out.println(user);
    }
    }

    private void logout() {
    
    //Save User data
    System.out.println(gameManager.saveUsers() ? "saved user successfully":"user failed to save");
    //save all other Game Data
    System.out.println(gameManager.saveGameData() ? "saved game data successfully":"game data failed to save");
    
    }

		private String getField(String prompt) {
		System.out.print(prompt + ": ");
		return scanner.nextLine();
	}

}
