package hr.java.scrabble.ga.util;

import hr.java.scrabble.config.ConfigReader;
import hr.java.scrabble.config.jndi.ConfigurationKey;
import hr.java.scrabble.ga.model.Chromosome;
import hr.java.scrabble.ga.model.Direction;
import hr.java.scrabble.ga.model.impl.GeneImpl;
import hr.java.scrabble.states.FreeCenterBoardState;
import hr.java.scrabble.states.PlayerState;
import hr.java.scrabble.states.TileState;
import hr.java.scrabble.validations.MoveValidation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PopulationImplCreationUtil {
    private PopulationImplCreationUtil() {
    }

    public static List<Chromosome<GeneImpl>> generateRandomPopulation(List<TileState> centerBoardTiles, FreeCenterBoardState freeCenterBoardState, PlayerState gaPlayerState, MoveValidation moveValidation) {
        List<Chromosome<GeneImpl>> population = new ArrayList<>();
        int populationSize = Integer.parseInt(ConfigReader.getValue(ConfigurationKey.GA_POPULATION_SIZE));

        IntStream.range(0, populationSize).parallel()
                .forEach(i -> {
                    Chromosome<GeneImpl> chromosome = ChromosomeImplCreationUtil.generateRandomChromosome(centerBoardTiles, freeCenterBoardState, gaPlayerState, moveValidation);
                    chromosome.calculateFitness();
                    population.add(chromosome);
                });

        return population;
    }

    private static boolean isConsecutive(List<Integer> numbers) {
        if (numbers == null || numbers.size() <= 1) return true;

        List<Integer> sorted = numbers.stream()
                .distinct()
                .sorted()
                .toList();

        return IntStream.range(1, sorted.size())
                .allMatch(i -> sorted.get(i) - sorted.get(i - 1) == 1);
    }

    private static Direction getChildDirection(List<GeneImpl> genes) {
        boolean isHorizontal = genes.stream()
                .map(gene -> gene.getGene().getRow())
                .collect(Collectors.toSet())
                .size() == 1;

        return isHorizontal ? Direction.ROW : Direction.COLUMN;
    }

}
