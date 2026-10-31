package ru.yandex.practicum;

import java.io.IOException;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
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

    private int steps;

    private WordleDictionary dictionary;

    private static final int MAX_ATTEMPTS = 6;

    private List<String> historySteps;

    private List<String> historyHints;

    Random random = new Random();

    public WordleGame() throws IOException {
        this.dictionary = WordleDictionaryLoader.makeWordleDictionary();
        this.answer = dictionary.getWords().get(random.nextInt(0, dictionary.getWords().size())).toLowerCase();
        steps = 0;
        historyHints = new ArrayList<>();
        historySteps = new ArrayList<>();
    }

    private String checkGuess(String word) {
        String hintString = null;
        try {
            dictionary.equalsWords(word);
        } catch (WordNotFoundInDictionary ex) {
            return null;
        }
        if (!overGame()) {
            hintString = dictionary.equalsChar(word, answer);
            historySteps.add(word);
            historyHints.add(hintString);
            steps++;
        } else {
            return "Попытки закончились, правильный ответ: " + answer;
        }
        return hintString;
    }

    private boolean overGame() {
        if (MAX_ATTEMPTS <= steps) {
            return true;
        } else {
            return false;
        }
    }

    private String giveHint() {
        List<String> allWords = dictionary.getWords();
        List<String> candidates = new ArrayList<>();

        Set<Character> mustHave = new HashSet<>();

        for (int i = 0; i < historyHints.size(); i++) {
            String hint = historyHints.get(i);
            String word = historySteps.get(i).toLowerCase();

            for (int j = 0; j < 5; j++) {
                char h = hint.charAt(j);
                if (h == '+' || h == '^') {
                    mustHave.add(word.charAt(j));
                }
            }
        }

        for (String candidate : allWords) {
            String lower = candidate.toLowerCase();

            if (historySteps.contains(lower)) {
                continue;
            }

            boolean hasAllMust = true;
            for (char c : mustHave) {
                if (lower.indexOf(c) == -1) {
                    hasAllMust = false;
                    break;
                }
            }
            if (!hasAllMust) {
                continue;
            }

            candidates.add(lower);
        }

        if (candidates.isEmpty()) {
            return null;
        }

        Random random = new Random();
        return candidates.get(random.nextInt(candidates.size()));
    }
}
