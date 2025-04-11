package hr.java.scrabble.ga.model;

import java.util.List;

public interface Chromosome<T extends Gene<?>> {
    double calculateFitness();
    List<T> getGenes();
    Chromosome<T> crossover(Chromosome<T> partner);
    void mutate();
}
