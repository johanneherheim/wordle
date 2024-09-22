package no.uib.inf102.wordle.controller.AI;

import static no.uib.inf102.wordle.model.word.AnswerType.WRONG;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import no.uib.inf102.wordle.model.Dictionary;
import no.uib.inf102.wordle.model.word.AnswerType;
import no.uib.inf102.wordle.model.word.WordleAnswer;
import no.uib.inf102.wordle.model.word.WordleWord;

public class FrequenctStrategyTest {

	private Dictionary dictionary = new Dictionary();

	@Test
	public void makeGuessBetterThanSaree() {
		FrequencyStrategy ai = new FrequencyStrategy(dictionary);
		String guess = ai.makeGuess(null);
		int count = countGreen(guess);
		assertTrue(dictionary.isLegalGuess(guess), "You must guess a word from the dictionary.");
		if (dictionary.isLegalGuess("saree")) {
			int expectedCount = countGreen("saree");
			assertTrue(expectedCount <= count, "The word ??? gave more green hits than " + guess);
		}
		WordleAnswer ans = new WordleAnswer("berry", dictionary);
	}

	public int countGreen(String guess) {
		int count = 0;
		for (String word : dictionary.getGuessWordsList()) {
			for (int i = 0; i < guess.length(); i++) {
				if (word.charAt(i) == guess.charAt(i))
					count++;
			}
		}
		return count;
	}

	@Test
	public void makeGuessafterScore() {
		AnswerType[] oneYellow = { WRONG, AnswerType.MISPLACED, WRONG, WRONG, WRONG };
		WordleWord feedback = new WordleWord("score", oneYellow);

		FrequencyStrategy ai = new FrequencyStrategy(dictionary);
		String guess = ai.makeGuess(feedback);
		String good = "catch";

		assertTrue(dictionary.isLegalGuess(guess), "You must guess a word from the dictionary.");

		if (dictionary.isLegalGuess("catch")) {
			int count = countGreen(feedback, guess);
			int expectedCount = countGreen(feedback, "catch");
			assertTrue(expectedCount <= count, "The word ??? gave more green hits than " + guess);
		}
	}

	public int countGreen(WordleWord feedback, String guess) {
		int count = 0;
		for (String word : dictionary.getGuessWordsList()) {
			if (WordleWord.isPossibleWord(word, feedback)) {
				for (int i = 0; i < guess.length(); i++) {
					if (word.charAt(i) == guess.charAt(i))
						count++;
				}
			}
		}
		return count;
	}
}
