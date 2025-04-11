package hr.java.scrabble.validations;

import hr.java.scrabble.components.TileComponent;
import hr.java.scrabble.game.GameConstants;
import hr.java.scrabble.game.WordAndScore;
import hr.java.scrabble.states.TileState;
import hr.java.scrabble.utilities.DialogUtility;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static hr.java.scrabble.game.GameConstants.NUM_OF_GRIDS;

public class MoveValidation {

    private MoveValidation(){}

    //ujedno zbraja bodove
    //predaje se referenca na WordScore objekt pa se u njega pohranjuju bodovi za dodanu rijec
    public static boolean validateMoveAndAssignWordScore(GridPane centerBoardGrid, Integer moveCount, WordAndScore wordAndScore){
                List<TileState> tilesOnBoard = centerBoardGrid.getChildren().stream()
                .filter(TileComponent.class::isInstance)
                .filter(node -> GridPane.getRowIndex(node) < NUM_OF_GRIDS
                        && GridPane.getColumnIndex(node) < NUM_OF_GRIDS)
                .map(node -> {
                    TileState tileState = ((TileComponent)node).getTileState().getCopy();
                    tileState.setRow(GridPane.getRowIndex(node));
                    tileState.setCol(GridPane.getColumnIndex(node));
                    return tileState;
                })
                .toList();

        List<TileState> oldTilesOnBoard = tilesOnBoard.stream()
                .filter(TileState::isPermanentlyLaid)
                .toList();

        List<TileState> newTilesOnBoard = new ArrayList<>(tilesOnBoard.stream()
                .filter(tileState -> !tileState.isPermanentlyLaid())
                .toList());

        boolean isHorizontalMove = isHorizontalMove(newTilesOnBoard);
        boolean isVerticalMove = isVerticalMove(newTilesOnBoard);
        boolean isMoveOrientationValid = (isHorizontalMove || isVerticalMove) || newTilesOnBoard.size() == 1;
        boolean isMoveTouchingOldTiles = isMoveTouchingOldTiles(oldTilesOnBoard, newTilesOnBoard);
        if(moveCount == 0)
            isMoveTouchingOldTiles = true;//za prvi potez nije bitno

        boolean isAnyTileInCenter = isAnyTileInCenter(newTilesOnBoard);
        boolean isWordValidAndNotInterfering = false;

        System.out.println(GameConstants.DEBUG_LINE);
        if(isHorizontalMove && newTilesOnBoard.size() > 1)
            isWordValidAndNotInterfering = HorizontalMoveValidation.validateHorizontalMoveAndAssignWordScore(tilesOnBoard, newTilesOnBoard, wordAndScore);
        else if(isVerticalMove && newTilesOnBoard.size() > 1)
            isWordValidAndNotInterfering = VerticalMoveValidation.validateVerticalMoveAndAssignWordScore(tilesOnBoard, newTilesOnBoard, wordAndScore);
        else if(isHorizontalMove && isVerticalMove && newTilesOnBoard.size() == 1)//kada je samo jedna plocica stavljena
            isWordValidAndNotInterfering = SingleTileMoveValidation.validateSingleTileMoveAndAssignWordScore(tilesOnBoard, newTilesOnBoard.getFirst(), wordAndScore);


        System.out.println(GameConstants.DEBUG_LINE);
        System.out.println(GameConstants.DEBUG_LINE);
        System.out.println("PRVI POTEZ:             " + ((isAnyTileInCenter && moveCount == 0) || moveCount > 0));
        System.out.println("ORIJENTACIJA:           " + isMoveOrientationValid);
        System.out.println("DIRA STARE PLOCICE:     " + isMoveTouchingOldTiles);
        System.out.println("DOBRA I NE REMETI DRUGE:" + isWordValidAndNotInterfering);
        System.out.println(GameConstants.DEBUG_LINE);

        if(!((isAnyTileInCenter && moveCount == 0) || moveCount > 0))
            DialogUtility.showDialog("Word validation", "First move must be placed in the center!");

        if(!isMoveOrientationValid)
            DialogUtility.showDialog("Word validation", "Tiles can only be placed horizontally or vertically!");

        if(!isMoveTouchingOldTiles)
            DialogUtility.showDialog("Word validation", "At least one tile has to touch the old tiles!");

        return ((isAnyTileInCenter && moveCount == 0) || moveCount > 0) //prvi potez mora ici preko centra
                && isMoveOrientationValid && isMoveTouchingOldTiles
                && isWordValidAndNotInterfering;
    }

    private static boolean isAnyTileInCenter(List<TileState> newTilesOnBoard) {
        return newTilesOnBoard.stream()
                .anyMatch(tileState -> tileState.getRow().equals(7)
                    && tileState.getCol().equals(7));
    }

    private static boolean isMoveTouchingOldTiles(List<TileState> oldTilesOnBoard, List<TileState> newTilesOnBoard) {
        return newTilesOnBoard.parallelStream().anyMatch(newTileState -> {
            Integer aboveRow = clamp(newTileState.getRow() - 1);
            Integer belowRow = clamp(newTileState.getRow() + 1);
            Integer leftCol = clamp(newTileState.getCol() - 1);
            Integer rightCol = clamp(newTileState.getCol() + 1);

            boolean abovePresent = oldTilesOnBoard.parallelStream()
                    .anyMatch(tileState -> tileState.getRow().equals(aboveRow) && tileState.getCol().equals(newTileState.getCol()));

            boolean belowPresent = oldTilesOnBoard.parallelStream()
                    .anyMatch(tileState -> tileState.getRow().equals(belowRow) && tileState.getCol().equals(newTileState.getCol()));

            boolean leftPresent = oldTilesOnBoard.parallelStream()
                    .anyMatch(tileState -> tileState.getRow().equals(newTileState.getRow()) && tileState.getCol().equals(leftCol));

            boolean rightPresent = oldTilesOnBoard.parallelStream()
                    .anyMatch(tileState -> tileState.getRow().equals(newTileState.getRow()) && tileState.getCol().equals(rightCol));

            return abovePresent || belowPresent || leftPresent || rightPresent;
        });
    }

    private static boolean isHorizontalMove(List<TileState> move){
        return move.stream()
                .map(TileState::getRow)
                .collect(Collectors.toSet())
                .size() == 1;
    }

    private static boolean isVerticalMove(List<TileState> move){
        return move.stream()
                .map(TileState::getCol)
                .collect(Collectors.toSet())
                .size() == 1;
    }

    private static Integer clamp(Integer number){
        return Math.min(Math.max(number, 0), NUM_OF_GRIDS - 1);
    }

}
