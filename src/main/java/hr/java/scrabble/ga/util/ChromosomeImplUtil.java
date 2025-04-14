package hr.java.scrabble.ga.util;

import hr.java.scrabble.ga.model.Chromosome;
import hr.java.scrabble.ga.model.Direction;
import hr.java.scrabble.ga.model.impl.ChromosomeImpl;
import hr.java.scrabble.ga.model.impl.GeneImpl;
import hr.java.scrabble.states.FreeCenterBoardState;
import hr.java.scrabble.states.FreeCenterBoardTile;
import hr.java.scrabble.states.PlayerState;
import hr.java.scrabble.states.TileStateBase;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import static hr.java.scrabble.ga.util.GARandomUtil.getRandomDirection;
import static hr.java.scrabble.ga.util.GARandomUtil.getRandomNumber;

public class ChromosomeImplUtil {

    private ChromosomeImplUtil(){}

    public static Chromosome<GeneImpl> generateRandomChromosome(FreeCenterBoardState freeCenterBoardState, PlayerState gaPlayerState){
        Chromosome<GeneImpl> chromosome = new ChromosomeImpl();
        Direction direction = getRandomDirection();

        freeCenterBoardState.getFreeCenterBoardTiles().stream()
                .findAny()
                .ifPresentOrElse(freeCenterBoardTile -> {//Random celija koja je slobodna.
                    if(Direction.ROW.equals(direction)){//ovisno o smjeru dohvatiti cijeli redak ili stupac
                        List<FreeCenterBoardTile> freeRow = freeCenterBoardState.getFreeCenterBoardTiles().stream()
                                .filter(freeTile -> freeTile.getRow().equals(freeCenterBoardTile.getRow()))
                                .sorted(Comparator.comparing(FreeCenterBoardTile::getCol))
                                .toList();

                        generateChromosomeForFreeLocation(gaPlayerState, freeRow, chromosome);
                    }
                    else{
                        List<FreeCenterBoardTile> freeColumn = freeCenterBoardState.getFreeCenterBoardTiles().stream()
                                .filter(freeTile -> freeTile.getCol().equals(freeCenterBoardTile.getCol()))
                                .sorted(Comparator.comparing(FreeCenterBoardTile::getRow))
                                .toList();

                        generateChromosomeForFreeLocation(gaPlayerState, freeColumn, chromosome);
                    }
                }, () -> {
                    throw new IllegalStateException("freeCenterBoardTile is empty");
                });

        return chromosome;
    }

    private static void generateChromosomeForFreeLocation(PlayerState gaPlayerState, List<FreeCenterBoardTile> freeLocations, Chromosome<GeneImpl> chromosome) {
        int start = getRandomNumber(0, freeLocations.size() - 1);
        int end = getRandomNumber(0, freeLocations.size() - 1);
        int from = Math.min(start, end);
        int to = Math.max(start, end);

        PlayerState playerStateCopy = gaPlayerState.getCopy();
        IntStream.rangeClosed(from, to)
                .mapToObj(freeLocations::get)
                .forEach(freeTile -> playerStateCopy.getPlayerBoardTiles().stream()//freeTile - uzimamo koordinate
                        .findAny()//maksimalno 7 puta ce se iterirati
                        .ifPresent(playerTile -> {//playerTile - uzimamo slovo i bodove
                            TileStateBase geneCore = new TileStateBase();
                            geneCore.setRow(freeTile.getRow());
                            geneCore.setCol(freeTile.getCol());
                            geneCore.setLetter(playerTile.getLetter());
                            geneCore.setPoints(playerTile.getPoints());

                            GeneImpl gene = new GeneImpl();
                            gene.setGene(geneCore);
                            chromosome.getGenes().add(gene);

                            playerStateCopy.getPlayerBoardTiles().remove(playerTile);
                        }));
    }

}
