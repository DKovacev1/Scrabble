package hr.java.scrabble.dto;

import hr.java.scrabble.states.CenterBoardState;
import hr.java.scrabble.states.TileBagState;

import java.io.Serializable;

public record CenterBoardStateAndTileBagStateDTO (CenterBoardState centerBoardState, TileBagState tileBagState) implements Serializable {
}
