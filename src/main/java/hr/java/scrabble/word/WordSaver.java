package hr.java.scrabble.word;

public class WordSaver extends WordThread implements Runnable{

    private final String word;

    public WordSaver(String word) {
        this.word = word;
    }

    @Override
    public void run(){
        saveNewWordToFile(word);
    }

}
