package hr.java.scrabble.states;

import hr.java.scrabble.game.GameConstants;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static hr.java.scrabble.game.GameConstants.PLAYER_TILE_GRID_DATA_ROW_INDEX;

@Getter
public class PlayerState implements Serializable {

    @Setter
    private Integer playerScore = 0;
    private List<TileState> playerBoardTiles = new ArrayList<>();

    public PlayerState() {}

    public PlayerState(List<TileState> playerBoardTiles) {
        this.playerBoardTiles = new ArrayList<>(playerBoardTiles);
    }

    public void addToPlayerScore(Integer playerScoreToAdd) {
        this.playerScore += playerScoreToAdd;
    }

    public void setPlayerBoardTiles(List<TileState> playerBoardTiles) {
        for (int i = 0; i < playerBoardTiles.size(); i++) {
            TileState tileState = playerBoardTiles.get(i);
            tileState.setRow(PLAYER_TILE_GRID_DATA_ROW_INDEX);
            tileState.setCol(i + 4);
        }
        this.playerBoardTiles = new ArrayList<>(playerBoardTiles);
    }

    public List<TileState> getShuffledPlayerTiles(){
        List<Integer> columnIndexes = new ArrayList<>(playerBoardTiles.stream()
                .map(TileState::getCol)
                .toList());

        Collections.shuffle(columnIndexes);

        List<TileState> shuffeledPlayerTiles = new ArrayList<>();
        IntStream.range(0, playerBoardTiles.size())
                .forEach(i -> {
                    TileState tileState = playerBoardTiles.get(i).getCopy();
                    tileState.setCol(columnIndexes.get(i));
                    shuffeledPlayerTiles.add(tileState);
                });

        return shuffeledPlayerTiles;
    }



    public void handlePlayerTilesSwap(List<TileState> tilesToSwap, TileBagState tileBagState) {
        List<Integer> oldColumnIndexes = tilesToSwap.stream()
                .map(TileState::getCol)
                .toList();

        tilesToSwap.forEach(tileState -> {
            tileState.setRow(-1);//takvi podaci se nalaze u tileBagState-u
            tileState.setCol(-1);
        });

        //makni plocice koje treba maknuti
        playerBoardTiles.removeAll(tilesToSwap);

        //izvuci nove
        List<TileState> newlyTakenTiles = tileBagState.getRandomTiles(tilesToSwap.size());

        //nove dodati u player state, postaviti im redke i stupce
        int newlyTakenTilesCounter = 0;
        for(int i = playerBoardTiles.size(); i < GameConstants.MAX_NUM_OF_TILES_FOR_PLAYER; i++){
            TileState tileState = newlyTakenTiles.get(newlyTakenTilesCounter);
            tileState.setRow(GameConstants.PLAYER_TILE_GRID_DATA_ROW_INDEX);
            tileState.setCol(oldColumnIndexes.get(newlyTakenTilesCounter));
            playerBoardTiles.add(tileState);
            newlyTakenTilesCounter++;
        }

        //stare vratiti u vrecicu
        tileBagState.returnTilesToBag(tilesToSwap);
    }

    public void reset(){
        this.playerScore = 0;
        this.playerBoardTiles = new ArrayList<>();
    }


    public PlayerState getCopy(){
        return new PlayerState(new ArrayList<>(this.playerBoardTiles));
    }

    public boolean playerHasTiles() {
        return !playerBoardTiles.isEmpty();
    }
}
