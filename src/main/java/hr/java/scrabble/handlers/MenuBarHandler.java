package hr.java.scrabble.handlers;

import hr.java.scrabble.utilities.MenuBarUtility;
import javafx.scene.control.MenuBar;
import lombok.Getter;

public class MenuBarHandler {

    @Getter
    private MenuBar menuBar;
    private final GameHandler gameHandler;

    public MenuBarHandler(MenuBar menuBar, GameHandler gameHandler) {
        this.gameHandler = gameHandler;
        this.menuBar = menuBar;
    }

    public void setMenuBarDefinition(){
        MenuBarUtility.setInitialMenuBarDefinition(menuBar, gameHandler);
    }

}
