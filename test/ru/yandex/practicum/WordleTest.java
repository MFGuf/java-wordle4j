package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordleTest {

    private WordleDictionary dict;

    // Храним StringWriter отдельно — это критично для правильного чтения логов
    private StringWriter gameLogWriter;
    private StringWriter errorLogWriter;

    private PrintWriter gameLog;
    private PrintWriter errorLog;
    private WordleGame game;

    @BeforeEach
    void setUp() throws IOException {
        dict = WordleDictionaryLoader.makeWordleDictionary();
        List<String> words = dict.getWords();

        if (words.isEmpty()) {
            throw new IllegalStateException("Словарь пуст! Положи words_ru.txt хотя бы с одним 5-буквенным словом.");
        }

        gameLogWriter = new StringWriter();
        errorLogWriter = new StringWriter();

        gameLog = new PrintWriter(gameLogWriter);
        errorLog = new PrintWriter(errorLogWriter);

        game = new WordleGame(dict, gameLog, errorLog);
    }

    // Тест 1: игра стартует корректно
    @Test
    void testInitialization() {
        assertNotNull(game.getAnswer());
        assertEquals(5, game.getAnswer().length());
        assertFalse(game.isFinished());
        assertFalse(game.isWon());
    }

    // Тест 2: валидное слово из словаря обрабатывается без ошибок
    @Test
    void testValidGuessFromDict() {
        String firstWord = dict.getWords().get(0).toUpperCase();
        String hint = game.checkGuess(firstWord);

        assertNotNull(hint);
        assertEquals(5, hint.length());
    }

    // Тест 3: слишком короткое слово возвращает null
    @Test
    void testInvalidLength() {
        String result = game.checkGuess("СЛО");
        assertNull(result);
    }

    // Тест 4: латиница вместо кириллицы возвращает null
    @Test
    void testInvalidCharacters() {
        String result = game.checkGuess("SLOVO");
        assertNull(result);
    }

    // Тест 5: слово, которого нет в словаре, возвращает null
    @Test
    void testWordNotFound() {
        // Подбираем заведомо несуществующее слово из 5 русских букв
        String rareWord = "КЫРЛЫ";
        String res = game.checkGuess(rareWord);
        assertNull(res);
    }

    // Тест 6: подсказка всегда возвращает слово из 5 букв
    @Test
    void testGiveHint() {
        String suggestion = game.giveHint();
        assertNotNull(suggestion);
        assertEquals(5, suggestion.length());
    }

    // Тест 7: после 3 попыток игра ещё не закончена
    @Test
    void testAttemptsCounting() {
        for (int i = 0; i < 3; i++) {
            String guess = dict.getWords().get(i % dict.getWords().size()).toUpperCase();
            game.checkGuess(guess);
        }
        assertFalse(game.isFinished(), "После 3 попыток игра не должна быть завершена");
    }

    // Тест 8: в логах есть запись с уровнем (INFO/DEBUG/WARN/ERROR)
    @Test
    void testLogging() {
        String validWord = dict.getWords().get(0).toUpperCase();
        game.checkGuess(validWord);

        // Обязательно сбрасываем буфер
        gameLog.flush();
        errorLog.flush();

        String logContent = gameLogWriter.toString();
        String errorContent = errorLogWriter.toString();
        String allLogs = logContent + errorContent;

        assertTrue(!allLogs.isEmpty(), "В логах должно быть хотя бы одно сообщение");

        assertTrue(
                allLogs.contains("INFO") ||
                        allLogs.contains("DEBUG") ||
                        allLogs.contains("WARN") ||
                        allLogs.contains("ERROR"),
                "В логах должен быть хотя бы один уровень (INFO/DEBUG/WARN/ERROR). Содержимое:\n" + allLogs
        );
    }
}
