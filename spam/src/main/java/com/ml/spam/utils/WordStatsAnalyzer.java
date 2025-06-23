package com.ml.spam.utils;

import com.ml.spam.dictionary.models.WordData;

import java.util.*;
import java.util.stream.Collectors;

public class WordStatsAnalyzer {

    private final Map<String, WordData> wordMap;

    public WordStatsAnalyzer(Map<String, WordData> wordMap) {
        this.wordMap = wordMap;
    }

    public List<WordData> filterBySpamGreaterThan(int threshold) {
        return wordMap.values().stream()
                .filter(w -> w.getSpamFrequency() > threshold)
                .collect(Collectors.toList());
    }

    public List<WordData> filterByHamGreaterThan(int threshold) {
        return wordMap.values().stream()
                .filter(w -> w.getHamFrequency() > threshold)
                .collect(Collectors.toList());
    }

    public List<WordData> filterBySpamGreaterThanHam() {
        return wordMap.values().stream()
                .filter(w -> w.getSpamFrequency() > w.getHamFrequency())
                .collect(Collectors.toList());
    }

    public List<WordData> filterByHamGreaterThanSpam() {
        return wordMap.values().stream()
                .filter(w -> w.getHamFrequency() > w.getSpamFrequency())
                .collect(Collectors.toList());
    }

    public List<WordData> getTopNBySpam(int n) {
        return wordMap.values().stream()
                .sorted(Comparator.comparingInt(WordData::getSpamFrequency).reversed())
                .limit(n)
                .collect(Collectors.toList());
    }

    public List<WordData> getTopNByHam(int n) {
        return wordMap.values().stream()
                .sorted(Comparator.comparingInt(WordData::getHamFrequency).reversed())
                .limit(n)
                .collect(Collectors.toList());
    }

    public List<WordData> getTopNByTotalFrequency(int n) {
        return wordMap.values().stream()
                .sorted(Comparator.comparingInt(w -> -(w.getSpamFrequency() + w.getHamFrequency())))
                .limit(n)
                .collect(Collectors.toList());
    }

    public void printAsTable(List<WordData> list) {
        System.out.printf("%-20s | %-10s | %-10s%n", "Palabra", "Spam", "Ham");
        System.out.println("---------------------|------------|-----------");
        for (WordData wd : list) {
            System.out.printf("%-20s | %-10d | %-10d%n",
                    wd.getWord(), wd.getSpamFrequency(), wd.getHamFrequency());
        }
    }
}
