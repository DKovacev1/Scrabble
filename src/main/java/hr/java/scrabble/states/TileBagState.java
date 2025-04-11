package hr.java.scrabble.states;

import hr.java.scrabble.utilities.TileBagUtility;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Setter
@Getter
public class TileBagState implements Serializable {

    private Map<TileState, Integer> tileBag;

    public TileBagState() {
        tileBag = TileBagUtility.generateNewTileBag();//inicijalizira kompletno nove
    }

    public List<TileState> getRandomTiles(Integer numOfTiles) {
        return TileBagUtility.getRandomPlayerTiles(numOfTiles, tileBag);
    }

    public void returnTilesToBag(List<TileState> tilesToSwap) {
        TileBagUtility.returnTilesToBag(tilesToSwap, tileBag);
    }

    public boolean isTileBagEmpty() {
        Optional<Integer> sum = tileBag.values().stream().reduce(Integer::sum);
        return sum.isPresent() && sum.get().equals(0);
    }

}
