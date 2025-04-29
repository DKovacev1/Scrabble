package hr.java.scrabble.ga.model.impl;

import hr.java.scrabble.ga.model.Chromosome;
import hr.java.scrabble.ga.model.Population;
import hr.java.scrabble.ga.util.PopulationImplCreationUtil;
import hr.java.scrabble.ga.util.PopulationImplEvolutionUtil;
import hr.java.scrabble.handlers.GADialogHandler;
import hr.java.scrabble.states.FreeCenterBoardState;
import hr.java.scrabble.states.PlayerState;
import hr.java.scrabble.states.TileState;
import hr.java.scrabble.utils.CenterBoardStateUtil;
import hr.java.scrabble.validations.MoveValidation;

import java.util.*;

public class PopulationImpl implements Population<Chromosome<GeneImpl>> {

    private final PlayerState gaPlayerState;
    private final List<TileState> centerBoardTiles;
    private final FreeCenterBoardState freeCenterBoardState;
    private final MoveValidation moveValidation;
    private final GADialogHandler gaDialogHandler;
    private List<Chromosome<GeneImpl>> population = new ArrayList<>();

    public PopulationImpl(List<TileState> centerBoardTiles, PlayerState gaPlayerState, MoveValidation moveValidation, GADialogHandler gaDialogHandler) {
        this.gaPlayerState = gaPlayerState;
        this.centerBoardTiles = centerBoardTiles;
        this.freeCenterBoardState = CenterBoardStateUtil.getFreeCenterBoardState(centerBoardTiles);
        this.moveValidation = moveValidation;
        this.gaDialogHandler = gaDialogHandler;
    }

    @Override
    public void createPopulation() {
        this.population = PopulationImplCreationUtil.generateRandomPopulation(centerBoardTiles, freeCenterBoardState, gaPlayerState, moveValidation);
    }

    @Override
    public void evolve() {
        PopulationImplEvolutionUtil.evolvePopulation(population, gaDialogHandler);
    }

    @Override
    public Optional<Chromosome<GeneImpl>> getBestIndividual() {
        return population.stream()
                .max(Comparator.comparing(Chromosome::getFitness));
    }

}
