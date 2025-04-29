package hr.java.scrabble.ga.util;

import hr.java.scrabble.config.ConfigReader;
import hr.java.scrabble.config.jndi.ConfigurationKey;
import hr.java.scrabble.ga.model.Chromosome;
import hr.java.scrabble.ga.model.impl.GeneImpl;
import hr.java.scrabble.handlers.GADialogHandler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class PopulationImplEvolutionUtil {
    private PopulationImplEvolutionUtil() {}

    public static void evolvePopulation(List<Chromosome<GeneImpl>> population, GADialogHandler gaDialogHandler) {
        BigDecimal gaPopulationSize = new BigDecimal(ConfigReader.getValue(ConfigurationKey.GA_POPULATION_SIZE));
        BigDecimal elitismRate = new BigDecimal(ConfigReader.getValue(ConfigurationKey.ELITISM_RATE));
        BigDecimal elitePopulationSize = gaPopulationSize.multiply(elitismRate);
        BigDecimal newChildrenSize = gaPopulationSize.subtract(gaPopulationSize.multiply(elitismRate));

        int generationCount = 0;
        int generationsWithoutImprovement = 0;
        int maxGenerationsWithoutImprovement = 100;
        int previousBestFitness = Integer.MIN_VALUE;

        while (generationsWithoutImprovement < maxGenerationsWithoutImprovement) {
            gaDialogHandler.getGaState().setGenerationCounter(generationCount);
            gaDialogHandler.updateDialog();

            List<Chromosome<GeneImpl>> eliteFromLastGen = population.stream()
                    .sorted(Collections.reverseOrder(Comparator.comparing(Chromosome::getFitness)))
                    .limit(elitePopulationSize.intValue())
                    .toList();

            List<Chromosome<GeneImpl>> newChildren = createNewChildrenFromPopulation(population, newChildrenSize.intValue());

            population.clear();
            population.addAll(eliteFromLastGen);
            population.addAll(newChildren);

            population.forEach(Chromosome::calculateFitness);

            int currentBestFitness = population.stream()
                    .mapToInt(Chromosome::getFitness)
                    .max()
                    .orElse(Integer.MIN_VALUE);

            if (currentBestFitness == previousBestFitness) {
                generationsWithoutImprovement++;
            } else {
                generationsWithoutImprovement = 0;
                previousBestFitness = currentBestFitness;
                System.out.println("Zadnji najbolji fitness:" + currentBestFitness);
            }
            generationCount++;
        }

        System.out.println("Broj generacija: " + (generationCount - 1));
    }

    private static List<Chromosome<GeneImpl>> createNewChildrenFromPopulation(List<Chromosome<GeneImpl>> population, int newChildrenSize) {
        List<Chromosome<GeneImpl>> newChildren = new ArrayList<>();

        IntStream.range(0, newChildrenSize).forEach(i -> {
            Chromosome<GeneImpl> parent11 = population.get(GARandomUtil.getRandomNumber(0, population.size() - 1));
            Chromosome<GeneImpl> parent12 = population.get(GARandomUtil.getRandomNumber(0, population.size() - 1));
            Chromosome<GeneImpl> parent21 = population.get(GARandomUtil.getRandomNumber(0, population.size() - 1));
            Chromosome<GeneImpl> parent22 = population.get(GARandomUtil.getRandomNumber(0, population.size() - 1));

            Chromosome<GeneImpl> parent1 = parent11.getFitness() > parent12.getFitness() ? parent11 : parent12;
            Chromosome<GeneImpl> parent2 = parent21.getFitness() > parent22.getFitness() ? parent21 : parent22;

            Chromosome<GeneImpl> child = parent1.crossover(parent2);
            child.mutate();
            newChildren.add(child);
        });

        return newChildren;
    }

}
