package hr.java.scrabble.utilities;

import hr.java.scrabble.game.GameConstants;
import hr.java.scrabble.states.CenterBoardState;
import hr.java.scrabble.states.GameplayHistory;
import hr.java.scrabble.states.PlayerState;
import hr.java.scrabble.states.TileState;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XMLFileReadUtility {

    private XMLFileReadUtility(){}

    public static GameplayHistory loadGameplayHistory() {

        GameplayHistory gameplayHistory = new GameplayHistory();

        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(GameConstants.GAMEPLAY_HISTORY_FILE);

            NodeList centerBoardStateList = document.getElementsByTagName("CenterBoardState");
            for (int i = 0; i < centerBoardStateList.getLength(); i++) {
                Node centerBoardStateNode = centerBoardStateList.item(i);
                if (centerBoardStateNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element centerBoardStateElement = (Element) centerBoardStateNode;

                    int moveCount = Integer.parseInt(centerBoardStateElement.getElementsByTagName("MoveCount").item(0).getTextContent());
                    List<TileState> centerBoardTiles = new ArrayList<>();

                    NodeList tileStateList = centerBoardStateElement.getElementsByTagName("TileState");
                    for (int j = 0; j < tileStateList.getLength(); j++) {
                        Node tileStateNode = tileStateList.item(j);
                        if (tileStateNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element tileStateElement = (Element) tileStateNode;

                            int row = Integer.parseInt(tileStateElement.getElementsByTagName("Row").item(0).getTextContent());
                            int col = Integer.parseInt(tileStateElement.getElementsByTagName("Col").item(0).getTextContent());
                            String letter = tileStateElement.getElementsByTagName("Letter").item(0).getTextContent();
                            int points = Integer.parseInt(tileStateElement.getElementsByTagName("Points").item(0).getTextContent());

                            TileState tileState = new TileState(letter, row, col, points);
                            centerBoardTiles.add(tileState);
                        }
                    }

                    CenterBoardState centerBoardState = new CenterBoardState(centerBoardTiles);
                    centerBoardState.setMoveCount(moveCount);
                    gameplayHistory.addCenterBoardState(centerBoardState);
                }
            }

            NodeList playerStateList = document.getElementsByTagName("PlayerState");
            for (int i = 0; i < playerStateList.getLength(); i++) {
                Node playerStateNode = playerStateList.item(i);
                if (playerStateNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element playerStateElement = (Element) playerStateNode;

                    int playerScore = Integer.parseInt(playerStateElement.getElementsByTagName("PlayerScore").item(0).getTextContent());
                    List<TileState> playerBoardTiles = new ArrayList<>();

                    NodeList tileStateList = playerStateElement.getElementsByTagName("TileState");
                    for (int j = 0; j < tileStateList.getLength(); j++) {
                        Node tileStateNode = tileStateList.item(j);
                        if (tileStateNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element tileStateElement = (Element) tileStateNode;

                            int row = Integer.parseInt(tileStateElement.getElementsByTagName("Row").item(0).getTextContent());
                            int col = Integer.parseInt(tileStateElement.getElementsByTagName("Col").item(0).getTextContent());
                            String letter = tileStateElement.getElementsByTagName("Letter").item(0).getTextContent();
                            int points = Integer.parseInt(tileStateElement.getElementsByTagName("Points").item(0).getTextContent());

                            TileState tileState = new TileState(letter, row, col, points);
                            playerBoardTiles.add(tileState);
                        }
                    }

                    PlayerState playerState = new PlayerState(playerBoardTiles);
                    playerState.setPlayerScore(playerScore);
                    gameplayHistory.addPlayerState(playerState);
                }
            }




        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }

        return gameplayHistory;
    }

}
