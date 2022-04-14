
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * @author Steven Dao
 * @version 1.3
 *
 * Date: 09/07/2021
 * Purpose: Performs a variety of miscellaneous functions.
 */
public class Functions {

    /**
     * Checks if the inputted value is an integer within the specified range (ex: 1-10)
     *
     * @param low  lower bound of the range
     * @param high upper bound of the range
     * @return the valid input
     */
    public static int checkIntRange(int low, int high) {
        Scanner in = new Scanner(System.in);
        boolean valid = false;
        int inputToInt = 0;

        while (!valid) {
            String input = in.nextLine();

            try {
                // if the input is not an int, it will trigger a NumberFormatException
                inputToInt = Integer.parseInt(input);

                if (inputToInt <= high && inputToInt >= low) {
                    valid = true;
                } else {
                    System.out.print("Input does not fall within the range; please try again: ");
                }
            } catch (NumberFormatException e) {
                System.out.print("Invalid input; please try again: ");
            }
        }
        return inputToInt;
    }

    /**
     * Checks if the inputted String is a valid key of the alphabet.
     *
     * @param alphabet the alphabet key to verify against the input key
     * @return the valid input key
     */
    public static String checkKey(String alphabet) {
        Scanner in = new Scanner(System.in);
        String input;
        boolean valid = false;

        do {
            input = in.nextLine();

            // allow the default key and quit options to be picked
            if (input.equals("1") || input.equals("2") || input.equals("3") || input.equals("\\q"))
                return input;

            // separate each character he modified key and original key, so we can compare them
            List<String> splitKey = Arrays.asList(input.split(""));
            List<String> splitAlphabet = Arrays.asList(alphabet.split(""));

            // if the elements and container size all match with each other, the key is valid
            if (splitAlphabet.containsAll(splitKey) && splitKey.containsAll(splitAlphabet)
                    && splitKey.size() == splitAlphabet.size())
                valid = true;
            else {
                System.out.print("Invalid key; please try again: ");
            }
        } while (!valid);

        return input;
    }

    /**
     * Returns the contents of the Strings separated by spaces and carried into the next line with
     * every 16th word.
     *
     * @param str the String separated by spaces
     * @return the formatted single String
     */
    public static String formatString(String str) {
        // convert to an array to remove whitespace
        String[] splitStr = str.split(" ");
        // store the message within a String so we can return the result
        StringBuilder message = new StringBuilder();

        // loop through the Strings in the ArrayList
        for (int i = 0; i < splitStr.length; ++i) {
            // add each String to the total message String
            message.append(splitStr[i]);

            /*
                For every 15 words that does not lead to the end of the list,
                skip to the next line and format the spacing
             */
            if (i != splitStr.length - 1 && i % 16 == 15)
                message.append("\n                   ");
                // also add a space between each String if it is not the last one
            else if (i < splitStr.length - 1)
                message.append(" ");
        }

        // return our converted message as a String
        return message.toString();
    }

    /**
     * Returns a list of words such that the message likely forms a sentence.
     *
     * @param decodedString the decoded message in the form of a single String
     * @param dictionaryWords the list of words to check against the decoded String
     * @return the list of words in a likely form of a sentence
     */
    public static ArrayList<String> formSentence(String decodedString, Set<String> dictionaryWords) {

        // store the length to prevent excessive method calls within the loop
        int decodedStringLength = decodedString.length();
        // the index of each found 'longest' word
        int placeholder = 0;
        // a consecutive count of 'words' consisting of only 1 letter
        int oneLetterCount = 0;
        // the decoded words stored into a list
        ArrayList<String> decodedMessage = new ArrayList<>();
        // the current substring that we are considering to be a word
        StringBuilder currentString = new StringBuilder();
        // the longest word within the substring that we have found so far
        String currentLongestString = "";
        // a flag that tells us whether we found a valid word for the current substring
        boolean foundNewWord = false;

        // loop through the message until we've found a word throughout each substring of the entire message
        while (placeholder < decodedStringLength) {
            // start at the beginning of the message, then begin at the end of each found word
            for (int i = placeholder; i < decodedStringLength; ++i) {
                // add the current character to the current substring
                currentString.append(decodedString.charAt(i));

                // check if the current substring is a valid word in our dictionary
                if (dictionaryWords.contains(currentString.toString())) {
                    // if it's a one-letter word, add to the consecutive count
                    if (currentString.length() == 1)
                        ++oneLetterCount;
                    else
                        // there are no current chains of consecutive one-letter words
                        oneLetterCount = 0;

                    // optimization: a sentence with 3 consecutive one-letter words is likely not a valid sentence
                    if (oneLetterCount > 3) {
                        // add the rest of the characters to the current string (should be an invalid word)
                        currentString.append(decodedString, i, decodedString.length() - 1);
                        // end the loop
                        i = decodedStringLength;
                    }
                    // the sentence is most likely still valid
                    else {
                        // the current string is our current longest word
                        currentLongestString = currentString.toString();
                        // we have at least one valid word
                        foundNewWord = true;
                        // update the index in case this is ultimately the longest word
                        placeholder = i + 1;
                    }
                    // optimization: speed the search by limiting the length of added words to a certain # of characters
                } else if (currentString.length() > 20) {
                    // add the rest of the characters to the current string (should be an invalid word)
                    currentString.append(decodedString, i, decodedString.length() - 1);
                    // end the loop
                    i = decodedStringLength;
                }
            }

            // we found a valid word from our dictionary
            if (foundNewWord) {
                // add it to our list of valid words
                decodedMessage.add(currentLongestString);
                // reset the current string to an empty String
                currentString.delete(0, currentString.length());
                // reset the flag for finding a valid word
                foundNewWord = false;
            }
            // we did not find another valid word
            else {
                // return an empty ArrayList since it will fail the dictionary check anyway
                decodedMessage.clear();
                // end the loop
                placeholder = decodedStringLength;
            }
        }

        return decodedMessage;
    }

    /**
     * Returns the most likely intelligible message and its key as an array of Strings by finding the message
     * with the highest average word length.
     *
     * @param possibleMatches the complete list of all possible decoded messages
     * @param keysToMatches the keys of which the index matches the list of decoded messages
     * @return the most likely matching message and key for the decoded message
     */
    public static String[] findMostLikelyMatch(ArrayList<ArrayList<String>> possibleMatches,
                                               ArrayList<String> keysToMatches) {

        // store the size to prevent excessive method calls within the loop
        int numOfPossibleMatches = possibleMatches.size();
        // the average word length of the most likely intelligible words to be the original phrase
        double highestAvgNumOfLetters = 0;
        // the index of the most likely original phrase
        int index = 0;

        // iterate through all possible matches to the original phrase
        for (int i = 0; i < numOfPossibleMatches; ++i) {
            // store the current decrypted list of words that we are currently looking at for readability
            ArrayList<String> currentMessage = possibleMatches.get(i);
            // the average length of the words within the current list
            double currentAvg = 0;

            // iterate through all words in each possible match
            for (String word : currentMessage)
                // add the length of each word to the sum
                currentAvg += word.length();

            // divide the sum by the number of words to compute the average
            currentAvg /= currentMessage.size();

            // replace the current highest if the current average is higher
            if (highestAvgNumOfLetters < currentAvg) {
                highestAvgNumOfLetters = currentAvg;
                // also update the index flag of the most likely intelligible message
                index = i;
            }
        }

        // return the most likely match and its corresponding key
        return new String[] {getArrayListAsString(possibleMatches.get(index)), keysToMatches.get(index)};
    }

    /**
     * Returns the contents of the Strings within the ArrayList as a single String, separated by spaces
     * and carried into the next line every 16th word.
     *
     * @param arrayList the ArrayList of type String
     * @return the contents of the ArrayList as a String
     */
    public static String getArrayListAsString(ArrayList<String> arrayList) {

        // store the message within a String so we can return the result
        StringBuilder message = new StringBuilder();
        // store the length to prevent excessive method calls within the loop
        int arrayListSize = arrayList.size();

        // loop through the Strings in the ArrayList
        for (int i = 0; i < arrayListSize; ++i) {
            // add each String to the total message String
            message.append(arrayList.get(i));

            if (i < arrayListSize - 1)
                message.append(" ");
        }

        // return our converted message as a String
        return message.toString();
    }

    /**
     * Returns the contents of the Strings within the ArrayList as a single String, separated by spaces.
     *
     * @param array the array of Strings to convert into a single String
     * @return the formatted single String
     */
    public static String getArrayAsString(String[] array) {
        // store the message within a String so we can return the result
        StringBuilder message = new StringBuilder();

        // loop through the Strings in the ArrayList
        for (int i = 0; i < array.length; ++i) {
            // add each String to the total message String
            message.append(array[i]);

            if (i < array.length - 1)
                message.append(" ");
        }

        // return our converted message as a String
        return message.toString();
    }

    /**
     * Returns a modified alphabet key within an ArrayList by analyzing the most frequently-occurring letters.
     * The amount of most-frequent elements returned within the key depends on the input parameter.
     *
     * @param fileName the name of the file to read the frequencies of each letter
     * @param alphabet the alphabet map that we are comparing the frequency to
     * @param message the message to analyze
     * @param numOfLetters the number of most frequent letters to return within the ArrayList
     * @return the modified alphabet key
     */
    public static ArrayList<String> getKeyByFrequency(String fileName, String alphabet,
                                                      String message, int numOfLetters) throws IOException {

        // the frequency of letters from the `.txt` file (most to least frequent)
        ArrayList<Character> letterFrequencies = new ArrayList<>();
        // the letters in the message sorted by frequency (most to least frequent)
        ArrayList<Character> messageFrequencies = new ArrayList<>();
        // store the length to prevent excessive method calls within the loop
        int messageLength = message.length();
        // the current line of that we are reading
        String line;

        // start reading from the dictionary file
        try (BufferedReader bReader = new BufferedReader(new FileReader(fileName))) {
            // continue to check each line until we've reached the end of the file
            while ((line = bReader.readLine()) != null) {
                // split the line into separate elements
                String[] splitLine = line.split(" ");

                // add each letter in order of its frequency (most to least)
                letterFrequencies.add(splitLine[0].charAt(0));
            }
        }

        // store the length to prevent excessive method calls within the loop
        int alphabetSize = alphabet.length();
        // count the number of time each letter appears in the message
        ArrayList<Integer> frequencies = new ArrayList<>(Collections.nCopies(alphabetSize, 0));

        // iterate through each character in the message
        for (int i = 0; i < messageLength; ++i) {
            // store the index matching the character's normalized ASCII value
            int index = (int) message.charAt(i) - 97;

            // increment the amount within the frequency ArrayList
            frequencies.add(index, frequencies.get(index) + 1);
            frequencies.remove(index + 1);
        }

        // store a copy of the frequencies in sorted, descending order
        ArrayList<Integer> reversedFrequencies = reverseSort(frequencies);

        // order the message frequencies from highest to lowest starting from index 0
        for (int i = 0; i < alphabetSize; ++i) {
            // get the highest frequency within the list and store its index
            int highestFreq = Functions.getMax(frequencies);
            int highestFreqIndex = frequencies.indexOf(highestFreq);

            // add each relative max to the frequency list, putting them in descending order
            frequencies.add(highestFreqIndex, -1);
            frequencies.remove(highestFreqIndex + 1);

            messageFrequencies.add(alphabet.charAt(highestFreqIndex));
        }

        // the modified alphabet key in the form of an ArrayList that we will convert
        ArrayList<String> keyAsList = new ArrayList<>(Collections.nCopies(alphabetSize, ""));

        // convert each most-frequent character in the message frequencies list to its modified alphabet form
        for (int i = 0; i < numOfLetters; ++i) {
            // normalize and convert each letter to our alphabet range of 0-26
            int txtFrequency = (int) letterFrequencies.get(i) - 97;
            int modifiedFrequency = (int) messageFrequencies.get(i) - 97;

            // add the corresponding characters to the modified alphabet and remove the empty placeholder
            keyAsList.add(txtFrequency, Character.toString(alphabet.charAt(modifiedFrequency)));
            keyAsList.remove(txtFrequency + 1);
        }

        // store the length to prevent excessive method calls within the loop
        int keySize = keyAsList.size() - 1;

        // similar to the loop above, except now we add the least frequent letters
        for (int i = keySize; i > keySize - numOfLetters; --i) {
            // normalize and convert each letter to our alphabet range of 0-26
            int txtFrequency = (int) letterFrequencies.get(i) - 97;
            int modifiedFrequency = (int) messageFrequencies.get(i) - 97;

            // add the corresponding characters to the modified alphabet and remove the empty string
            keyAsList.add(txtFrequency, Character.toString(alphabet.charAt(modifiedFrequency)));
            keyAsList.remove(txtFrequency + 1);

            // if the frequency of the current character is 0, its placement is set; we can check the next letter
            if (reversedFrequencies.get(i) == 0)
                numOfLetters += 1;
        }

        return keyAsList;
    }

    /**
     * Returns the maximum value in the ArrayList.
     *
     * @param inputArray the ArrayList of integer values
     * @return the max value in the ArrayList
     */
    public static int getMax(ArrayList<Integer> inputArray) {

        // the maximum value to return
        int currentMax = 0;

        // iterate through all numbers in the ArrayList
        for (int number : inputArray) {
            // if the current max is less than the iterated number, it's new current max
            if (currentMax < number)
                currentMax = number;
        }

        // return the highest value
        return currentMax;
    }

    /**
     * Returns a list of elements from the second list which were not present in the first list.
     *
     * @param arr1 the source list to compare
     * @param arr2 the target list to check against the source
     * @return the missing elements from the source list
     */
    public static ArrayList<String> getMissingElements(List<String> arr1, List<String> arr2) {

        ArrayList<String> missingElements = new ArrayList<>();

        // iterate through the second list
        for (String str : arr2)
            if (!arr1.contains(str))
                missingElements.add(str);

        return missingElements;
    }

    /**
     * Reads from a dictionary file, parses the data, and then returns the words of the dictionary within a set.
     *
     * @param fileName the name of the dictionary file to read from
     * @return the words as a Set of Strings
     */
    public static Set<String> readWordsFromDictionary(String fileName) throws IOException {

        // initialize the variables needed to retrieve the data
        Set<String> retrievedWords = new HashSet<>();
        // a flag that tells us to exit when the word has a non-letter character
        boolean hasNonLetterChar = false;
        // the current line of that we are reading
        String line;

        // start reading from the dictionary file
        try (BufferedReader bReader = new BufferedReader(new FileReader(fileName))) {
            // continue to check each line until we've reached the end of the file
            while ((line = bReader.readLine()) != null) {
                // separate each word in the definition and term by their spaces
                String[] splitLine = line.split(" ");

                // skip the lines that are empty, if there are any
                if (splitLine.length > 0) {
                    // iterate through the list of potential words
                    for (String potentialWord : splitLine) {
                        // the first word should be the dictionary word
                        String word = potentialWord.toLowerCase(Locale.ROOT);
                        // store the length to prevent excessive method calls within the loop
                        int wordLength = word.length();

                        // separate each character so we can check if there is a non-letter character
                        for (int i = 0; i < wordLength; ++i) {
                            // if it has a non-letter character, update the flag
                            if (!Character.isLetter(word.charAt(i)))
                                hasNonLetterChar = true;
                        }

                        // if the word has all valid letters, add the word to the ArrayList
                        if (!hasNonLetterChar)
                            retrievedWords.add(word);

                        // reset the flag
                        hasNonLetterChar = false;
                    }
                }
            }
        }

        return retrievedWords;
    }

    /**
     * Returns a sorted array in descending order.
     *
     * @param inputArray the ArrayList to reverse sort
     * @return the input ArrayList sorted in descending order
     */
    public static ArrayList<Integer> reverseSort(ArrayList<Integer> inputArray) {

        // create an ArrayList and sort it in ascending order
        ArrayList<Integer> sortedArray = new ArrayList<>(inputArray);
        Collections.sort(sortedArray);

        // create another ArrayList which will store the ordered elements in reverse order
        ArrayList<Integer> reverseSortedArr = new ArrayList<>();
        int listSize = sortedArray.size();

        // add elements starting from the end of the sorted ArrayList
        for (int i = listSize - 1; i >= 0; --i)
            reverseSortedArr.add(sortedArray.get(i));

        // return the reverse sorted ArrayList
        return reverseSortedArr;
    }

    /**
     * Reverses a copy of a String and returns the result.
     *
     * @param inputString the provided String
     * @return a copy of the input String in reverse
     */
    public static String reverseString(String inputString) {

        // store the length to prevent excessive method calls within the loop
        int inputStringLength = inputString.length();
        // store the resulting String so we can return it
        StringBuilder result = new StringBuilder();

        // add the String back in reverse order
        for (int i = inputStringLength - 1; i >= 0 ; --i) {
            result.append(inputString.charAt(i));
        }

        // return the reversed String
        return result.toString();
    }
}
