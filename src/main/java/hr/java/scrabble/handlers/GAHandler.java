package hr.java.scrabble.handlers;

import hr.java.scrabble.ga.GAThread;

public class GAHandler {

    private final GameHandler gameHandler;
    private GADialogHandler gaDialogHandler;

    public GAHandler(GameHandler gameHandler) {
        this.gameHandler = gameHandler;
        this.gaDialogHandler = new GADialogHandler();
    }

    public void handleGAEvolution() {
        this.gaDialogHandler = new GADialogHandler();
        GAThread gaThread = new GAThread(gameHandler, gaDialogHandler);
        gaThread.start();
    }

}
