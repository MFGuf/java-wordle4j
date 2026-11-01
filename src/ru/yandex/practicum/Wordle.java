package ru.yandex.practicum;

import exceptions.WordNotFoundInDictionary;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Wordle {

    public static void main(String[] args) {
        try (PrintWriter gameLog = new PrintWriter(new FileWriter("game.log", true));
             PrintWriter errorLog = new PrintWriter(new FileWriter("errors.log", true))) {

            gameLog.println("ИГРА НАЧАЛАСЬ");

            WordleGame game;
            try {
                WordleDictionary dict = WordleDictionaryLoader.makeWordleDictionary();
                List<String> words = dict.getWords();

                if (words.isEmpty()) {
                    throw new IOException("Словарь пуст — невозможно начать игру");
                }

                Random random = new Random();
                String answer = words.get(random.nextInt(words.size())).toLowerCase();

                game = new WordleGame(dict, answer, gameLog, errorLog);

                gameLog.println("Словарь загружен. Загадано слово: " + answer);
                System.out.println("Игра началась. Цель — угадать слово из 5 букв.");
                System.out.println("Вводи слово или нажми Enter для подсказки.");

            } catch (IOException e) {
                errorLog.println("CRITICAL | Dictionary load failed | " + e.getMessage());
                e.printStackTrace(errorLog);
                System.out.println("Не удалось загрузить словарь. Подробности в errors.log");
                return;
            }

            Scanner scanner = new Scanner(System.in);

            while (!game.isFinished()) {
                System.out.print("Твой ход: ");
                String guess = scanner.nextLine().trim().toLowerCase();

                if (guess.isEmpty()) {
                    gameLog.println("HINT_REQUEST | status=started");
                    try {
                        String suggestion = game.giveHint();
                        if (suggestion != null) {
                            System.out.println("Подсказка: " + suggestion);
                            String hint = game.checkGuess(suggestion);
                            if (hint != null) {
                                System.out.println(hint);
                                gameLog.printf("HINT_RESULT | suggested=%s | hint=%s%n", suggestion, hint);
                            } else {
                                System.out.println("Подсказка не прошла проверку.");
                                gameLog.println("HINT_RESULT | validation_failed");
                            }
                        } else {
                            System.out.println("Нет подходящих слов для подсказки.");
                            gameLog.println("HINT_RESULT | no_candidates");
                        }
                    } catch (IllegalStateException e) {
                        gameLog.println("HINT_ERROR | state=finished | message=" + e.getMessage());
                        System.out.println("Нельзя запрашивать подсказку — игра завершена.");
                    }
                    continue;
                }

                if (!isValidGuess(guess)) {
                    String reason = getValidationReason(guess);
                    System.out.println("Неверный ввод: " + reason);
                    gameLog.printf("INPUT_FAIL | word=\"%s\" | reason=%s%n", guess, reason);
                    continue;
                }

                try {
                    String hint = game.checkGuess(guess);
                    if (hint != null) {
                        System.out.println(hint);
                        gameLog.printf("GUESS_OK | word=%s | hint=%s%n", guess, hint);
                    } else {
                        System.out.println("Слово не найдено в словаре или не соответствует правилам.");
                        gameLog.printf("GUESS_FAIL | word=%s%n", guess);
                    }
                } catch (IllegalStateException e) {
                    gameLog.println("GAME_ERROR | illegal_state | message=" + e.getMessage());
                    System.out.println("Нельзя делать ход — игра завершена.");
                }
            }

            printResult(game, gameLog);
            gameLog.println("ИГРА ОКОНЧЕНА");

        } catch (IOException e) {
            System.err.println("Не удалось создать файлы логов: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static boolean isValidGuess(String s) {
        if (s == null || s.isEmpty()) {
            return false;
        }
        return s.length() == 5 && s.matches("[а-яё]{5}");
    }

    private static String getValidationReason(String s) {
        if (s == null) {
            return "пустая строка";
        }
        if (s.isEmpty()) {
            return "пустой ввод (нажат Enter)";
        }
        if (s.length() != 5) {
            return "неверная длина (" + s.length() + "), нужно ровно 5 букв";
        }
        if (!s.matches("[а-яё]{5}")) {
            return "содержит нерусские буквы, цифры или спецсимволы";
        }
        return "неизвестная ошибка";
    }

    private static void printResult(WordleGame game, PrintWriter gameLog) {
        if (game.isWon()) {
            System.out.println("Поздравляю с победой, ты угадал слово");
            gameLog.println("RESULT | status=WIN | answer=" + game.getAnswer());
        } else {
            System.out.println("Попытки закончились. Загаданное слово было: " + game.getAnswer());
            gameLog.println("RESULT | status=LOSE | answer=" + game.getAnswer());
        }
    }
}
