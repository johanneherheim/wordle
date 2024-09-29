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

  We can ignore O(1) to find the total, and then we have O(k) \* 4 = O(k)

## Task 2 - EliminateStrategy

- `WordleWordList::eliminateWords`: O(m\*k)
  - EliminateWords has a couple of O(1) operations, and then a for-loop with m itarations with a O(k) operation inside. This gives us O(m\*k)

## Task 3 - FrequencyStrategy

- `FrequencyStrategy::makeGuess`: O(m\*k)
  - makeGuess has two helpermethod that has O(m\*k) operations (eliminateWords and findBestWord) and findBestWord has a helpermethod getFrequencyForEachPos that has O(m\*k) operations.
    - getFrequencyForEachPos has the runtime O(k) + O(m) \* O(k) = O(m\*k)
    - findBestWord has the runtime O(m\*k) + O(m) \* O(k) = O(m\*k)
    - makeGuess has the runtime O(m\*k) + O(m\*k) = O(m\*k)

# Task 4 - Make your own (better) AI

I started with the frequency-strategy, and made improvements to it.

1. i added an if-sentence so that double letters are not counted twice in the score. This improved the score from 3.9 to 3.6.
2. i added bonus point for unique letters, this improved the score from 3.6 to 3.595
3. i added a method that uses eliminationwords if many words score the same score. This improved the score from 3.595 to 3.545.

When i was at step 2, i realised that somtimes the ai had four green letters, but there was many possible words left, and therfore i figured it would be better to guess a word that we know is wrong, but has many of the letters we dont know, but are included in the possible words. This is why i added the eliminationwords method. This method takes use of the words with best score, and then chooses the word that has the best worst-case scenario.
