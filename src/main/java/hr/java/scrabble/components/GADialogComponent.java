package hr.java.scrabble.components;

import hr.java.scrabble.states.GAState;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class GADialogComponent extends VBox {

    private GAState gaState = new GAState();

    public GADialogComponent() {
        super();
        super.setSpacing(10);
        super.setMinSize(200, 80);
        updateComponent();
    }

    public void updateComponent(GAState gaState) {
        this.gaState = gaState;
        updateComponent();
    }

    private void updateComponent(){
        super.getChildren().clear();
        super.getChildren().add(new Text( "Available letters: " + String.join(", ", gaState.getAvailableLetters())));
        super.getChildren().add(new Text("Generation counter: " + gaState.getGenerationCounter().toString()));
        if(gaState.getFinalWord() != null)
            super.getChildren().add(new Text("Final word: " + gaState.getFinalWord()));
        super.autosize();
    }

}
