package hr.java.scrabble.ga.model.impl;

import hr.java.scrabble.ga.model.Chromosome;
import hr.java.scrabble.game.WordAndScore;
import hr.java.scrabble.states.TileState;
import hr.java.scrabble.validations.MoveValidation;

import java.util.ArrayList;
import java.util.List;

public class ChromosomeImpl implements Chromosome<GeneImpl> {

    private final List<GeneImpl> chromosome = new ArrayList<>();
    private final List<TileState> centerBoardTiles;
    private double fitness = 0;

    public ChromosomeImpl(List<TileState> centerBoardTiles) {
        this.centerBoardTiles = centerBoardTiles;
    }

    @Override
    public void calculateFitness() {
        WordAndScore wordAndScore = new WordAndScore();
        List<TileState> tilesOnBoard = new ArrayList<>(centerBoardTiles);
        tilesOnBoard.addAll(
                chromosome.stream()
                        .map(GeneImpl::getGene)
                        .map(tileStateBase -> {
                            TileState tileState = new TileState(tileStateBase.getLetter(), tileStateBase.getRow(), tileStateBase.getCol(), tileStateBase.getPoints());
                            tileState.setPermanentlyLaid(false);//plocice kromosoma nisu fiksno postavljene
                            return tileState;
                        })
                        .toList()
        );

        MoveValidation.validateMoveAndAssignWordScore(tilesOnBoard, 1, wordAndScore, false);
        this.fitness = wordAndScore.getScore();
    }

    @Override
    public double getFitness() {
        return fitness;
    }

    @Override
    public List<GeneImpl> getGenes() {
        return chromosome;
    }

    @Override
    public Chromosome<GeneImpl> crossover(Chromosome<GeneImpl> partner) {
        return null;
    }

    @Override
    public void mutate() {

    }

}
