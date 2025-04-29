package hr.java.scrabble.networking.server;

import hr.java.scrabble.config.ConfigReader;
import hr.java.scrabble.dto.MessageDTO;
import hr.java.scrabble.config.jndi.ConfigurationKey;
import hr.java.scrabble.networking.chat.ChatService;
import hr.java.scrabble.networking.chat.ChatServiceImpl;
import hr.java.scrabble.states.TileBagState;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.rmi.NoSuchObjectException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
public class Server implements Runnable{

    private ServerSocket server;
    private Registry registry;

    private AtomicBoolean serverRunning = new AtomicBoolean(false);
    private AtomicBoolean gameInProgress = new AtomicBoolean(false);
    private AtomicInteger playerTurnIndex = new AtomicInteger(0);

    private List<ServerListener> clients;
    private TileBagState tileBagState;


    @Override
    public void run() {
        acceptRequests();
    }

    private void acceptRequests() {

        try{
            //socketi
            clients = new ArrayList<>();
            tileBagState = new TileBagState();
            server = new ServerSocket(Integer.parseInt(ConfigReader.getValue(ConfigurationKey.SERVER_PORT)));
            serverRunning.set(true);
            gameInProgress.set(false);
            System.err.println("Server listening on port: " + server.getLocalPort());

            //rmi
            registry = LocateRegistry.createRegistry(Integer.parseInt(ConfigReader.getValue(ConfigurationKey.SERVER_RMI_PORT)));
            ChatService remoteService = new ChatServiceImpl();
            ChatService skeleton = (ChatService) UnicastRemoteObject.exportObject(remoteService, Integer.parseInt(ConfigReader.getValue(ConfigurationKey.RANDOM_PORT_HINT)));
            registry.rebind(ChatService.REMOTE_OBJECT_NAME, skeleton);
            System.err.println("Chat service ready!");

            while (serverRunning.get()) {

                Socket clientSocket;
                try {
                    //System.out.println("SERVER ACCEPTING");
                    clientSocket = server.accept();
                } catch (SocketException e) {
                    //izadji iz loop-a, server je zatvoren
                    break;
                }

                if(!gameInProgress.get()){
                    System.err.println("Client connected from port: " + clientSocket.getPort());

                    ServerListener serverListener = new ServerListener(this, clientSocket);
                    clients.add(serverListener);

                    Thread clientHandlerThread =  new Thread(serverListener);
                    clientHandlerThread.start();
                } else {
                    // Reject new client when the game is in progress
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                    objectOutputStream.writeObject(new MessageDTO("Game is in progress!"));
                    //System.out.println("GAME IN PROGRESS");
                    clientSocket.close();
                }
            }
        }
        catch (BindException e){
            e.printStackTrace();
            //Platform.runLater(() -> DialogUtility.showDialog("Server error", "Host is already assigned!"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }


        //unexportanje regirsta jer je problem kod ponobnog pokretanja
        if (registry != null) {
            try {
                UnicastRemoteObject.unexportObject(registry, true);
            } catch (NoSuchObjectException e) {
                // Ignore if the registry is already unexported
            }
        }

        //System.out.println("SERVER SHUT DOWN");
    }

    public void stop() {
        serverRunning.set(false);
        gameInProgress.set(false);

        try {
            server.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}