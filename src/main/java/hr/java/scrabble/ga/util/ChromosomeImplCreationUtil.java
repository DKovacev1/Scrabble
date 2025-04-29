package hr.java.scrabble.ga.util;

import hr.java.scrabble.ga.model.Chromosome;
import hr.java.scrabble.ga.model.Direction;
import hr.java.scrabble.ga.model.impl.ChromosomeImpl;
import hr.java.scrabble.ga.model.impl.GeneImpl;
import hr.java.scrabble.game.GameConstants;
import hr.java.scrabble.states.*;
import hr.java.scrabble.validations.MoveValidation;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import static hr.java.scrabble.ga.util.GARandomUtil.getRandomDirection;
import static hr.java.scrabble.ga.util.GARandomUtil.getRandomNumber;

public class ChromosomeImplCreationUtil {

    private ChromosomeImplCreationUtil(){}

    public static Chromosome<GeneImpl> generateRandomChromosome(List<TileState> centerBoardTiles, FreeCenterBoardState freeCenterBoardState, PlayerState gaPlayerState, MoveValidation moveValidation){
        Chromosome<GeneImpl> chromosome = new ChromosomeImpl(centerBoardTiles, freeCenterBoardState, gaPlayerState, moveValidation);
        Direction direction = getRandomDirection();

        if(!freeCenterBoardState.getFreeCenterBoardTiles().isEmpty()){
            int randomIndex = GARandomUtil.getRandomNumber(0, freeCenterBoardState.getFreeCenterBoardTiles().size() - 1);
            FreeCenterBoardTile freeCenterBoardTile = freeCenterBoardState.getFreeCenterBoardTiles().get(randomIndex);
                if(Direction.ROW.equals(direction)){//ovisno o smjeru dohvatiti cijeli redak ili stupac
                    List<FreeCenterBoardTile> freeRow = freeCenterBoardState.getFreeCenterBoardTiles().stream()
                            .filter(freeTile -> freeTile.getRow().equals(freeCenterBoardTile.getRow()))
                            .sorted(Comparator.comparing(FreeCenterBoardTile::getCol).reversed())
                            .toList();

                    generateChromosomeForFreeLocation(gaPlayerState, freeRow, chromosome);
                }
                else {
                    List<FreeCenterBoardTile> freeColumn = freeCenterBoardState.getFreeCenterBoardTiles().stream()
                            .filter(freeTile -> freeTile.getCol().equals(freeCenterBoardTile.getCol()))
                            .sorted(Comparator.comparing(FreeCenterBoardTile::getRow).reversed())
                            .toList();

                    generateChromosomeForFreeLocation(gaPlayerState, freeColumn, chromosome);
                }
        }
        else
            throw new IllegalStateException("freeCenterBoardTile is empty");

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
                .forEach(freeTile -> {
                    if(!playerStateCopy.getPlayerBoardTiles().isEmpty() && chromosome.getGenes().size() < GameConstants.MAX_NUM_OF_TILES_FOR_PLAYER){
                        TileStateBase playerTile = playerStateCopy.getPlayerBoardTiles().get(GARandomUtil.getRandomNumber(0, playerStateCopy.getPlayerBoardTiles().size() - 1));

                        TileStateBase geneCore = new TileStateBase();
                        geneCore.setRow(freeTile.getRow());
                        geneCore.setCol(freeTile.getCol());
                        geneCore.setLetter(playerTile.getLetter());
                        geneCore.setPoints(playerTile.getPoints());

                        GeneImpl gene = new GeneImpl(geneCore, playerTile);
                        chromosome.getGenes().add(gene);
                        playerStateCopy.getPlayerBoardTiles().remove(playerTile);
                    }
                });
    }

}
