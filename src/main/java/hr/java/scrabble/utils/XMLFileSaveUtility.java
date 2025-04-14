package hr.java.scrabble.utils;

import hr.java.scrabble.game.GameConstants;
import hr.java.scrabble.states.GameplayHistory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileWriter;
import java.io.IOException;

public class XMLFileSaveUtility {

    private XMLFileSaveUtility(){}

    public static void saveGameplayHistory(GameplayHistory gameplayHistory) {
        //sve plocice moraju biti nepomicne


        //spremanje
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            Document document = documentBuilder.newDocument();
            Element rootElement = document.createElement("GameplayHistory");

            Element centerBoardStateListElement = document.createElement("CenterBoardStateList");

            gameplayHistory.getCenterBoardStateList().forEach(centerBoardState -> {
                Element centerBoardStateElement = document.createElement("CenterBoardState");

                Element moveCountElement = document.createElement("MoveCount");
                moveCountElement.setTextContent(centerBoardState.getMoveCount().toString());

                Element centerBoardTilesElement = document.createElement("CenterBoardTiles");
                centerBoardState.getCenterBoardTiles().forEach(tileState -> {
                    Element tileStateElement = document.createElement("TileState");

                    Element rowElement = document.createElement("Row");
                    rowElement.setTextContent(tileState.getRow().toString());

                    Element colElement = document.createElement("Col");
                    colElement.setTextContent(tileState.getCol().toString());

                    Element letterElement = document.createElement("Letter");
                    letterElement.setTextContent(tileState.getLetter());

                    Element pointsElement = document.createElement("Points");
                    pointsElement.setTextContent(tileState.getPoints().toString());

                    Element permanentlyLaidElement = document.createElement("PermanentlyLaid");
                    permanentlyLaidElement.setTextContent(Boolean.toString(true));//uvijek true

                    tileStateElement.appendChild(rowElement);
                    tileStateElement.appendChild(colElement);
                    tileStateElement.appendChild(letterElement);
                    tileStateElement.appendChild(pointsElement);
                    tileStateElement.appendChild(permanentlyLaidElement);

                    centerBoardTilesElement.appendChild(tileStateElement);
                });


                centerBoardStateElement.appendChild(moveCountElement);
                centerBoardStateElement.appendChild(centerBoardTilesElement);

                centerBoardStateListElement.appendChild(centerBoardStateElement);
            });

            Element playerStateListElement = document.createElement("PlayerStateList");

            gameplayHistory.getPlayerStateList().forEach(playerState -> {
                Element playerStateElement = document.createElement("PlayerState");

                Element playerScoreElement = document.createElement("PlayerScore");
                playerScoreElement.setTextContent(playerState.getPlayerScore().toString());
                playerStateElement.appendChild(playerScoreElement);

                Element playerBoardTilesElement = document.createElement("PlayerBoardTiles");


                playerState.getPlayerBoardTiles().forEach(tileState -> {
                    Element tileStateElement = document.createElement("TileState");

                    Element rowElement = document.createElement("Row");
                    rowElement.setTextContent(tileState.getRow().toString());

                    Element colElement = document.createElement("Col");
                    colElement.setTextContent(tileState.getCol().toString());

                    Element letterElement = document.createElement("Letter");
                    letterElement.setTextContent(tileState.getLetter());

                    Element pointsElement = document.createElement("Points");
                    pointsElement.setTextContent(tileState.getPoints().toString());

                    Element permanentlyLaidElement = document.createElement("PermanentlyLaid");
                    permanentlyLaidElement.setTextContent(Boolean.toString(true));//uvijek true

                    tileStateElement.appendChild(rowElement);
                    tileStateElement.appendChild(colElement);
                    tileStateElement.appendChild(letterElement);
                    tileStateElement.appendChild(pointsElement);
                    tileStateElement.appendChild(permanentlyLaidElement);

                    playerBoardTilesElement.appendChild(tileStateElement);
                });


                playerStateElement.appendChild(playerBoardTilesElement);
                playerStateListElement.appendChild(playerStateElement);
            });

            rootElement.appendChild(centerBoardStateListElement);
            rootElement.appendChild(playerStateListElement);
            document.appendChild(rootElement);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new FileWriter(GameConstants.GAMEPLAY_HISTORY_FILE));

            transformer.transform(domSource, streamResult);

        } catch (ParserConfigurationException | IOException | TransformerException e) {
            throw new RuntimeException(e);
        }

    }

}
