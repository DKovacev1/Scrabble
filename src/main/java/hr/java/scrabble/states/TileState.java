package hr.java.scrabble.states;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class TileState implements Serializable {

    private Integer row;
    private Integer col;
    private String letter;
    private Integer points;
    private boolean permanentlyLaid;


    public TileState(String letter, Integer row, Integer col, Integer points) {
        this.letter = letter;
        this.row = row;
        this.col = col;
        this.points = points;
    }

    public TileState(String letter, Integer points) {
        this.letter = letter;
        this.row = -1;//samo u tileBag
        this.col = -1;//samo u tileBag
        this.points = points;
    }

    public TileState getCopy(){
        TileState tileState = new TileState(letter, row, col, points);
        tileState.setPermanentlyLaid(permanentlyLaid);
        return tileState;
    }

    public TileState() {}

}
