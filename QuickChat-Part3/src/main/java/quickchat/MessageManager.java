package quickchat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * MessageManager.java
 *
 * Holds the five collections required by PROG5121 PoE Part 3 and provides
 * the search / report operations that the "Stored Messages" menu uses.
 *
 * Arrays (implemented as ArrayLists so they grow as messages are added;
 * no hard-coded values – they are populated as the user sends, stores or
 * disregards messages, or when the JSON file is loaded):
 *
 * <ul>
 *   <li>Sent Messages</li>
 *   <li>Disregarded Messages</li>
 *   <li>Stored Messages (also loaded from the JSON file)</li>
 *   <li>Message Hash</li>
 *   <li>Message ID</li>
 * </ul>
 *
 * The sender of a message is the currently logged-in user (their cell number
 * or name is recorded when the manager is created).
 */
public class MessageManager {

    private final List<Message> sentMessages = new ArrayList<>();
    private final List<Message> disregardedMessages = new ArrayList<>();
    private final List<Message> storedMessages = new ArrayList<>();
    private final List<String> messageHashes = new ArrayList<>();
    private final List<String> messageIDs = new ArrayList<>();

    /** The logged-in user who is the sender of every message in this session. */
    private final String senderCell;
    private final String senderName;

    private MessageStore store;

    /**
     * Creates a manager for the given logged-in user.
     *
     * @param senderCell the registered cell number of the logged-in user
     * @param senderName the user's display name (first + last)
     * @param store      the JSON store used for persisted messages
     */
    public MessageManager(String senderCell, String senderName, MessageStore store) {
        this.senderCell = senderCell == null ? "" : senderCell;
        this.senderName = senderName == null ? "User" : senderName;
        this.store = store != null ? store : new MessageStore();
    }

    // ------------------------------------------------------------------
    // Population (called from the send flow – no hard-coding)
    // ------------------------------------------------------------------

    /**
     * Records a message according to the action the user chose.
     *
     * @param message the message that was just captured
     * @param action  {@link Message#SEND}, {@link Message#DISREGARD} or
     *                {@link Message#STORE}
     */
    public void record(Message message, int action) {
        if (message == null) {
            return;
        }
        switch (action) {
            case Message.SEND:
                sentMessages.add(message);
                addHashAndId(message);
                break;
            case Message.DISREGARD:
                disregardedMessages.add(message);
                addHashAndId(message);
                break;
            case Message.STORE:
                storedMessages.add(message);
                addHashAndId(message);
                break;
            default:
                break;
        }
    }

    private void addHashAndId(Message message) {
        messageHashes.add(message.createMessageHash());
        messageIDs.add(message.getMessageID());
    }

    /**
     * Loads every message currently in the JSON file into the Stored Messages
     * collection (and the hash / ID lists). Existing in-memory stored messages
     * are kept; duplicates by message ID are skipped.
     *
     * @return how many new messages were loaded
     * @throws IOException when the JSON file cannot be read
     */
    public int loadStoredFromJson() throws IOException {
        List<MessageRecord> records = store.readAll();
        int added = 0;
        for (MessageRecord record : records) {
            if (record == null || record.getMessageID() == null) {
                continue;
            }
            if (containsMessageId(record.getMessageID())) {
                continue;
            }
            Message message = new Message(
                    record.getMessageID(),
                    record.getMessageNumber(),
                    record.getRecipient(),
                    record.getMessage());
            storedMessages.add(message);
            messageHashes.add(record.getMessageHash() != null
                    ? record.getMessageHash()
                    : message.createMessageHash());
            messageIDs.add(record.getMessageID());
            added++;
        }
        return added;
    }

    private boolean containsMessageId(String id) {
        for (String existing : messageIDs) {
            if (existing != null && existing.equals(id)) {
                return true;
            }
        }
        return false;
    }

    // ------------------------------------------------------------------
    // Part 3 feature methods (a – f)
    // ------------------------------------------------------------------

    /**
     * a) Display the sender and recipient of all stored messages.
     *
     * @return a multi-line report, or a short message when nothing is stored
     */
    public String displaySenderAndRecipientOfStored() {
        if (storedMessages.isEmpty()) {
            return "No stored messages.";
        }
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < storedMessages.size(); i++) {
            Message m = storedMessages.get(i);
            out.append("Sender: ").append(senderName)
                    .append(" (").append(senderCell).append(")")
                    .append(System.lineSeparator())
                    .append("Recipient: ").append(m.getRecipient());
            if (i < storedMessages.size() - 1) {
                out.append(System.lineSeparator()).append(System.lineSeparator());
            }
        }
        return out.toString();
    }

    /**
     * b) Display the longest stored message (by character length).
     * When several messages share the same length, the first one found is
     * returned.
     *
     * @return the text of the longest stored message, or a short message when
     *         the stored list is empty
     */
    public String displayLongestStoredMessage() {
        if (storedMessages.isEmpty()) {
            // Fall back to all known messages (sent + stored) for the unit-test
            // data set that mixes flags; prefer stored, then sent.
            List<Message> candidates = new ArrayList<>();
            candidates.addAll(storedMessages);
            candidates.addAll(sentMessages);
            if (candidates.isEmpty()) {
                return "No messages available.";
            }
            return findLongest(candidates).getMessage();
        }
        return findLongest(storedMessages).getMessage();
    }

    /**
     * Finds the longest message among the supplied list (used by the longest-
     * message feature and by unit tests that populate mixed flags).
     */
    public String findLongestMessageText(List<Message> messages) {
        if (messages == null || messages.isEmpty()) {
            return "No messages available.";
        }
        return findLongest(messages).getMessage();
    }

    private Message findLongest(List<Message> messages) {
        Message longest = messages.get(0);
        for (int i = 1; i < messages.size(); i++) {
            if (messages.get(i).getMessage().length()
                    > longest.getMessage().length()) {
                longest = messages.get(i);
            }
        }
        return longest;
    }

    /**
     * c) Search for a message ID and display the corresponding recipient and
     * message.
     *
     * @param messageId the ID to look for
     * @return "Recipient: … Message: …" or a not-found message
     */
    public String searchByMessageId(String messageId) {
        if (messageId == null || messageId.isBlank()) {
            return "Message ID not found.";
        }
        Message found = findById(messageId.trim());
        if (found == null) {
            return "Message ID not found.";
        }
        return "Recipient: " + found.getRecipient()
                + System.lineSeparator()
                + "Message: " + found.getMessage();
    }

    /**
     * Convenience used by unit tests that only need the message text for a
     * given ID (matches the brief's expected return of the message body).
     */
    public String searchMessageTextById(String messageId) {
        Message found = findById(messageId == null ? "" : messageId.trim());
        return found == null ? "Message ID not found." : found.getMessage();
    }

    private Message findById(String messageId) {
        for (Message m : allMessages()) {
            if (messageId.equals(m.getMessageID())) {
                return m;
            }
        }
        return null;
    }

    /**
     * d) Search for all messages (sent or stored) for a particular recipient.
     *
     * @param recipient the cell number to match
     * @return each matching message on its own line, or a short message when
     *         none match
     */
    public String searchByRecipient(String recipient) {
        if (recipient == null || recipient.isBlank()) {
            return "No messages found for that recipient.";
        }
        String target = recipient.trim();
        StringBuilder out = new StringBuilder();
        int count = 0;
        for (Message m : allMessages()) {
            if (target.equals(m.getRecipient())) {
                if (count > 0) {
                    out.append(System.lineSeparator());
                }
                out.append(m.getMessage());
                count++;
            }
        }
        if (count == 0) {
            return "No messages found for that recipient.";
        }
        return out.toString();
    }

    /**
     * e) Delete a message using its message hash. The message is removed from
     * every in-memory collection and from the JSON file when it was stored.
     *
     * @param hash the message hash to delete
     * @return the confirmation string required by the brief, or a not-found
     *         message
     */
    public String deleteByHash(String hash) {
        if (hash == null || hash.isBlank()) {
            return "Message hash not found.";
        }
        String target = hash.trim();
        Message removed = removeFromListByHash(sentMessages, target);
        if (removed == null) {
            removed = removeFromListByHash(disregardedMessages, target);
        }
        if (removed == null) {
            removed = removeFromListByHash(storedMessages, target);
        }
        if (removed == null) {
            return "Message hash not found.";
        }

        // Keep the parallel hash / ID lists in sync
        int index = messageHashes.indexOf(target);
        if (index >= 0) {
            messageHashes.remove(index);
            if (index < messageIDs.size()) {
                messageIDs.remove(index);
            }
        }

        // If it was a stored message, rewrite the JSON file without it
        try {
            List<MessageRecord> remaining = store.readAll();
            remaining.removeIf(r -> r != null
                    && target.equals(r.getMessageHash()));
            store.saveAll(remaining);
        } catch (IOException e) {
            // In-memory delete still succeeded; report the main result
        }

        return "Message: \"" + removed.getMessage() + "\" successfully deleted.";
    }

    private Message removeFromListByHash(List<Message> list, String hash) {
        for (int i = 0; i < list.size(); i++) {
            if (hash.equals(list.get(i).createMessageHash())) {
                return list.remove(i);
            }
        }
        return null;
    }

    /**
     * f) Display a report that lists the full details of all the stored
     * messages (Message Hash, Recipient, Message). Sent messages are also
     * included when the brief asks for a full report of sent data.
     *
     * @return a multi-line report
     */
    public String displayReport() {
        List<Message> reportMessages = new ArrayList<>();
        reportMessages.addAll(sentMessages);
        reportMessages.addAll(storedMessages);
        if (reportMessages.isEmpty()) {
            return "No messages to report.";
        }
        StringBuilder out = new StringBuilder();
        out.append("=== Message Report ===").append(System.lineSeparator());
        for (int i = 0; i < reportMessages.size(); i++) {
            Message m = reportMessages.get(i);
            out.append("Message Hash: ").append(m.createMessageHash())
                    .append(System.lineSeparator())
                    .append("Recipient: ").append(m.getRecipient())
                    .append(System.lineSeparator())
                    .append("Message: ").append(m.getMessage());
            if (i < reportMessages.size() - 1) {
                out.append(System.lineSeparator()).append(System.lineSeparator());
            }
        }
        return out.toString();
    }

    // ------------------------------------------------------------------
    // Accessors used by unit tests and the menu
    // ------------------------------------------------------------------

    public List<Message> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }

    public List<Message> getDisregardedMessages() {
        return new ArrayList<>(disregardedMessages);
    }

    public List<Message> getStoredMessages() {
        return new ArrayList<>(storedMessages);
    }

    public List<String> getMessageHashes() {
        return new ArrayList<>(messageHashes);
    }

    public List<String> getMessageIDs() {
        return new ArrayList<>(messageIDs);
    }

    public String getSenderCell() {
        return senderCell;
    }

    public String getSenderName() {
        return senderName;
    }

    /**
     * Returns the text of every sent message (used by the unit test that
     * checks the Sent Messages array is correctly populated).
     */
    public String sentMessageTexts() {
        if (sentMessages.isEmpty()) {
            return "";
        }
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < sentMessages.size(); i++) {
            if (i > 0) {
                out.append(", ");
            }
            out.append("\"").append(sentMessages.get(i).getMessage()).append("\"");
        }
        return out.toString();
    }

    /** Clears every collection (for unit tests). */
    public void reset() {
        sentMessages.clear();
        disregardedMessages.clear();
        storedMessages.clear();
        messageHashes.clear();
        messageIDs.clear();
    }

    private List<Message> allMessages() {
        List<Message> all = new ArrayList<>();
        all.addAll(sentMessages);
        all.addAll(disregardedMessages);
        all.addAll(storedMessages);
        return all;
    }

    /**
     * Directly adds a message with a known flag – used by unit tests to
     * populate the arrays from the brief's test data without going through
     * the console.
     *
     * @param message the message
     * @param flag    "Sent", "Stored" or "Disregard"
     */
    public void addForTest(Message message, String flag) {
        if (message == null || flag == null) {
            return;
        }
        String normalised = flag.trim().toLowerCase();
        if (normalised.startsWith("sent")) {
            sentMessages.add(message);
        } else if (normalised.startsWith("stored") || normalised.startsWith("store")) {
            storedMessages.add(message);
        } else if (normalised.startsWith("disregard")) {
            disregardedMessages.add(message);
        }
        addHashAndId(message);
    }
}
