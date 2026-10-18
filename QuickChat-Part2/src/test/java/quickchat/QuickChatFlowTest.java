package quickchat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * QuickChatFlowTest.java
 *
 * End-to-end test of the console flow in {@link Main}: registration, login,
 * the "Welcome to QuickChat." message, the numeric menu, the message for
 * loop, "Coming Soon." for option 2 and quitting with option 3.
 *
 * The test feeds a fixed script of input through a {@link Scanner} and checks
 * the console output, so no keyboard input is needed when the tests run on
 * GitHub Actions.
 */
public class QuickChatFlowTest {

    /** Lines typed by the simulated user. */
    private static final String INPUT = String.join(System.lineSeparator(),
            // Registration (all values valid for Part 1)
            "kyl_1", "Ch&&sec@ke99!", "+27838968976", "Kyle", "Smith",
            // Login
            "kyl_1", "Ch&&sec@ke99!",
            // Two messages this session
            "2",
            // Send Messages -> message 1: sent
            "1",
            "+27718693002", "Hi Mike, can you join us for dinner tonight?",
            "1",
            // message 2: disregarded (and deleted with 0)
            "+27838884567", "Hi Keegan, did you receive the payment?",
            "2", "0",
            // Show recently sent messages
            "2",
            // Quit
            "3") + System.lineSeparator();

    private PrintStream originalOut;
    private ByteArrayOutputStream capturedOut;

    @BeforeEach
    void setUp() {
        Message.resetSentMessages();
        originalOut = System.out;
        capturedOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOut, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        Message.resetSentMessages();
    }

    @Test
    void testFullConsoleFlow() {
        Main.run(new Scanner(INPUT));

        String output = capturedOut.toString(StandardCharsets.UTF_8);

        assertTrue(output.contains("Welcome to QuickChat."), output);
        assertTrue(output.contains("Welcome Kyle, Smith it is great to see you again."),
                output);
        assertTrue(output.contains("Message successfully sent."), output);
        assertTrue(output.contains("Press 0 to delete the message."), output);
        assertTrue(output.contains("Message deleted."), output);
        assertTrue(output.contains("Coming Soon."), output);
        assertTrue(output.contains("Total messages sent: 1"), output);
        assertTrue(output.contains("Goodbye."), output);
    }
}
