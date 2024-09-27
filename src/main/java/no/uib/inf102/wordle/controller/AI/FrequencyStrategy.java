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
    public String makeGuess(WordleWord feedback) { // O(m * k)
        // fjerner ikke ord ved første gjett
        if (feedback != null) {
            guesses.eliminateWords(feedback); // O(m * k)
        }

        List<String> possibleWords = guesses.possibleAnswers(); // O(1)

        if (possibleWords.size() == 1) {
            return possibleWords.get(0); // O(1)
        }

        String guess = findBestWord(possibleWords); // O(m*k)

        return guess;
    }

    private String findBestWord(List<String> possibleAnswers) { // O(m*k)
        HashMap<Character, Integer>[] frequency = getFrequencyForEachPos(possibleAnswers); // O(m*k)

        // her lagrer jeg beste score og word til å sammenligne resten
        String bestWord = null;
        int bestScore = 0;

        for (String word : possibleAnswers) { // O(m)
            int score = 0;

            // plusser sammen antal Integer fra frequency hashmap-et
            for (int i = 0; i < word.length(); i++) { // O(k)
                char letter = word.charAt(i);
                score += frequency[i].getOrDefault(letter, 0);
            }

            // lagrer scoren hvis den er høyere
            if (score > bestScore) {
                bestScore = score;
                bestWord = word;
            }
        }

        return bestWord;
    }

    HashMap<Character, Integer>[] getFrequencyForEachPos(List<String> possibleWords) { // O(m*k)
        int wordLength = possibleWords.get(0).length();
        HashMap<Character, Integer>[] frequency = new HashMap[wordLength];

        // Initialize frequency maps for each position
        for (int i = 0; i < wordLength; i++) { // O(k)
            frequency[i] = new HashMap<>();
        }

        // Populate the frequency map for each position in the words
        for (String word : possibleWords) { // O(m)
            for (int i = 0; i < word.length(); i++) { // O(k)
                char letter = word.charAt(i);
                frequency[i].put(letter, frequency[i].getOrDefault(letter, 0) + 1);
            }
        }

        return frequency;
    }

    @Override
    public void reset() {
        guesses = new WordleWordList(dictionary);
    }
}