package hr.java.scrabble.word;

import hr.java.scrabble.utilities.FileUtility;

public abstract class WordThread extends Thread{

    private static Boolean wordFileInProgress = false;

    protected synchronized void saveNewWordToFile(String word){
        if(wordFileInProgress){
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        wordFileInProgress = true;

        FileUtility.saveNewLastWord(word);

        wordFileInProgress = false;

        notifyAll();
    }

    protected synchronized String getLastWordFromFile(){
        if(wordFileInProgress){
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        wordFileInProgress = true;

        String word = FileUtility.getLastWord();

        wordFileInProgress = false;

        notifyAll();

        return word;
    }

}
