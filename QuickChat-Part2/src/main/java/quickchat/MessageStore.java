package quickchat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * MessageStore.java
 *
 * Reads and writes the list of messages as a JSON file ("messages.json").
 *
 * JSON handling is done with Google Gson instead of hand-written string
 * concatenation, because Gson escapes quotes, newlines and other special
 * characters correctly (see the reference below). The store is a normal
 * object rather than static methods, so unit tests can point it at a
 * temporary file with {@link Message#useStore(MessageStore)}.
 *
 * Reference (attribution required by the brief):
 *   Google (2024) Gson - A Java serialization/deserialization library to
 *   convert Java Objects into JSON and back.
 *   https://github.com/google/gson (accessed 2026).
 */
public class MessageStore {

    /** File name used when the application runs normally. */
    public static final String DEFAULT_FILE_NAME = "messages.json";

    private static final Type MESSAGE_LIST_TYPE =
            new TypeToken<List<MessageRecord>>() {
            }.getType();

    private final Path file;
    private final Gson gson;

    /**
     * Creates a store that writes to "messages.json" in the current working
     * directory (i.e. the project folder when run from NetBeans).
     */
    public MessageStore() {
        this(Paths.get(DEFAULT_FILE_NAME));
    }

    /**
     * Creates a store that writes to a specific file.
     *
     * @param file the JSON file to use
     */
    public MessageStore(Path file) {
        this.file = file;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    /**
     * @return the file this store reads from and writes to
     */
    public Path getFile() {
        return file;
    }

    /**
     * Reads every stored message from the JSON file.
     *
     * @return the stored messages; an empty list when the file does not exist
     *         yet (nothing has been stored)
     * @throws IOException when the file cannot be read or does not contain
     *                     valid JSON
     */
    public List<MessageRecord> readAll() throws IOException {
        if (!Files.exists(file)) {
            return new ArrayList<>();
        }
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            List<MessageRecord> records = gson.fromJson(reader, MESSAGE_LIST_TYPE);
            return records == null ? new ArrayList<>() : new ArrayList<>(records);
        } catch (JsonSyntaxException e) {
            throw new IOException("The messages file does not contain valid JSON", e);
        }
    }

    /**
     * Adds one message to the JSON file, keeping any messages already stored.
     *
     * @param record the message to add
     * @throws IOException when the file cannot be read or written
     */
    public void append(MessageRecord record) throws IOException {
        List<MessageRecord> records = readAll();
        records.add(record);
        saveAll(records);
    }

    /**
     * Replaces the contents of the JSON file with the supplied messages.
     *
     * @param records the messages to write
     * @throws IOException when the file cannot be written
     */
    public void saveAll(List<MessageRecord> records) throws IOException {
        Path parent = file.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            gson.toJson(records, MESSAGE_LIST_TYPE, writer);
            writer.write(System.lineSeparator());
        }
    }
}
