package hr.java.scrabble.states;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class FreeCenterBoardState {
    private List<FreeCenterBoardTile> freeCenterBoardTiles = new ArrayList<>();
}
