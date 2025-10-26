package com.model;


import java.util.List;
import java.util.Scanner;
import com.speech.Speak;

public class GameUI {

private String[] mainMenuOptions = {"Register Account", "Login", "Play Game","Continue Game","Show Leader Board","Show all users","Logout"};
private Scanner scanner;
private GameManager gameManager;
private GameProgress progress;

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
		System.out.println("\nWelcome to Escape Games! Please choose a room to play:\n1: Shipwreck\n2: Prison\n3: Forest");
		String roomChoice = scanner.nextLine();
		if (roomChoice.equals("1")) {
			System.out.println("You have chosen the Shipwreck room.\n");

		} else if (roomChoice.equals("2")) {
			System.out.println("You have chosen the Prison room.\n");
		} else if (roomChoice.equals("3")) {
			System.out.println("You have chosen the Forest room.\n");
		} else {
			System.out.println("Please select 1, 2, or 3.");
			return;
		}
        //GameData gameData = gameDataFacade.getGameData();
			GameDataLoader loader = new GameDataLoader();
			GameData gameData = loader.readGameData();
			List<Room> rooms = gameData.getRooms();
			Room chosenRoom = rooms.get(Integer.parseInt(roomChoice)-1);
    		System.out.println("Room: " + chosenRoom.getName());
    		System.out.println(chosenRoom.getDescription());
			Speak.speak(chosenRoom.getDescription());
    		System.out.println("----------------------");
		
   	 	List<Puzzle> puzzles = gameData.getPuzzles();
    	/*for (Puzzle puzzle : puzzles) {
      		System.out.println("Puzzle ID: " + puzzle.getPuzzleId() + ", prompt: " + puzzle.getType() + "\nanswer:" + puzzle.getAnswer());
   		}*/
		progress = new GameProgress(puzzles);
		gameManager.startTimer();
		System.out.println("You quickly take a look around. The room is fairly bare.");
		boolean firstChoiceB = true;
		boolean keyFound = false;
		while (firstChoiceB) {
			if (progress.getToDoPuzzles().isEmpty()) {
				System.out.println("Congratulations! You have completed all puzzles in this room. Go investigate the end of the maze to escape!");
				break;
			}
			System.out.println("There is a desk to investigate and a door to another room.\n" +
			"Which do you want to investigate? (Type 'desk' or 'door')");
			String firstChoice = scanner.nextLine().trim().toLowerCase();
			if (firstChoice.equals("desk")) {
				System.out.println("You walk over to the desk and find a locked drawer. Inside is a notebook. It may have some useful information.\n"
				+ "Do you want to read the notebook, or further explore the desk? (Type 'read' or 'explore')");
				String deskChoice = scanner.nextLine().trim().toLowerCase();
				if (deskChoice.equals("read")) {
					System.out.println("You open the notebook and find what appears to be a diary. There are some strange doodles and writings in the margins.\n" +
					"As you read further, you notice a series of numbers that seem to be a code: 4-6-3-1. This might be useful later.\n" +
					"You close the notebook and ponder its significance. Do you want to investigate further? (Type 'yes' or 'no')");
					String investigateChoice = scanner.nextLine().trim().toLowerCase();
					if (investigateChoice.equals("yes")) {
						System.out.println("You investigate further and notice that the notes and doodles seem to have patterns. \nDo you want to try to decode the pictures or the words now? (Type 'pictures' or 'words')");
						String decodeChoice = scanner.nextLine().trim().toLowerCase();
						if (decodeChoice.equals("pictures")) {
							for (Puzzle puzzle : puzzles) {	
								if (puzzle.getType() == "MemoryPuzzle" && !puzzle.isCompleted()) {
									System.out.println("You focus on the memory puzzle.");
									puzzle.playPuzzle();
									if (puzzle.checkComplete()) {
										System.out.println("You have successfully decoded the memory puzzle!");
										puzzle.setCompleted(true);
									}
								else {
									System.out.println("No more picture puzzles available to decode.");
								}
								if (progress.getToDoPuzzles().isEmpty()) {
									System.out.println("Congratulations! You have completed all puzzles in this room.");
									break;
								}
								}
							}
						} else if (decodeChoice.equals("words")) {
							for (Puzzle puzzle : puzzles) {	
								if (puzzle.getType() == "WordPuzzle" && !puzzle.isCompleted()) {
									System.out.println("You focus on the word puzzle.");
									puzzle.playPuzzle();
									if (puzzle.checkComplete()) {
										System.out.println("You have successfully decoded the words!");
										puzzle.setCompleted(true);
									}
									else {
										System.out.println("No more word puzzles available to decode.");
										}
									}
								}
						}
						else if (decodeChoice.equals("M")) {
							displayGameMenu();
						}
						else {
							System.out.println("You decide to hold off on decoding for now.");
							continue;
						}
					}
					else if (investigateChoice.equals("M")) {
						displayGameMenu();
					}
					else {
						System.out.println("You decide to hold off on decoding for now.");
						continue;
					}
				} else if (deskChoice.equals("explore")) {
					System.out.println("You explore the desk further and find a hidden compartment containing a small key. This could be useful later.");
					keyFound = true;
					continue;
				}	
				else if (deskChoice.equals("M")) {
					displayGameMenu();
				}
				gameManager.startNextPuzzle();
			} else if (firstChoice.equals("door")) {
				if (keyFound) {
					System.out.println("You use the key to unlock the door and proceed into the room. It appears to be a maze. Good luck!");
					for (Puzzle puzzle : puzzles) {	
								if (puzzle.getType() == "MazePuzzle" && !puzzle.isCompleted()) {
									puzzle.playPuzzle();
									if (puzzle.checkComplete()) {
										System.out.println("You have successfully completed the maze puzzle!");
										puzzle.setCompleted(true);
									}
								else {
									System.out.println("No more maze puzzles available to complete.");
								}
							}
						}
					System.out.println("Do you want to continue exploring the room or exit? ('continue' or 'exit'?)");
					String continueChoice = scanner.nextLine().trim().toLowerCase();
					if (continueChoice.equals("continue")) {
						for (Puzzle puzzle : puzzles) {	
								if (puzzle.getType().equals("MazePuzzle") && !puzzle.isCompleted()) {
									puzzle.playPuzzle();
									System.out.println(puzzle.getType());
									if (puzzle.checkComplete()) {
										System.out.println("You have successfully completed the maze puzzle!");
										puzzle.setCompleted(true);
									}
								else {
									System.out.println("No more maze puzzles available to complete.");
								}
							}
						}
					}
					else if (continueChoice.equals("exit")) {
						continue;
					}
					else if (continueChoice.equals("M")) {
						displayGameMenu();
					}
				} else {
					System.out.println("The door is locked. You need a key to open it.");
				}
			}
			else if (!firstChoice.equals("M")) {
				displayGameMenu();
			}
			else {
				System.out.println("Invalid choice. Please type 'desk' or 'door'.");
			}
		}
		System.out.println("You are almost out! You find the end of the maze and see an exit door. There is a keypad next to it.\n" +
		"What do you enter on the keypad to unlock the door?");
		String keypadEntry = scanner.nextLine().trim();
		if (keypadEntry.equals("4631")) {
			System.out.println("The door unlocks and you step outside, escaping the room! Congratulations!");
			gameManager.endGame();
			progress.updateProgress();
			int finalScore = progress.getTotalScore();
		}
		else if (keypadEntry.equals("M")) {
				displayGameMenu();
		}
		else {
			System.out.println("Incorrect code. You cannot exit yet. Keep trying!");
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

	private void displayGameMenu() {
		System.out.println("Main Menu:");
		System.out.println("1. Continue Game");
		System.out.println("2. Get hint");
		System.out.println("3. Show progress");
		System.out.println("4. Pause game");
		System.out.println("5. End game");
		System.out.print("Enter your choice: ");
		String ans = scanner.nextLine(); 
		if (ans.equals("2")) {
			//return progress.useHint();
		}
		else if (ans.equals("3")) {
			progress.updateProgress();
			System.out.println("Percent complete: " + progress.getCompletionPercentage() + "%, Hints left: " + 
			gameManager.getNumHints() + "Time elapsed: " + gameManager.getElapsedTime() + " seconds.");
		}
		else if (ans.equals("4")) {
			gameManager.pauseGame();
		}
		else if (ans.equals("5")) {
			gameManager.endGame();
		}
		else {
			System.out.println("Invalid choice. Please try again.");
			displayGameMenu();
		}
	}

}
