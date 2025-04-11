package hr.java.scrabble.controllers;

import hr.java.scrabble.handlers.GameHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

public class ScrabbleController implements Initializable {

    @FXML
    private GridPane screenGridPane;
    @FXML
    private MenuBar menuBar;

    private final GameHandler gameHandler = new GameHandler();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gameHandler.setMenuBarHandler(menuBar);

        screenGridPane.add(gameHandler.getOffsetGrid(), 0, 0);
        screenGridPane.add(gameHandler.getLastWordText(), 1, 0);
        screenGridPane.add(gameHandler.getCenterBoardGrid(), 1, 1);
        screenGridPane.add(gameHandler.getScoreText(), 2, 1);
        screenGridPane.add(gameHandler.getPlayerActionsHandler(), 1, 2);
        screenGridPane.add(gameHandler.getChatHandler().getChatComponent(), 1, 3);

    }

}