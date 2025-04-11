package hr.java.scrabble.ga.model;

public interface Gene<T> {
    T getGene();
    void setGene(T gene);
}
