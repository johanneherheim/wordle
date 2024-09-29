package no.uib.inf102.wordle.controller.AI;

import java.util.HashMap;
import java.util.List;

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

        return findBestWord(possibleWords); // O(m * k)
    }

    /**
     * Finds the best word from a list of possible answers based on their frequency
     * score.
     *
     * @param possibleAnswers the list of possible answers
     * @return the highest scoring word
     */
    private String findBestWord(List<String> possibleAnswers) { // O(m * k)
        HashMap<Character, Integer>[] frequency = getFrequencyForEachPos(possibleAnswers); // O(m*k)

        // her lagrer jeg beste score og word til å sammenligne resten
        String bestWord = null;
        int bestScore = 0;

        for (String word : possibleAnswers) { // O(m) * O(k)

            int score = giveScoreToWord(word, frequency); // O(k)

            if (score > bestScore) {
                bestScore = score;
                bestWord = word;
            }
        }

        return bestWord;
    }

    /**
     * Scores a word based on the frequency of its letters in their respective
     * positions.
     * 
     * @param word      the word to score
     * @param frequency an array of frequency maps for each position
     * @return the calculated score for the word
     */
    private int giveScoreToWord(String word, HashMap<Character, Integer>[] frequency) { // O(k)
        int score = 0;

        // sum of all values in frequency hashMap
        for (int i = 0; i < word.length(); i++) { // O(k)
            char letter = word.charAt(i);
            score += frequency[i].getOrDefault(letter, 0);
        }

        return score;
    }

    /**
     * Calculates the frequency of letters in each position for the given list of
     * words.
     * 
     * @param possibleWords a list of words to analyze
     * @return an array of frequency maps, where each map contains the frequency of
     *         letters at a specific position
     */
    static HashMap<Character, Integer>[] getFrequencyForEachPos(List<String> possibleWords) { // O(m*k)
        int wordLength = possibleWords.get(0).length();
        @SuppressWarnings("unchecked")
        HashMap<Character, Integer>[] frequency = new HashMap[wordLength];

        // Initialiserer frequency hashmap for hver posisjon i ordet
        for (int i = 0; i < wordLength; i++) { // O(k)
            frequency[i] = new HashMap<Character, Integer>();
        }

        // fyller ut hashmap for hver posisjon
        for (String word : possibleWords) { // O(m) * O(k)
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