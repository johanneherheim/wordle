package no.uib.inf102.wordle.controller.AI;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;

import no.uib.inf102.wordle.model.Dictionary;
import no.uib.inf102.wordle.model.word.WordleWord;
import no.uib.inf102.wordle.model.word.WordleWordList;

public class MyStrategy implements IStrategy {

    private Dictionary dictionary;
    private WordleWordList guesses;
    private FrequencyStrategy frequencyStrategy; // Declare FrequencyStrategy here

    public MyStrategy(Dictionary dictionary) {
        this.dictionary = dictionary;
        this.frequencyStrategy = new FrequencyStrategy(dictionary); // Initialize after setting dictionary
        reset();
    }

    @Override
    public String makeGuess(WordleWord feedback) {

        // Eliminate impossible words based on feedback
        if (feedback != null) {
            guesses.eliminateWords(feedback);
        }

        List<String> possibleWords = guesses.possibleAnswers();

        if (possibleWords.size() == 1) {
            return possibleWords.get(0); // Return the only remaining word
        }

        String guess = findBestWord(possibleWords); // Find the best word to guess

        return guess;
    }

    private String findBestWord(List<String> possibleWords) {
        HashMap<Character, Integer>[] frequency = frequencyStrategy.getFrequencyForEachPos(possibleWords);
        String bestWord = null;
        int bestScore = 0;
        List<String> bestWords = new ArrayList<>(); // To hold all words with the best score

        for (String word : possibleWords) {
            int score = 0;
            boolean hasDuplicateLetters = false;
            HashSet<Character> uniqueLetters = new HashSet<>();

            for (int i = 0; i < word.length(); i++) {
                char letter = word.charAt(i);
                score += frequency[i].getOrDefault(letter, 0);

                // Track unique letters for additional scoring
                if (!uniqueLetters.add(letter)) {
                    hasDuplicateLetters = true;
                }
            }

            // Increase score for unique letters
            score += uniqueLetters.size(); // Add bonus for unique letters

            // Penalize the score if there are duplicate letters
            if (hasDuplicateLetters) {
                score /= 2; // Reduce score by half for duplicates
            }

            // Update the best score and track best words
            if (score > bestScore) {
                bestScore = score;
                bestWord = word;
                bestWords.clear(); // Clear previous best words
                bestWords.add(word); // Add new best word
            } else if (score == bestScore) {
                bestWords.add(word); // Add current word to the best words list
            }
        }

        // If multiple best scoring words are found, pick one for elimination
        if (possibleWords.size() > 4) {
            return getEliminationWord(bestWords); // Use a method to determine the elimination word
        }

        return bestWord; // Return the best scoring word
    }

    private String getEliminationWord(List<String> bestWords) {
        int minRemaining = Integer.MAX_VALUE;
        String bestGuess = null;

        for (String guess : bestWords) {
            HashMap<String, List<String>> feedbackGroups = new HashMap<>();

            // Group possible words by feedback for this guess
            for (String possible : guesses.possibleAnswers()) {
                String feedback = generateFeedback(guess, possible);
                feedbackGroups.putIfAbsent(feedback, new ArrayList<>());
                feedbackGroups.get(feedback).add(possible);
            }

            // Calculate the maximum number of remaining words for this guess
            int maxRemaining = feedbackGroups.values().stream().mapToInt(List::size).max().orElse(0);

            // If this guess has fewer remaining possibilities, it's a better guess
            if (maxRemaining < minRemaining) {
                minRemaining = maxRemaining;
                bestGuess = guess;
            }
        }

        return bestGuess != null ? bestGuess : bestWords.get(0); // Fallback in case of no guess found
    }

    // Method to generate feedback based on a guess and a possible answer
    private String generateFeedback(String guess, String possible) {
        StringBuilder feedback = new StringBuilder("-----"); // Assume 5-letter words with '-' for initial feedback

        boolean[] guessed = new boolean[5]; // Track guessed letters
        boolean[] actual = new boolean[5]; // Track actual letters

        // First pass: Check for correct letters in the correct position
        for (int i = 0; i < guess.length(); i++) {
            if (guess.charAt(i) == possible.charAt(i)) {
                feedback.setCharAt(i, 'G'); // 'G' for correct
                guessed[i] = true;
                actual[i] = true;
            }
        }

        // Second pass: Check for correct letters in the wrong position
        for (int i = 0; i < guess.length(); i++) {
            if (!guessed[i]) {
                for (int j = 0; j < possible.length(); j++) {
                    if (!actual[j] && guess.charAt(i) == possible.charAt(j)) {
                        feedback.setCharAt(i, 'Y'); // 'Y' for present but wrong position
                        actual[j] = true; // Mark this letter as used in actual
                        break;
                    }
                }
            }
        }

        return feedback.toString(); // Return the feedback string
    }

    @Override
    public void reset() {
        guesses = new WordleWordList(dictionary); // Reset the word list
    }
}
