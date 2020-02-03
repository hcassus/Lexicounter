package com.advancedtelematic.interview.wordcount.counter;

import com.advancedtelematic.interview.wordcount.reader.CharacterReader;

import java.io.EOFException;
import java.util.*;

public class SerialWordCounter {

    private Map<String, Integer> countMap;

    public SerialWordCounter(Map<String, Integer> countingMap){
        this.countMap = countingMap;
    }

    public SerialWordCounter(){
        this.countMap = new TreeMap<>();
    }

    public Map<String, Integer> countWords(CharacterReader reader) {
        Character currentChar;
        try {
            String word = "";
            do {
                currentChar = reader.nextCharacter();
                if (isWordComplete(currentChar, word)) {
                    if (!word.isBlank()) {
                        addWordOccurrence(countMap, word);
                        word = "";
                    }
                } else {
                    word = word.concat(String.valueOf(currentChar));
                }
            } while (true);
        } catch (EOFException e) {
            reader.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return sortMap(countMap);
    }

    private boolean isWordComplete(Character character, String word) {
        return !String.valueOf(character).matches("\\p{L}") && !hasConnectingCharacter(word, character);
    }

    private boolean hasConnectingCharacter(String word, Character character) {
        boolean hasConnectingChar = character.equals('\'') || character.equals('-');
        return !word.isBlank() && hasConnectingChar;
    }

    Map<String, Integer> sortMap(Map<String, Integer> countMap) {
        TreeMap<String, Integer> keySortedMap = new TreeMap<>(countMap);
        LinkedHashMap<String, Integer> valueSortedMap = new LinkedHashMap<>();

        keySortedMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(e -> valueSortedMap.put(e.getKey(), e.getValue()));
        return valueSortedMap;

    }

    private void addWordOccurrence(Map<String, Integer> countMap, String word) {
        word = word.toLowerCase();
        countMap.putIfAbsent(word, 0);
        countMap.compute(word, (k, v) -> v + 1);
    }

}
