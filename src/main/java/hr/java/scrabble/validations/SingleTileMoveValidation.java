package hr.java.scrabble.validations;

import hr.java.scrabble.game.GameConstants;
import hr.java.scrabble.game.WordAndScore;
import hr.java.scrabble.states.TileState;
import hr.java.scrabble.utils.TilesUtility;

import java.util.*;
import java.util.stream.Collectors;

public class SingleTileMoveValidation {

    private SingleTileMoveValidation(){}

    //treba gledati samo cijeli redak i stupac unutar kojih se nalazi ta nova plocica
    //malo drugacija pravila u odnosu na to kada su dvije ili vise plocice pa tako cine rijec
    public static boolean validateSingleTileMoveAndAssignWordScore(List<TileState> tilesOnBoard, TileState newTileOnBoard, WordAndScore wordAndScore) {
        boolean rowWordFlag = false;
        boolean colWordFlag = false;

        Map<TileState, Integer> scoringMap = new HashMap<>();

        Optional<TileState> mostLeftTileState = TilesUtility.getMostLeftTileState(newTileOnBoard, tilesOnBoard);
        Optional<TileState> mostRightTileState = TilesUtility.getMostRightTileState(newTileOnBoard, tilesOnBoard);

        //spoji, vidi jel rijec ispravna
        if (mostLeftTileState.isPresent() && mostRightTileState.isPresent()) {
            rowWordFlag = true;
            TileState finalMostLeftTileState = mostLeftTileState.get();
            TileState finalMostRightTileState = mostRightTileState.get();

            List<TileState> rowTileStates = tilesOnBoard.stream()
                    .filter(tileState -> tileState.getRow().equals(newTileOnBoard.getRow())
                            && tileState.getCol() >= finalMostLeftTileState.getCol()
                            && tileState.getCol() <= finalMostRightTileState.getCol())
                    .sorted(Comparator.comparing(TileState::getCol))
                    .toList();

            rowTileStates.forEach(tileState -> scoringMap.put(tileState, tileState.getPoints()));//bodovanje ide na kompletnu novu rijec, ne samo slova koja su dodana


            String rowWord = rowTileStates.stream()
                    .map(TileState::getLetter)
                    .collect(Collectors.joining());

            System.out.println("rowWord: " + rowWord);
            if (!WordValidating.isWordValid(rowWord))
                return false;//rijec se ne nalazi u rijecniku

            wordAndScore.setWord("Row word: " + rowWord + "\n");

            rowTileStates.forEach(tileState -> scoringMap.put(tileState, tileState.getPoints()));//bodovanje ide na kompletnu novu rijec, ne samo slova koja su dodana
        }


        Optional<TileState> mostTopTileState = TilesUtility.getMostTopTileState(newTileOnBoard, tilesOnBoard);
        Optional<TileState> mostBottomTileState = TilesUtility.getMostBottomTileState(newTileOnBoard, tilesOnBoard);

        //spoji, vidi jel rijec ispravna
        if (mostTopTileState.isPresent() && mostBottomTileState.isPresent()) {
            colWordFlag = true;
            TileState finalMostTopTileState = mostTopTileState.get();
            TileState finalMostBottomTileState = mostBottomTileState.get();

            List<TileState> columnTileStates = tilesOnBoard.stream()
                    .filter(tileState -> tileState.getCol().equals(newTileOnBoard.getCol())
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

            wordAndScore.setWord( wordAndScore.getWord() + "Column word: " + columnWord + "\n");

            columnTileStates.forEach(tileState -> scoringMap.put(tileState, tileState.getPoints()));//bodovanje ide na kompletnu novu rijec, ne samo slova koja su dodana
        }


        if (rowWordFlag && colWordFlag) {
            scoringMap.put(newTileOnBoard, newTileOnBoard.getPoints() + newTileOnBoard.getPoints());
        }

        //do ovdje imamo vrijednosti za pojedinu plocicu
        //provjera DL i TL za plocicu

        switch (GameConstants.BOARD_SCORING[newTileOnBoard.getRow()][newTileOnBoard.getCol()].getScoringFormulaId()) {
            case 1 -> { //DL  utjece samo na pojedicu plocicu
                int oldScore = scoringMap.get(newTileOnBoard);
                int newScore = oldScore * 2;
                scoringMap.put(newTileOnBoard, newScore);
            }
            case 2 -> { //TL
                int oldScore = scoringMap.get(newTileOnBoard);
                int newScore = oldScore * 3;
                scoringMap.put(newTileOnBoard, newScore);
            }
        }

        //sumiranje svih vrijednosti bodova
        wordAndScore.addToScore(scoringMap.values().stream().mapToInt(Integer::intValue).sum());

        //provjera DW i TW za plocicu
        switch (GameConstants.BOARD_SCORING[newTileOnBoard.getRow()][newTileOnBoard.getCol()].getScoringFormulaId()) {
            case 3 -> { //DW  utjece na sumu bodova cijele nove rijeci
                wordAndScore.addToScore(wordAndScore.getScore());// *2
            }
            case 4 -> { //TW
                wordAndScore.addToScore(wordAndScore.getScore() + wordAndScore.getScore());// *3
            }
        }

        return true;
    }

}
