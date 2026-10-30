package ru.yandex.practicum;
import java.util.List;
import Exeptions.WordNotFoundInDictionary;

/*
этот класс содержит в себе список слов List<String>
    его методы похожи на методы списка, но учитывают особенности игры
    также этот класс может содержать рутинные функции по сравнению слов, букв и т.д.
 */
public class WordleDictionary {
    private final List<String> words;
    public WordleDictionary(List<String> words) {
        this.words = words;
    }

    public List<String> getWords() {
        return words;
    }

    public void equalsWords(String word) throws WordNotFoundInDictionary {
        if (!words.contains(word)) {
            throw new WordNotFoundInDictionary("Слово не найдено в словаре");
        }
    }

    public String equalsChar(String word, String answer) {
        String equalsString = "";
        for (int i = 0; i < 5; i++) {
            if (word.charAt(i) == answer.charAt(i)) {
                equalsString += "+";
            } else if (answer.indexOf(word.charAt(i)) != -1) {
                equalsString += "^";
            } else {
                equalsString += "-";
            }
        }
        return equalsString;
    }
}
