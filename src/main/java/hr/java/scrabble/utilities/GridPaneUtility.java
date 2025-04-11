package hr.java.scrabble.utilities;

import hr.java.scrabble.components.TileComponent;
import hr.java.scrabble.game.GameConstants;
import hr.java.scrabble.handlers.GameHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.util.List;
import java.util.Optional;

import static hr.java.scrabble.game.GameConstants.*;

public class GridPaneUtility {

    private GridPaneUtility(){}

    public static void setGridConstrainst(GridPane gridPane, Integer numOfRows, Integer numOfColumns) {
        //podesavanje constraintsa za sve retke i stupce
        // Set row constraints to ensure all rows fill the available vertical space
        for (int i = 0; i < numOfRows; i++) {
            RowConstraints row = new RowConstraints();
            row.setVgrow(Priority.ALWAYS);
            gridPane.getRowConstraints().add(row);
        }

        // Set column constraints to ensure all columns fill the available horizontal space
        for (int i = 0; i < numOfColumns; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setHgrow(Priority.ALWAYS);
            gridPane.getColumnConstraints().add(column);
        }
    }

    //za dohvatiti redak treba provjeravati visine pojedinih redaka (node.heightProperty)
    public static Optional<Integer> getGridRow(MouseEvent mouseEvent, GameHandler gameHandler) {
        List<Integer> heightsOfRows = gameHandler.getCenterBoardGrid().getChildren().stream()
                .filter(node -> GridPane.getColumnIndex(node) == 0)
                .map(node -> ((Region)node).heightProperty().intValue())
                .toList();

        int startY = gameHandler.getOffsetGrid().heightProperty().intValue() + GameConstants.MENU_BAR_HEIGHT;
        int mouseInGridY = (int) mouseEvent.getSceneY() - startY;

        return getPosition(heightsOfRows, mouseInGridY, WHOLE_GRID_NUM_OF_ROWS);
    }

    //za dohvatiti stupac treba provjeravati sirine pojedinih stupaca (node.widthProperty)
    public static Optional<Integer> getGridCol(MouseEvent mouseEvent, GameHandler gameHandler) {
        List<Integer> widthsOfCols = gameHandler.getCenterBoardGrid().getChildren().stream()
                .filter(node -> GridPane.getRowIndex(node) == 0)
                .map(node -> ((Region)node).widthProperty().intValue())
                .toList();

        int startX = gameHandler.getOffsetGrid().widthProperty().intValue();
        int mouseInGridX = (int) mouseEvent.getSceneX() - startX;

        return getPosition(widthsOfCols, mouseInGridX, WHOLE_GRID_NUM_OF_COLS);
    }

    public static Optional<Integer> getPosition(List<Integer> dimensions, int mousePosition, int numOfSeparations){
        int sum = 0;
        Integer postition = null;
        for(int i = 0; i < dimensions.size(); i++){
            if(sum < mousePosition){
                postition = i;
                sum += dimensions.get(i);
            }
            else
                break;
        }

        if(mousePosition > sum || (postition != null && postition > numOfSeparations - 1))
            return Optional.empty();

        return Optional.ofNullable(postition);
    }

    public static void handleDrop(MouseEvent mouseEvent, TileComponent tileComponent){
        //postavi plocicu na centralni grid
        Optional<Integer> newCentralRow = getGridRow(mouseEvent, tileComponent.getGameHandler());
        Optional<Integer> newCentralCol = getGridCol(mouseEvent, tileComponent.getGameHandler());

        if (!tileComponent.getTileState().isPermanentlyLaid() && newCentralRow.isPresent() && newCentralCol.isPresent()
                && isInPlayableArea(newCentralRow.get(), newCentralCol.get())
                && (!tileComponent.getGameHandler().tileExistsInWholeGrid(newCentralRow.get(), newCentralCol.get())
                || (tileComponent.getGameHandler().tileExistsInWholeGrid(newCentralRow.get(), newCentralCol.get()) && newCentralRow.get().equals(GridPane.getRowIndex(tileComponent)) && newCentralCol.get().equals(GridPane.getColumnIndex(tileComponent)))
        )
        ) {
            //dodaj na novo mjesto u centralnom gridu
            tileComponent.getGameHandler().getCenterBoardGrid().getChildren().remove(tileComponent);
            tileComponent.getGameHandler().getCenterBoardGrid().add(tileComponent.getCopy(), newCentralCol.get(), newCentralRow.get());
        }

    }

    private static boolean isInPlayableArea(int row, int col) {
        return (row >= 0 && row < NUM_OF_GRIDS && col >= 0 && col < NUM_OF_GRIDS )
                || (row == PLAYER_TILE_GRID_DATA_ROW_INDEX && col >= PLAYER_TILE_GRID_DATA_COL_START_INDEX && col <= PLAYER_TILE_GRID_DATA_COL_END_INDEX);
    }
}
