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

    @Test
    void testInitialization() {
        assertNotNull(game.getAnswer());
        assertEquals(5, game.getAnswer().length());
        assertFalse(game.isFinished());
        assertFalse(game.isWon());
    }

    @Test
    void testValidGuessFromDict() {
        String firstWord = dict.getWords().get(0).toUpperCase();
        String hint = game.checkGuess(firstWord);

        assertNotNull(hint);
        assertEquals(5, hint.length());
    }

    @Test
    void testInvalidLength() {
        String result = game.checkGuess("СЛО");
        assertNull(result);
    }

    @Test
    void testInvalidCharacters() {
        String result = game.checkGuess("SLOVO");
        assertNull(result);
    }

    @Test
    void testWordNotFound() {
        String rareWord = "КЫРЛЫ";
        String res = game.checkGuess(rareWord);
        assertNull(res);
    }

    @Test
    void testGiveHint() {
        String suggestion = game.giveHint();
        assertNotNull(suggestion);
        assertEquals(5, suggestion.length());
    }

    @Test
    void testAttemptsCounting() {
        for (int i = 0; i < 3; i++) {
            String guess = dict.getWords().get(i % dict.getWords().size()).toUpperCase();
            game.checkGuess(guess);
        }
        assertFalse(game.isFinished(), "После 3 попыток игра не должна быть завершена");
    }

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
