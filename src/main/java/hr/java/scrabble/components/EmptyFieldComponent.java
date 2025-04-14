package hr.java.scrabble.components;

import hr.java.scrabble.game.FieldEnum;
import hr.java.scrabble.game.GameConstants;
import hr.java.scrabble.utils.ColorUtility;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import static hr.java.scrabble.game.GameConstants.EMPTY_STRING;


public class EmptyFieldComponent extends VBox {


    public EmptyFieldComponent() {
        Label label = new Label(EMPTY_STRING);
        label.setFont(GameConstants.EMPTY_FIELD_LETTER_FONT);

        super.getChildren().add(label);
        super.setMouseTransparent(true);
        super.setAlignment(Pos.CENTER);
        super.setAlignment(Pos.CENTER);
    }

    public EmptyFieldComponent(FieldEnum fieldEnum) {
        Label label = new Label(fieldEnum.getLetters());
        label.setFont(GameConstants.EMPTY_FIELD_LETTER_FONT);

        super.getChildren().add(label);
        super.setMouseTransparent(true);
        super.setAlignment(Pos.CENTER);
        super.setAlignment(Pos.CENTER);

        super.setStyle(
                " -fx-background-color: " + ColorUtility.getHex(fieldEnum.getColor()) + "; " +
                        " -fx-border-color: " + ColorUtility.getHex(fieldEnum.getBorderColor()) + "; " +
                        " -fx-border-width: 1px; "
        );
    }

}
