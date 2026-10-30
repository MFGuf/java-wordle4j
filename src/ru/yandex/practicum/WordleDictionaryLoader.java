package ru.yandex.practicum;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.LinkedList;
/*
этот класс содержит в себе всю рутину по работе с файлами словарей и с кодировками
    ему нужны методы по загрузке списка слов из файла по имени файла
    на выходе должен быть класс WordleDictionary
 */
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

        }
        WordleDictionary words = new WordleDictionary(wordleDictionary);
        return words;
    }
}
