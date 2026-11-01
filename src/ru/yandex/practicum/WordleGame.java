package ru.yandex.practicum;

import exceptions.WordNotFoundInDictionary;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class WordleGame {

    private final String answer;
    private int steps;
    private final WordleDictionary dictionary;
    private static final int MAX_ATTEMPTS = 6;

    private final List<String> historySteps;
    private final List<String> historyHints;
    private final PrintWriter gameLog;
    private final PrintWriter errorLog;
    private final Random random;


    public WordleGame(
            WordleDictionary dictionary,
            String answer,
            PrintWriter gameLog,
            PrintWriter errorLog
    ) {
        this.dictionary = dictionary;
        this.answer = answer;
        this.gameLog = gameLog;
        this.errorLog = errorLog;
        this.random = new Random(); // обязательно инициализируем
        this.steps = 0;
        this.historySteps = new ArrayList<>();
        this.historyHints = new ArrayList<>();

        logInfo("Game initialized. Answer word selected: " + answer);
    }

    // Конструктор для тестов с фиксированным сидом (опционально)
    public WordleGame(
            WordleDictionary dictionary,
            String answer,
            PrintWriter gameLog,
            PrintWriter errorLog,
            long seed
    ) {
        this.dictionary = dictionary;
        this.answer = answer;
        this.gameLog = gameLog;
        this.errorLog = errorLog;
        this.random = new Random(seed);
        this.steps = 0;
        this.historySteps = new ArrayList<>();
        this.historyHints = new ArrayList<>();

        logInfo("Game initialized with seed: " + seed + ". Answer word: " + answer);
    }

    public String getAnswer() {
        return answer;
    }

    private void ensureNotFinished() {
        if (isFinished()) {
            String msg = "Попытка действия после завершения игры";
            logError(msg);
            throw new IllegalStateException(msg);
        }
    }

    public String checkGuess(String word) {
        ensureNotFinished();

        logDebug("checkGuess called with word: " + word);

        if (word == null || word.isEmpty() || word.length() != 5 || !word.matches("[А-Яа-яёЁ]{5}")) {
            logWarn("checkGuess: invalid input format. word=\"" + word + "\"");
            return null;
        }

        try {
            dictionary.equalsWords(word);
        } catch (WordNotFoundInDictionary ex) {
            logWarn("checkGuess: word not in dictionary. word=\"" + word + "\", reason=" + ex.getMessage());
            return null;
        }

        String hintString = dictionary.equalsChar(word, answer);
        historySteps.add(word.toLowerCase());
        historyHints.add(hintString);

        logInfo("checkGuess: valid guess. word=" + word + ", hint=" + hintString);
        steps++;
        return hintString;
    }

    private boolean overGame() {
        return MAX_ATTEMPTS <= steps;
    }

    public String giveHint() {
        ensureNotFinished();

        logDebug("giveHint called. steps=" + steps);

        List<String> allWords = dictionary.getWords();
        List<String> candidates = new ArrayList<>();

        Set<Character> mustHaveAnywhere = new HashSet<>();
        Character[] fixedPosition = new Character[5];

        for (int i = 0; i < historyHints.size(); i++) {
            String hint = historyHints.get(i);
            String word = historySteps.get(i).toLowerCase();

            for (int j = 0; j < 5; j++) {
                char h = hint.charAt(j);
                if (h == '+') {
                    fixedPosition[j] = word.charAt(j);
                } else if (h == '^') {
                    mustHaveAnywhere.add(word.charAt(j));
                }
            }
        }

        for (String candidate : allWords) {
            String lower = candidate.toLowerCase();

            if (historySteps.contains(lower)) {
                continue;
            }

            boolean positionsMatch = true;
            for (int i = 0; i < 5; i++) {
                if (fixedPosition[i] != null && lower.charAt(i) != fixedPosition[i]) {
                    positionsMatch = false;
                    break;
                }
            }
            if (!positionsMatch) {
                continue;
            }

            boolean hasAllMust = true;
            for (char c : mustHaveAnywhere) {
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
            logInfo("giveHint: no candidates found");
            return null;
        }

        String suggestion = candidates.get(random.nextInt(candidates.size()));
        logInfo("giveHint: returned suggestion=" + suggestion);
        return suggestion;
    }

    public boolean isFinished() {
        if (historyHints.isEmpty()) {
            return false;
        }
        return overGame() || historyHints.get(historyHints.size() - 1).equals("+++++");
    }

    public boolean isWon() {
        if (historyHints.isEmpty()) {
            return false;
        }
        return historyHints.get(historyHints.size() - 1).equals("+++++");
    }

    private void logInfo(String message) {
        if (gameLog != null) {
            gameLog.println("INFO | " + message);
        }
    }

    private void logDebug(String message) {
        if (gameLog != null) {
            gameLog.println("DEBUG | " + message);
        }
    }

    private void logWarn(String message) {
        if (gameLog != null) {
            gameLog.println("WARN | " + message);
        }
    }

    private void logError(String message) {
        if (errorLog != null) {
            errorLog.println("ERROR | " + message);
        }
    }
}
