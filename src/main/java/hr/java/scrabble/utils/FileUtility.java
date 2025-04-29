package hr.java.scrabble.utils;

import hr.java.scrabble.game.GameConstants;
import hr.java.scrabble.states.CenterBoardState;
import hr.java.scrabble.states.PlayerState;
import hr.java.scrabble.states.TileBagState;

import java.io.*;

public class FileUtility {

    private FileUtility(){}

    public static void saveGame(CenterBoardState centerBoardState, PlayerState playerState, TileBagState tileBagState) {
        try{
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(GameConstants.GAME_SAVE_PATH + GameConstants.CENTER_TILE_FILE));
            objectOutputStream.writeObject(centerBoardState);

            objectOutputStream = new ObjectOutputStream(new FileOutputStream(GameConstants.GAME_SAVE_PATH + GameConstants.PLAYER_TILE_FILE));
            objectOutputStream.writeObject(playerState);

            objectOutputStream = new ObjectOutputStream(new FileOutputStream(GameConstants.GAME_SAVE_PATH + GameConstants.TILE_BAG_FILE));
            objectOutputStream.writeObject(tileBagState);

            objectOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void loadGame(CenterBoardState centerBoardState, PlayerState playerState, TileBagState tileBagState) {

        try{
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(GameConstants.GAME_SAVE_PATH + GameConstants.CENTER_TILE_FILE));
            CenterBoardState loadedCenterBoardState = (CenterBoardState) objectInputStream.readObject();
            centerBoardState.setCenterBoardTiles(loadedCenterBoardState.getCenterBoardTiles());
            centerBoardState.setMoveCount(loadedCenterBoardState.getMoveCount());

            objectInputStream = new ObjectInputStream(new FileInputStream(GameConstants.GAME_SAVE_PATH + GameConstants.PLAYER_TILE_FILE));
            PlayerState loadedPlayerState = (PlayerState) objectInputStream.readObject();
            playerState.setPlayerScore(loadedPlayerState.getPlayerScore());
            playerState.setPlayerBoardTiles(loadedPlayerState.getPlayerBoardTiles());

            objectInputStream = new ObjectInputStream(new FileInputStream(GameConstants.GAME_SAVE_PATH + GameConstants.TILE_BAG_FILE));
            TileBagState loadedTileBagState = (TileBagState) objectInputStream.readObject();
            tileBagState.setTileBag(loadedTileBagState.getTileBag());
            tileBagState.setIdCounter(loadedTileBagState.getIdCounter());

            objectInputStream.close();
        } catch (FileNotFoundException e){
            BasicDialogUtility.showDialog("File error", "Game save not found. Try to save the game first.");
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    public static void saveNewLastWord(String word) {
        try(ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(GameConstants.LAST_WORD_FILE))){
            objectOutputStream.writeObject(word);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getLastWord() {
        try(ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(GameConstants.LAST_WORD_FILE))){
            return (String) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
