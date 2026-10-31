package ru.yandex.practicum;

import Exceptions.WordNotFoundInDictionary;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Wordle {

    public static void main(String[] args) {
        try (PrintWriter gameLog = new PrintWriter(new FileWriter("game.log", true));
             PrintWriter errorLog = new PrintWriter(new FileWriter("errors.log", true))) {

            gameLog.println("ИГРА НАЧАЛАСЬ");

            WordleGame game;
            try {
                WordleDictionary dict = WordleDictionaryLoader.makeWordleDictionary();
                if (dict.getWords().isEmpty()) {
                    throw new IOException("Словарь пуст");
                }

                game = new WordleGame(dict, gameLog, errorLog);
                gameLog.println("Словарь загружен, игра инициализирована");
            } catch (IOException e) {
                errorLog.println("CRITICAL | Dictionary load failed | " + e.getMessage());
                e.printStackTrace(errorLog);
                System.out.println("Не удалось загрузить словарь. Подробности в errors.log");
                return;
            }

            Scanner scanner = new Scanner(System.in);
            System.out.println("Игра началась. Цель — угадать загаданное слово из 5 букв. ENTER - подсказка");

            while (true) {
                System.out.print("Введи слово из 5 букв (ENTER — подсказка): ");
                String guess = scanner.nextLine().trim().toLowerCase();

                if (guess.isEmpty()) {
                    gameLog.println("HINT_REQUEST | status=started");
                    try {
                        String suggestion = game.giveHint();
                        if (suggestion != null) {
                            System.out.println("Подсказка: " + suggestion);
                            gameLog.printf("HINT_RESULT | suggested=%s%n", suggestion);
                        } else {
                            System.out.println("Нет подходящих слов для подсказки.");
                            gameLog.println("HINT_RESULT | no_candidates");
                        }
                    } catch (IllegalStateException e) {
                        gameLog.println("HINT_ERROR | state=finished | message=" + e.getMessage());
                        System.out.println("Нельзя запрашивать подсказку — игра завершена.");
                    }

                    if (game.isFinished()) {
                        printResult(game, gameLog);
                        break;
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
                        System.out.println("Слово не найдено в словаре или некорректно.");
                        gameLog.printf("GUESS_FAIL | word=%s%n", guess);
                    }
                } catch (IllegalStateException e) {
                    gameLog.println("GAME_ERROR | illegal_state | message=" + e.getMessage());
                    System.out.println("Нельзя делать ход — игра завершена.");
                }

                if (game.isFinished()) {
                    printResult(game, gameLog);
                    break;
                }
            }

            gameLog.println("ИГРА ОКОНЧЕНА");

        } catch (IOException e) {
            System.err.println("Не удалось создать файлы логов: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static boolean isValidGuess(String s) {
        if (s == null || s.isEmpty()) return false;
        return s.length() == 5 && s.matches("[а-яё]{5}");
    }

    private static String getValidationReason(String s) {
        if (s == null) return "пустая строка";
        if (s.isEmpty()) return "пустой ввод (Enter)";
        if (s.length() != 5) return "неверная длина (" + s.length() + ")";
        if (!s.matches("[а-яё]{5}")) return "содержит нерусские буквы, цифры или спецсимволы";
        return "неизвестная ошибка";
    }

    private static void printResult(WordleGame game, PrintWriter gameLog) {
        if (game.isWon()) {
            System.out.println("Вы угадали, поздравляю с победой!");
            gameLog.println("RESULT | status=WIN | answer=" + game.getAnswer());
        } else {
            System.out.println("Попытки закончились. Загаданное слово было: " + game.getAnswer());
            gameLog.println("RESULT | status=LOSE | answer=" + game.getAnswer());
        }
    }
}
