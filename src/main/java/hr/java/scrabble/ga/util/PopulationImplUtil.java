package hr.java.scrabble.ga.util;

import hr.java.scrabble.config.ConfigReader;
import hr.java.scrabble.config.jndi.ConfigurationKey;
import hr.java.scrabble.ga.model.Chromosome;
import hr.java.scrabble.ga.model.impl.GeneImpl;
import hr.java.scrabble.states.FreeCenterBoardState;
import hr.java.scrabble.states.PlayerState;

import java.util.ArrayList;
import java.util.List;

public class PopulationImplUtil {
    private PopulationImplUtil() {}

    public static List<Chromosome<GeneImpl>> generateRandomPopulation(FreeCenterBoardState freeCenterBoardState, PlayerState gaPlayerState) {
        List<Chromosome<GeneImpl>> population = new ArrayList<>();
        int populationSize = Integer.parseInt(ConfigReader.getValue(ConfigurationKey.GA_POPULATION_SIZE));

        for (int i = 0; i < populationSize; i++)
            population.add(ChromosomeImplUtil.generateRandomChromosome(freeCenterBoardState, gaPlayerState));

        return population;
    }

}
