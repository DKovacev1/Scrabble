package hr.java.scrabble.networking.chat;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ChatService extends Remote{
    String REMOTE_OBJECT_NAME = "hr.java.scrabble.service";
    void sendChatMessage(String chatMessage) throws RemoteException;
    List<String> returnChatHistory() throws RemoteException;
}
