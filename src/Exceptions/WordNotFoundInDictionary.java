package Exceptions;

public class WordNotFoundInDictionary extends Exception {
    public WordNotFoundInDictionary(String message) {
        super(message);
    }
}
