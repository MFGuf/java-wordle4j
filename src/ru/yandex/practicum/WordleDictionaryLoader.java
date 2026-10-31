package ru.yandex.practicum;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.LinkedList;

public class WordleDictionaryLoader {
    public static WordleDictionary makeWordleDictionary() throws IOException {
        List<String> wordleDictionary = new LinkedList<>();
        File filename = new File("words_ru.txt");
        try (BufferedReader br = new BufferedReader(new FileReader(filename, StandardCharsets.UTF_8))) {
            while (br.ready()) {
                String line = br.readLine();
                if (line.length() == 5) {
                    wordleDictionary.add(line.toLowerCase().replace("ё", "е"));
                }
            }
        } catch (IOException ex) {
            throw new IOException("Словарь пуст: в words_ru.txt нет слов длиной 5 букв");
        }
        return new WordleDictionary(wordleDictionary);
    }
}
