package hr.java.scrabble.handlers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import lombok.Getter;

import java.rmi.RemoteException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SideBarHandler {

    private final GameHandler gameHandler;

    @Getter
    private VBox sideBarComponent;

    private TextArea chatArea;
    private TextField sendMessageField;

    public SideBarHandler(GameHandler gameHandler) {
        this.gameHandler = gameHandler;
        sideBarComponent = new VBox();
        sideBarComponent.setPadding(new Insets(20, 20, 20, 20));
    }

    public void addTileBagComponent() {
        sideBarComponent.getChildren().clear();

        String tileBagText = "Tile Bag: " + gameHandler.getTileBagState().getTileBagSize() + "\n";
        String groupedLetters = gameHandler.getTileBagState().getTileBag()
                .entrySet().stream()
                .sorted(Comparator.comparing(entry -> entry.getKey().getLetter()))
                .map(entry -> (entry.getKey().getLetter()+ " ").repeat(entry.getValue()))
                .collect(Collectors.joining("  "));

        Text text = new Text(tileBagText + groupedLetters);
        text.setFont(new Font(15));

        sideBarComponent.getChildren().add(text);
    }

    public void addChatComponent(){
        Button sendButton;
        chatArea = new TextArea();
        chatArea.setEditable(false);

        sendMessageField = new TextField();
        HBox.setHgrow(sendMessageField, Priority.ALWAYS);//da bude sire

        sendButton = new Button("Send message");
        sendButton.setOnAction(this::sendChatMessage);

        sideBarComponent.setSpacing(10);
        sideBarComponent.getChildren().add(chatArea);//prvi redak

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.getChildren().add(sendMessageField);
        hBox.getChildren().add(sendButton);

        sideBarComponent.getChildren().add(hBox);//drugi redak

        //"listener"
        Timeline timeline = getTimeline();
        timeline.playFromStart();
    }

    public void removeSideBarComponent(){
        sideBarComponent.getChildren().removeAll(sideBarComponent.getChildren());
        //updateTileBagComponent();
    }

    private Timeline getTimeline() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), keyFrame -> {
            if(gameHandler.getClient() != null ){
                List<String> chatHistory;
                try {
                    chatHistory = gameHandler.getClient().getChatService().returnChatHistory();
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }

                StringBuilder sb = new StringBuilder();
                for(String message : chatHistory) {
                    sb.append(message);
                    sb.append("\n");
                }

                chatArea.setText(sb.toString());
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        return timeline;
    }

    private void sendChatMessage(ActionEvent actionEvent) {
        String message = sendMessageField.getText();
        gameHandler.getClient().sendChatMessage(message);
        sendMessageField.setText("");
    }

}
