package hr.java.scrabble.utils;

import hr.java.scrabble.game.GameConstants;
import hr.java.scrabble.game.GameModeContext;
import hr.java.scrabble.handlers.GameHandler;
import hr.java.scrabble.networking.client.Client;
import hr.java.scrabble.networking.server.Server;
import hr.java.scrabble.states.GameplayHistory;
import hr.java.scrabble.word.LastWordReader;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.util.Duration;

public class GameModeContextUtility {

    private GameModeContextUtility(){}

    public static void setupGameModeContext(GameHandler gameHandler, GameModeContext gameModeContext) {
        if(gameModeContext == null)//vrati na inicijalno stanje
            setupInitial(gameHandler);

        if(GameModeContext.MULTIPLAYER_HOST_AND_CLIENT.equals(gameModeContext))
            setupHostAndClient(gameHandler);

        if(GameModeContext.MULTIPLAYER_CLIENT.equals(gameModeContext))
            setupClient(gameHandler);

        if(GameModeContext.SINGLEPLAYER.equals(gameModeContext))
            setupSingleplayer(gameHandler);

        if(GameModeContext.GAMEPLAY_REPLAY.equals(gameModeContext))
            setupGameplayReplay(gameHandler);

        if(GameModeContext.SINGLEPLAYER_GA.equals(gameModeContext))
            setupSingleplayerGA(gameHandler);
    }

    private static void setupGameplayReplay(GameHandler gameHandler) {
        clearStatesAndScreen(gameHandler);

        gameHandler.getScoreText().setText(GameConstants.SCORE + gameHandler.getPlayerState().getPlayerScore().toString());
        gameHandler.getPlayerActionsHandler().setSingleplayerActions();
        gameHandler.getPlayerActionsHandler().setDisableForAllActions(true);
    }

    private static void setupInitial(GameHandler gameHandler) {
        clearStatesAndScreen(gameHandler);

        gameHandler.getPlayerActionsHandler().removeAllActions();
        gameHandler.getMenuBarHandler().setMenuBarDefinition();
    }

    private static void setupSingleplayer(GameHandler gameHandler) {
        clearStatesAndScreen(gameHandler);

        gameHandler.getTileBagState().setTileBag(TileBagUtility.generateNewTileBag());
        gameHandler.getPlayerState().setPlayerBoardTiles(GameHandlerUtility.getRandomInitialPlayerTiles(gameHandler.getTileBagState()));

        gameHandler.putTilesFromPlayerStateToGrid();
        gameHandler.putTilesFromCenterGameStateToGrid();
        gameHandler.getPlayerActionsHandler().setSingleplayerActions();

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), keyFrame -> {
            Platform.runLater(new LastWordReader(gameHandler));
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.playFromStart();
    }

    private static void setupClient(GameHandler gameHandler) {
        if(gameHandler.getClient() == null || !gameHandler.getClient().isConnected()) {
            gameHandler.setClient(new Client(gameHandler));
            gameHandler.getPlayerActionsHandler().setInLobbyMultiplayerActions();
        }
        else
            DialogUtility.showDialog("Connection error", "You are already connected!");
    }

    private static void setupHostAndClient(GameHandler gameHandler) {
        gameHandler.setServer(new Server());
        Thread thread = new Thread(gameHandler.getServer());
        thread.start();

        //problem kod ponovnog postavljanja igraca kao host i klijent - treba malo pricekati da se server "pokrene"
        // Wait for the server to start before initializing the client
        int count = 0;
        while (!gameHandler.getServer().getServerRunning().get() && count < 10) {
            // Wait for the server to start
            try {
                System.out.println("PETLJA");
                Thread.sleep(100); // Wait for 100 milliseconds before checking again
                count++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(count == 10){//10 pokusaja
            Platform.runLater(() -> DialogUtility.showDialog("Server error", "Host is already assigned!"));
            gameHandler.setServer(null);
        }

        if(gameHandler.getServer() != null){//jedino ako je server dignut probaj sloziti klijenta
            gameHandler.setClient(new Client(gameHandler));
            if(!gameHandler.getClient().isConnected())
                gameHandler.setClient(null);
        }

        gameHandler.getPlayerActionsHandler().setInLobbyMultiplayerActions();
    }

    private static void clearStatesAndScreen(GameHandler gameHandler){
        gameHandler.getChatHandler().removeChatComponent();
        if(gameHandler.getServer() != null)
            gameHandler.getServer().stop();

        if(gameHandler.getClient() != null)
            gameHandler.getClient().stop();

        gameHandler.setClient(null);
        gameHandler.setServer(null);

        gameHandler.getPlayerState().reset();
        gameHandler.getCenterBoardState().reset();

        gameHandler.putTilesFromPlayerStateToGrid();
        gameHandler.putTilesFromCenterGameStateToGrid();

        gameHandler.setGameplayHistory(new GameplayHistory());

        gameHandler.getScoreText().setText(GameConstants.SCORE + 0);
        gameHandler.getLastWordText().setText("");
    }

    private static void setupSingleplayerGA(GameHandler gameHandler) {
        clearStatesAndScreen(gameHandler);

        gameHandler.getTileBagState().setTileBag(TileBagUtility.generateNewTileBag());
        gameHandler.getPlayerState().setPlayerBoardTiles(GameHandlerUtility.getRandomInitialPlayerTiles(gameHandler.getTileBagState()));
        gameHandler.getGaPlayerState().setPlayerBoardTiles(GameHandlerUtility.getRandomInitialPlayerTiles(gameHandler.getTileBagState()));//ga

        gameHandler.putTilesFromPlayerStateToGrid();
        gameHandler.putTilesFromCenterGameStateToGrid();
        gameHandler.getPlayerActionsHandler().setSingleplayerActions();
    }

}
