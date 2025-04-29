package hr.java.scrabble.states;

import hr.java.scrabble.utils.TileBagUtility;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Setter
@Getter
public class TileBagState implements Serializable {

    private Map<TileState, Integer> tileBag;
    private AtomicInteger idCounter;

    public TileBagState() {
        tileBag = TileBagUtility.generateNewTileBag();//inicijalizira kompletno nove
        idCounter = new AtomicInteger(0);
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

    public boolean canTilesBeSwapped(int numOfTiles) {
        Optional<Integer> sum = tileBag.values().stream().reduce(Integer::sum);
        return sum.isPresent() && sum.get() >= numOfTiles;
    }

    public int getTileBagSize() {
        return tileBag.values().stream().reduce(0, Integer::sum);
    }

}
