package hr.java.scrabble.states;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class GameplayHistory {

    private List<CenterBoardState> centerBoardStateList = new ArrayList<>();
    private List<PlayerState> playerStateList = new ArrayList<>();

    public GameplayHistory() {}

    public void addCenterBoardState(CenterBoardState centerBoardState){
        centerBoardStateList.add(centerBoardState);
    }

    public void addPlayerState(PlayerState playerState){
        playerStateList.add(playerState);
    }

}
