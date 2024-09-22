package no.uib.inf102.wordle.controller.AI;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.uib.inf102.wordle.model.Dictionary;
import no.uib.inf102.wordle.model.word.WordleWord;
import no.uib.inf102.wordle.model.word.WordleWordList;

/**
 * This strategy finds the word within the possible words which has the highest
 * expected number of green matches.
 */
public class FrequencyStrategy implements IStrategy {

    private Dictionary dictionary;
    private WordleWordList guesses;

    public FrequencyStrategy(Dictionary dictionary) {
        this.dictionary = dictionary;
        reset();
    }

    @Override
    public String makeGuess(WordleWord feedback) {
        HashMap<Character, Integer> frequencyMap = getMostUsedLetters(dictionary);
        String bestGuess = findBestGuess(frequencyMap, dictionary.getGuessWordsList());
        return bestGuess;
    }

    private String findBestGuess(HashMap<Character, Integer> freqMap, List<String> wordlist) {

        if (wordlist.isEmpty())
            return "";

        String bestWord = wordlist.get(0);
        int bestScore = Integer.MIN_VALUE;

        for (String s : wordlist) {
            int tempScore = 0;
            for (Character c : s.toCharArray()) {
                tempScore += freqMap.getOrDefault(c, 0);
            }
            if (tempScore > bestScore) {
                bestScore = tempScore;
                bestWord = s;
            }
        }
        return bestWord;
    }

    private HashMap<Character, Integer> getMostUsedLetters(Dictionary dictionary) {
        HashMap<Character, Integer> freqMap = new HashMap<>();
        for (String s : dictionary.getAnswerWordsList()) {
            for (Character c : s.toCharArray()) {
                freqMap.put(c, freqMap.getOrDefault(c, 0) + 1);
            }
        }
        return freqMap;
    }

    @Override
    public void reset() {
        guesses = new WordleWordList(dictionary);
    }
}