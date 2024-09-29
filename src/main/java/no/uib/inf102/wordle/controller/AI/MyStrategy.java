package no.uib.inf102.wordle.controller.AI;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

import no.uib.inf102.wordle.model.Dictionary;
import no.uib.inf102.wordle.model.word.WordleAnswer;
import no.uib.inf102.wordle.model.word.WordleWord;
import no.uib.inf102.wordle.model.word.WordleWordList;

public class MyStrategy implements IStrategy {

    private Dictionary dictionary;
    private WordleWordList guesses;

    public MyStrategy(Dictionary dictionary) {
        this.dictionary = dictionary;
        reset();
    }

    @Override
    public String makeGuess(WordleWord feedback) { // O(m * k), same as frequencyStrategy

        if (feedback != null) {
            guesses.eliminateWords(feedback);
        }

        List<String> possibleWords = guesses.possibleAnswers();

        if (possibleWords.size() == 1) {
            return possibleWords.get(0);
        }

        return findBestWord(possibleWords);
    }

    /**
     * Finds the best word to guess from the list of possible words based on
     * frequency analysis.
     * 
     * @param possibleWords a list of potential words to consider for guessing
     * @return the word with the highest score based on letter frequency and
     *         uniqueness
     */
    private String findBestWord(List<String> possibleWords) { // O(m*k)
        HashMap<Character, Integer>[] frequency = FrequencyStrategy.getFrequencyForEachPos(possibleWords); // O(m*k)
        String bestWord = null;
        int bestScore = 0;
        // lagrer alle ord med høyest score i denne lista
        List<String> bestWords = new ArrayList<>();

        for (String word : possibleWords) { // O(m) * O(k)
            int score = giveScoreToWord(word, frequency);

            // bytte verdier for bestescores
            if (score > bestScore) {
                bestScore = score;
                bestWord = word;
                bestWords.clear();
                bestWords.add(word);
            } else if (score == bestScore) {
                bestWords.add(word);
            }
        }

        // ved mange mulige ord, bruker jeg ikke det første ordet med best score
        if (possibleWords.size() > possibleWords.get(0).length()) {
            return getEliminationWord(bestWords);
        }
        return bestWord;
    }

    private int giveScoreToWord(String word, HashMap<Character, Integer>[] frequency) {
        int score = 0;

        boolean hasDuplicateLetters = false;
        HashSet<Character> uniqueLetters = new HashSet<>();

        for (int i = 0; i < word.length(); i++) { // O(k)
            char letter = word.charAt(i);
            // øker score med frequency
            score += frequency[i].getOrDefault(letter, 0);

            // returnerer true hvis bokstaven ikke fantes i hashset
            if (!uniqueLetters.add(letter)) {
                hasDuplicateLetters = true;
            }
        }

        // bonuspoeng for antall unike bokstaver
        score += uniqueLetters.size();

        // deler score på 2 ved dobble konsonanter
        if (hasDuplicateLetters) {
            score /= 2;
        }

        return score;
    }

    /**
     * 
     * Selects the word that minimizes the maximum number of remaining possibilities
     * in the worst-case scenario.
     * 
     * @param bestWords a list of the best scoring words
     * @return returns the word with best worst-case scenario
     */
    private String getEliminationWord(List<String> bestWords) {
        // representerer den minste av den største feedbackgruppen for alle gjett
        int minRemainingWords = guesses.possibleAnswers().size();
        String bestGuess = null;

        // itererer gjennom alle de beste ordene
        for (String guess : bestWords) {

            // størrelse på hver feedback-gruppe representerer worst-case gjenværenede ord
            HashMap<WordleWord, List<String>> feedbackGroups = new HashMap<>();

            // hvert gjett blir sammenlignet med alle mulige svar
            for (String possible : guesses.possibleAnswers()) {
                // genererer feedback for hvert par med gjett-muligsvar
                WordleWord feedback = WordleAnswer.matchWord(guess, possible);

                // alle svar med samme feedback blir gruppert sammen
                if (!feedbackGroups.containsKey(feedback)) {
                    feedbackGroups.put(feedback, new ArrayList<>());
                }
                feedbackGroups.get(feedback).add(possible);
            }

            int maxRemaining = 0;
            // for hvert gjett vil vi finne den minste feedbackgruppen
            for (List<String> group : feedbackGroups.values()) {
                if (group.size() > maxRemaining) {
                    maxRemaining = group.size();
                }
            }

            if (maxRemaining < minRemainingWords) {
                minRemainingWords = maxRemaining;
                bestGuess = guess;
            }
        }
        // returnerer det ordet som har best mulig worst-case scenario
        return bestGuess;
    }

    @Override
    public void reset() {
        guesses = new WordleWordList(dictionary); // Reset the word list
    }
}
