package hr.java.scrabble.ga.util;

import hr.java.scrabble.config.ConfigReader;
import hr.java.scrabble.config.jndi.ConfigurationKey;
import hr.java.scrabble.ga.model.impl.ChromosomeImpl;
import hr.java.scrabble.ga.model.impl.GeneImpl;

import java.math.BigDecimal;
import java.util.List;

public class ChromosomeImplMutationUtil {

    private ChromosomeImplMutationUtil(){}

    public static void mutate(ChromosomeImpl chromosome) {
        chromosome.getGenes().forEach(gene -> {
            int randomNumber = GARandomUtil.getRandomNumber(0, 100);
            BigDecimal mutationRate = new BigDecimal(ConfigReader.getValue(ConfigurationKey.MUTATION_RATE)).multiply(BigDecimal.valueOf(100));
            if(randomNumber < mutationRate.intValue())
                swapTwoGenes(chromosome);
        });
    }

    private static void swapTwoGenes(ChromosomeImpl chromosome) {
        List<GeneImpl> genes = chromosome.getGenes();
        int size = genes.size();

        if (size < 2) // nije moguće zamijeniti gene ako ih ima manje od 2
            return;

        int index1 = GARandomUtil.getRandomNumber(0, size - 1);
        int index2;

        do {
            index2 = GARandomUtil.getRandomNumber(0, size - 1);
        } while (index1 == index2);

        GeneImpl gene1 = genes.get(index1);
        GeneImpl gene2 = genes.get(index2);

        String letter1 = genes.get(index1).getGene().getLetter();
        int points1 = genes.get(index1).getGene().getPoints();

        gene1.getGene().setLetter(gene2.getGene().getLetter());
        gene1.getGene().setPoints(gene2.getGene().getPoints());

        gene2.getGene().setLetter(letter1);
        gene2.getGene().setPoints(points1);
    }

}
