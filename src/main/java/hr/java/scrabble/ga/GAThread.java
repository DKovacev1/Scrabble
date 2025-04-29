package hr.java.scrabble.ga;

import hr.java.scrabble.ga.model.Chromosome;
import hr.java.scrabble.ga.model.Population;
import hr.java.scrabble.ga.model.impl.GeneImpl;
import hr.java.scrabble.ga.model.impl.PopulationImpl;
import hr.java.scrabble.ga.util.GARandomUtil;
import hr.java.scrabble.handlers.GADialogHandler;
import hr.java.scrabble.handlers.GameHandler;
import hr.java.scrabble.states.PlayerState;
import hr.java.scrabble.states.TileState;
import hr.java.scrabble.states.TileStateBase;
import hr.java.scrabble.utils.BasicDialogUtility;
import hr.java.scrabble.utils.GameHandlerUtility;
import javafx.application.Platform;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static hr.java.scrabble.game.GameConstants.GA_MAX_ATTEMPTS;
import static hr.java.scrabble.game.GameConstants.MAX_NUM_OF_TILES_FOR_PLAYER;

@RequiredArgsConstructor
public class GAThread extends Thread implements Runnable {

    private final GameHandler gameHandler;
    private final GADialogHandler gaDialogHandler;
    private final AtomicInteger attemptCounter = new AtomicInteger(1);

    @Override
    public void run() {
        evolve();
    }

    private void evolve() {
        while (attemptCounter.get() <= GA_MAX_ATTEMPTS) {
            gaDialogHandler.getGaState().setAvailableLetters(
                    gameHandler.getGaPlayerState().getPlayerBoardTiles().stream()
                            .map(TileStateBase::getLetter)
                            .toList()
            );
            gaDialogHandler.openDialog();

            gameHandler.getPlayerActionsHandler().setDisableForAllActions(true);
            Platform.runLater(() -> GameHandlerUtility.setDraggableForPlayerTiles(gameHandler, true));

            Population<Chromosome<GeneImpl>> population = new PopulationImpl(
                    gameHandler.getCenterBoardState().getCenterBoardTiles(),
                    gameHandler.getGaPlayerState(),
                    gameHandler.getMoveValidation(),
                    gaDialogHandler
            );
            population.createPopulation();
            population.evolve();

            if (population.getBestIndividual().isPresent()) {
                Chromosome<GeneImpl> bestIndividual = population.getBestIndividual().get();
                putBestIndividualToCenterBoardState(bestIndividual);
                updateGaPlayerState(bestIndividual);

                gaDialogHandler.getGaState().setFinalWord(
                        new StringBuilder(
                                bestIndividual.getGenes().stream()
                                        .map(gene -> gene.getGene().getLetter())
                                        .collect(Collectors.joining(""))
                        ).reverse().toString()
                );
                gaDialogHandler.updateDialog();
                gaDialogHandler.allowClosing();

                gameHandler.getPlayerActionsHandler().setDisableForAllActions(false);
                Platform.runLater(() -> {
                    GameHandlerUtility.setDraggableForPlayerTiles(gameHandler, false);
                    gameHandler.putTilesFromCenterGameStateToGrid();
                    gameHandler.putTilesFromPlayerStateToGrid();
                });
                return;//nasli smo rjesenje
            } else {
                swapGaPlayerStateTiles();
                attemptCounter.incrementAndGet();
            }
        }

        // Ako nakon 10 pokušaja i dalje nema najboljeg
        BasicDialogUtility.showDialog("GA error", "Best individual is not present after 10 attempts.");
    }

    private void putBestIndividualToCenterBoardState(Chromosome<GeneImpl> bestIndividual) {
        //dodavanje u centerboardstate odakle ce se citati i prikazati na ekanu
        List<TileState> centerBoardTiles = new ArrayList<>(gameHandler.getCenterBoardState().getCenterBoardTiles());

        bestIndividual.getGenes().forEach(geneImpl -> {
            TileState tileState = new TileState(
                    geneImpl.getGene().getLetter(),
                    geneImpl.getGene().getRow(),
                    geneImpl.getGene().getCol(),
                    geneImpl.getGene().getPoints()
            );
            tileState.setPermanentlyLaid(true);
            centerBoardTiles.add(tileState);
        });

        gameHandler.getCenterBoardState().setCenterBoardTiles(centerBoardTiles);
    }

    private void updateGaPlayerState(Chromosome<GeneImpl> bestIndividual) {
        PlayerState gaPlayerState = gameHandler.getGaPlayerState();

        bestIndividual.getGenes().forEach(gene -> gaPlayerState.getPlayerBoardTiles() //micanje plocica iz gaPlayerState ovisno o najboljem kromosomu
                .removeIf(tileState -> tileState.getCol().equals(gene.getInitialTile().getCol()) && tileState.getRow().equals(gene.getInitialTile().getRow())));

        //dodavanje novih plocica za GA iz vrecice
        gaPlayerState.getPlayerBoardTiles().addAll(gameHandler.getTileBagState().getRandomTiles(MAX_NUM_OF_TILES_FOR_PLAYER - gaPlayerState.getPlayerBoardTiles().size()));
        gaPlayerState.setPlayerScore(gaPlayerState.getPlayerScore() + bestIndividual.getFitness());
        gameHandler.getCenterBoardState().incrementMoveCount();
    }

    private void swapGaPlayerStateTiles() {
        int tilesToSwapCount = GARandomUtil.getRandomNumber(0, gameHandler.getTileBagState().getTileBagSize());
        List<TileState> tilesToSwap = new ArrayList<>();

        List<TileState> availableTiles = new ArrayList<>(gameHandler.getGaPlayerState().getPlayerBoardTiles());
        IntStream.range(0, tilesToSwapCount).forEach(i -> {
            TileState tileState = availableTiles.get(GARandomUtil.getRandomNumber(0, availableTiles.size()));
            tilesToSwap.add(tileState);
            availableTiles.remove(tileState);
        });

        gameHandler.getGaPlayerState().handlePlayerTilesSwap(tilesToSwap, gameHandler.getTileBagState());
    }

}
