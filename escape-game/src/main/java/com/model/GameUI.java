package com.model;


import java.util.List;


public class GameUI {

  
  public static void main(String[] args) throws Exception {
    GameDataFacade gameDataFacade = GameDataFacade.getInstance();    
    List<User> users = gameDataFacade.getUsers();
    for (User user : users) {
      System.out.println("User ID: " + user.getUserId() + ", Name: " + user.getFirstName() + " " + user.getLastName());
    }

    GameData gameData = gameDataFacade.loadGameData();
    List<Puzzle> puzzles = gameData.getPuzzles();
    for (Puzzle puzzle : puzzles) {
      System.out.println("Puzzle ID: " + puzzle.getPuzzleId() + ", prompt: " + puzzle.getType() + "\nanswer:" + puzzle.getAnswer());
    }
  }

}
