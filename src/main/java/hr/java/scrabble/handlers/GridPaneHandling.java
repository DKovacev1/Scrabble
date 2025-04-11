package hr.java.scrabble.handlers;

public interface GridPaneHandling {
    void putTilesFromGridToPlayerState();
    void putTilesFromGridToCenterGameState();


    void putTilesFromPlayerStateToGrid();
    void putTilesFromCenterGameStateToGrid();

    boolean tileExistsInWholeGrid(Integer row, Integer col);
}
