package hr.java.scrabble.handlers;

import hr.java.scrabble.components.TileComponent;
import hr.java.scrabble.game.GameConstants;
import hr.java.scrabble.game.GameModeContext;
import hr.java.scrabble.game.WordAndScore;
import hr.java.scrabble.networking.client.Client;
import hr.java.scrabble.networking.server.Server;
import hr.java.scrabble.states.*;
import hr.java.scrabble.utils.*;
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

import static hr.java.scrabble.game.GameConstants.WHOLE_GRID_NUM_OF_COLS;
import static hr.java.scrabble.game.GameConstants.WHOLE_GRID_NUM_OF_ROWS;

@Getter
@Setter
public class GameHandler implements GridPaneHandling {

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
    private final ChatHandler chatHandler;
    private final GAHandler gaHandler;

    private GameplayHistory gameplayHistory;
    private GameModeContext gameModeContext;

    private Client client;
    private Server server;

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

        chatHandler = new ChatHandler(this);
        lastWordText = new Text();
    }

    @Override
    public void putTilesFromPlayerStateToGrid() {
        GameHandlerUtility.fillEmptyPlayerBoard(this.centerBoardGrid);
        GameHandlerUtility.addTilesToGrid(playerState.getPlayerBoardTiles(), this);
    }

    @Override
    public void putTilesFromCenterGameStateToGrid() {
        GameHandlerUtility.fillEmptyGameBoard(this.centerBoardGrid);
        GameHandlerUtility.addTilesToGrid(centerBoardState.getCenterBoardTiles(), this);
    }

    @Override
    public void putTilesFromGridToPlayerState() {
        GameHandlerUtility.putTilesFromGridToPlayerState(playerState, centerBoardGrid);
    }

    @Override
    public void putTilesFromGridToCenterGameState() {
        WordAndScore wordAndScore = new WordAndScore();
        if (MoveValidation.validateMoveAndAssignWordScore(centerBoardGrid, centerBoardState.getMoveCount(), wordAndScore)) {
            //----validacija je prosla----
            centerBoardState.incrementMoveCount();
            playerState.addToPlayerScore(wordAndScore.getScore());
            scoreText.setText(GameConstants.SCORE + playerState.getPlayerScore().toString());
            GameHandlerUtility.handleAddingTilesFromGridToCenterGameState(playerState, centerBoardState, tileBagState, centerBoardGrid);

            if(gameModeContext.equals(GameModeContext.MULTIPLAYER_HOST_AND_CLIENT) || gameModeContext.equals(GameModeContext.MULTIPLAYER_CLIENT)){
                client.sendCenterBoardStateAndTileBag(centerBoardState, tileBagState);
            }

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

            if(gameModeContext.equals(GameModeContext.SINGLEPLAYER_GA))
                gaHandler.evolveAndShowBestChromosome();

            if(tileBagState.isTileBagEmpty() && !playerState.playerHasTiles()){
                DialogUtility.showDialog("Game end", "Congratulations! You have won the game!");
                if(client != null && client.isConnected())
                    client.sendIWon();
            }

        } else {
            //vrati stanje prije nego su se plocice dodale na plocu
            putTilesFromCenterGameStateToGrid();
            putTilesFromPlayerStateToGrid();
        }
    }

    @Override
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
        List<TileState> tilesToSwap = new ArrayList<>(DialogUtility.showSwapTilesDialog(GameHandlerUtility.getPlayerTilesFromGrid(centerBoardGrid)));
        if (!tilesToSwap.isEmpty()) {
            playerState.handlePlayerTilesSwap(tilesToSwap, tileBagState);
            putTilesFromPlayerStateToGrid();
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
