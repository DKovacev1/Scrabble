package hr.java.scrabble.networking.client;

import hr.java.scrabble.config.ConfigReader;
import hr.java.scrabble.dto.CenterBoardStateAndTileBagStateDTO;
import hr.java.scrabble.dto.LeaveGameDTO;
import hr.java.scrabble.dto.PlayerInformationDTO;
import hr.java.scrabble.dto.WinnerAnnouncementDTO;
import hr.java.scrabble.handlers.GameHandler;
import hr.java.scrabble.config.jndi.ConfigurationKey;
import hr.java.scrabble.networking.chat.ChatService;
import hr.java.scrabble.states.CenterBoardState;
import hr.java.scrabble.states.TileBagState;
import hr.java.scrabble.utils.DialogUtility;
import lombok.Getter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client{
    private String playerName;

    private Socket clientSocket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    @Getter
    private ChatService chatService;
    private ClientListener clientListener;

    public Client(GameHandler gameHandler){
        try{
            //socketi
            this.clientSocket = new Socket(
                    ConfigReader.getValue(ConfigurationKey.HOST),
                    Integer.parseInt(ConfigReader.getValue(ConfigurationKey.SERVER_PORT))
            );
            this.objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            this.objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
            clientListener = new ClientListener(gameHandler, clientSocket, objectInputStream);
            new Thread(clientListener).start();
            System.err.println("Client connected to " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());

            //rmi
            //String rmiPort = ConfigurationReader.getValue("rmi.port");
            //Registry registry = LocateRegistry.getRegistry("localhost", Integer.parseInt(rmiPort));
            Registry registry = LocateRegistry.getRegistry("localhost", Integer.parseInt(ConfigReader.getValue(ConfigurationKey.SERVER_RMI_PORT)));
            chatService = (ChatService) registry.lookup(ChatService.REMOTE_OBJECT_NAME);

        } catch (RemoteException | NotBoundException e) {
            DialogUtility.showDialog("Connection error", "Server not found!");
            e.printStackTrace();
        }
        catch (ConnectException e){
            e.printStackTrace();
            DialogUtility.showDialog("Connection error", "Server not found!");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendCenterBoardStateAndTileBag(CenterBoardState centerBoardState, TileBagState tileBagState) {
        try{
            objectOutputStream.writeObject(new CenterBoardStateAndTileBagStateDTO(centerBoardState, tileBagState));
            objectOutputStream.reset();
            System.out.println("KLIJENT JE POSLAO: " + centerBoardState);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendPlayerInformation(PlayerInformationDTO playerInformationDTO) {
        this.playerName = playerInformationDTO.playerName();
        try{
            objectOutputStream.writeObject(playerInformationDTO);
            objectOutputStream.reset();
            System.out.println("KLIJENT JE POSLAO: " + playerInformationDTO);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendIWon() {
        try{
            objectOutputStream.writeObject(new WinnerAnnouncementDTO(playerName));
            objectOutputStream.reset();
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendLeavingTheGame() {
        try{
            objectOutputStream.writeObject(new LeaveGameDTO());
            objectOutputStream.reset();
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendChatMessage(String message){
        try {
            String playerNameAndMesage = playerName + ": " + message;
            chatService.sendChatMessage(playerNameAndMesage);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        try {
            if (clientListener != null) {
                clientListener.stopListening(); // Stop the listener thread
            }
            if (objectOutputStream != null) {
                objectOutputStream.close();
            }
            if (objectInputStream != null) {
                objectInputStream.close();
            }
            if (clientSocket != null) {
                clientSocket.close(); // Close the client socket
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected(){
        return this.clientSocket != null && this.clientSocket.isConnected();
    }

}
