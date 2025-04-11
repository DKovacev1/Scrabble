package hr.java.scrabble.states;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class CenterBoardState implements Serializable {
    private Integer moveCount;
    private List<TileState> centerBoardTiles = new ArrayList<>();

    public CenterBoardState(List<TileState> centerBoardTiles) {
        this.centerBoardTiles = centerBoardTiles;
        this.moveCount = 0;
    }

    public CenterBoardState() {
        this.moveCount = 0;
    }

    public void incrementMoveCount() {
        this.moveCount++;
    }

    public void reset(){
        this.moveCount = 0;
        this.centerBoardTiles = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "CenterBoardState{" +
                "moveCount=" + moveCount +
                ", centerBoardTiles=" + centerBoardTiles +
                '}';
    }

}
