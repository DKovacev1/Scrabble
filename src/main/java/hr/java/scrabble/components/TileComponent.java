package hr.java.scrabble.components;

import hr.java.scrabble.game.GameConstants;
import hr.java.scrabble.handlers.GameHandler;
import hr.java.scrabble.states.TileState;
import hr.java.scrabble.utilities.GameHandlerUtility;
import hr.java.scrabble.utilities.GridPaneUtility;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import static hr.java.scrabble.game.GameConstants.TILE_STYLE;

public class TileComponent extends HBox implements Draggable{

    private TileState tileState;
    private final GameHandler gameHandler;

    private double orgSceneX, orgSceneY;
    private double orgTranslateX, orgTranslateY;

    public TileComponent(GameHandler gameHandler, TileState tileState) {
        super();
        this.gameHandler = gameHandler;
        this.tileState = tileState;

        Text letterText = new Text(tileState.getLetter());
        letterText.setTextAlignment(TextAlignment.CENTER);
        letterText.setFont(GameConstants.LETTER_FONT);

        if(tileState.getPoints() != null){
            String points = tileState.getPoints().toString();
            Text pointsText = new Text(points.length() == 1 ? points + " " : points);
            pointsText.setFont(GameConstants.SUBSCRIPT_FONT);
            pointsText.setTranslateY(5);

            super.getChildren().addAll(letterText, pointsText);
        }
        else
            super.getChildren().addAll(letterText);

        super.setAlignment(Pos.CENTER);
        super.setStyle(TILE_STYLE);
        super.toFront();

        if(!tileState.isPermanentlyLaid()) {
            makeDraggable();
            super.setOnMouseReleased(mouseEvent -> GridPaneUtility.handleDrop(mouseEvent,this));
        }
        else{
            removeDraggable();
        }
    }

    private boolean tileExists(Integer row, Integer col) {
        return gameHandler.tileExistsInWholeGrid(row, col);
    }

    public TileState getTileState() {
        return tileState;
    }

    public void setTileState(TileState tileState) {
        this.tileState = tileState;
    }

    public TileComponent getCopy(){
        return new TileComponent(this.gameHandler, tileState);
    }

    public GameHandler getGameHandler() {
        return gameHandler;
    }


    @Override
    public void makeDraggable() {
        this.setOnMousePressed(this::onMousePressed);
        this.setOnMouseDragged(this::onMouseDragged);
    }

    @Override
    public void removeDraggable() {
        this.setOnMousePressed(null);
        this.setOnMouseDragged(null);
    }

    private void onMousePressed(MouseEvent event) {
        orgSceneX = event.getSceneX();
        orgSceneY = event.getSceneY();
        orgTranslateX = ((Node) event.getSource()).getTranslateX();
        orgTranslateY = ((Node) event.getSource()).getTranslateY();

        ((Node) event.getSource()).toFront();
    }

    private void onMouseDragged(MouseEvent event) {
        double offsetX = event.getSceneX() - orgSceneX;
        double offsetY = event.getSceneY() - orgSceneY;
        double newTranslateX = orgTranslateX + offsetX;
        double newTranslateY = orgTranslateY + offsetY;

        ((Node) event.getSource()).setTranslateX(newTranslateX);
        ((Node) event.getSource()).setTranslateY(newTranslateY);

        ((Node) event.getSource()).toFront();
    }

}
