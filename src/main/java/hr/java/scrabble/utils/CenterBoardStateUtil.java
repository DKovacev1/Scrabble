package hr.java.scrabble.utils;

import hr.java.scrabble.game.GameConstants;
import hr.java.scrabble.states.FreeCenterBoardState;
import hr.java.scrabble.states.FreeCenterBoardTile;
import hr.java.scrabble.states.TileState;

import java.util.List;

public class CenterBoardStateUtil {
    private CenterBoardStateUtil() {}

    public static FreeCenterBoardState getFreeCenterBoardState(List<TileState> centerBoardTiles) {
        FreeCenterBoardState freeCenterBoardState = new FreeCenterBoardState();
        for (int row = 0; row < GameConstants.NUM_OF_GRIDS; row++){
            for (int col = 0; col < GameConstants.NUM_OF_GRIDS; col++){
                int finalRow = row;
                int finalCol = col;

                boolean isSlotFree = centerBoardTiles.stream()
                        .noneMatch(tileState -> tileState.getRow().equals(finalRow) && tileState.getCol().equals(finalCol));

                if(isSlotFree){
                    FreeCenterBoardTile freeCenterBoardTile = new FreeCenterBoardTile();
                    freeCenterBoardTile.setRow(row);
                    freeCenterBoardTile.setCol(col);
                    freeCenterBoardState.getFreeCenterBoardTiles().add(freeCenterBoardTile);
                }
            }
        }

        //System.out.println(centerBoardTiles);
        //System.out.println("----");
        //System.out.println(freeCenterBoardState);
        return freeCenterBoardState;
    }

}
