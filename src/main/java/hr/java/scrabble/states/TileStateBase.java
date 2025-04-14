package hr.java.scrabble.states;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TileStateBase implements Serializable {
    private Integer row;
    private Integer col;
    private String letter;
    private Integer points;
}
