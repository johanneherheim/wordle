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

		for (String s : possibleAnswers) {
			boolean isWrong = false;
			int i = 0;

			HashMap<Character, Integer> freqMap = new HashMap<>();
			for (Character ch : s.toCharArray()) {
				freqMap.put(ch, freqMap.getOrDefault(ch, 0) + 1);
			}

			for (WordleCharacter wc : feedback) {
				char letter = wc.letter;
				AnswerType answerType = wc.answerType;

				if (answerType == AnswerType.CORRECT) {
					if (s.charAt(i) != letter) {
						isWrong = true;
						break;
					}
					freqMap.put(letter, freqMap.get(letter) - 1);
				} else if (answerType == AnswerType.MISPLACED) {
					if (s.charAt(i) == letter || freqMap.getOrDefault(letter, 0) <= 0) {
						isWrong = true;
						break;
					}
					freqMap.put(letter, freqMap.get(letter) - 1);
				}
				i++;
			}
			if (!isWrong) {
				newPossibleWords.add(s);
			}
		}
		possibleAnswers = newPossibleWords;
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
