package hr.java.scrabble.game;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import static hr.java.scrabble.game.FieldEnum.*;

public class GameConstants {

    private GameConstants() {}

    public static final Integer NUM_OF_GRIDS = 15;

    public static final Integer MENU_BAR_HEIGHT = 25;

    public static final Integer WHOLE_GRID_NUM_OF_ROWS = 18;
    public static final Integer WHOLE_GRID_NUM_OF_COLS = NUM_OF_GRIDS;

    public static final Integer PLAYER_TILE_GRID_DATA_ROW_INDEX = 16;
    public static final Integer PLAYER_TILE_GRID_DATA_COL_START_INDEX = 4;
    public static final Integer PLAYER_TILE_GRID_DATA_COL_END_INDEX = 10;
    public static final Integer MAX_NUM_OF_TILES_FOR_PLAYER = 7;

    public static final String EMPTY_STRING = "\u200E\u200E";
    public static final String SCORE = "    Score: ";

    public static final FieldEnum[][] BOARD_SCORING = {
            {TW, EMPTY, EMPTY, DL, EMPTY, EMPTY, EMPTY, TW, EMPTY, EMPTY, EMPTY, DL, EMPTY, EMPTY, TW},
            {EMPTY, DW, EMPTY, EMPTY, EMPTY, TL, EMPTY, EMPTY, EMPTY, TL, EMPTY, EMPTY, EMPTY, DW, EMPTY},
            {EMPTY, EMPTY, DW, EMPTY, EMPTY, EMPTY, DL, EMPTY, DL, EMPTY, EMPTY, EMPTY, DW, EMPTY, EMPTY},
            {DL, EMPTY, EMPTY, DW, EMPTY, EMPTY, EMPTY, DL, EMPTY, EMPTY, EMPTY, DW, EMPTY, EMPTY, DL},
            {EMPTY, EMPTY, EMPTY, EMPTY, DW, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, DW, EMPTY, EMPTY, EMPTY, EMPTY},
            {EMPTY, TL, EMPTY, EMPTY, EMPTY, TL, EMPTY, EMPTY, EMPTY, TL, EMPTY, EMPTY, EMPTY, TL, EMPTY},
            {EMPTY, EMPTY, DL, EMPTY, EMPTY, EMPTY, DL, EMPTY, DL, EMPTY, EMPTY, EMPTY, DL, EMPTY, EMPTY},
            {TW, EMPTY, EMPTY, DL, EMPTY, EMPTY, EMPTY, CENTER, EMPTY, EMPTY, EMPTY, DL, EMPTY, EMPTY, TW},
            {EMPTY, EMPTY, DL, EMPTY, EMPTY, EMPTY, DL, EMPTY, DL, EMPTY, EMPTY, EMPTY, DL, EMPTY, EMPTY},
            {EMPTY, TL, EMPTY, EMPTY, EMPTY, TL, EMPTY, EMPTY, EMPTY, TL, EMPTY, EMPTY, EMPTY, TL, EMPTY},
            {EMPTY, EMPTY, EMPTY, EMPTY, DW, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, DW, EMPTY, EMPTY, EMPTY, EMPTY},
            {DL, EMPTY, EMPTY, DW, EMPTY, EMPTY, EMPTY, DL, EMPTY, EMPTY, EMPTY, DW, EMPTY, EMPTY, DL},
            {EMPTY, EMPTY, DW, EMPTY, EMPTY, EMPTY, DL, EMPTY, DL, EMPTY, EMPTY, EMPTY, DW, EMPTY, EMPTY},
            {EMPTY, DW, EMPTY, EMPTY, EMPTY, TL, EMPTY, EMPTY, EMPTY, TL, EMPTY, EMPTY, EMPTY, DW, EMPTY},
            {TW, EMPTY, EMPTY, DL, EMPTY, EMPTY, EMPTY, TW, EMPTY, EMPTY, EMPTY, DL, EMPTY, EMPTY, TW},
            {TRANSPARENT, TRANSPARENT, TRANSPARENT, TRANSPARENT, TRANSPARENT, TRANSPARENT, TRANSPARENT, TRANSPARENT, TRANSPARENT, TRANSPARENT, TRANSPARENT, TRANSPARENT, TRANSPARENT, TRANSPARENT, TRANSPARENT},
            {TRANSPARENT, TRANSPARENT, TRANSPARENT, TRANSPARENT, TRANSPARENT, TRANSPARENT, TRANSPARENT, TRANSPARENT, TRANSPARENT, TRANSPARENT, TRANSPARENT, TRANSPARENT, TRANSPARENT, TRANSPARENT, TRANSPARENT},
            {TRANSPARENT, TRANSPARENT, TRANSPARENT, TRANSPARENT, TRANSPARENT, TRANSPARENT, TRANSPARENT, TRANSPARENT, TRANSPARENT, TRANSPARENT, TRANSPARENT, TRANSPARENT, TRANSPARENT, TRANSPARENT, TRANSPARENT}
    };

    public static final String TILE_STYLE = "-fx-background-color: #eacd95; -fx-background-radius: 5px; -fx-border-color: #c7aa73; -fx-border-width: 1px; -fx-border-radius: 5px;";
    public static final String SELECTED_TILE_STYLE = "-fx-background-color: #eacd95; -fx-background-radius: 5px; -fx-border-color: #FF0000; -fx-border-width: 1px; -fx-border-radius: 5px;";

    public static final Font LETTER_FONT = Font.font("Consolas", FontWeight.NORMAL, 14);
    public static final Font EMPTY_FIELD_LETTER_FONT = Font.font("Consolas", FontWeight.NORMAL, 17);
    public static final Font SUBSCRIPT_FONT = Font.font("Consolas", FontWeight.NORMAL, 9);
    public static final String DEBUG_LINE = "--------------------------------";

    /*public static final String API_KEY = "HeDN0IrWJ9iLtq1lhGyLvA==dxBkNE0aGO9zeE3z";
    public static final String API_URL = "https://api.api-ninjas.com/v1/dictionary?word=";
    public static final String X_API_KEY = "X-Api-Key";*/

    public static final String GAME_SAVE_PATH = "gamesave";
    public static final String CENTER_TILE_FILE = "/centerBoardState.dat";
    public static final String PLAYER_TILE_FILE = "/playerState.dat";
    public static final String TILE_BAG_FILE = "/tileBagState.dat";
    public static final String LAST_WORD_FILE = "lastword/lastword.dat";
    public static final String GAMEPLAY_HISTORY_FILE = "gameplayhistory/gameplayhistory.xml";


    public static final String DOCUMENTATION_PATH_AND_FILE = "documentation/documentation.html";
    public static final String JAVA_FILE_PATH = "src/main/java";

    public static final int GA_MAX_ATTEMPTS = 5;

}
