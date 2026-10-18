package quickchat;

/**
 * MessageRecord.java
 *
 * Plain data object (POJO) describing how one message is written to the
 * JSON file. It exists so that the JSON layout is explicit and stable,
 * instead of serialising the whole {@link Message} object (which also has
 * behaviour and static state).
 *
 * The JSON produced looks like this:
 * <pre>
 * [
 *   {
 *     "messageID": "0012345678",
 *     "messageNumber": 0,
 *     "messageHash": "00:0:HITONIGHT",
 *     "recipient": "+27718693002",
 *     "message": "Hi Mike, can you join us for dinner tonight?",
 *     "status": "Stored",
 *     "timestamp": "2026-10-17T15:22:29"
 *   }
 * ]
 * </pre>
 */
public class MessageRecord {

    private String messageID;
    private int messageNumber;
    private String messageHash;
    private String recipient;
    private String message;
    private String status;
    private String timestamp;

    /**
     * No-argument constructor required by JSON deserialisation libraries.
     */
    public MessageRecord() {
    }

    /**
     * Creates a fully populated record.
     *
     * @param messageID     the generated message ID
     * @param messageNumber the sequential number of the message
     * @param messageHash   the generated message hash
     * @param recipient     the recipient cell number
     * @param message       the message payload
     * @param status        "Sent", "Stored" or "Disregarded"
     * @param timestamp     when the message was captured
     */
    public MessageRecord(String messageID, int messageNumber, String messageHash,
                         String recipient, String message, String status,
                         String timestamp) {
        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.messageHash = messageHash;
        this.recipient = recipient;
        this.message = message;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getMessageID() {
        return messageID;
    }

    public int getMessageNumber() {
        return messageNumber;
    }

    public String getMessageHash() {
        return messageHash;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
