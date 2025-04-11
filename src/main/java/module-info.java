module hr.java.scrabble {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpcore;
    requires org.json;
    requires lombok;
    requires java.rmi;
    requires java.naming;
    requires java.xml;

    opens hr.java.scrabble to javafx.fxml;
    exports hr.java.scrabble;
    exports hr.java.scrabble.components;
    opens hr.java.scrabble.components to javafx.fxml;
    exports hr.java.scrabble.controllers;
    opens hr.java.scrabble.controllers to javafx.fxml;
    exports hr.java.scrabble.utilities;
    opens hr.java.scrabble.utilities to javafx.fxml;
    exports hr.java.scrabble.handlers;
    opens hr.java.scrabble.handlers to javafx.fxml;
    exports hr.java.scrabble.states;
    opens hr.java.scrabble.states to javafx.fxml;
    exports hr.java.scrabble.validations;
    opens hr.java.scrabble.validations to javafx.fxml;

    exports hr.java.scrabble.networking.chat;
}