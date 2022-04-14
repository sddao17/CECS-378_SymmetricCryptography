
/**
 * @author Steven Dao
 * @version 1.0
 *
 * Date: 09/20/2021
 * Purpose: To attempt to encrypt and decrypt messages of symmetric cryptography.
 */
public class Main {

    /**
     * Tests all other functions of the application.
     *
     * @param args the command-line arguments to the application
     */
    public static void main(String[] args) {

        /*
            Menu handling
         */


        int input;

        do {

            // Deciphering messages using substitution cipher logic
            System.out.print(
                    "========================================================================" +
                    "\n                         Symmetric Cryptography" +
                    "\n========================================================================" +
                    "\n\nPlease select an option:" +
                    "\n\t1) Decipher messages using brute-force attacks" +
                    "\n\t2) Encrypt messages using simple substitution" +
                    "\n\t3) Decrypt messages using simple substitution" +
                    "\n\t0) Quit" +
                    "\n >> ");

            input = Functions.checkIntRange(0, 3);

            switch (input) {
                /*
                    Part 1 Tests
                 */
                case 1 -> {
                    // Deciphering messages using substitution cipher logic
                    System.out.println(
                            "\n========================================================================" +
                            "\n        Deciphering messages using Substitution cipher logic ..." +
                            "\n========================================================================");
                    Part1.main(args);
                }

                /*
                    Part 2a Tests
                 */
                case 2 -> {
                    // print the results of Part 2a to the console
                    System.out.println(
                            "\n============================================================================" +
                            "\n                       Testing Encryption program ..." +
                            "\n============================================================================");
                    Part2a.main(args);
                }

                    /*
                    Part 2a Tests
                 */
                case 3 -> {
                    // print the results of Part 2b to the console
                    System.out.println(
                            "\n============================================================================" +
                            "\n                       Testing Decryption program ..." +
                            "\n============================================================================");
                    Part2b.main(args);
                }
            }

        } while (input != 0);
    }
}
