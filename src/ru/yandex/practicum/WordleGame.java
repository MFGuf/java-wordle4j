package ru.yandex.practicum;

import java.io.IOException;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import Exceptions.WordNotFoundInDictionary;

/*
в этом классе хранится словарь и состояние игры
    текущий шаг
    всё что пользователь вводил
    правильный ответ

в этом классе нужны методы, которые
    проанализируют совпадение слова с ответом
    предложат слово-подсказку с учётом всего, что вводил пользователь ранее

не забудьте про специальные типы исключений для игровых и неигровых ошибок
 */
public class WordleGame {

    private final String answer;

    private static int steps = 0;

    private final WordleDictionary dictionary;

    private static final int MAX_ATTEMPTS = 6;

    Random random = new Random();

    public WordleGame() throws IOException {
        this.dictionary = WordleDictionaryLoader.makeWordleDictionary();
        this.answer = dictionary.getWords().get(random.nextInt(0, dictionary.getWords().size()));
    }

    private String checkGuess(String word) {
        String hintString = null;
        try {
            if (overGame()) {
                dictionary.equalsWords(word);
                hintString = dictionary.equalsChar(word, answer);
            } else {
                System.out.println("Попытки закончились, правильный ответ: " + answer);
            }
        } catch (WordNotFoundInDictionary ex) {

        } finally {
            steps += 1;
            return hintString;
        }
    }

    private static boolean overGame() {
        if (MAX_ATTEMPTS > steps) {
            return true;
        } else {
            return false;
        }
    }

    private static String giveHint() {

    }
}
