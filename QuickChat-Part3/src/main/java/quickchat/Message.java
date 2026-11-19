package quickchat;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Message.java
 *
 * Model for one QuickChat message plus all the behaviour required by the
 * PROG5121 PoE Part 2 brief:
 *
 * <ul>
 *   <li>a unique ten digit message ID</li>
 *   <li>the sequential number of the message (supplied by the loop counter)</li>
 *   <li>the recipient cell number (validated with the Part 1 regex rules)</li>
 *   <li>the message text (maximum 250 characters)</li>
 *   <li>an auto-generated message hash</li>
 *   <li>send / disregard / store handling, including JSON storage</li>
 * </ul>
 *
 * The hash format is
 * {@code <first two digits of ID>:<message number>:<first word><last word>}
 * in capitals, for example {@code 00:0:HITONIGHT}.
 */
public class Message {

    // ---- Message length rule ----
    public static final int MAX_MESSAGE_LENGTH = ValidationUtils.MAX_MESSAGE_LENGTH;

    // ---- Options for SentMessage(), matching the console menu ----
    public static final int SEND = 1;
    public static final int DISREGARD = 2;
    public static final int STORE = 3;

    // ---- Exact system messages required by the brief ----
    public static final String MESSAGE_READY = "Message ready to send.";
    public static final String RECIPIENT_SUCCESS =
            "Cell phone number successfully captured.";
    public static final String RECIPIENT_FAIL =
            "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
    public static final String SENT_SUCCESS = "Message successfully sent.";
    public static final String DISREGARD_PROMPT = "Press 0 to delete the message.";
    public static final String STORED_SUCCESS = "Message successfully stored.";
    public static final String NO_MESSAGES_SENT = "No messages have been sent yet.";

    /**
     * Messages that were actually sent during this run of the application.
     * Kept centrally so that {@link #printMessages()} and
     * {@link #returnTotalMessagess()} work exactly as described in the brief.
     */
    private static final List<Message> SENT_MESSAGES = new ArrayList<>();

    /** Used to build the random ten digit message ID. */
    private static final Random RANDOM = new Random();

    /** Where "Stored" messages are written. Replaceable for unit tests. */
    private static MessageStore store = new MessageStore();

    private final String messageID;
    private final int messageNumber;
    private final String recipient;
    private final String messageText;

    /** Guards against counting the same message twice if SentMessage() repeats. */
    private boolean sent;

    /**
     * Creates a message with an explicitly supplied ID (used by unit tests so
     * that the expected hash is predictable).
     *
     * @param messageID     the ten digit message ID
     * @param messageNumber the sequential number of this message
     * @param recipient     the recipient cell number
     * @param messageText   the message payload
     */
    public Message(String messageID, int messageNumber, String recipient,
                   String messageText) {
        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.messageText = messageText;
    }

    /**
     * Creates a message and generates its ID automatically. This is the
     * constructor the console application uses.
     *
     * @param messageNumber the sequential number of this message
     * @param recipient     the recipient cell number
     * @param messageText   the message payload
     */
    public Message(int messageNumber, String recipient, String messageText) {
        this(generateMessageID(), messageNumber, recipient, messageText);
    }

    // ------------------------------------------------------------------
    // Getters
    // ------------------------------------------------------------------

    public String getMessageID() {
        return messageID;
    }

    public int getMessageNumber() {
        return messageNumber;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMessage() {
        return messageText;
    }

    public boolean isSent() {
        return sent;
    }

    // ------------------------------------------------------------------
    // Required Part 2 methods
    // ------------------------------------------------------------------

    /**
     * Ensures that the message ID is not more than ten characters long.
     *
     * @return {@code true} when the ID is valid
     */
    public boolean checkMessageID() {
        return messageID != null && !messageID.isEmpty()
                && messageID.length() <= 10;
    }

    /**
     * Ensures that the recipient cell number is no more than ten characters
     * long (excluding the country code) and contains an international code.
     * <p>
     * This reuses the Part 1 validation rule from {@link ValidationUtils} as
     * the brief suggests, but returns the Part 2 wording of the messages.
     *
     * @return the success or failure message required by the brief
     */
    public String checkRecipientCell() {
        return ValidationUtils.isValidCellNumber(recipient)
                ? RECIPIENT_SUCCESS
                : RECIPIENT_FAIL;
    }

    /**
     * Checks the 250 character message limit.
     *
     * @return "Message ready to send." or a message stating how many
     *         characters need to be removed
     */
    public String checkMessageLength() {
        return ValidationUtils.messageLengthFeedback(messageText);
    }

    /**
     * Creates the message hash from string manipulation:
     * the first two digits of the message ID, a colon, the message number,
     * a colon, and the first and last words of the message, all in capitals.
     *
     * @return the message hash, for example {@code 00:0:HITONIGHT}
     */
    public String createMessageHash() {
        String idPrefix = messageID != null && messageID.length() >= 2
                ? messageID.substring(0, 2)
                : (messageID == null ? "" : messageID);
        String firstWord = firstWord(messageText);
        String lastWord = lastWord(messageText);

        return (idPrefix + ":" + messageNumber + ":" + firstWord + lastWord)
                .toUpperCase(Locale.ROOT);
    }

    /**
     * Handles the user's choice after a message has been captured.
     *
     * @param choice {@link #SEND}, {@link #DISREGARD} or {@link #STORE}
     * @return the confirmation message required by the brief
     */
    public String SentMessage(int choice) {
        switch (choice) {
            case SEND:
                registerAsSent();
                return SENT_SUCCESS;
            case DISREGARD:
                return DISREGARD_PROMPT;
            case STORE:
                return storeMessage();
            default:
                return "Invalid option selected; the message was not sent, stored or disregarded.";
        }
    }

    /**
     * Writes this message to the JSON file ("messages.json") so that it can be
     * sent later.
     *
     * <p>JSON serialisation is handled by Google Gson; see
     * {@link MessageStore} for the full reference.</p>
     *
     * @return "Message successfully stored." or an error description
     */
    public String storeMessage() {
        try {
            store.append(toRecord("Stored"));
            return STORED_SUCCESS;
        } catch (IOException e) {
            return "The message could not be stored: " + e.getMessage();
        }
    }

    // ------------------------------------------------------------------
    // Display helpers
    // ------------------------------------------------------------------

    /**
     * @return "Message ID generated: &lt;Message ID&gt;"
     */
    public String displayMessageID() {
        return "Message ID generated: " + messageID;
    }

    /**
     * The full message details in the order required by the brief:
     * Message ID, Message Hash, Recipient, Message.
     *
     * @return the formatted details
     */
    public String getMessageDetails() {
        return "Message ID: " + messageID + System.lineSeparator()
                + "Message Hash: " + createMessageHash() + System.lineSeparator()
                + "Recipient: " + recipient + System.lineSeparator()
                + "Message: " + messageText;
    }

    /**
     * @return every message sent while the program has been running
     */
    public static String printMessages() {
        if (SENT_MESSAGES.isEmpty()) {
            return NO_MESSAGES_SENT;
        }
        StringBuilder report = new StringBuilder();
        for (int i = 0; i < SENT_MESSAGES.size(); i++) {
            report.append(SENT_MESSAGES.get(i).getMessageDetails());
            if (i < SENT_MESSAGES.size() - 1) {
                report.append(System.lineSeparator()).append(System.lineSeparator());
            }
        }
        return report.toString();
    }

    /**
     * @return the total number of messages sent so far
     */
    public static int returnTotalMessagess() {
        return SENT_MESSAGES.size();
    }

    @Override
    public String toString() {
        return getMessageDetails();
    }

    // ------------------------------------------------------------------
    // ID generation and supporting helpers
    // ------------------------------------------------------------------

    /**
     * Generates a random ten digit message ID.
     *
     * <p>The first two digits are fixed as "00" so that the ID (and therefore
     * the hash) matches the format shown in the brief's example,
     * "00:0:HITHANKS". The remaining eight digits are random, which keeps the
     * ID unique for each message created during a run.</p>
     *
     * @return a ten character numeric ID
     */
    public static String generateMessageID() {
        return String.format(Locale.ROOT, "00%08d", RANDOM.nextInt(100_000_000));
    }

    /**
     * Replaces the JSON store used by {@link #storeMessage()}. Unit tests use
     * this to write to a temporary folder instead of the project folder.
     *
     * @param messageStore the store to use
     */
    public static void useStore(MessageStore messageStore) {
        store = messageStore;
    }

    /**
     * @return the JSON store currently in use
     */
    public static MessageStore getStore() {
        return store;
    }

    /**
     * Clears the list of sent messages. Used by unit tests so that each test
     * starts from a known state, and by the console app if a fresh session is
     * required.
     */
    public static void resetSentMessages() {
        SENT_MESSAGES.clear();
    }

    /**
     * Converts this message into the object that is written to JSON.
     *
     * @param status "Sent", "Stored" or "Disregarded"
     * @return the JSON-ready record
     */
    public MessageRecord toRecord(String status) {
        String timestamp = LocalDateTime.now()
                .truncatedTo(ChronoUnit.SECONDS)
                .toString();
        return new MessageRecord(messageID, messageNumber, createMessageHash(),
                recipient, messageText, status, timestamp);
    }

    /** Adds this message to the sent list exactly once. */
    private void registerAsSent() {
        if (!sent) {
            SENT_MESSAGES.add(this);
            sent = true;
        }
    }

    /**
     * @return the first word of the supplied text with punctuation removed
     */
    private static String firstWord(String text) {
        if (text == null) {
            return "";
        }
        for (String token : text.trim().split("\\s+")) {
            String cleaned = removePunctuation(token);
            if (!cleaned.isEmpty()) {
                return cleaned;
            }
        }
        return "";
    }

    /**
     * @return the last word of the supplied text with punctuation removed
     */
    private static String lastWord(String text) {
        if (text == null) {
            return "";
        }
        String[] tokens = text.trim().split("\\s+");
        for (int i = tokens.length - 1; i >= 0; i--) {
            String cleaned = removePunctuation(tokens[i]);
            if (!cleaned.isEmpty()) {
                return cleaned;
            }
        }
        return "";
    }

    /** Keeps only letters so that "tonight?" becomes "tonight". */
    private static String removePunctuation(String token) {
        return token.replaceAll("[^A-Za-z]", "");
    }
}
