package quickchat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * MessageTest.java
 *
 * Unit tests for {@link Message}, using the test data supplied in the
 * PROG5121 Part 2 brief so that the results line up with the marking guide.
 *
 * Test Case 1 (from the brief):
 *   Message ID     auto generated -> "0012345678" in these tests so that the
 *                  expected hash is predictable
 *   Recipient      +27718693002
 *   Message        "Hi Mike, can you join us for dinner tonight?"
 *   Expected hash  "00:0:HITONIGHT"
 */
public class MessageTest {

    /** Test Case 1 data from the brief. */
    private static final String MESSAGE_ID = "0012345678";
    private static final int MESSAGE_NUMBER = 0;
    private static final String RECIPIENT = "+27718693002";
    private static final String MESSAGE_TEXT =
            "Hi Mike, can you join us for dinner tonight?";

    /** A temporary folder so JSON tests never write into the project. */
    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        Message.resetSentMessages();
        Message.useStore(new MessageStore(tempDir.resolve("messages.json")));
    }

    // ------------------------------------------------------------------
    // Message length (250 characters)
    // ------------------------------------------------------------------

    @Test
    void testMessageWithinCharacterLimit() {
        Message message = new Message(MESSAGE_ID, MESSAGE_NUMBER, RECIPIENT,
                MESSAGE_TEXT);
        assertEquals("Message ready to send.", message.checkMessageLength());
    }

    @Test
    void testMessageExceedsCharacterLimit() {
        String longMessage = "a".repeat(Message.MAX_MESSAGE_LENGTH + 10);
        Message message = new Message(MESSAGE_ID, MESSAGE_NUMBER, RECIPIENT,
                longMessage);
        assertEquals("Message exceeds 250 characters by 10; please reduce the size.",
                message.checkMessageLength());
    }

    // ------------------------------------------------------------------
    // Recipient cell number (reuses the Part 1 rule)
    // ------------------------------------------------------------------

    @Test
    void testRecipientNumberCorrectlyFormatted() {
        Message message = new Message(MESSAGE_ID, MESSAGE_NUMBER, RECIPIENT,
                MESSAGE_TEXT);
        assertEquals(Message.RECIPIENT_SUCCESS, message.checkRecipientCell());
    }

    @Test
    void testRecipientNumberIncorrectlyFormatted() {
        Message message = new Message(MESSAGE_ID, MESSAGE_NUMBER, "08575975889",
                "Hi Keegan, did you receive the payment?");
        assertEquals(Message.RECIPIENT_FAIL, message.checkRecipientCell());
    }

    // ------------------------------------------------------------------
    // Message hash
    // ------------------------------------------------------------------

    @Test
    void testMessageHashForTestCaseOne() {
        Message message = new Message(MESSAGE_ID, MESSAGE_NUMBER, RECIPIENT,
                MESSAGE_TEXT);
        assertEquals("00:0:HITONIGHT", message.createMessageHash());
    }

    @Test
    void testMessageHashesInLoop() {
        String[][] testData = {
                // {message ID, message number, message, expected hash}
                {"0012345678", "0", MESSAGE_TEXT, "00:0:HITONIGHT"},
                {"0098765432", "1", "Hi Keegan, did you receive the payment?",
                        "00:1:HIPAYMENT"},
                {"0045678901", "2", "Did you get the cake?", "00:2:DIDCAKE"},
                {"0055555555", "3", "Yohoooo, I am at your gate.",
                        "00:3:YOHOOOOGATE"},
                {"0033334444", "4", "Ok, I am leaving without you.",
                        "00:4:OKYOU"},
        };

        for (String[] data : testData) {
            Message message = new Message(data[0], Integer.parseInt(data[1]),
                    RECIPIENT, data[2]);
            assertEquals(data[3], message.createMessageHash(),
                    "Unexpected hash for message ID " + data[0]);
        }
    }

    @Test
    void testMessageHashUsesUpperCase() {
        Message message = new Message("0012345678", 0, RECIPIENT,
                "did you get the cake?");
        assertEquals("00:0:DIDCAKE", message.createMessageHash());
    }

    // ------------------------------------------------------------------
    // Message ID
    // ------------------------------------------------------------------

    @Test
    void testGeneratedMessageIDIsTenDigits() {
        String messageID = Message.generateMessageID();
        assertEquals(10, messageID.length());
        assertTrue(messageID.matches("\\d{10}"),
                "The message ID should contain ten digits but was " + messageID);
        assertTrue(messageID.startsWith("00"),
                "The message ID should start with 00 so the hash matches the example");
    }

    @Test
    void testGeneratedMessageIDsAreUnique() {
        assertNotEquals(Message.generateMessageID(), Message.generateMessageID());
    }

    @Test
    void testCheckMessageIDAcceptsTenCharacters() {
        Message message = new Message(MESSAGE_ID, MESSAGE_NUMBER, RECIPIENT,
                MESSAGE_TEXT);
        assertTrue(message.checkMessageID());
    }

    @Test
    void testCheckMessageIDRejectsMoreThanTenCharacters() {
        Message message = new Message("00123456789", MESSAGE_NUMBER, RECIPIENT,
                MESSAGE_TEXT);
        assertFalse(message.checkMessageID());
    }

    @Test
    void testMessageIDDisplayFormat() {
        Message message = new Message(MESSAGE_ID, MESSAGE_NUMBER, RECIPIENT,
                MESSAGE_TEXT);
        assertEquals("Message ID generated: 0012345678",
                message.displayMessageID());
    }

    // ------------------------------------------------------------------
    // Send / disregard / store
    // ------------------------------------------------------------------

    @Test
    void testSentMessageOptionSend() {
        Message message = new Message(MESSAGE_ID, MESSAGE_NUMBER, RECIPIENT,
                MESSAGE_TEXT);
        assertEquals("Message successfully sent.", message.SentMessage(Message.SEND));
        assertTrue(message.isSent());
    }

    @Test
    void testSentMessageOptionDisregard() {
        Message message = new Message(MESSAGE_ID, MESSAGE_NUMBER, RECIPIENT,
                MESSAGE_TEXT);
        assertEquals("Press 0 to delete the message.",
                message.SentMessage(Message.DISREGARD));
        assertFalse(message.isSent());
    }

    @Test
    void testSentMessageOptionStore() {
        Message message = new Message(MESSAGE_ID, MESSAGE_NUMBER, RECIPIENT,
                MESSAGE_TEXT);
        assertEquals("Message successfully stored.",
                message.SentMessage(Message.STORE));
        assertFalse(message.isSent());
        assertTrue(Files.exists(tempDir.resolve("messages.json")),
                "Storing a message should create messages.json");
    }

    @Test
    void testInvalidOptionDoesNotSendMessage() {
        Message message = new Message(MESSAGE_ID, MESSAGE_NUMBER, RECIPIENT,
                MESSAGE_TEXT);
        message.SentMessage(99);
        assertEquals(0, Message.returnTotalMessagess());
    }

    @Test
    void testTotalMessagesSentAndPrintMessages() {
        Message first = new Message("0012345678", 0, RECIPIENT, MESSAGE_TEXT);
        Message second = new Message("0098765432", 1, RECIPIENT,
                "Hi Keegan, did you receive the payment?");

        first.SentMessage(Message.SEND);
        second.SentMessage(Message.SEND);

        assertEquals(2, Message.returnTotalMessagess());
        String report = Message.printMessages();
        assertTrue(report.contains("00:0:HITONIGHT"), report);
        assertTrue(report.contains("00:1:HIPAYMENT"), report);
        assertTrue(report.contains(MESSAGE_TEXT), report);
    }

    @Test
    void testPrintMessagesWhenNothingSent() {
        assertEquals(Message.NO_MESSAGES_SENT, Message.printMessages());
    }

    // ------------------------------------------------------------------
    // JSON storage
    // ------------------------------------------------------------------

    @Test
    void testStoredMessageIsWrittenToJsonFile() throws IOException {
        Message message = new Message(MESSAGE_ID, MESSAGE_NUMBER, RECIPIENT,
                MESSAGE_TEXT);
        message.storeMessage();

        Path file = tempDir.resolve("messages.json");
        assertTrue(Files.exists(file));

        String json = Files.readString(file);
        assertTrue(json.contains("\"messageHash\""), json);
        assertTrue(json.contains("00:0:HITONIGHT"), json);
        assertTrue(json.contains("Hi Mike, can you join us for dinner tonight?"),
                json);

        List<MessageRecord> stored = new MessageStore(file).readAll();
        assertEquals(1, stored.size());
        assertEquals(MESSAGE_ID, stored.get(0).getMessageID());
        assertEquals("00:0:HITONIGHT", stored.get(0).getMessageHash());
        assertEquals("Stored", stored.get(0).getStatus());
    }
}
