package hr.java.scrabble.game;


import lombok.Getter;
import lombok.Setter;

@Getter
public class WordAndScore {
    @Setter
    private String word = "";
    private Integer score = 0;

    public WordAndScore() {}

    public void addToScore(Integer value){
        score += value;
    }

}
