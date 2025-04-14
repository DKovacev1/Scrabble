package hr.java.scrabble.ga.model.impl;

import hr.java.scrabble.ga.model.Chromosome;
import hr.java.scrabble.ga.model.Direction;

import java.util.*;
import java.util.stream.Collectors;

public class ChromosomeImpl implements Chromosome<GeneImpl> {

    private final List<GeneImpl> chromosome = new ArrayList<>();

    @Override
    public double calculateFitness() {
        return 0;
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

    public Optional<Direction> getChromosomeDirection(){
        Optional<Direction> direction;
        Set<Integer> rowIndexSet = chromosome.stream()
                .map(gene -> gene.getGene().getRow())
                .collect(Collectors.toSet());

        if(rowIndexSet.size() == 1)
            direction = Optional.of(Direction.ROW);
        else
            direction = Optional.of(Direction.COLUMN);

        return direction;
    }

}
