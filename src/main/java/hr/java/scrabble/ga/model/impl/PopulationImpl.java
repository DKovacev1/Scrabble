package hr.java.scrabble.ga.model.impl;

import hr.java.scrabble.ga.model.Chromosome;
import hr.java.scrabble.ga.model.Population;
import hr.java.scrabble.ga.util.PopulationImplUtil;
import hr.java.scrabble.states.FreeCenterBoardState;
import hr.java.scrabble.states.PlayerState;
import hr.java.scrabble.states.TileState;
import hr.java.scrabble.utils.CenterBoardStateUtil;

import java.util.ArrayList;
import java.util.List;

public class PopulationImpl implements Population<ChromosomeImpl> {

    private final PlayerState gaPlayerState;
    private final List<TileState> centerBoardTiles;
    private final FreeCenterBoardState freeCenterBoardState;
    private List<Chromosome<GeneImpl>> population = new ArrayList<>();

    public PopulationImpl(List<TileState> centerBoardTiles, PlayerState gaPlayerState) {
        this.gaPlayerState = gaPlayerState;
        this.centerBoardTiles = centerBoardTiles;
        this.freeCenterBoardState = CenterBoardStateUtil.getFreeCenterBoardState(centerBoardTiles);
    }

    @Override
    public void createPopulation() {
        this.population = PopulationImplUtil.generateRandomPopulation(centerBoardTiles, freeCenterBoardState, gaPlayerState);
    }

    @Override
    public void evolve() {

    }

    @Override
    public ChromosomeImpl getBestIndividual() {
        //dohvatiti najboljeg
        //proci sva slova koja ima u kromosomima i umanjiti stanje u TileBagState
        return null;
    }

}
