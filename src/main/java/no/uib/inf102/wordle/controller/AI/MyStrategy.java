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
            guesses.eliminateWords(feedback); // O(m) * O(k) = O(m * k)
        }

        List<String> possibleWords = guesses.possibleAnswers(); // O(1)

        if (possibleWords.size() == 1) { // O(1)
            return possibleWords.get(0); // O(1)
        }

        return findBestWord(possibleWords); // O(m * k)
    }

    /**
     * Finds the best word to guess from the list of possible words based on
     * frequency analysis.
     * 
     * @param possibleWords a list of potential words to consider for guessing
     * @return the word with the highest score based on letter frequency and
     *         uniqueness
     */
    private String findBestWord(List<String> possibleWords) { // O(m * k)
        HashMap<Character, Integer>[] frequency = FrequencyStrategy.getFrequencyForEachPos(possibleWords); // O(m * k)
        String bestWord = null;
        int bestScore = 0;
        // lagrer alle ord med høyest score i denne lista
        List<String> bestWords = new ArrayList<>();
        final int bestWordsLengthLimit = 10;

        for (String word : possibleWords) { // O(m) * O(k) = O(m * k)
            int score = giveScoreToWord(word, frequency); // O(k)

            // bytte verdier for bestescores
            if (score > bestScore) {
                bestScore = score;
                bestWord = word;
                bestWords.clear();
                bestWords.add(word);
            } else if (score == bestScore) {
                bestWords.add(word);
            }
            if (bestWords.size() >= bestWordsLengthLimit) {
                break;
            }
        }

        // ved mange mulige ord, bruker jeg ikke det første ordet med best score
        if (possibleWords.size() > possibleWords.get(0).length()) { // O(1)
            return getEliminationWord(bestWords); // O(j * m * k) where j < 10
        }
        return bestWord;
    }

    /**
     * Calculates the score for a given word based on its frequency and uniqueness
     * of letters.
     * The score is calculated by adding the frequency of each letter in the word
     * and adding bonus points for the number of unique letters.
     * If the word contains duplicate letters, the score is divided by 2.
     *
     * @param word      the word for which the score is calculated
     * @param frequency an array of hash maps representing the frequency of each
     *                  letter at each position in the word
     * @return the score for the given word
     */
    private int giveScoreToWord(String word, HashMap<Character, Integer>[] frequency) { // O(k)
        int score = 0;

        boolean hasDuplicateLetters = false;
        HashSet<Character> uniqueLetters = new HashSet<>();

        for (int i = 0; i < word.length(); i++) { // O(k)
            char letter = word.charAt(i); // O(1)
            // øker score med frequency
            score += frequency[i].getOrDefault(letter, 0); // O(1)

            // returnerer true hvis bokstaven ikke fantes i hashset
            if (!uniqueLetters.add(letter)) { // O(1)
                hasDuplicateLetters = true; // O(1)
            }
        }

        // bonuspoeng for antall unike bokstaver
        score += uniqueLetters.size(); // O(1)

        // deler score på 2 ved dobble konsonanter
        if (hasDuplicateLetters) { // O(1)
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
    private String getEliminationWord(List<String> bestWords) { // O(j * m * k) where j <= 10
        // representerer den minste av den største feedbackgruppen for alle gjett
        int minRemainingWords = guesses.possibleAnswers().size();
        String bestGuess = null;

        // itererer gjennom alle de beste ordene
        for (String guess : bestWords) { // O(j) * O(m * k)

            // størrelse på hver feedback-gruppe representerer worst-case gjenværenede ord
            HashMap<WordleWord, List<String>> feedbackGroups = new HashMap<>();

            // hvert gjett blir sammenlignet med alle mulige svar
            for (String possible : guesses.possibleAnswers()) { // O(m * k)
                // genererer feedback for hvert par med gjett-muligsvar
                WordleWord feedback = WordleAnswer.matchWord(guess, possible); // O(k)

                // alle svar med samme feedback blir gruppert sammen
                if (!feedbackGroups.containsKey(feedback)) { // O(k)
                    feedbackGroups.put(feedback, new ArrayList<>()); // O(1)
                }
                feedbackGroups.get(feedback).add(possible); // O(1)
            }

            int maxRemainingWords = 0;
            // for hvert gjett vil vi finne den minste feedbackgruppen
            for (List<String> group : feedbackGroups.values()) { // O(m)
                if (group.size() > maxRemainingWords) {
                    maxRemainingWords = group.size();
                }
            }

            if (maxRemainingWords < minRemainingWords) {
                minRemainingWords = maxRemainingWords;
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
