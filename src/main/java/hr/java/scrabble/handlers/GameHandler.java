package hr.java.scrabble.handlers;

import hr.java.scrabble.components.TileComponent;
import hr.java.scrabble.game.GameConstants;
import hr.java.scrabble.game.GameModeContext;
import hr.java.scrabble.game.WordAndScore;
import hr.java.scrabble.networking.client.Client;
import hr.java.scrabble.networking.server.Server;
import hr.java.scrabble.states.*;
import hr.java.scrabble.utils.*;
import hr.java.scrabble.validations.DictionaryWordValidation;
import hr.java.scrabble.validations.MoveValidation;
import hr.java.scrabble.word.WordSaver;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static hr.java.scrabble.game.GameConstants.*;

@Getter
@Setter
public class GameHandler {

    private final GridPane offsetGrid;//koristi se za racunanje tocne pozicije kod drag and dropa
    private final GridPane centerBoardGrid;//glavna ploca
    private final Text scoreText;//text za score
    private final Text lastWordText;//text za zadni potez

    private final CenterBoardState centerBoardState;
    private final PlayerState playerState;
    private final PlayerState gaPlayerState;
    private final TileBagState tileBagState;

    private MenuBarHandler menuBarHandler;
    private final PlayerActionsHandler playerActionsHandler;
    private final SideBarHandler sideBarHandler;
    private final GAHandler gaHandler;

    private GameplayHistory gameplayHistory;
    private GameModeContext gameModeContext;

    private Client client;
    private Server server;

    private final MoveValidation moveValidation;

    public GameHandler() {
        offsetGrid = new GridPane();
        offsetGrid.toBack();

        //centralni grid
        centerBoardGrid = new GridPane();
        GridPaneUtility.setGridConstrainst(this.centerBoardGrid, WHOLE_GRID_NUM_OF_ROWS, WHOLE_GRID_NUM_OF_COLS);
        GameHandlerUtility.fillEmptyGameBoard(this.centerBoardGrid);
        GameHandlerUtility.fillEmptyPlayerBoard(this.centerBoardGrid);

        centerBoardState = new CenterBoardState();
        tileBagState = new TileBagState();
        playerState = new PlayerState();
        gaPlayerState = new PlayerState();
        gameplayHistory = new GameplayHistory();

        scoreText = new Text(GameConstants.SCORE + playerState.getPlayerScore().toString());
        scoreText.setFont(new Font(18));
        playerActionsHandler = new PlayerActionsHandler(this);
        gaHandler = new GAHandler(this);

        sideBarHandler = new SideBarHandler(this);
        lastWordText = new Text();
        moveValidation = new MoveValidation(new DictionaryWordValidation());
    }

    public void putTilesFromPlayerStateToGrid() {
        GameHandlerUtility.fillEmptyPlayerBoard(this.centerBoardGrid);
        GameHandlerUtility.addTilesToGrid(playerState.getPlayerBoardTiles(), this);
    }

    public void putTilesFromCenterGameStateToGrid() {
        GameHandlerUtility.fillEmptyGameBoard(this.centerBoardGrid);
        GameHandlerUtility.addTilesToGrid(centerBoardState.getCenterBoardTiles(), this);
    }

    public void putTilesFromGridToPlayerState() {
        GameHandlerUtility.putTilesFromGridToPlayerState(playerState, centerBoardGrid);
    }

    public void putTilesFromGridToCenterGameState() {
        WordAndScore wordAndScore = new WordAndScore();
        List<TileState> tilesOnBoard = centerBoardGrid.getChildren().stream()
                .filter(TileComponent.class::isInstance)
                .filter(node -> GridPane.getRowIndex(node) < NUM_OF_GRIDS
                        && GridPane.getColumnIndex(node) < NUM_OF_GRIDS)
                .map(node -> {
                    TileState tileState = ((TileComponent)node).getTileState().getCopy();
                    tileState.setRow(GridPane.getRowIndex(node));
                    tileState.setCol(GridPane.getColumnIndex(node));
                    return tileState;
                })
                .toList();

        if (moveValidation.validateMoveAndAssignWordScore(tilesOnBoard, centerBoardState.getMoveCount(), wordAndScore, true)) {
            //----validacija je prosla----
            centerBoardState.incrementMoveCount();
            playerState.addToPlayerScore(wordAndScore.getScore());
            scoreText.setText(GameConstants.SCORE + playerState.getPlayerScore().toString());
            GameHandlerUtility.handleAddingTilesFromGridToCenterGameState(playerState, centerBoardState, tileBagState, centerBoardGrid);

            if(gameModeContext.equals(GameModeContext.MULTIPLAYER_HOST_AND_CLIENT) || gameModeContext.equals(GameModeContext.MULTIPLAYER_CLIENT)){
                client.sendCenterBoardStateAndTileBag(centerBoardState, tileBagState);
            }

            if(gameModeContext.equals(GameModeContext.SINGLEPLAYER_GA))
                gaHandler.handleGAEvolution();

            putTilesFromCenterGameStateToGrid();
            putTilesFromPlayerStateToGrid();

            if(gameModeContext.equals(GameModeContext.SINGLEPLAYER)){
                WordSaver wordSaver = new WordSaver(wordAndScore.getWord());
                wordSaver.start();

                CenterBoardState tmpCenterBoardState = new CenterBoardState(new ArrayList<>(centerBoardState.getCenterBoardTiles()));
                tmpCenterBoardState.setMoveCount(centerBoardState.getMoveCount());

                PlayerState tmpPlayerState = new PlayerState(new ArrayList<>(playerState.getPlayerBoardTiles()));
                tmpPlayerState.setPlayerScore(playerState.getPlayerScore());

                gameplayHistory.addCenterBoardState(tmpCenterBoardState);//povijest se biljezi samo za singleplayer
                gameplayHistory.addPlayerState(tmpPlayerState);

                XMLFileSaveUtility.saveGameplayHistory(gameplayHistory);
            }

            if(tileBagState.isTileBagEmpty()){
                if(!playerState.playerHasTiles()){
                    BasicDialogUtility.showDialog("Game end", "Congratulations! You have won the game!");
                    if(client != null && client.isConnected())
                        client.sendIWon();
                    return;
                }
                if(!gaPlayerState.playerHasTiles()){
                    BasicDialogUtility.showDialog("Game end", "Genetic algorithm has won the game!");
                }
            }
        }
        else{
            putTilesFromCenterGameStateToGrid();
            putTilesFromPlayerStateToGrid();
        }

        sideBarHandler.addTileBagComponent();
        if(GameModeContext.MULTIPLAYER_CLIENT.equals(gameModeContext) || gameModeContext.equals(GameModeContext.MULTIPLAYER_HOST_AND_CLIENT))
            sideBarHandler.addChatComponent();
    }

    public boolean tileExistsInWholeGrid(Integer row, Integer col) {
        return centerBoardGrid.getChildren().stream()
                .filter(TileComponent.class::isInstance)
                .anyMatch(node -> GridPane.getRowIndex(node).equals(row) && GridPane.getColumnIndex(node).equals(col));
    }

    public void shufflePlayerTiles() {
        GameHandlerUtility.removeTemporaryTilesFromCenterGrid(centerBoardGrid);
        GameHandlerUtility.fillEmptyPlayerBoard(this.centerBoardGrid);
        GameHandlerUtility.addTilesToGrid(playerState.getShuffledPlayerTiles(), this);
    }

    public void resetPlayerTiles() {
        GameHandlerUtility.removeTemporaryTilesFromCenterGrid(centerBoardGrid);
        putTilesFromPlayerStateToGrid();
    }

    public void swapPlayerTiles() {
        putTilesFromCenterGameStateToGrid();
        putTilesFromPlayerStateToGrid();
        List<TileState> tilesToSwap = new ArrayList<>(BasicDialogUtility.showSwapTilesDialog(GameHandlerUtility.getPlayerTilesFromGrid(centerBoardGrid)));
        if (!tilesToSwap.isEmpty()) {
            playerState.handlePlayerTilesSwap(tilesToSwap, tileBagState);
            putTilesFromPlayerStateToGrid();
            sideBarHandler.addTileBagComponent();
            if(GameModeContext.MULTIPLAYER_CLIENT.equals(gameModeContext) || gameModeContext.equals(GameModeContext.MULTIPLAYER_HOST_AND_CLIENT))
                sideBarHandler.addChatComponent();
        }
    }

    public void setGameContext(GameModeContext gameModeContext) {
        GameModeContextUtility.setupGameModeContext(this, gameModeContext);
        this.gameModeContext = gameModeContext;
    }

    public void saveGame() {
        FileUtility.saveGame(centerBoardState, playerState, tileBagState);
    }

    public void loadGame() {
        FileUtility.loadGame(centerBoardState, playerState, tileBagState);
        putTilesFromCenterGameStateToGrid();
        putTilesFromPlayerStateToGrid();
    }

    public void setMenuBarHandler(MenuBar menuBar) {
        this.menuBarHandler = new MenuBarHandler(menuBar, this);
        menuBarHandler.setMenuBarDefinition();
    }

}
