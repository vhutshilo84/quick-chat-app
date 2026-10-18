package quickchat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * MessageStoreTest.java
 *
 * Unit tests for the JSON persistence layer ({@link MessageStore}).
 * Every test uses a temporary file so the project folder is never modified.
 */
public class MessageStoreTest {

    @TempDir
    Path tempDir;

    private Path file;
    private MessageStore store;

    @BeforeEach
    void setUp() {
        file = tempDir.resolve("messages.json");
        store = new MessageStore(file);
    }

    @Test
    void testReadingMissingFileReturnsEmptyList() throws IOException {
        assertTrue(store.readAll().isEmpty());
    }

    @Test
    void testAppendKeepsExistingMessages() throws IOException {
        store.append(record("0012345678", 0, "00:0:HITONIGHT",
                "+27718693002", "Hi Mike, can you join us for dinner tonight?"));
        store.append(record("0098765432", 1, "00:1:HIPAYMENT",
                "+27838884567", "Hi Keegan, did you receive the payment?"));

        List<MessageRecord> stored = store.readAll();
        assertEquals(2, stored.size());
        assertEquals("0012345678", stored.get(0).getMessageID());
        assertEquals("00:1:HIPAYMENT", stored.get(1).getMessageHash());
        assertEquals("Hi Keegan, did you receive the payment?",
                stored.get(1).getMessage());
    }

    @Test
    void testSavedFileIsValidJsonArray() throws IOException {
        store.append(record("0012345678", 0, "00:0:HITONIGHT",
                "+27718693002", "Hi Mike, can you join us for dinner tonight?"));

        String json = Files.readString(file);
        assertTrue(json.trim().startsWith("["), json);
        assertTrue(json.trim().endsWith("]"), json);
        assertTrue(json.contains("\"recipient\""), json);
        assertTrue(json.contains("\"messageHash\""), json);
    }

    @Test
    void testInvalidJsonProducesIOException() throws IOException {
        Files.writeString(file, "this is not json");
        assertThrows(IOException.class, () -> store.readAll());
    }

    private MessageRecord record(String id, int number, String hash,
                                 String recipient, String message) {
        return new MessageRecord(id, number, hash, recipient, message,
                "Stored", "2026-10-17T15:22:29");
    }
}
