package quickchat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * MessageManagerTest.java
 *
 * Unit tests for the Part 3 arrays and report features, using the exact test
 * data and expected responses from the PROG5121 PoE brief (pages 18–19).
 *
 * Test Data Message 1  Recipient +27834557896  "Did you get the cake?"          Sent
 * Test Data Message 2  Recipient +27838884567  "Where are you? You are late!…"  Stored
 * Test Data Message 3  Recipient +27834484567  "Yohoooo, I am at your gate."    Disregard
 * Test Data Message 4  Developer 0838884567    "It is dinner time !"            Sent
 * Test Data Message 5  Recipient +27838884567  "Ok, I am leaving without you."  Stored
 */
public class MessageManagerTest {

    @TempDir
    Path tempDir;

    private MessageManager manager;

    // ---- Fixed IDs so hashes and searches are predictable ----
    private static final String ID1 = "0011111111";
    private static final String ID2 = "0022222222";
    private static final String ID3 = "0033333333";
    private static final String ID4 = "0044444444";
    private static final String ID5 = "0055555555";

    private static final String MSG1 = "Did you get the cake?";
    private static final String MSG2 =
            "Where are you? You are late! I have asked you to be on time.";
    private static final String MSG3 = "Yohoooo, I am at your gate.";
    private static final String MSG4 = "It is dinner time !";
    private static final String MSG5 = "Ok, I am leaving without you.";

    @BeforeEach
    void setUp() {
        Message.resetSentMessages();
        Message.useStore(new MessageStore(tempDir.resolve("messages.json")));
        manager = new MessageManager("+27830000000", "Test User",
                Message.getStore());
        populateWithBriefTestData();
    }

    /**
     * Populates the five arrays from the brief's test data (no hard-coding of
     * results – the data itself is the brief's Developer entry).
     */
    private void populateWithBriefTestData() {
        Message m1 = new Message(ID1, 0, "+27834557896", MSG1);
        Message m2 = new Message(ID2, 1, "+27838884567", MSG2);
        Message m3 = new Message(ID3, 2, "+27834484567", MSG3);
        // Message 4 uses the number given under "Developer" in the brief
        Message m4 = new Message(ID4, 3, "0838884567", MSG4);
        Message m5 = new Message(ID5, 4, "+27838884567", MSG5);

        manager.addForTest(m1, "Sent");
        manager.addForTest(m2, "Stored");
        manager.addForTest(m3, "Disregard");
        manager.addForTest(m4, "Sent");
        manager.addForTest(m5, "Stored");
    }

    // ------------------------------------------------------------------
    // Sent Messages array correctly populated
    // ------------------------------------------------------------------

    @Test
    void testSentMessagesArrayCorrectlyPopulated() {
        // Brief expects the two Sent messages:
        // "Did you get the cake?", "It is dinner time!"
        List<Message> sent = manager.getSentMessages();
        assertEquals(2, sent.size());
        assertEquals(MSG1, sent.get(0).getMessage());
        assertEquals(MSG4, sent.get(1).getMessage());

        String combined = manager.sentMessageTexts();
        assertTrue(combined.contains(MSG1), combined);
        assertTrue(combined.contains(MSG4), combined);
    }

    // ------------------------------------------------------------------
    // Display the longest Message
    // ------------------------------------------------------------------

    @Test
    void testDisplayLongestMessage() {
        // Among messages 1–4 the longest is Message 2
        List<Message> messages1to4 = List.of(
                new Message(ID1, 0, "+27834557896", MSG1),
                new Message(ID2, 1, "+27838884567", MSG2),
                new Message(ID3, 2, "+27834484567", MSG3),
                new Message(ID4, 3, "0838884567", MSG4));
        assertEquals(MSG2, manager.findLongestMessageText(messages1to4));
    }

    // ------------------------------------------------------------------
    // Search for messageID
    // ------------------------------------------------------------------

    @Test
    void testSearchForMessageId() {
        // Brief: Test Data message 4, "0838884567" context, returns the message text
        assertEquals(MSG4, manager.searchMessageTextById(ID4));
    }

    // ------------------------------------------------------------------
    // Search all messages for a particular recipient
    // ------------------------------------------------------------------

    @Test
    void testSearchMessagesForParticularRecipient() {
        // Brief: +27838884567 → Message 2 and Message 5
        String result = manager.searchByRecipient("+27838884567");
        assertTrue(result.contains(MSG2), result);
        assertTrue(result.contains(MSG5), result);
    }

    // ------------------------------------------------------------------
    // Delete a message using a message hash
    // ------------------------------------------------------------------

    @Test
    void testDeleteMessageUsingMessageHash() {
        // Brief: Test Message 2
        Message m2 = new Message(ID2, 1, "+27838884567", MSG2);
        String hash = m2.createMessageHash();

        String result = manager.deleteByHash(hash);
        assertEquals("Message: \"" + MSG2 + "\" successfully deleted.", result);

        // Confirm it is gone from stored messages
        assertTrue(manager.getStoredMessages().stream()
                .noneMatch(m -> MSG2.equals(m.getMessage())));
    }

    // ------------------------------------------------------------------
    // Display Report
    // ------------------------------------------------------------------

    @Test
    void testDisplayReportContainsHashRecipientAndMessage() {
        String report = manager.displayReport();
        // Report must include Message Hash, Recipient and Message for the
        // sent / stored entries
        assertTrue(report.contains("Message Hash:"), report);
        assertTrue(report.contains("Recipient:"), report);
        assertTrue(report.contains("Message:"), report);
        assertTrue(report.contains(MSG1), report);
        assertTrue(report.contains(MSG2), report);
        assertTrue(report.contains(MSG4), report);
        assertTrue(report.contains(MSG5), report);
    }

    // ------------------------------------------------------------------
    // Extra checks that the five arrays are populated
    // ------------------------------------------------------------------

    @Test
    void testArraysPopulatedWithCorrectCounts() {
        assertEquals(2, manager.getSentMessages().size());
        assertEquals(1, manager.getDisregardedMessages().size());
        assertEquals(2, manager.getStoredMessages().size());
        assertEquals(5, manager.getMessageHashes().size());
        assertEquals(5, manager.getMessageIDs().size());
    }

    @Test
    void testDisregardedMessageIsMessage3() {
        assertEquals(MSG3, manager.getDisregardedMessages().get(0).getMessage());
    }

    @Test
    void testSenderAndRecipientOfStored() {
        String display = manager.displaySenderAndRecipientOfStored();
        assertTrue(display.contains("Sender:"), display);
        assertTrue(display.contains("+27838884567"), display);
    }
}
