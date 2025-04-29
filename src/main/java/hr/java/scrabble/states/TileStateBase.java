package hr.java.scrabble.states;

import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class TileStateBase implements Serializable {
    private Integer row;
    private Integer col;
    private String letter;
    private Integer points;

    public TileStateBase getCopy(){
        return new TileStateBase(this.getRow(), this.getCol(), this.getLetter(), this.getPoints());
    }
}
