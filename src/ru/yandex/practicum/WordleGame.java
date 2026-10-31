package ru.yandex.practicum;

import java.io.IOException;
import java.util.Random;

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

    private int steps;

    private WordleDictionary dictionary;
    Random random = new Random();

    public WordleGame() throws IOException {
        this.dictionary = WordleDictionaryLoader.makeWordleDictionary();
        this.answer = dictionary.getWords().get(random.nextInt(0, dictionary.getWords().size()));
    }
}
