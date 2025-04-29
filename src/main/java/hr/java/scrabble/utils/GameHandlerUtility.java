package hr.java.scrabble.utils;

import hr.java.scrabble.components.EmptyFieldComponent;
import hr.java.scrabble.components.TileComponent;
import hr.java.scrabble.game.GameConstants;
import hr.java.scrabble.handlers.GameHandler;
import hr.java.scrabble.states.CenterBoardState;
import hr.java.scrabble.states.PlayerState;
import hr.java.scrabble.states.TileBagState;
import hr.java.scrabble.states.TileState;
import javafx.scene.layout.GridPane;

import java.util.Comparator;
import java.util.List;

import static hr.java.scrabble.game.GameConstants.*;

public class GameHandlerUtility {

    private GameHandlerUtility(){}

    public static void fillEmptyGameBoard(GridPane centerBoardGrid) {
        //obrisati prvih 15 redova
        centerBoardGrid.getChildren().removeAll(
                centerBoardGrid.getChildren().stream()
                        .filter(node -> GridPane.getRowIndex(node) >= 0 && GridPane.getRowIndex(node) < NUM_OF_GRIDS)
                        .toList()
        );

        for (int row = 0; row < WHOLE_GRID_NUM_OF_ROWS; row++) {
            for (int col = 0; col < WHOLE_GRID_NUM_OF_COLS; col++) {
                if (row < NUM_OF_GRIDS)
                    centerBoardGrid.add(new EmptyFieldComponent(BOARD_SCORING[row][col]), col, row);
            }
        }
    }

    public static void fillEmptyPlayerBoard(GridPane centerBoardGrid) {
        centerBoardGrid.getChildren().removeAll(
                centerBoardGrid.getChildren().stream()
                        .filter(node -> GridPane.getRowIndex(node) >= NUM_OF_GRIDS
                                && GridPane.getRowIndex(node) < WHOLE_GRID_NUM_OF_ROWS)
                        .toList()
        );

        for (int row = 0; row < WHOLE_GRID_NUM_OF_ROWS; row++) {
            for (int col = 0; col < WHOLE_GRID_NUM_OF_COLS; col++) {
                if (row >= NUM_OF_GRIDS) {
                    EmptyFieldComponent emptyFieldComponent = getEmptyFieldComponent(row, col);
                    centerBoardGrid.add(emptyFieldComponent, col, row);
                }
            }
        }
    }

    public static void removeTemporaryTilesFromCenterGrid(GridPane centerBoardGrid) {
        centerBoardGrid.getChildren().removeAll(
                centerBoardGrid.getChildren().stream()
                        .filter(node -> GridPane.getRowIndex(node) >= 0
                                && GridPane.getRowIndex(node) < NUM_OF_GRIDS)
                        .filter(node -> node instanceof TileComponent)
                        .filter(node -> !((TileComponent) node).getTileState().isPermanentlyLaid())
                        .toList()
        );
    }

    public static void addTilesToGrid(List<TileState> tiles, GameHandler gameHandler) {
        tiles.forEach(tileState -> {
            TileComponent tileComponent = new TileComponent(gameHandler, tileState);
            gameHandler.getCenterBoardGrid().add(tileComponent, tileState.getCol(), tileState.getRow());
        });
    }

    public static void putTilesFromGridToPlayerState(PlayerState playerState, GridPane centerBoardGrid) {
        playerState.setPlayerBoardTiles(
                centerBoardGrid.getChildren().stream()
                        .filter(TileComponent.class::isInstance)
                        .filter(node -> GridPane.getRowIndex(node).equals(PLAYER_TILE_GRID_DATA_ROW_INDEX)
                                && GridPane.getColumnIndex(node) >= PLAYER_TILE_GRID_DATA_COL_START_INDEX
                                && GridPane.getColumnIndex(node) <= PLAYER_TILE_GRID_DATA_COL_END_INDEX)
                        .map(TileComponent.class::cast)
                        .map(TileComponent::getTileState)
                        .toList()
        );
    }

    private static EmptyFieldComponent getEmptyFieldComponent(int row, int col) {
        EmptyFieldComponent emptyFieldComponent = new EmptyFieldComponent(BOARD_SCORING[row][col]);
        if (row == PLAYER_TILE_GRID_DATA_ROW_INDEX + 1 //crta da se vidi gdje su postavljene plocice igraca
                && col >= PLAYER_TILE_GRID_DATA_COL_START_INDEX
                && col <= PLAYER_TILE_GRID_DATA_COL_END_INDEX) {
            emptyFieldComponent.setStyle("-fx-background-color: linear-gradient(to bottom, #a88747 30%, transparent 30%)");
        }
        return emptyFieldComponent;
    }

    public static void handleAddingTilesFromGridToCenterGameState(PlayerState playerState, CenterBoardState centerBoardState,
                                                                  TileBagState tileBagState, GridPane centerBoardGrid) {
        //plocice koje su dodane na grid
        List<TileState> tilesAddedToGrid = centerBoardGrid.getChildren().stream()
                .filter(TileComponent.class::isInstance)
                .filter(node -> GridPane.getRowIndex(node) < NUM_OF_GRIDS
                        && GridPane.getColumnIndex(node) < NUM_OF_GRIDS //dohvat preko GridPane jer u tileState nisu zapisane nove koordinate
                        && !((TileComponent) node).getTileState().isPermanentlyLaid())
                .map(node -> ((TileComponent) node).getTileState())
                .toList();

        //makni plocice iz playerstate-a
        playerState.getPlayerBoardTiles().removeAll(tilesAddedToGrid);

        //cini ih nepomicnima
        centerBoardGrid.getChildren().stream()
                .filter(TileComponent.class::isInstance)
                .filter(node -> GridPane.getRowIndex(node) < NUM_OF_GRIDS
                        && GridPane.getColumnIndex(node) < NUM_OF_GRIDS)//dohvat preko GridPane jer u tileState nisu zapisane nove koordinate
                .forEach(node -> ((TileComponent) node).removeDraggable());

        //dodaj ih u centerboardstate
        centerBoardState.setCenterBoardTiles(centerBoardGrid.getChildren().stream()
                .filter(TileComponent.class::isInstance)
                .filter(node -> GridPane.getRowIndex(node) < NUM_OF_GRIDS && GridPane.getColumnIndex(node) < NUM_OF_GRIDS)
                .map(node -> {
                    TileState tileState = ((TileComponent) node).getTileState();
                    tileState.setRow(GridPane.getRowIndex(node));
                    tileState.setCol(GridPane.getColumnIndex(node));
                    tileState.setPermanentlyLaid(true);
                    return tileState;
                })
                .toList()
        );

        //izvuci nove plocice
        List<TileState> newlyTakenTiles = tileBagState.getRandomTiles(tilesAddedToGrid.size());

        //dodaj nove
        playerState.getPlayerBoardTiles().addAll(newlyTakenTiles);

        //postavi im retke i stupce
        for(int i = 0; i < playerState.getPlayerBoardTiles().size(); i++){
            TileState tileState = playerState.getPlayerBoardTiles().get(i);
            tileState.setRow(GameConstants.PLAYER_TILE_GRID_DATA_ROW_INDEX);
            tileState.setCol(i + PLAYER_TILE_GRID_DATA_COL_START_INDEX);
        }

    }

    public static List<TileState> getRandomInitialPlayerTiles(TileBagState tileBagState) {
        List<TileState> initialPlayerTiles = tileBagState.getRandomTiles(MAX_NUM_OF_TILES_FOR_PLAYER);

        for (int i = 0; i < initialPlayerTiles.size(); i++) {
            TileState tileState = initialPlayerTiles.get(i);
            tileState.setRow(PLAYER_TILE_GRID_DATA_ROW_INDEX);
            tileState.setCol(i + 4);
        }

        return initialPlayerTiles;
    }

    public static List<TileState> getPlayerTilesFromGrid(GridPane centerBoardGrid) {
        return centerBoardGrid.getChildren().stream()
                .filter(TileComponent.class::isInstance)
                .filter(node -> GridPane.getRowIndex(node).equals(PLAYER_TILE_GRID_DATA_ROW_INDEX)
                        && GridPane.getColumnIndex(node) >= PLAYER_TILE_GRID_DATA_COL_START_INDEX
                        && GridPane.getColumnIndex(node) <= PLAYER_TILE_GRID_DATA_COL_END_INDEX)
                .sorted(Comparator.comparing(GridPane::getColumnIndex))
                .map(TileComponent.class::cast)
                .map(TileComponent::getTileState)
                .toList();
    }

    public static void setDraggableForPlayerTiles(GameHandler gameHandler, boolean permanentlyLaid) {
        gameHandler.getPlayerState().getPlayerBoardTiles().forEach(tileState -> tileState.setPermanentlyLaid(permanentlyLaid));
        gameHandler.putTilesFromPlayerStateToGrid();
    }

}
