package hr.java.scrabble.utilities;

import hr.java.scrabble.states.TileState;

import java.util.List;
import java.util.Optional;

import static hr.java.scrabble.game.GameConstants.NUM_OF_GRIDS;

public class TilesUtility {

    private TilesUtility(){}

    public static Optional<TileState> getMostLeftTileState(TileState tileStateFrom, List<TileState> tilesOnBoard){
        //probaj otici skroz lijevo ako ima starih plocica
        Optional<TileState> mostLeftOldTileState = Optional.empty();
        for(int col = tileStateFrom.getCol(); col >= 0; col--){
            int tmpCol = col;//stavljeno zbog lambde
            Optional<TileState> nextLeftTile = tilesOnBoard.stream()
                    .filter(tileState -> tileState.getRow().equals(tileStateFrom.getRow())
                            && tileState.getCol().equals(tmpCol))
                    .findAny();

            if(nextLeftTile.isPresent())
                mostLeftOldTileState = nextLeftTile;
            else
                break;
        }
        return mostLeftOldTileState;
    }

    public static Optional<TileState> getMostRightTileState(TileState tileStateFrom, List<TileState> tilesOnBoard) {
        Optional<TileState> mostRightOldTileState = Optional.empty();
        for(int col = tileStateFrom.getCol(); col < NUM_OF_GRIDS; col++){
            int tmpCol = col;//stavljeno zbog lambde
            Optional<TileState> nextRightTile = tilesOnBoard.stream()
                    .filter(tileState -> tileState.getRow().equals(tileStateFrom.getRow())
                            && tileState.getCol().equals(tmpCol))
                    .findAny();

            if(nextRightTile.isPresent())
                mostRightOldTileState = nextRightTile;
            else
                break;
        }
        return mostRightOldTileState;
    }

    public static Optional<TileState> getMostTopTileState(TileState tileStateFrom, List<TileState> tilesOnBoard) {
        Optional<TileState> mostTopTileState = Optional.empty();
        //trazi najvisi
        for(int row = tileStateFrom.getRow(); row >= 0; row--){
            int tmpRow = row;//stavljeno zbog lambde
            Optional<TileState> nextTopTile = tilesOnBoard.stream()
                    .filter(tileState -> tileState.getCol().equals(tileStateFrom.getCol())
                            && tileState.getRow().equals(tmpRow))
                    .findAny();

            if(nextTopTile.isPresent())
                mostTopTileState = nextTopTile;
            else
                break;
        }
        return mostTopTileState;
    }


    public static Optional<TileState> getMostBottomTileState(TileState tileStateFrom, List<TileState> tilesOnBoard) {
        Optional<TileState> mostBottomTileState = Optional.empty();
        //trazi najnizi
        for(int row = tileStateFrom.getRow(); row < NUM_OF_GRIDS; row++){
            int tmpRow = row;//stavljeno zbog lambde
            Optional<TileState> nextBelowTile = tilesOnBoard.stream()
                    .filter(tileState -> tileState.getCol().equals(tileStateFrom.getCol())
                            && tileState.getRow().equals(tmpRow))
                    .findAny();

            if(nextBelowTile.isPresent())
                mostBottomTileState = nextBelowTile;
            else
                break;
        }
        return mostBottomTileState;
    }
}
