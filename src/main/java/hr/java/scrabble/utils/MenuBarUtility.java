package hr.java.scrabble.utils;

import hr.java.scrabble.game.GameModeContext;
import hr.java.scrabble.handlers.GameHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

public class MenuBarUtility {

    private MenuBarUtility(){}

    public static void setInitialMenuBarDefinition(MenuBar menuBar, GameHandler gameHandler){
        menuBar.getMenus().clear();
        menuBar.getMenus().add(getPlayMenu(menuBar, gameHandler));
        menuBar.getMenus().add(getDocumentationMenu());
        menuBar.getMenus().add(getWatchMenu(menuBar, gameHandler));
    }

    private static void setMenuBarDefinitionToLeaveSingleplayerGame(MenuBar menuBar, GameHandler gameHandler){
        menuBar.getMenus().clear();
        menuBar.getMenus().add(getLeaveMenu(menuBar, gameHandler));
        menuBar.getMenus().add(getFileMenu(gameHandler));
        menuBar.getMenus().add(getDocumentationMenu());
    }

    private static void setMenuBarDefinitionToLeaveMultiplayerGame(MenuBar menuBar, GameHandler gameHandler){
        menuBar.getMenus().clear();
        menuBar.getMenus().add(getLeaveMenu(menuBar, gameHandler));
        menuBar.getMenus().add(getDocumentationMenu());
    }

    private static Menu getLeaveMenu(MenuBar menuBar, GameHandler gameHandler) {
        Menu menu = new Menu("Play");
        MenuItem menuItem1 = new MenuItem("Stop playing");
        menu.getItems().add(menuItem1);

        menuItem1.setOnAction(e -> {
            if (gameHandler.getClient() != null && gameHandler.getClient().isConnected()) {
                gameHandler.getClient().sendLeavingTheGame();
            }
            else{
                gameHandler.getPlayerActionsHandler().removeAllActions();
                setInitialMenuBarDefinition(menuBar, gameHandler);
            }
            gameHandler.setGameContext(null);
        });

        return menu;
    }

    private static Menu getPlayMenu(MenuBar menuBar, GameHandler gameHandler){
        Menu menu = new Menu("Play");

        MenuItem menuItem1 = new MenuItem("Singleplayer");
        menuItem1.setOnAction(e -> {
            gameHandler.setGameContext(GameModeContext.SINGLEPLAYER);
            setMenuBarDefinitionToLeaveSingleplayerGame(menuBar, gameHandler);
        });
        menu.getItems().add(menuItem1);

        MenuItem menuItemGA = new MenuItem("Singleplayer GA");
        menuItemGA.setOnAction(e -> {
            gameHandler.setGameContext(GameModeContext.SINGLEPLAYER_GA);
            setMenuBarDefinitionToLeaveSingleplayerGame(menuBar, gameHandler);
        });
        menu.getItems().add(menuItemGA);

        menu.getItems().add(getMultiplayerMenu(menuBar, gameHandler));

        return menu;
    }

    private static Menu getMultiplayerMenu(MenuBar menuBar, GameHandler gameHandler){
        Menu menu2 = new Menu("Multiplayer");
        MenuItem menuItem21 = new MenuItem("Host the game");
        MenuItem menuItem22 = new MenuItem("Connect to game");
        menuItem21.setOnAction(e -> {
            gameHandler.setGameContext(GameModeContext.MULTIPLAYER_HOST_AND_CLIENT);//pokrenuti hostanje servera
            setMenuBarDefinitionToLeaveMultiplayerGame(menuBar, gameHandler);
        });
        menuItem22.setOnAction(e -> {
            gameHandler.setGameContext(GameModeContext.MULTIPLAYER_CLIENT);
            setMenuBarDefinitionToLeaveMultiplayerGame(menuBar, gameHandler);
        });

        menu2.getItems().add(menuItem21);
        menu2.getItems().add(menuItem22);

        return menu2;
    }

    private static Menu getFileMenu(GameHandler gameHandler){
        Menu menu = new Menu("File");
        MenuItem menuItem1 = new MenuItem("Save game");
        MenuItem menuItem2 = new MenuItem("Load game");

        menuItem1.setOnAction(actionEvent -> gameHandler.saveGame());
        menuItem2.setOnAction(actionEvent -> gameHandler.loadGame());

        menu.getItems().add(menuItem1);
        menu.getItems().add(menuItem2);
        return menu;
    }

    private static Menu getDocumentationMenu(){
        Menu menu = new Menu("Documentation");
        MenuItem menuItem1 = new MenuItem("Generate");
        menuItem1.setOnAction(actionEvent -> DocumentationUtility.generateDocumentation());
        menu.getItems().add(menuItem1);
        return menu;
    }

    private static Menu getWatchMenu(MenuBar menuBar, GameHandler gameHandler){
        Menu menu = new Menu("Watch");
        MenuItem menuItem = new MenuItem("Replay last game");
        menu.getItems().add(menuItem);

        menuItem.setOnAction(actionEvent -> GameplayHistoryUtility.replayGameplayHistory(gameHandler));

        return menu;
    }

}
