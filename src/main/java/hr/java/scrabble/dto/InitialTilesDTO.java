package hr.java.scrabble.dto;

import hr.java.scrabble.states.TileState;

import java.io.Serializable;
import java.util.List;

public record InitialTilesDTO (List<TileState> initialTiles) implements Serializable {
}
