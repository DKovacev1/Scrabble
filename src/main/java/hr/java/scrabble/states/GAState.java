package hr.java.scrabble.states;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class GAState {
    private List<String> availableLetters = new ArrayList<>();
    private Integer generationCounter = 0;
    private String finalWord;
}
