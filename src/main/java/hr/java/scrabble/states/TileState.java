package hr.java.scrabble.states;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Setter
@Getter
@ToString
public class TileState extends TileStateBase implements Serializable {

    private boolean permanentlyLaid;

    public TileState(String letter, Integer row, Integer col, Integer points) {
        super(row, col, letter, points);
    }

    public TileState(String letter, Integer points) {
        super(-1, -1, letter, points);
    }

    @Override
    public TileState getCopy(){
        TileState tileState = new TileState(super.getLetter(), super.getRow(), super.getCol(), super.getPoints());
        tileState.setPermanentlyLaid(permanentlyLaid);
        return tileState;
    }

}
