package hr.java.scrabble.ga.model;

public interface Population<T extends Chromosome<?>> {
    void createPopulation();
    T getBestIndividual();
}
