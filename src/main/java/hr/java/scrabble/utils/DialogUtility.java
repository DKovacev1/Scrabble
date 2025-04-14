package hr.java.scrabble.utils;

import hr.java.scrabble.components.TileSwapComponent;
import hr.java.scrabble.states.TileState;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public class DialogUtility {

    private DialogUtility(){}

    public static List<TileState> showSwapTilesDialog(List<TileState> playerTiles) {
        List<TileState> tilesToSwap = new ArrayList<>();
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Swap tiles");

        Label label = new Label("Select the tiles that you want to swap:");
        label.setFont(new Font(15));

        GridPane swapTilesGridPane = new GridPane();
        GridPaneUtility.setGridConstrainst(swapTilesGridPane, 1, 7);

        IntStream.range(0, playerTiles.size())
                        .forEach(i -> {
                            TileSwapComponent tileSwapComponent = new TileSwapComponent(playerTiles.get(i));
                            tileSwapComponent.setMinHeight(28);
                            swapTilesGridPane.add(tileSwapComponent, i, 1);
                        });

        VBox vBox = new VBox();
        vBox.getChildren().add(label);
        vBox.getChildren().add(swapTilesGridPane);

        dialog.getDialogPane().setContent(vBox);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        // Show the dialog and wait for the user response
        Optional<ButtonType> result = dialog.showAndWait();

        // Check which button was pressed
        result.ifPresent(buttonType -> {
            if (buttonType == ButtonType.OK) {
                // OK button was pressed, handle accordingly
                tilesToSwap.addAll(swapTilesGridPane.getChildren().stream()
                        .filter(node -> node instanceof TileSwapComponent)
                        .filter(node -> ((TileSwapComponent) node).isNeedToSwap())
                        .map(node -> ((TileSwapComponent) node).getTileState())
                        .toList());
            }
        });

        return tilesToSwap;
    }

    public static void showDialog(String dialogTitle, String message){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(dialogTitle);


        Label label = new Label(message);
        label.setFont(new Font(15));

        dialog.getDialogPane().setContent(label);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);

        dialog.showAndWait();
    }

    public static String getPlayerData() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Enter username");

        Label label = new Label("Enter username:");
        label.setFont(new Font(15));

        TextField textField = new TextField();

        HBox hBox = new HBox();
        hBox.getChildren().add(label);
        hBox.getChildren().add(textField);
        dialog.getDialogPane().setContent(hBox);

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);

        boolean isUsernameValid = false;
        do{
            dialog.showAndWait();
            if(textField.getText().isEmpty())
                showDialog("Username error", "Username must be entered!");
            else
                isUsernameValid = true;

        }while(!isUsernameValid);

        return textField.getText();
    }

}
