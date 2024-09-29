# Runtime Analysis

For each method of the tasks give a runtime analysis in Big-O notation and a description of why it has this runtime.

**If you have implemented new methods not listed you must add these as well, e.g. any helper methods. You need to show how you analyzed any methods used by the methods listed below.**

The runtime should be expressed using these three parameters:

- `n` - number of words in the list allWords
- `m` - number of words in the list possibleWords
- `k` - number of letters in the wordleWords

## Task 1 - matchWord

- `WordleAnswer::matchWord`: O(k)

  - O(1) - get wordlength
  - O(1) - check for correct word length
  - O(k) - turn string word into char array
  - O(1) - create AnswerType[] object for feedback
  - O(1) - create HashMap object for word
  - O(k) - iterate through answerArray and add to hashmap
  - O(k) - iterate through guess and check if the letters are correct
  - O(k) - iterate through guess and check if the letters are misplaced or wrong
  - O(k) - return new wordleword object with feedback

  We can ignore O(1) to find the total, and then we have three for-loops over wordleword-length, which gives us 4 \* O(k), and a convertion from string to char[] which also is O(k).

  In total, `WordleAnswer::matchWord` har the runtime O(k) \* 4 = O(k)

## Task 2 - EliminateStrategy

- `WordleWordList::eliminateWords`: O(m\*k)

  - O(1) - create a new list
  - O(m) - iterate through all possible words
    - O(k) - check if the word is possible with `WordleWord::isPossible`
    - O(1) - add the word to the new list if it is possible
  - O(1) - set the new list as the possible words

  `WordleWordList::eliminateWords` has a couple of O(1) operations, and then a for-loop with m itarations with a O(k) operation inside. This gives us O(m\*k)

  O(1) + O(m) \* O(k) + O(1) = O(m\*k)

## Task 3 - FrequencyStrategy

- `FrequencyStrategy::makeGuess`: O(m\*k)

  `FrequencyStrategy::makeGuess` uses four helper methods in total:

  - `FrequencyStrategy::eliminateWords`: O(m\*k) (known from Task 2)
  - `FrequencyStrategy::findBestWord`: O(m\*k) + O(m) \* O(k) = O(m\*k)
    - O(m \* k) - `FrequencyStrategy::getFrequencyForEachPos`
    - O(m) - iterate through all possible words
      - O(k) - give score to Word
      - O(1) - check if the word is better than the best word
    - O(1) - return the best word
  - `FrequencyStrategy::getFrequencyForEachPos` : O(k) + O(m) \* O(k) = O(m\*k)
    - O(k) - iterate through all positions in the word
    - O(m) - iterate through all possible words
      - O(k) - calculate the score for each word
  - `FrequencyStrategy::giveScoreToWord`: O(k)
    - O(1) - initialize score
    - O(k) - iterate through all positions in the word
      - O(1) - increase score with frequency of the letter

  `FrequencyStrategy::makeGuess` has a couple of O(1) operations, and then a for-loop with m itarations with a O(k) operation inside. This gives us O(m\*k)

  This gives us the runtime O(m\*k) + O(m\*k) + O(m\*k) + O(k) = O(m\*k)

# Task 4 - Make your own (better) AI

I started with the frequency-strategy, and made improvements to it.

First i added an if-sentence in `MyStrategy::giveScoreToWord` so that double letters are not counted twice in the score. This improved the score from 3.9 to 3.6. Then i added bonus point for unique letters, this improved the score from 3.6 to 3.595.

Lastly i added a method that uses eliminationwords if many words score the same score. This improved the score from 3.595 to 3.545. This method works by using the words with the best score, and then chooses the word that has the best worst-case scenario. This ensures that we dont get stuck with someting like this: might, light, fight, right, tight, sight, night. I added a limit to minimize the length of bestWords, so the method does not take too long to run.
