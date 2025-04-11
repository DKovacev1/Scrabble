package hr.java.scrabble.networking.server;

import hr.java.scrabble.dto.*;
import hr.java.scrabble.game.GameConstants;
import lombok.Getter;
import lombok.Setter;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerListener implements Runnable {

    private final Server server;
    private final Socket clientSocket;
    @Getter
    private ObjectInputStream objectInputStream;
    @Getter
    private ObjectOutputStream objectOutputStream;
    private PlayerInformationDTO playerInformationDTO;
    @Setter
    private boolean palyerTurn = false;

    public ServerListener(Server server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
        try {
            this.objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
            this.objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                Object receivedObject = objectInputStream.readObject();

                //primljene informacije o igracu
                if(receivedObject instanceof PlayerInformationDTO playerInformationDTO2){
                    this.playerInformationDTO = playerInformationDTO2;

                    long numberOfReadyPlayers = server.getClients().stream()
                            .filter(serverListener -> serverListener.getPlayerData() != null)
                            .count();

                    //slanje pocetnih plocica svima
                    if(numberOfReadyPlayers == server.getClients().size()){
                        for (ObjectOutputStream objectOutputStreamInLoop : server.getClients().stream().map(ServerListener::getObjectOutputStream).toList()) {
                            InitialTilesDTO initialTilesDTO = new InitialTilesDTO(server.getTileBagState().getRandomTiles(GameConstants.MAX_NUM_OF_TILES_FOR_PLAYER));
                            objectOutputStreamInLoop.writeObject(initialTilesDTO);
                            objectOutputStreamInLoop.reset();

                            objectOutputStreamInLoop.writeObject(new InGameMultiplayerActionsDTO());
                            objectOutputStreamInLoop.reset();

                            objectOutputStreamInLoop.writeObject(new YourTurnDTO(false));
                            objectOutputStreamInLoop.reset();
                        }

                        //nakon sta su svi dobili inicijalne plocice i podatke, posalji im game state
                        for (ObjectOutputStream objectOutputStreamInLoop : server.getClients().stream().map(ServerListener::getObjectOutputStream).toList()) {
                            objectOutputStreamInLoop.writeObject(server.getTileBagState());
                            objectOutputStreamInLoop.reset();
                        }

                        //postavi prvog igraca
                        server.getClients().get(server.getPlayerTurnIndex().get()).setPalyerTurn(true);
                        server.getClients().get(server.getPlayerTurnIndex().get()).getObjectOutputStream().writeObject(new YourTurnDTO(true));
                        server.getClients().get(server.getPlayerTurnIndex().get()).getObjectOutputStream().reset();

                        server.getGameInProgress().set(true);
                    }

                }

                //primljeno novo stanje sredisnjih plocica i vrecice
                if (receivedObject instanceof CenterBoardStateAndTileBagStateDTO centerBoardStateAndTileBagStateDTO) {

                    //salji vrecicu svima
                    server.getTileBagState().setTileBag(centerBoardStateAndTileBagStateDTO.tileBagState().getTileBag());//postavi nove vrijednosti i salji ih svima
                    for (ObjectOutputStream objectOutputStreamInLoop : server.getClients().stream().map(ServerListener::getObjectOutputStream).toList()) {
                        objectOutputStreamInLoop.writeObject(server.getTileBagState());
                        objectOutputStreamInLoop.reset();
                    }

                    //salji centralnu plocu svima
                    for (ObjectOutputStream objectOutputStreamInLoop : server.getClients().stream().map(ServerListener::getObjectOutputStream).toList()) {
                        objectOutputStreamInLoop.writeObject(centerBoardStateAndTileBagStateDTO.centerBoardState());
                        objectOutputStreamInLoop.reset();
                    }

                    //sve disable-aj
                    for (ObjectOutputStream objectOutputStreamInLoop : server.getClients().stream().map(ServerListener::getObjectOutputStream).toList()) {
                        objectOutputStreamInLoop.writeObject(new YourTurnDTO(false));
                        objectOutputStreamInLoop.reset();
                    }

                    setNextPlayerIndex();

                    //odredi iduceg igraca
                    server.getClients().get(server.getPlayerTurnIndex().get()).setPalyerTurn(true);
                    server.getClients().get(server.getPlayerTurnIndex().get()).getObjectOutputStream().writeObject(new YourTurnDTO(true));
                    server.getClients().get(server.getPlayerTurnIndex().get()).getObjectOutputStream().reset();
                }

                //pobjednik
                if(receivedObject instanceof WinnerAnnouncementDTO winnerAnnouncementDTO){
                    //salji svima samo ne sebi jer je vec prikazano
                    for (ObjectOutputStream objectOutputStreamInLoop : server.getClients().stream()
                            .filter(serverListener -> !serverListener.getObjectInputStream().equals(objectInputStream))
                            .map(ServerListener::getObjectOutputStream).toList()) {
                        objectOutputStreamInLoop.writeObject(winnerAnnouncementDTO);
                        objectOutputStreamInLoop.reset();

                        //onemoguci pokrete svima
                        objectOutputStreamInLoop.writeObject(new YourTurnDTO(false));
                        objectOutputStreamInLoop.reset();
                    }
                }

                //jedan izlazi -> izlaze svi
                if(receivedObject instanceof LeaveGameDTO leaveGameDTO){
                    for (ObjectOutputStream objectOutputStreamInLoop : server.getClients().stream()
                            .map(ServerListener::getObjectOutputStream).toList()) {
                        objectOutputStreamInLoop.writeObject(leaveGameDTO);
                        objectOutputStreamInLoop.reset();
                    }
                    //stop();
                }

            }
        } catch (EOFException e) {
            //e.printStackTrace();
            System.err.println("Client from port " + clientSocket.getPort() + " disconnected");
            try {
                server.getClients().remove(this);
                clientSocket.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } catch (IOException e){
            //e.printStackTrace();
            System.err.println("Client from port " + clientSocket.getPort() + " disconnected");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public PlayerInformationDTO getPlayerData() {
        return playerInformationDTO;
    }

    public void setNextPlayerIndex() {
        if (server.getPlayerTurnIndex().get() < server.getClients().size() - 1)
            server.getPlayerTurnIndex().set(server.getPlayerTurnIndex().get() + 1);
        else
            server.getPlayerTurnIndex().set(0);
    }
}
