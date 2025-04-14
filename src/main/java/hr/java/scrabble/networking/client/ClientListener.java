package hr.java.scrabble.networking.client;

import hr.java.scrabble.dto.*;
import hr.java.scrabble.handlers.GameHandler;
import hr.java.scrabble.states.CenterBoardState;
import hr.java.scrabble.states.TileBagState;
import hr.java.scrabble.utils.DialogUtility;
import hr.java.scrabble.utils.GameHandlerUtility;
import javafx.application.Platform;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;

public class ClientListener implements Runnable {
    private final GameHandler gameHandler;
    private final Socket clientSocket;
    private final ObjectInputStream objectInputStream;
    private boolean listening;

    public ClientListener(GameHandler gameHandler, Socket clientSocket, ObjectInputStream objectInputStream) {
        this.gameHandler = gameHandler;
        this.clientSocket = clientSocket;
        this.objectInputStream = objectInputStream;
        this.listening = true;
    }

    @Override
    public void run() {
        try {
            while (listening) {
                Object receivedObject = objectInputStream.readObject();

                if (receivedObject instanceof MessageDTO messageDTO)
                    Platform.runLater(() -> DialogUtility.showDialog("Server message", messageDTO.message()));

                if (receivedObject instanceof InGameMultiplayerActionsDTO){
                    Platform.runLater(() -> gameHandler.getPlayerActionsHandler().setInGameMultiplayerActions());
                }

                if (receivedObject instanceof InitialTilesDTO initialTilesDTO) {
                    gameHandler.getPlayerState().setPlayerBoardTiles(initialTilesDTO.initialTiles());
                    Platform.runLater(() -> gameHandler.putTilesFromPlayerStateToGrid());
                }

                if(receivedObject instanceof YourTurnDTO yourTurnDTO) {
                    Platform.runLater(() -> {
                        gameHandler.getPlayerActionsHandler().setDisableForAllActions(!yourTurnDTO.isYourTurn());
                        GameHandlerUtility.setDraggableForAllPlayerTiles(gameHandler, !yourTurnDTO.isYourTurn());
                    });
                }

                if (receivedObject instanceof CenterBoardState centerBoardStateIn) {
                    gameHandler.getCenterBoardState().setCenterBoardTiles(centerBoardStateIn.getCenterBoardTiles());
                    gameHandler.getCenterBoardState().setMoveCount(centerBoardStateIn.getMoveCount());
                    Platform.runLater(() -> {
                        gameHandler.putTilesFromCenterGameStateToGrid();
                        gameHandler.putTilesFromPlayerStateToGrid();
                    });
                }

                if (receivedObject instanceof TileBagState tileBagStateIn) {
                    gameHandler.getTileBagState().setTileBag(tileBagStateIn.getTileBag());
                }

                if (receivedObject instanceof WinnerAnnouncementDTO winnerAnnouncementDTO) {
                    Platform.runLater(() -> DialogUtility.showDialog(
                            "Game end",
                            "Player " + winnerAnnouncementDTO.playerName() + " has won the game!")
                    );
                }

                if(receivedObject instanceof LeaveGameDTO){
                    Platform.runLater(() -> gameHandler.setGameContext(null));
                }

            }
        }catch(EOFException e){
            stopListening();
            System.err.println("Client socket closed");
            try {
                clientSocket.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            Platform.runLater(() -> gameHandler.getPlayerActionsHandler().removeAllActions());
        }catch (SocketException e) {
            System.err.println("Client socket closed");
            stopListening();
            try {
                clientSocket.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            Platform.runLater(() -> gameHandler.getPlayerActionsHandler().removeAllActions());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("CLIENT STOPPED LISTENING");
    }

    public void stopListening() {
        listening = false;
        try {
            clientSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
