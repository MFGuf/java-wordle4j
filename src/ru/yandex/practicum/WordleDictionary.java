package ru.yandex.practicum;
import java.util.List;

import Exceptions.WordNotFoundInDictionary;

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
        if (!words.contains(word.toLowerCase())) {
            throw new WordNotFoundInDictionary("Слово не найдено в словаре");
        }
    }

    public static String equalsChar(String word, String answer) {
            char[] result = new char[5];
            char[] available = answer.toCharArray();

            for (int i = 0; i < 5; i++) {
                if (word.charAt(i) == available[i]) {
                    result[i] = '+';
                    available[i] = 0;
                } else {
                    result[i] = 0;
                }
            }

            for (int i = 0; i < 5; i++) {
                if (result[i] != 0) {
                    continue;
                }

                char c = word.charAt(i);
                boolean found = false;

                for (int j = 0; j < 5; j++) {
                    if (available[j] == c) {
                        result[i] = '^';
                        available[j] = 0;
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    result[i] = '-';
                }
            }

            return new String(result);
    }
}
