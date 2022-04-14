
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;

/**
 * @author Steven Dao
 * @version 1.0
 *
 * Date: 09/20/2021
 * Purpose: To create a method that encrypts a given message using the provided key.
 */
public class Part2a {

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
                System.out.print("\nEnter the message to encrypt  (`\\q` to stop input / quit):\n >> ");
                BufferedReader bReader = new BufferedReader(new InputStreamReader(System.in));

                String line;

                // read each line of input and add it to the message String
                while (!(line = bReader.readLine()).equals("\\q")) {
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
                Part 2a - encrypt the message using the provided key
            */
                String encryptedMessage = encrypt(message.toString(), currentKey);


                // output each form of each message, formatted so that every 16th word is carried into the next line
                System.out.println(
                        "Original message: " + Functions.formatString(
                                Functions.getArrayAsString(message.toString().split(" "))) +
                                "\nEncrypted message: " + Functions.formatString(encryptedMessage) +
                                "\n\t- Alphabet key: " + currentKey);

            } // file name and/or extension is incorrect, or the file is unreadable
            catch (IOException e) {
                System.out.println("The text file could not be read; please check the file and try again.");
            }
        } while (true);
    }

    /**
     * Encrypts a given message using the provided key.
     *
     * @param message the original message to encrypt
     * @param key the modified alphabet key used to encrypt the message
     * @return the encrypted message as a String
     */
    public static String encrypt(String message, String key) {

        // convert the original message to all lowercase for standardization
        message = message.toLowerCase(Locale.ROOT);
        // store all letter characters within a string when standardizing the encrypted message
        StringBuilder encodedString = new StringBuilder();
        // store the length to prevent excessive method calls within the loop
        int messageLength = message.length();
        // set a counter which resets after every 5th letter (for standard encryption standards)
        int count = 0;

        // iterate through each character in the message
        for (int i = 0; i < messageLength; ++i) {
            // only add characters which are letters for standardization
            if (Character.isLetter(message.charAt(i))) {
                encodedString.append(message.charAt(i));
                // update the count flag for adding spaces
                ++count;
            }

            // add a space every 5th letter
            if (count == 5) {
                encodedString.append(" ");
                count = 0;
            }
        }

        // store the length to prevent excessive method calls within the loop
        int encodedStringLength = encodedString.length();

        // encode each character in the converted message
        for (int i = 0; i < encodedStringLength; ++i) {
            /*
                Use the modified alphabet key to replace each standardized character with the corresponding
                character in the modified key, then add the encoded character to the StringBuilder
            */
            if (Character.isLetter(encodedString.charAt(i)))
                encodedString.replace(i, i + 1,
                        Character.toString(key.charAt((int) encodedString.charAt(i) - 97)));
        }

        // format the String to skip to the next line every 15th word and return it
        return Functions.getArrayAsString(encodedString.toString().split(" "));
    }
}
