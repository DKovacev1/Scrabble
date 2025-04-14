package hr.java.scrabble.utils;

import hr.java.scrabble.game.GameConstants;
import hr.java.scrabble.states.TileState;

import java.util.*;

public class TileBagUtility {

    private TileBagUtility(){}

    public static Map<TileState, Integer> generateNewTileBag(){
        //plocica, brojPlocica
        Map<TileState, Integer> tileBag = new HashMap<>();

        if (!GameConstants.IS_GAME_SIMPLIFIED) {
            tileBag.put(new TileState("A", 1), 9);
            tileBag.put(new TileState("B", 3), 2);
            tileBag.put(new TileState("C", 3), 2);
            tileBag.put(new TileState("D", 2), 4);
            tileBag.put(new TileState("E", 1), 12);
            tileBag.put(new TileState("F", 4), 2);
            tileBag.put(new TileState("G", 2), 3);
            tileBag.put(new TileState("H", 4), 2);
            tileBag.put(new TileState("I", 1), 9);
            tileBag.put(new TileState("J", 8), 1);
            tileBag.put(new TileState("K", 5), 1);
            tileBag.put(new TileState("L", 1), 4);
            tileBag.put(new TileState("M", 3), 2);
            tileBag.put(new TileState("N", 1), 6);
            tileBag.put(new TileState("O", 1), 8);
            tileBag.put(new TileState("P", 3), 2);
            tileBag.put(new TileState("Q", 10), 1);
            tileBag.put(new TileState("E", 1), 6);
            tileBag.put(new TileState("S", 1), 4);
            tileBag.put(new TileState("T", 1), 6);
            tileBag.put(new TileState("U", 1), 4);
            tileBag.put(new TileState("V", 4), 2);
            tileBag.put(new TileState("W", 4), 2);
            tileBag.put(new TileState("X", 8), 1);
            tileBag.put(new TileState("Y", 4), 2);
            tileBag.put(new TileState("Z", 10), 1);
        }
        else {
            tileBag.put(new TileState("A", 1), 3);
            tileBag.put(new TileState("B", 3), 3);
            tileBag.put(new TileState("C", 3), 3);
            tileBag.put(new TileState("D", 2), 3);
            tileBag.put(new TileState("E", 1), 3);
            /*tileBag.put(new TileState("F", 4), 2);
            tileBag.put(new TileState("G", 2), 2);
            tileBag.put(new TileState("H", 4), 2);
            tileBag.put(new TileState("I", 1), 2);*/
        }

        return tileBag;
    }

    public static List<TileState> getRandomPlayerTiles(Integer numOfTiles, Map<TileState, Integer> tileBag) {
        List<TileState> playerTiles = new ArrayList<>();

        // Create a list of available tiles from the tileBag
        List<TileState> availableTiles = new ArrayList<>();
        for (Map.Entry<TileState, Integer> entry : tileBag.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                availableTiles.add(entry.getKey());
            }
        }

        // Shuffle the available tiles to simulate randomness
        Collections.shuffle(availableTiles);

        // Determine the number of tiles to select (minimum of numOfTiles and available tiles)
        int tilesToSelect = Math.min(numOfTiles, availableTiles.size());

        // Add the selected tiles to the playerTiles list
        for (int i = 0; i < tilesToSelect; i++) {
            playerTiles.add(availableTiles.get(i));
        }

        // Remove the selected tiles from the tileBag
        for (TileState tile : playerTiles) {
            tileBag.put(tile, tileBag.get(tile) - 1);
        }

        //dodano jer lista availableTiles sadrzi vise plocica sa istom referencom,
        //bilo je problema kod postavljanja stupaca poslje pa kod prikaza
        return playerTiles.stream()
                .map(TileState::getCopy)
                .toList();
    }

    public static void returnTilesToBag(List<TileState> tilesToSwap, Map<TileState, Integer> tileBag) {
        tilesToSwap.forEach(tileState -> {
            tileState.setRow(-1);//jer su se takve dodavale u prazan tilebag zbog konstruktora
            tileState.setCol(-1);
            Integer count = tileBag.getOrDefault(tileState, 0);
            // Increment the count by 1
            tileBag.put(tileState, count + 1);
        });
    }

}
