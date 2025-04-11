package hr.java.scrabble.dto;

import java.io.Serializable;

public record YourTurnDTO (boolean isYourTurn) implements Serializable {
}
