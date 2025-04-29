package hr.java.scrabble.ga.model;

import java.util.List;

public interface Chromosome<T extends Gene<?>> {
    void calculateFitness();
    int getFitness();
    List<T> getGenes();
    Chromosome<T> crossover(Chromosome<T> partner);
    void mutate();
    Chromosome<T>  getCopy();
}
