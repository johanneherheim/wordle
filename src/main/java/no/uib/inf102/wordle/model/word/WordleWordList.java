package no.uib.inf102.wordle.model.word;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

import no.uib.inf102.wordle.model.Dictionary;

/**
 * This class describes a structure of two lists for a game of Wordle: The list
 * of words that can be used as guesses and the list of words that can be
 * possible answers.
 */
public class WordleWordList {

	/**
	 * All words in the game. These words can be used as guesses.
	 */
	private Dictionary allWords;

	/**
	 * A subset of <code>allWords</code>. <br>
	 * </br>
	 * These words can be the answer to a wordle game.
	 */
	private List<String> possibleAnswers;

	/**
	 * Create a WordleWordList that uses the full words and limited answers of the
	 * GetWords class.
	 */
	public WordleWordList(Dictionary dictionary) {
		this.allWords = dictionary;
		this.possibleAnswers = new ArrayList<>(dictionary.getAnswerWordsList());
	}

	/**
	 * Get the list of all guessing words.
	 * 
	 * @return all words
	 */
	public Dictionary getAllWords() {
		return allWords;
	}

	/**
	 * Returns the list of possible answers.
	 * 
	 * @return
	 */
	public List<String> possibleAnswers() {
		return Collections.unmodifiableList(possibleAnswers);
	}

	/**
	 * Eliminates words from the possible answers list using the given
	 * <code>feedback</code>
	 * 
	 * @param feedback
	 */
	public void eliminateWords(WordleWord feedback) {

		List<String> newPossibleWords = new ArrayList<>();

		HashMap<Character, Integer> misplaced = new HashMap<>();
		HashMap<Integer, Character> correctResult = new HashMap<>();

		int i = 0;
		for (WordleCharacter wc : feedback) {
			if (wc.answerType == AnswerType.MISPLACED) {
				misplaced.put(wc.letter, misplaced.getOrDefault(wc.letter, 0) + 1);
			}
			if (wc.answerType == AnswerType.CORRECT) {
				correctResult.put(i, wc.letter);
			}
			i++;
		}

		for (String answer : possibleAnswers) {
			newPossibleWords.add(answer);
			boolean isRemoved = false;

			char[] answerArray = answer.toCharArray();

			HashMap<Character, Integer> frequency = new HashMap<>();
			for (Character c : answerArray) {
				frequency.put(c, frequency.getOrDefault(c, 0) + 1);
			}

			for (Character c : misplaced.keySet()) {
				if (!(misplaced.get(c) == frequency.getOrDefault(c, 0)) &&
						!newPossibleWords.isEmpty()) {
					newPossibleWords.remove(newPossibleWords.size() - 1);
					isRemoved = true;
				}
			}

			if (!isRemoved) {
				for (int j : correctResult.keySet()) {
					if (!(answerArray[j] == correctResult.get(j))) {
						newPossibleWords.remove(newPossibleWords.size() - 1);
					}
				}
			}
		}
		possibleAnswers = newPossibleWords;

	}

	/**
	 * Returns the amount of possible answers in this WordleWordList
	 * 
	 * @return size of
	 */
	public int size() {
		return possibleAnswers.size();
	}

	/**
	 * Removes the given <code>answer</code> from the list of possible answers.
	 * 
	 * @param answer
	 */
	public void remove(String answer) {
		possibleAnswers.remove(answer);
	}

	/**
	 * Returns the word length in the list of valid guesses.
	 * 
	 * @return
	 */
	public int wordLength() {
		return allWords.WORD_LENGTH;
	}

}
