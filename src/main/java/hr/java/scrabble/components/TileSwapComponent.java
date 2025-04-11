package hr.java.scrabble.components;

import hr.java.scrabble.game.GameConstants;
import hr.java.scrabble.states.TileState;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import lombok.Getter;
import lombok.Setter;

import static hr.java.scrabble.game.GameConstants.SELECTED_TILE_STYLE;
import static hr.java.scrabble.game.GameConstants.TILE_STYLE;

@Setter
@Getter
public class TileSwapComponent extends HBox {
    private TileState tileState;
    private boolean needToSwap = false;

    public TileSwapComponent(TileState tileState) {
        super();
        this.tileState = tileState;

        Text letterText = new Text(tileState.getLetter());
        letterText.setTextAlignment(TextAlignment.CENTER);
        letterText.setFont(GameConstants.LETTER_FONT);

        if(tileState.getPoints() != null){
            Text pointsText = new Text(tileState.getPoints().toString());
            pointsText.setFont(GameConstants.SUBSCRIPT_FONT);
            pointsText.setTranslateY(5);
            super.getChildren().addAll(letterText, pointsText);
        }
        else
            super.getChildren().addAll(letterText);

        super.setAlignment(Pos.CENTER);
        super.setStyle(TILE_STYLE);
        super.toFront();

        super.setOnMouseClicked(mouseEvent -> {
            needToSwap = !needToSwap;
            if(needToSwap)
                super.setStyle(SELECTED_TILE_STYLE);
            else
                super.setStyle(TILE_STYLE);
        });
    }

}
