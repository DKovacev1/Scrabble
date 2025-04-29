package hr.java.scrabble.ga.model;

import java.util.Optional;

public interface Population<T extends Chromosome<?>> {
    void createPopulation();
    void evolve();
    Optional<T> getBestIndividual();
}
