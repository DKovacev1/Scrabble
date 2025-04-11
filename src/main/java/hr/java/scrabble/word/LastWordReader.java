package hr.java.scrabble.word;

import hr.java.scrabble.game.GameModeContext;
import hr.java.scrabble.handlers.GameHandler;

public class LastWordReader extends WordThread implements Runnable{

    private final GameHandler gameHandler;

    public LastWordReader(GameHandler gameHandler) {
        this.gameHandler = gameHandler;
    }

    @Override
    public void run() {
        if(GameModeContext.SINGLEPLAYER.equals(gameHandler.getGameModeContext())){
            gameHandler.getLastWordText().setText("Last word: " + getLastWordFromFile());
        }
        else{
            gameHandler.getLastWordText().setText("");
        }
    }

}
