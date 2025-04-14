package hr.java.scrabble.handlers;

import hr.java.scrabble.dto.PlayerInformationDTO;
import hr.java.scrabble.utils.DialogUtility;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class PlayerActionsHandler extends HBox {

    private final GameHandler gameHandler;

    public PlayerActionsHandler(GameHandler gameHandler){
        this.gameHandler = gameHandler;
        super.setAlignment(Pos.CENTER);
        super.setSpacing(10);
    }

    public void setSingleplayerActions(){
        super.getChildren().removeAll(super.getChildren());
        Button shuffleButton = new Button("Shuffle");
        Button resetButton = new Button("Reset");
        Button swapButton = new Button("Swap");
        Button submitButton = new Button("Submit");

        shuffleButton.setOnAction(actionEvent -> gameHandler.shufflePlayerTiles());
        resetButton.setOnAction(actionEvent -> gameHandler.resetPlayerTiles());
        swapButton.setOnAction(actionEvent -> gameHandler.swapPlayerTiles());
        submitButton.setOnAction(actionEvent -> {
            gameHandler.putTilesFromGridToCenterGameState();
            gameHandler.putTilesFromGridToPlayerState();
        });

        super.getChildren().add(shuffleButton);
        super.getChildren().add(resetButton);
        super.getChildren().add(swapButton);
        super.getChildren().add(submitButton);
    }

    public void setInGameMultiplayerActions(){
        super.getChildren().removeAll(super.getChildren());

        setSingleplayerActions();
        Button passButton = new Button("Pass");//TODO preskociti potez
        passButton.setOnAction(actionEvent -> {});
    }

    public void setInLobbyMultiplayerActions(){
        super.getChildren().removeAll(super.getChildren());
        Button readyButton = new Button("Ready");
        readyButton.setOnAction(actionEvent -> {
            String playerName = DialogUtility.getPlayerData();
            PlayerInformationDTO playerInformationDTO = new PlayerInformationDTO(playerName);

            gameHandler.getClient().sendPlayerInformation(playerInformationDTO);
            super.getChildren().removeAll(super.getChildren());
            super.getChildren().add(new Text("Waiting until all players are ready."));

            gameHandler.getChatHandler().buildNewChatComponent();
        });

        super.getChildren().add(readyButton);
    }

    public void removeAllActions() {
        super.getChildren().removeAll(super.getChildren());
    }

    public void setDisableForAllActions(boolean value) {
        super.getChildren().forEach(node -> {
            Button button = (Button) node;
            button.setDisable(value);
        });
    }

}
