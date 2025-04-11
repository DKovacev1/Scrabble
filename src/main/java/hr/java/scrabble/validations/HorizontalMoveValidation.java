package hr.java.scrabble.validations;

import hr.java.scrabble.game.GameConstants;
import hr.java.scrabble.game.WordAndScore;
import hr.java.scrabble.states.TileState;
import hr.java.scrabble.utilities.TilesUtility;

import java.util.*;
import java.util.stream.Collectors;

public class HorizontalMoveValidation {

    private HorizontalMoveValidation(){}

    //boduju se samo plocice u retku, za stupce se radi jedino validacija
    public static boolean validateHorizontalMoveAndAssignWordScore(List<TileState> tilesOnBoard, List<TileState> newTilesOnBoard, WordAndScore wordAndScore) {

        newTilesOnBoard.sort(Comparator.comparing(TileState::getCol));
        Integer moveRow = newTilesOnBoard.getFirst().getRow();

        TileState mostLeftNewTileState = newTilesOnBoard.getFirst();
        TileState mostRightNewTileState = newTilesOnBoard.getLast();

        Optional<TileState> mostLeftOldTileState = TilesUtility.getMostLeftTileState(mostLeftNewTileState, tilesOnBoard);
        Optional<TileState> mostRightOldTileState = TilesUtility.getMostRightTileState(mostRightNewTileState, tilesOnBoard);

        //ako ih ima, postavi ih kao rubne plocice
        TileState mostLeftTileState = mostLeftOldTileState.orElse(mostLeftNewTileState);
        TileState mostRightTileState = mostRightOldTileState.orElse(mostRightNewTileState);

        List<TileState> wholeWordTileStates = tilesOnBoard.stream()
                .filter(tileState -> tileState.getRow().equals(moveRow)
                        && tileState.getCol() >= mostLeftTileState.getCol()
                        && tileState.getCol() <= mostRightTileState.getCol())
                .sorted(Comparator.comparing(TileState::getCol))
                .toList();

        //provjera je li sve spojeno
        if ((mostRightTileState.getCol() - mostLeftTileState.getCol() + 1) != wholeWordTileStates.size())
            return false;//odvojene su

        //provjera rijeci za redak horizontalno
        String word = wholeWordTileStates.stream()
                .map(TileState::getLetter)
                .collect(Collectors.joining());

        System.out.println("horizontal word: " + word);
        if (!WordValidating.isWordValid(word))
            return false;//rijec se ne nalazi u rijecniku

        wordAndScore.setWord(word);

        //prvo dodaj bodove za sva nova slova u horizontali
        Map<TileState, Integer> scoringMap = new HashMap<>();
        wholeWordTileStates.forEach(tileState -> scoringMap.put(tileState, tileState.getPoints()));//bodovanje ide na kompletnu novu rijec, ne samo slova koja su dodana

        //za svaku plocicu u retku probaj otici maksimalno gore i dolje, probaj formirati rijec te ju provjeri preko api-a
        for (TileState tileStateInRow : wholeWordTileStates) {
            int col = tileStateInRow.getCol();

            Optional<TileState> mostTopTileState = TilesUtility.getMostTopTileState(tileStateInRow, tilesOnBoard);
            Optional<TileState> mostBottomTileState = TilesUtility.getMostBottomTileState(tileStateInRow, tilesOnBoard);

            //spoji, vidi jel rijec ispravna
            if (mostTopTileState.isPresent() && mostBottomTileState.isPresent()) {
                TileState finalMostTopTileState = mostTopTileState.get();
                TileState finalMostBottomTileState = mostBottomTileState.get();

                List<TileState> columnTileStates = tilesOnBoard.stream()
                        .filter(tileState -> tileState.getCol().equals(col)
                                && tileState.getRow() >= finalMostTopTileState.getRow()
                                && tileState.getRow() <= finalMostBottomTileState.getRow())
                        .sorted(Comparator.comparing(TileState::getRow))
                        .toList();

                String columnWord = columnTileStates.stream()
                        .map(TileState::getLetter)
                        .collect(Collectors.joining());

                System.out.println("columnWord: " + columnWord);
                if (!WordValidating.isWordValid(columnWord))
                    return false;//rijec se ne nalazi u rijecniku

                columnTileStates.forEach(tileState -> scoringMap.put(tileState, tileState.getPoints()));//bodovanje ide na kompletnu novu rijec, ne samo slova koja su dodana

                //dodavanje bodova za slova koja se ponavljaju u rijecima za stupce
                if (newTilesOnBoard.contains(tileStateInRow)
                        && columnTileStates.contains(tileStateInRow)
                        && columnTileStates.size() > 1) {

                    int oldScore = scoringMap.get(tileStateInRow);
                    int tileScore = tileStateInRow.getPoints();
                    int newScore = oldScore + tileScore;
                    scoringMap.put(tileStateInRow, newScore);
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
