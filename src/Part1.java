
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * @author Steven Dao
 * @version 1.0
 *
 * Date: 09/06/2021
 * Purpose: To attempt to decode encrypted messages using substitution cipher logic.
 */
public class Part1 {

    /**
     * Tests all other functions of the application.
     *
     * @param args the command-line arguments to the application
     */
    public static void main(String[] args) {

        // handle I/O exceptions
        try {
            StringBuilder message;

            // loop until the user chooses to quit
            do {
                message = new StringBuilder();

                System.out.print("\nEnter the encrypted message  (`\\q` to stop input / quit):\n >> ");
                BufferedReader bReader = new BufferedReader(new InputStreamReader(System.in));

                String line;

                // read each line of input and add it to the message String
                while (!(line = bReader.readLine()).equals("\\q")) {
                    message.append(line.trim());
                    System.out.print(" >> ");
                }

                // stop the program once the user doesn't input a message
                if (!message.toString().equals("")) {
                    System.out.println("Encrypted message: " + Functions.formatString(message.toString()));

                    // lazy way to estimate total execution time
                    long startTime = System.nanoTime();


                            /*
                                Part 1 - decipher encrypted texts using a simple substitution cipher
                             */
                    String[] result = Part1.decipher(message.toString());


                    // normalize the time given in nanoseconds
                    double endTime = (double) (System.nanoTime() - startTime) / 1_000_000_000;

                    // print out the deciphered message and its key, or no match if none was found
                    if (result.length == 0)
                        System.out.println("- No match found.");
                    else
                        System.out.println("- Most likely match: " + Functions.formatString(result[0]) +
                                "\n\t- Alphabet key: " + result[1]);

                    // print the estimated execution time
                    System.out.println("\t- Execution time: " + endTime + " seconds");
                }

            } while (!message.toString().equals(""));

        } // file could not be found within the directory
        catch (FileNotFoundException e) {
            System.out.println("The text file does not exist.");
        } // file name and/or extension is incorrect, or the file is unreadable
        catch (IOException e) {
            System.out.println("The text file could not be read; please check the file and try again.");
        }
    }

    /**
     * Deciphers an encrypted message using substitution cipher logic and returns the result as a String.
     *
     * @param encodedMessage the encrypted message that we are attempting to decode
     * @return the decoded message as a String
     */
    public static String[] decipher(String encodedMessage) throws IOException {

        // the list of dictionary words; use a set to prevent duplicates
        /*
            Dictionary source from Gwicks:
            http://www.gwicks.net/dictionaries.htm
         */
        Set<String> dictionaryWords = Functions.readWordsFromDictionary("dictionary.txt");


        // the alphabet map of values used for all messages
        String alphabet = "abcdefghijklmnopqrstuvwxyz";


        // the size of the alphabet (prevents multiple calls to .length() function within loops)
        int alphabetSize = alphabet.length();
        // the set of random keys
        Set<String> possibleKeys = new HashSet<>();
        // the current modified alphabet key to build upon using the provided alphabet map
        StringBuilder currentKey = new StringBuilder();
        // the message split and separated by spaces (to uniformly decode messages regardless of formatting)
        String[] splitMessage = encodedMessage.toLowerCase(Locale.ROOT).split(" ");


        // add all rotational shifts to the possible keys
        for (int i = 0; i < alphabetSize; ++i) {
            for (int j = 0; j < alphabetSize; ++j) {
                currentKey.append(alphabet.charAt((i + j) % 26));
            }

            // add the complete key (should be the size of the alphabet)
            possibleKeys.add(currentKey.toString());
            // reset the current key to an empty String
            currentKey.delete(0, currentKey.length());
        }

        // add all simple substitution keys using our dictionary
        for (String word : dictionaryWords) {
            // store the length to prevent excessive method calls within the loop
            int wordLength = word.length();

            // decode each character in the word
            for (int i = 0; i < wordLength; ++i) {
                // add each non-repeating character of the word to the key
                if (!currentKey.toString().contains(String.valueOf(word.charAt(i)))) {
                    currentKey.append(word.charAt(i));
                }
            }

            // add the rest of the alphabet to the end of the key
            for (int i = 0; i < alphabetSize; ++i) {
                // add the alphabet character to the key if it doesn't yet exist
                if (!currentKey.toString().contains(String.valueOf(alphabet.charAt(i)))) {
                    currentKey.append(alphabet.charAt(i));
                }
            }

            // add the complete key (should be the size of the alphabet)
            possibleKeys.add(currentKey.toString());
            // reset the current key to an empty String
            currentKey.delete(0, currentKey.length());
        }

        ArrayList<String> reversedKeys = new ArrayList<>();

        // reverse all previous keys
        for (String key : possibleKeys)
            reversedKeys.add(Functions.reverseString(key));

        // add all reversed keys to the potential key candidates
        possibleKeys.addAll(reversedKeys);

        // remove all whitespace from the message
        String trimmedMessage = encodedMessage.replaceAll(" ", "");

        // attempt to use frequency analysis to generate likely keys
        ArrayList<String> frequencyKey =
                Functions.getKeyByFrequency("letter_frequencies.txt", alphabet, trimmedMessage, 1);

        // convert the alphabet into a list, so we can directly compare the missing elements
        String[] splitAlphabet = alphabet.split("");
        ArrayList<String> alphabetAsList = new ArrayList<>(Arrays.asList(splitAlphabet));

        // get the missing keys from the frequency analysis
        ArrayList<String> missingLetters =
                Functions.getMissingElements(frequencyKey, alphabetAsList);

        // store the size to prevent excessive method calls within the loop
        int frequencySize = frequencyKey.size();

        // add random keys now after getting the initial characters gathered from frequency analysis
        for (long i = 500_000 - possibleKeys.size(); i > 0; --i) {
            // create copies of the frequencies and missing letters, so we can reuse them
            ArrayList<String> frequencyCopy = new ArrayList<>(frequencyKey);
            ArrayList<String> missingCopy = new ArrayList<>(missingLetters);

            // randomly shuffle the leftover characters
            Collections.shuffle(missingCopy);

            // keep track of the missing letters not yet added back to the key
            int count = 0;

            // iterate through the key utilizing frequency analysis
            for (int j = 0; j < frequencySize; ++j) {
                // check if the current key has an empty placeholder at each index
                if (frequencyCopy.get(j).equals("")) {
                    // replace the placeholder with a missing letter
                    frequencyCopy.add(j, missingCopy.get(count));
                    frequencyCopy.remove(j + 1);
                    ++count;
                }
            }

            StringBuilder newKey = new StringBuilder();

            // convert the completed random key to a String
            for (String str : frequencyCopy)
                newKey.append(str);

            // add the randomized key to our list
            possibleKeys.add(newKey.toString());
        }


        // a list of all possible intelligible messages
        ArrayList<ArrayList<String>> possibleMatches = new ArrayList<>();
        // a list of the keys which correspond to the matches above (they should have matching indexes)
        ArrayList<String> keysToMatches = new ArrayList<>();
        // a flag which will let us know whether there was an intelligible message
        boolean foundPossibleMatch = false;

        // decipher the encoded message using all possible keys
        for (String key : possibleKeys) {
            // store the message that will be decoded in a single mutable String
            StringBuilder decodedString = new StringBuilder();

            // decode each word in the message
            for (String word : splitMessage) {
                // store the length to prevent excessive method calls within the loop
                int wordLength = word.length();

                // decode each character in the word
                for (int i = 0; i < wordLength; ++i) {
                    // exclude special characters
                    if (Character.isLetter(word.charAt(i)))
                        /*
                            Use the modified alphabet key to replace each encoded character
                            with the corresponding character in the modified key
                            Then add the decoded character to the decoded message String
                         */
                        decodedString.append(alphabet.charAt(key.indexOf(word.charAt(i))));
                }
            }

            // separate the single string into a list of separated valid words
            ArrayList<String> decodedMessage = Functions.formSentence(decodedString.toString(), dictionaryWords);

            // check if the function returned a full sentence of valid words
            if (decodedMessage.size() > 0) {
                // the words are all valid; add them to our list of intelligible decoded messages
                possibleMatches.add(decodedMessage);
                // add the key which corresponds to the potential match using the same index
                keysToMatches.add(key);
                // update the flag which tells us we have at least one intelligible decoded message
                foundPossibleMatch = true;
            }
        }

        // if we didn't find a valid message, let the user know there are no matches
        if (foundPossibleMatch)
            return Functions.findMostLikelyMatch(possibleMatches, keysToMatches);

        // if we've reached this point, we didn't find any matches; return an empty array
        return new String[0];
    }
}
