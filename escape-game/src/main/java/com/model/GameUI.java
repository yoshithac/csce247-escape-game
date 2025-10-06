package com.model;


import java.util.List;


public class GameUI {

  
  public static void main(String[] args) throws Exception {
    GameDataFacade gameDataFacade = GameDataFacade.getInstance();    
    List<User> users = gameDataFacade.getUsers();
    for (User user : users) {
      System.out.println("User ID: " + user.getUserId() + ", Name: " + user.getFirstName() + " " + user.getLastName());
    }
  }

}
