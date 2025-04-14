package hr.java.scrabble.handlers;

import hr.java.scrabble.ga.model.Population;
import hr.java.scrabble.ga.model.impl.ChromosomeImpl;
import hr.java.scrabble.ga.model.impl.PopulationImpl;

public class GAHandler {

    private final GameHandler gameHandler;

    public GAHandler(GameHandler gameHandler) {
        this.gameHandler = gameHandler;
    }

    public void evolveAndShowBestChromosome() {
        Population<ChromosomeImpl> population = new PopulationImpl(gameHandler.getCenterBoardState().getCenterBoardTiles(), gameHandler.getGaPlayerState());
        population.createPopulation();



        System.out.println(population);
    }

}
