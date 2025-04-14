package hr.java.scrabble.utils;

import hr.java.scrabble.game.GameConstants;
import hr.java.scrabble.game.GameModeContext;
import hr.java.scrabble.handlers.GameHandler;
import hr.java.scrabble.states.GameplayHistory;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.util.concurrent.atomic.AtomicInteger;

public class GameplayHistoryUtility {

    private GameplayHistoryUtility(){}

    public static void replayGameplayHistory(GameHandler gameHandler) {
        gameHandler.setGameContext(GameModeContext.GAMEPLAY_REPLAY);

        GameplayHistory gameplayHistory = XMLFileReadUtility.loadGameplayHistory();
        AtomicInteger i = new AtomicInteger(0);

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), keyFrame -> {
            if (!gameplayHistory.getPlayerStateList().isEmpty()) {
                gameHandler.getPlayerState().setPlayerScore(gameplayHistory.getPlayerStateList().get(i.get()).getPlayerScore());
                gameHandler.getPlayerState().setPlayerBoardTiles(gameplayHistory.getPlayerStateList().get(i.get()).getPlayerBoardTiles());

                gameHandler.getCenterBoardState().setMoveCount(gameplayHistory.getCenterBoardStateList().get(i.get()).getMoveCount());
                gameHandler.getCenterBoardState().setCenterBoardTiles(gameplayHistory.getCenterBoardStateList().get(i.get()).getCenterBoardTiles());

                gameHandler.putTilesFromPlayerStateToGrid();
                gameHandler.putTilesFromCenterGameStateToGrid();
                gameHandler.getScoreText().setText(GameConstants.SCORE + gameHandler.getPlayerState().getPlayerScore());

                i.set(i.get() + 1);
            }
        }), new KeyFrame(Duration.seconds(2).add(Duration.seconds(2)), event -> {
            if (i.get() >= gameplayHistory.getPlayerStateList().size()) { //prerano bi prekinuo
                gameHandler.setGameContext(null); // vracanje na inicijalno stanje nakon 2s
            }
        }));

        timeline.setCycleCount(!gameplayHistory.getPlayerStateList().isEmpty() ? gameplayHistory.getPlayerStateList().size() : 0);
        timeline.playFromStart();
    }

}
