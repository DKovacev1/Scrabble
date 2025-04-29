package hr.java.scrabble.ga.util;

import hr.java.scrabble.ga.model.Chromosome;
import hr.java.scrabble.ga.model.impl.ChromosomeImpl;
import hr.java.scrabble.ga.model.impl.GeneImpl;
import hr.java.scrabble.states.TileState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ChromosomeImplCrossoverUtil {

    private ChromosomeImplCrossoverUtil(){}

    public static Chromosome<GeneImpl> crossover(ChromosomeImpl parent1, ChromosomeImpl parent2) {
        AtomicInteger chosenParentNum = new AtomicInteger(GARandomUtil.getRandomNumber(1, 2));
        int maxChildSize = Math.min(parent1.getGenes().size(), parent2.getGenes().size());
        ChromosomeImpl child = (ChromosomeImpl)(chosenParentNum.get() == 1 ? parent1 : parent2).getCopy();

        child.getGenes().forEach(gene -> { //ciscenje slova i bodova, lokacije zadrzavamo
            gene.getGene().setLetter(null);
            gene.getGene().setPoints(null);
        });

        AtomicInteger parentIndex = new AtomicInteger(0);
        IntStream.range(0, maxChildSize).forEach(index -> {
            GeneImpl geneFromChild = child.getGenes().get(index);

            // Tražimo prvi gen koji može biti postavljen (počevši od trenutnog parentIndex-a)
            while (parentIndex.get() < parent1.getGenes().size() && parentIndex.get() < parent2.getGenes().size()) {
                chosenParentNum.set(GARandomUtil.getRandomNumber(1, 2));
                GeneImpl geneFromParent = chosenParentNum.get() == 1
                        ? parent1.getGenes().get(parentIndex.get())
                        : parent2.getGenes().get(parentIndex.get());

                if (canGeneBeAdded(child, geneFromParent)) {
                    geneFromChild.getGene().setLetter(geneFromParent.getGene().getLetter());
                    geneFromChild.getGene().setPoints(geneFromParent.getGene().getPoints());
                    child.getGenes().set(index, geneFromChild);
                    parentIndex.incrementAndGet();
                    break; // izađi iz while petlje jer smo uspješno postavili gen
                }

                parentIndex.incrementAndGet(); // pokušaj s idućim genom
            }
        });

        //micemo sva prazna mjesta
        child.getGenes().removeIf(gene -> gene.getGene().getLetter() == null);

        return child;
    }

    private static boolean canGeneBeAdded(ChromosomeImpl child, GeneImpl geneFromParent) {
        List<String> lettersFromPlayerState = child.getGaPlayerState().getPlayerBoardTiles().stream()
                .map(TileState::getLetter)
                .toList();

        List<String> lettersFromNewGene = new ArrayList<>(child.getGenes().stream()
                .map(gene -> gene.getGene().getLetter())
                .filter(Objects::nonNull)
                .toList());

        lettersFromNewGene.add(geneFromParent.getGene().getLetter());

        Map<String, Long> countMapPlayerState = lettersFromPlayerState.stream()
                .collect(Collectors.groupingBy(letter -> letter, Collectors.counting()));

        Map<String, Long> countMapGene= lettersFromNewGene.stream()
                .collect(Collectors.groupingBy(letter -> letter, Collectors.counting()));

        for (Map.Entry<String, Long> entry : countMapGene.entrySet()) {
            String letter = entry.getKey();
            long countInList2 = entry.getValue();
            long countInList1 = countMapPlayerState.getOrDefault(letter, 0L);

            if (countInList2 > countInList1)
                return false;
        }

        return true;
    }

}
