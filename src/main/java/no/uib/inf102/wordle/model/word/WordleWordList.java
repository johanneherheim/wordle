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
		List<String> newPossibleAnswers = new ArrayList<>();
		for (String s : possibleAnswers) {
			if (WordleWord.isPossibleWord(s, feedback)) {
				newPossibleAnswers.add(s);
			}
		}
		possibleAnswers = newPossibleAnswers;
	}

	public static String replaceCharAt(String s, int index, char newChar) {
		// Check if the index is valid
		if (index < 0 || index >= s.length()) {
			throw new IllegalArgumentException("Index out of bounds");
		}

		// Convert string to StringBuilder for modification
		StringBuilder sb = new StringBuilder(s);

		// Replace the character at the specified index
		sb.setCharAt(index, newChar);

		// Return the modified string
		return sb.toString();
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
