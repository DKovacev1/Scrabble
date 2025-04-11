package hr.java.scrabble.ga.model;

import java.util.List;

public interface Population<T extends Chromosome<?>> {
    List<T> getIndividuals();
    T getBestIndividual();
    void addIndividual(T individual);
    void replaceAll(List<T> newIndividuals);
    double getAverageFitness();
}
