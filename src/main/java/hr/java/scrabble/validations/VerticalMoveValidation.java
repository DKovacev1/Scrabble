package hr.java.scrabble.validations;

import hr.java.scrabble.game.GameConstants;
import hr.java.scrabble.game.WordAndScore;
import hr.java.scrabble.states.TileState;
import hr.java.scrabble.utilities.TilesUtility;

import java.util.*;
import java.util.stream.Collectors;

public class VerticalMoveValidation {

    private VerticalMoveValidation(){}

    //boduju se samo plocice u stupcu, za retke se radi jedino validacija
    public static boolean validateVerticalMoveAndAssignWordScore(List<TileState> tilesOnBoard, List<TileState> newTilesOnBoard, WordAndScore wordAndScore) {
        newTilesOnBoard.sort(Comparator.comparing(TileState::getRow));//sortiraj po indeksu retka
        Integer moveCol = newTilesOnBoard.getFirst().getCol();

        TileState mostTopNewTileState = newTilesOnBoard.getFirst();
        TileState mostBottomNewTileState = newTilesOnBoard.getLast();

        Optional<TileState> mostTopOldTileState = TilesUtility.getMostTopTileState(mostTopNewTileState, tilesOnBoard);
        Optional<TileState> mostBottomOldTileState = TilesUtility.getMostBottomTileState(mostTopNewTileState, tilesOnBoard);

        //ako ih ima, postavi ih kao rubne plocice
        TileState mostTopTileState = mostTopOldTileState.orElse(mostTopNewTileState);
        TileState mostBottomTileState = mostBottomOldTileState.orElse(mostBottomNewTileState);

        List<TileState> wholeWordTileStates = tilesOnBoard.stream()
                .filter(tileState -> tileState.getCol().equals(moveCol)
                        && tileState.getRow() >= mostTopTileState.getRow()
                        && tileState.getRow() <= mostBottomTileState.getRow())
                .sorted(Comparator.comparing(TileState::getRow))
                .toList();

        //provjera je li sve spojeno
        if ((mostBottomTileState.getRow() - mostTopTileState.getRow() + 1) != wholeWordTileStates.size())
            return false;//odvojene su

        //provjera rijeci za stupac vertikalno
        String word = wholeWordTileStates.stream()
                .sorted(Comparator.comparing(TileState::getRow))
                .map(TileState::getLetter)
                .collect(Collectors.joining());

        System.out.println("vertical word: " + word);
        if (!WordValidating.isWordValid(word))
            return false;//rijec se ne nalazi u rijecniku

        wordAndScore.setWord(word);

        //prvo dodaj bodove za sva nova slova u vertikali
        Map<TileState, Integer> scoringMap = new HashMap<>();
        wholeWordTileStates.forEach(tileState -> scoringMap.put(tileState, tileState.getPoints()));//bodovanje ide na kompletnu novu rijec, ne samo slova koja su dodana

        //za svaku plocicu u stupci probaj otici maksimalno lijevo i desno, probaj formirati rijec te ju provjeri preko api-a
        for (TileState tileStateInCol : wholeWordTileStates) {
            int row = tileStateInCol.getRow();

            Optional<TileState> mostLeftTileState = TilesUtility.getMostLeftTileState(tileStateInCol, tilesOnBoard);
            Optional<TileState> mostRightTileState = TilesUtility.getMostRightTileState(tileStateInCol, tilesOnBoard);

            //spoji, vidi jel rijec ispravna
            if (mostLeftTileState.isPresent() && mostRightTileState.isPresent()) {
                TileState finalMostLeftTileState = mostLeftTileState.get();
                TileState finalMostRightTileState = mostRightTileState.get();

                List<TileState> rowTileStates = tilesOnBoard.stream()
                        .filter(tileState -> tileState.getRow().equals(row)
                                && tileState.getCol() >= finalMostLeftTileState.getCol()
                                && tileState.getCol() <= finalMostRightTileState.getCol())
                        .sorted(Comparator.comparing(TileState::getCol))
                        .toList();

                String rowWord = rowTileStates.stream()
                        .map(TileState::getLetter)
                        .collect(Collectors.joining());

                System.out.println("rowWord: " + rowWord);
                if (!WordValidating.isWordValid(rowWord))
                    return false;//rijec se ne nalazi u rijecniku

                //dodavanje bodova za slova koja se ponavljaju u rijecima za retke
                if (newTilesOnBoard.contains(tileStateInCol)
                        && rowTileStates.contains(tileStateInCol)
                        && rowTileStates.size() > 1) {

                    int oldScore = scoringMap.get(tileStateInCol);
                    int tileScore = tileStateInCol.getPoints();
                    int newScore = oldScore + tileScore;
                    scoringMap.put(tileStateInCol, newScore);
                }
            }
        }

        //do ovdje imamo vrijednosti za pojedinu plocicu
        //provjera DL i TL po plocici
        scoringMap.keySet().stream()
                .filter(tileState -> !tileState.isPermanentlyLaid())//premuim bodovi idu samo na NOVE plocie
                .forEach(tileState -> {
                    switch (GameConstants.BOARD_SCORING[tileState.getRow()][tileState.getCol()].getScoringFormulaId()){
                        case 1 -> { //DL  utjece samo na pojedicu plocicu
                            int oldScore = scoringMap.get(tileState);
                            int newScore = oldScore * 2;
                            scoringMap.put(tileState, newScore);
                        }
                        case 2 -> { //TL
                            int oldScore = scoringMap.get(tileState);
                            int newScore = oldScore * 3;
                            scoringMap.put(tileState, newScore);
                        }
                    }
                });

        //sumiranje svih vrijednosti bodova
        wordAndScore.addToScore(scoringMap.values().stream().mapToInt(Integer::intValue).sum());

        //provjera DW i TW za cijelu rijec i za cijelu rijec
        scoringMap.keySet().stream()
                .filter(tileState -> !tileState.isPermanentlyLaid())//premuim bodovi idu samo na NOVE plocie
                .forEach(tileState -> {
                    switch (GameConstants.BOARD_SCORING[tileState.getRow()][tileState.getCol()].getScoringFormulaId()){
                        case 3 -> { //DW  utjece na sumu bodova cijele nove rijeci
                            wordAndScore.addToScore(wordAndScore.getScore());// *2
                        }
                        case 4 -> { //TW
                            wordAndScore.addToScore(wordAndScore.getScore() + wordAndScore.getScore());// *3
                        }
                    }
                });


        return true;
    }

}
