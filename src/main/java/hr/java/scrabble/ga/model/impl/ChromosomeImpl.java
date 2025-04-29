package hr.java.scrabble.ga.model.impl;

import hr.java.scrabble.ga.model.Chromosome;
import hr.java.scrabble.ga.util.ChromosomeImplCrossoverUtil;
import hr.java.scrabble.ga.util.ChromosomeImplMutationUtil;
import hr.java.scrabble.game.WordAndScore;
import hr.java.scrabble.states.FreeCenterBoardState;
import hr.java.scrabble.states.PlayerState;
import hr.java.scrabble.states.TileState;
import hr.java.scrabble.validations.MoveValidation;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
public class ChromosomeImpl implements Chromosome<GeneImpl> {

    private final List<TileState> centerBoardTiles;
    @Getter
    private final FreeCenterBoardState freeCenterBoardState;
    @Getter
    private final PlayerState gaPlayerState;
    private final MoveValidation moveValidation;

    private final List<GeneImpl> chromosome = new ArrayList<>();
    private int fitness = 0;

    public ChromosomeImpl(List<TileState> centerBoardTiles, FreeCenterBoardState freeCenterBoardState, PlayerState gaPlayerState, MoveValidation moveValidation) {
        this.centerBoardTiles = centerBoardTiles;
        this.freeCenterBoardState = freeCenterBoardState;
        this.gaPlayerState = gaPlayerState;
        this.moveValidation = moveValidation;
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

        boolean isMoveValid = moveValidation.validateMoveAndAssignWordScore(tilesOnBoard, 1, wordAndScore, false);
        if(isMoveValid)
            this.fitness = wordAndScore.getScore();
        else
            this.fitness = 0;
    }

    @Override
    public int getFitness() {
        return fitness;
    }

    @Override
    public List<GeneImpl> getGenes() {
        return chromosome;
    }

    @Override
    public Chromosome<GeneImpl> crossover(Chromosome<GeneImpl> partner) {
        return ChromosomeImplCrossoverUtil.crossover(this, (ChromosomeImpl) partner);
    }

    @Override
    public void mutate() {
        ChromosomeImplMutationUtil.mutate(this);
    }

    @Override
    public Chromosome<GeneImpl> getCopy() {
        Chromosome<GeneImpl> copy = new ChromosomeImpl(centerBoardTiles, freeCenterBoardState, gaPlayerState, moveValidation);
        chromosome.forEach(gene -> copy.getGenes().add(new GeneImpl(gene.getCopy(), gene.getInitialTile())));
        return copy;
    }

}
