package hr.java.scrabble.validations;

import hr.java.scrabble.game.WordAndScore;
import hr.java.scrabble.states.TileState;
import hr.java.scrabble.utils.BasicDialogUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static hr.java.scrabble.game.GameConstants.NUM_OF_GRIDS;

public class MoveValidation {

    private final HorizontalMoveValidation horizontalMoveValidation;
    private final VerticalMoveValidation verticalMoveValidation;
    private final SingleTileMoveValidation singleTileMoveValidation;

    public static final String WORD_VALIDATION = "Word validation";

    public MoveValidation(WordValidation wordValidation){
        this.horizontalMoveValidation = new HorizontalMoveValidation(wordValidation);
        this.verticalMoveValidation = new VerticalMoveValidation(wordValidation);
        this.singleTileMoveValidation = new SingleTileMoveValidation(wordValidation);
    }

    //ujedno zbraja bodove
    //predaje se referenca na WordScore objekt pa se u njega pohranjuju bodovi za dodanu rijec
    public boolean validateMoveAndAssignWordScore(List<TileState> tilesOnBoard, Integer moveCount, WordAndScore wordAndScore, boolean showDialog){
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

        //System.out.println(GameConstants.DEBUG_LINE);
        if(isHorizontalMove && newTilesOnBoard.size() > 1)
            isWordValidAndNotInterfering = horizontalMoveValidation.validateHorizontalMoveAndAssignWordScore(tilesOnBoard, newTilesOnBoard, wordAndScore);
        else if(isVerticalMove && newTilesOnBoard.size() > 1)
            isWordValidAndNotInterfering = verticalMoveValidation.validateVerticalMoveAndAssignWordScore(tilesOnBoard, newTilesOnBoard, wordAndScore);
        else if(isHorizontalMove && isVerticalMove && newTilesOnBoard.size() == 1)//kada je samo jedna plocica stavljena
            isWordValidAndNotInterfering = singleTileMoveValidation.validateSingleTileMoveAndAssignWordScore(tilesOnBoard, newTilesOnBoard.getFirst(), wordAndScore);

        //System.out.println(GameConstants.DEBUG_LINE);
        //System.out.println(GameConstants.DEBUG_LINE);
        //System.out.println("PRVI POTEZ:             " + ((isAnyTileInCenter && moveCount == 0) || moveCount > 0));
        //System.out.println("ORIJENTACIJA:           " + isMoveOrientationValid);
        //System.out.println("DIRA STARE PLOCICE:     " + isMoveTouchingOldTiles);
        //System.out.println("DOBRA I NE REMETI DRUGE:" + isWordValidAndNotInterfering);
        //System.out.println(GameConstants.DEBUG_LINE);

        if(showDialog && !((isAnyTileInCenter && moveCount == 0) || moveCount > 0))
            BasicDialogUtility.showDialog(WORD_VALIDATION, "First move must be placed in the center!");

        if(showDialog && !isMoveOrientationValid)
            BasicDialogUtility.showDialog(WORD_VALIDATION, "Tiles can only be placed horizontally or vertically!");

        if(showDialog && !isMoveTouchingOldTiles)
            BasicDialogUtility.showDialog(WORD_VALIDATION, "At least one tile has to touch the old tiles!");

        return ((isAnyTileInCenter && moveCount == 0) || moveCount > 0) //prvi potez mora ici preko centra
                && isMoveOrientationValid && isMoveTouchingOldTiles
                && isWordValidAndNotInterfering;
    }

    private boolean isAnyTileInCenter(List<TileState> newTilesOnBoard) {
        return newTilesOnBoard.stream()
                .anyMatch(tileState -> tileState.getRow().equals(7)
                    && tileState.getCol().equals(7));
    }

    private boolean isMoveTouchingOldTiles(List<TileState> oldTilesOnBoard, List<TileState> newTilesOnBoard) {
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

    private boolean isHorizontalMove(List<TileState> move){
        return move.stream()
                .map(TileState::getRow)
                .collect(Collectors.toSet())
                .size() == 1;
    }

    private boolean isVerticalMove(List<TileState> move){
        return move.stream()
                .map(TileState::getCol)
                .collect(Collectors.toSet())
                .size() == 1;
    }

    private Integer clamp(Integer number){
        return Math.min(Math.max(number, 0), NUM_OF_GRIDS - 1);
    }

}
