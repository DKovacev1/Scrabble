package hr.java.scrabble.validations;

import hr.java.scrabble.utils.BasicDialogUtility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;


public class DictionaryWordValidation implements WordValidation {

    private final Set<String> words = new HashSet<>();

    public DictionaryWordValidation() {
        loadWords();
    }

    private void loadWords() {
        String fileName = "sowpods.txt"; //https://www.wordgamedictionary.com/sowpods/download/sowpods.txt
        try (InputStream is = getClass().getResourceAsStream("/" + fileName)) {
            if (is == null) {
                throw new IOException("File not found: " + fileName);
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                reader.lines().forEach(line -> {
                    String word = line.toLowerCase();
                    words.add(word);
                });
            }

        } catch (Exception e) {
            BasicDialogUtility.showDialog("Error", "File not found or cannot be read: " + fileName);
        }
    }

    @Override
    public boolean isWordValid(String word) {
        return words.contains(word.toLowerCase());
    }

}
