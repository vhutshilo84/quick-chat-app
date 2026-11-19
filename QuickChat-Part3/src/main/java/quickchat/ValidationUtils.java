package quickchat;

import java.util.regex.Pattern;

/**
 * ValidationUtils.java
 *
 * Shared validation rules for the QuickChat application (PROG5121 PoE).
 *
 * Part 1 introduced username, password and cell phone checks inside
 * {@link Login}. Part 2 needs exactly the same cell phone check when a
 * message recipient is captured (the brief explicitly says "try and reuse
 * the method and tests you used in the last task"), so the rules now live
 * in one place and both classes call them. This removes duplicated logic
 * and means a rule can only ever be changed in one place.
 *
 * All methods are static and stateless, which keeps them easy to unit test.
 *
 * Regex attribution
 * -----------------
 * The international cell phone pattern below is based on the general
 * approach for validating international phone numbers with Java regular
 * expressions described by Baeldung:
 *   Baeldung (2024) "Validate Phone Numbers With Java Regex".
 *   https://www.baeldung.com/java-regex-validate-phone-numbers
 *   (accessed 2026)
 * The pattern has been adapted for this brief: a "+" and a 1-3 digit
 * country code are required, followed by a subscriber number of 7-10
 * digits (the brief's "no more than ten characters" rule).
 */
public final class ValidationUtils {

    /** Maximum length of a chat message, as required by the brief. */
    public static final int MAX_MESSAGE_LENGTH = 250;

    /** Maximum length of a username (Part 1 rule). */
    public static final int MAX_USERNAME_LENGTH = 5;

    /** International number: "+" + country code (1-3 digits) + 7-10 digits. */
    private static final Pattern CELL_NUMBER_PATTERN =
            Pattern.compile("^\\+\\d{1,3}\\d{7,10}$");

    private static final Pattern UPPERCASE_PATTERN = Pattern.compile(".*[A-Z].*");
    private static final Pattern DIGIT_PATTERN = Pattern.compile(".*\\d.*");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile(".*[^a-zA-Z0-9].*");

    /** Utility class: not meant to be instantiated. */
    private ValidationUtils() {
    }

    /**
     * Checks that a cell phone number contains the international country code
     * (a leading "+") and that the number itself is no longer than ten digits.
     *
     * @param cellNumber the number to check, may be {@code null}
     * @return {@code true} when the number is correctly formatted
     */
    public static boolean isValidCellNumber(String cellNumber) {
        if (cellNumber == null || cellNumber.isBlank()) {
            return false;
        }
        return CELL_NUMBER_PATTERN.matcher(cellNumber.trim()).matches();
    }

    /**
     * Checks that a username contains an underscore and is no more than five
     * characters long.
     *
     * @param username the username to check, may be {@code null}
     * @return {@code true} when the username is correctly formatted
     */
    public static boolean isValidUsername(String username) {
        if (username == null) {
            return false;
        }
        return username.contains("_") && username.length() <= MAX_USERNAME_LENGTH;
    }

    /**
     * Checks the password complexity rules: at least eight characters, a
     * capital letter, a number and a special character.
     *
     * @param password the password to check, may be {@code null}
     * @return {@code true} when every complexity rule is met
     */
    public static boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        }
        boolean longEnough = password.length() >= 8;
        boolean hasUppercase = UPPERCASE_PATTERN.matcher(password).matches();
        boolean hasDigit = DIGIT_PATTERN.matcher(password).matches();
        boolean hasSpecialCharacter = SPECIAL_CHAR_PATTERN.matcher(password).matches();

        return longEnough && hasUppercase && hasDigit && hasSpecialCharacter;
    }

    /**
     * Checks that a message does not exceed the 250 character limit.
     *
     * @param message the message text, may be {@code null}
     * @return {@code true} when the message may be sent
     */
    public static boolean isValidMessageLength(String message) {
        return message != null && message.length() <= MAX_MESSAGE_LENGTH;
    }

    /**
     * Returns the exact feedback required by the brief for a message that is
     * either within, or over, the 250 character limit.
     *
     * @param message the message text, may be {@code null}
     * @return "Message ready to send." or
     *         "Message exceeds 250 characters by X; please reduce the size."
     */
    public static String messageLengthFeedback(String message) {
        int length = message == null ? 0 : message.length();
        if (length <= MAX_MESSAGE_LENGTH) {
            return "Message ready to send.";
        }
        int excess = length - MAX_MESSAGE_LENGTH;
        return "Message exceeds 250 characters by " + excess
                + "; please reduce the size.";
    }
}
