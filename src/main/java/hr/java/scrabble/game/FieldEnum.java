package hr.java.scrabble.game;

import javafx.scene.paint.Color;

import static hr.java.scrabble.game.GameConstants.EMPTY_STRING;

public enum FieldEnum {

    EMPTY(0, ColorConstants.DEFAULT_COLOR, ColorConstants.BORDER_COLOR, EMPTY_STRING, 0),
    DL(1, ColorConstants.DOUBLE_LETTER_COLOR, ColorConstants.BORDER_COLOR, "DL", 1),
    TL(2, ColorConstants.TRIPLE_LETTER_COLOR, ColorConstants.BORDER_COLOR, "TL", 2),
    DW(3, ColorConstants.DOUBLE_WORD_COLOR, ColorConstants.BORDER_COLOR, "DW", 3),
    TW(4, ColorConstants.TRIPLE_WORD_COLOR, ColorConstants.BORDER_COLOR, "TW", 4),
    CENTER(5, ColorConstants.CENTER_COLOR, ColorConstants.BORDER_COLOR, "★", 3),
    TRANSPARENT(6, ColorConstants.TRANSPARENT, ColorConstants.TRANSPARENT, EMPTY_STRING, 0);

    private final Integer id;
    private final Color color;
    private final Color borderColor;
    private final String letters;
    private final Integer scoringFormulaId;

    FieldEnum(Integer id, Color color, Color borderColor, String letters, Integer scoringFormulaId) {
        this.id = id;
        this.color = color;
        this.borderColor = borderColor;
        this.letters = letters;
        this.scoringFormulaId = scoringFormulaId;
    }

    public Integer getId() {
        return id;
    }

    public Color getColor() {
        return color;
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public String getLetters() {
        return letters;
    }

    public Integer getScoringFormulaId() {
        return scoringFormulaId;
    }

}
