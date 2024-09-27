package no.uib.inf102.wordle.controller.AI;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

import no.uib.inf102.wordle.model.Dictionary;
import no.uib.inf102.wordle.model.word.WordleWord;
import no.uib.inf102.wordle.model.word.WordleWordList;

public class MyStrategy implements IStrategy {

    private Dictionary dictionary;
    private WordleWordList guesses;
    private FrequencyStrategy frequencyStrategy;

    public MyStrategy(Dictionary dictionary) {
        this.dictionary = dictionary;
        this.frequencyStrategy = new FrequencyStrategy(dictionary);
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

        String guess = findBestWord(possibleWords);

        return guess;
    }

    private String findBestWord(List<String> possibleWords) { // O(m*k)
        HashMap<Character, Integer>[] frequency = frequencyStrategy.getFrequencyForEachPos(possibleWords); // O(m*k)
        String bestWord = null;
        int bestScore = 0;
        List<String> bestWords = new ArrayList<>();

        for (String word : possibleWords) { // O(m) * O(k)
            int score = 0;
            boolean hasDuplicateLetters = false;
            HashSet<Character> uniqueLetters = new HashSet<>();

            for (int i = 0; i < word.length(); i++) { // O(k)
                char letter = word.charAt(i);
                score += frequency[i].getOrDefault(letter, 0);

                if (!uniqueLetters.add(letter)) {
                    hasDuplicateLetters = true;
                }
            }

            score += uniqueLetters.size(); // bonuspoeng

            if (hasDuplicateLetters) {
                score /= 2; // minuspoeng
            }

            if (score > bestScore) {
                bestScore = score;
                bestWord = word;
                bestWords.clear();
                bestWords.add(word);
            } else if (score == bestScore) {
                bestWords.add(word);
            }
        }

        if (possibleWords.size() > 2) {
            System.out.println("EliminationWord ...");
            return getEliminationWord(bestWords); // O(m*k)
        }
        System.out.println("bestword:" + bestWord);
        return bestWord;
    }

    private String getEliminationWord(List<String> bestWords) { // O(m*k)
        int minRemaining = Integer.MAX_VALUE;
        String bestGuess = null;

        for (String guess : bestWords) { // O(j), less than O(m) but varies
            HashMap<String, List<String>> feedbackGroups = new HashMap<>();

            for (String possible : guesses.possibleAnswers()) { // O(m)
                String feedback = generateFeedback(guess, possible); // O(k)
                feedbackGroups.putIfAbsent(feedback, new ArrayList<>());
                feedbackGroups.get(feedback).add(possible);
            }

            int maxRemaining = feedbackGroups.values().stream().mapToInt(List::size).max().orElse(0);

            if (maxRemaining < minRemaining) {
                minRemaining = maxRemaining;
                bestGuess = guess;
            }
        }

        return bestGuess;
    }

    private String generateFeedback(String guess, String possible) { // O(k)
        StringBuilder feedback = new StringBuilder("-----");

        boolean[] guessed = new boolean[5];
        boolean[] actual = new boolean[5];

        for (int i = 0; i < guess.length(); i++) {
            if (guess.charAt(i) == possible.charAt(i)) {
                feedback.setCharAt(i, 'G');
                guessed[i] = true;
                actual[i] = true;
            }
        }

        for (int i = 0; i < guess.length(); i++) {
            if (!guessed[i]) {
                for (int j = 0; j < possible.length(); j++) {
                    if (!actual[j] && guess.charAt(i) == possible.charAt(j)) {
                        feedback.setCharAt(i, 'Y');
                        actual[j] = true;
                        break;
                    }
                }
            }
        }

        return feedback.toString();
    }

    @Override
    public void reset() {
        guesses = new WordleWordList(dictionary); // Reset the word list
    }
}
