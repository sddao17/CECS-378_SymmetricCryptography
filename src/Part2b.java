
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Set;

/**
 * @author Steven Dao
 * @version 1.0
 *
 * Date: 09/20/2021
 * Purpose: To create a method that decrypts a given message using the provided key.
 */
public class Part2b {

    /**
     * Tests all other functions of the application.
     *
     * @param args the command-line arguments to the application
     */
    public static void main(String[] args) {

        // set default keys as options for the user
        String[] defaultKeys = new String[] {
                "zyxwvutsrqponmlkjihgfedcba",
                "lazybcdefghijkmnopqrstuvwx",
                "mnbvcxzlkjhgfdsapoiuytrewq"
        };

        StringBuilder message;

        // loop until the user chooses to quit
        do {
            message = new StringBuilder();
            String inputKey;

            try {
                System.out.print("\nEnter the message to decrypt  (`\\q` to stop input / quit):\n >> ");
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

                String line;

                // read each line of input and add it to the message String
                while (!(line = br.readLine()).equals("\\q")) {
                    message.append(line.trim());
                    System.out.print(" >> ");
                }

                // stop the program once the user doesn't input a message
                if (message.toString().equals(""))
                    break;

                System.out.print("Enter the key  (1-3 for default keys, `\\q` to quit):\n >> ");
                inputKey = Functions.checkKey("abcdefghijklmnopqrstuvwxyz");

                // stop the program once the user doesn't input a key
                if (inputKey.equals("\\q"))
                    break;

                // if the user chooses a default key, choose one from the list of keys
                String currentKey = switch (inputKey) {
                    case "1" -> defaultKeys[0];
                    case "2" -> defaultKeys[1];
                    case "3" -> defaultKeys[2];
                    default -> inputKey;
                };


                /*
                    Part 2b - decrypt the message using the provided key
                */
                String decryptedMessage = decrypt(message.toString(), currentKey);


                // output each form of each message, formatted so that every 16th word is carried into the next line
                System.out.println(
                        "Encrypted message: " + Functions.formatString(message.toString()) +
                        "\nDecrypted message: " + Functions.formatString(decryptedMessage) +
                        "\n\t- Alphabet key: " + currentKey);

            } // file could not be found within the directory
            catch (FileNotFoundException e) {
                System.out.println("The text file does not exist.");
            } // file name and/or extension is incorrect, or the file is unreadable
            catch (IOException e) {
                System.out.println("The text file could not be read; please check the file and try again.");
            }

        } while (true);
    }

    /**
     * Decrypts a given message using the provided key.
     *
     * @param encodedMessage the encrypted message to decrypt
     * @param key            the modified alphabet key used to decrypt the message
     * @return the decrypted message as a String
     */
    public static String decrypt(String encodedMessage, String key) throws IOException {

        // split the message to remove whitespace
        String[] splitMessage = encodedMessage.split(" ");
        // convert to StringBuilder, so we can eventually replace characters within the String
        StringBuilder decodedString = new StringBuilder();

        // add all characters together into a single String without whitespace
        for (String str : splitMessage)
            decodedString.append(str);

        // the original alphabet key
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        // store the length to prevent excessive method calls within the loop
        int decodedStringLength = decodedString.length();

        // encode each character in the converted message
        for (int i = 0; i < decodedStringLength; ++i) {
            /*
                Use the modified alphabet key to replace each standardized character with the corresponding
                character in the modified key, then add the encoded character to the StringBuilder
            */
            if (Character.isLetter(decodedString.charAt(i)))
                decodedString.replace(i, i + 1,
                        Character.toString(alphabet.charAt(key.indexOf(decodedString.charAt(i)))));
        }

        // the list of dictionary words; use a set to prevent duplicates
        /*
            Dictionary source from Gwicks:
            http://www.gwicks.net/dictionaries.htm
         */
        Set<String> dictionaryWords = Functions.readWordsFromDictionary("dictionary.txt");

        // separate the single String into a list of valid words from our dictionary
        ArrayList<String> decodedWords = Functions.formSentence(decodedString.toString(), dictionaryWords);

        /*
            Format only the list of valid words in ou message (excluding the key) into a
            properly spaced sentence and return it
         */
        return Functions.getArrayAsString(new String[] {
                Functions.findMostLikelyMatch(new ArrayList<>() {{add(decodedWords);}},
                new ArrayList<>() {{add(key);}})[0]
        });
    }
}