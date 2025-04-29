package hr.java.scrabble.states;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class FreeCenterBoardState {
    private List<FreeCenterBoardTile> freeCenterBoardTiles = new ArrayList<>();
}
