package hr.java.scrabble.handlers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import lombok.Getter;

import java.rmi.RemoteException;
import java.util.List;

public class ChatHandler {

    private final GameHandler gameHandler;

    @Getter
    private VBox chatComponent = new VBox();

    private TextArea chatArea;
    private TextField sendMessageField;

    public ChatHandler(GameHandler gameHandler) {
        this.gameHandler = gameHandler;
    }

    public void buildNewChatComponent(){
        Button sendButton;
        chatArea = new TextArea();
        chatArea.setEditable(false);

        sendMessageField = new TextField();
        HBox.setHgrow(sendMessageField, Priority.ALWAYS);//da bude sire

        sendButton = new Button("Send message");
        sendButton.setOnAction(this::sendChatMessage);

        chatComponent.setSpacing(10);
        chatComponent.getChildren().add(chatArea);//prvi redak

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.getChildren().add(sendMessageField);
        hBox.getChildren().add(sendButton);

        chatComponent.getChildren().add(hBox);//drugi redak

        //"listener"
        Timeline timeline = getTimeline();
        timeline.playFromStart();
    }

    public void removeChatComponent(){
        chatComponent.getChildren().removeAll(chatComponent.getChildren());
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
