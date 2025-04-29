package hr.java.scrabble.handlers;

import hr.java.scrabble.components.GADialogComponent;
import hr.java.scrabble.states.GAState;
import javafx.application.Platform;
import javafx.scene.control.Dialog;
import javafx.stage.Window;
import lombok.Getter;
import lombok.Setter;

public class GADialogHandler {
    private final Dialog<Void> dialog;
    private final GADialogComponent gaDialogComponent;
    @Getter
    @Setter
    private GAState gaState;

    public GADialogHandler() {
        dialog = new Dialog<>();
        dialog.setTitle("GA Progress");
        dialog.setResizable(false);

        gaState = new GAState();

        gaDialogComponent = new GADialogComponent();
        gaDialogComponent.updateComponent(gaState);
        dialog.getDialogPane().setContent(gaDialogComponent);
    }

    public void updateDialog() {
        Platform.runLater(() -> gaDialogComponent.updateComponent(gaState));
    }

    public void openDialog() {
        Platform.runLater(dialog::show);
    }

    public void allowClosing(){
        Platform.runLater(() -> {
            Window window = dialog.getDialogPane().getScene().getWindow();
            window.setOnCloseRequest(event -> window.hide());
        });
    }
}
