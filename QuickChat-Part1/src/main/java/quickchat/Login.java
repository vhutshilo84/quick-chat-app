package quickchat;

import java.util.regex.Pattern;

/**
 * Login.java
 *
 * Handles registration and login validation for the QuickChat application
 * (PROG5121 PoE - Part 1).
 *
 * Covers:
 *  - Username format checking
 *  - Password complexity checking
 *  - South African / international cell phone number checking
 *  - Registration message logic
 *  - Login verification and status messaging
 */
public class Login {

    // ---- Stored registration details ----
    private String username;
    private String password;
    private String cellPhoneNumber;
    private String firstName;
    private String lastName;

    // ---- Standard system response messages (kept as constants so the
    //      Login class and its JUnit tests always agree on exact wording) ----
    public static final String USERNAME_SUCCESS =
            "Username successfully captured.";
    public static final String USERNAME_FAIL =
            "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.";

    public static final String PASSWORD_SUCCESS =
            "Password successfully captured.";
    public static final String PASSWORD_FAIL =
            "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";

    public static final String CELL_SUCCESS =
            "Cell number successfully captured.";
    public static final String CELL_FAIL =
            "Cell number is incorrectly formatted or does not contain an international code; please correct the number and try again.";

    public static final String LOGIN_FAIL =
            "Username or password incorrect, please try again.";

    // Regex used to validate the cell phone number. A South African / international
    // number is expected to start with '+' followed by a 1-3 digit country code and
    // then the subscriber number (no more than ten characters long in total).
    //
    // Reference / attribution: pattern structure adapted from the general
    // international phone number validation approach described in
    // Baeldung, "Validate Phone Numbers with Java",
    // https://www.baeldung.com/java-regex-validate-phone-numbers (accessed 2026),
    // adjusted to the ten-character subscriber-number rule required by this brief.
    private static final Pattern CELL_PATTERN =
            Pattern.compile("^\\+\\d{1,3}\\d{7,10}$");

    // Regex building blocks for password complexity checks.
    private static final Pattern HAS_UPPERCASE = Pattern.compile(".*[A-Z].*");
    private static final Pattern HAS_DIGIT = Pattern.compile(".*\\d.*");
    private static final Pattern HAS_SPECIAL_CHAR = Pattern.compile(".*[^a-zA-Z0-9].*");

    /**
     * Creates a Login object holding the details a user has entered at
     * registration time.
     */
    public Login(String username, String password, String cellPhoneNumber,
                 String firstName, String lastName) {
        this.username = username;
        this.password = password;
        this.cellPhoneNumber = cellPhoneNumber;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // ---- Getters (kept simple / no unnecessary setters, since these
    //      details should not change once a user has registered) ----
    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    /**
     * Checks that the username contains an underscore and is no more than
     * five characters long.
     */
    public boolean checkUserName() {
        if (username == null) {
            return false;
        }
        return username.contains("_") && username.length() <= 5;
    }

    /**
     * Checks that the password:
     *  - is at least eight characters long
     *  - contains a capital letter
     *  - contains a number
     *  - contains a special character
     */
    public boolean checkPasswordComplexity() {
        if (password == null) {
            return false;
        }
        boolean longEnough = password.length() >= 8;
        boolean hasUpper = HAS_UPPERCASE.matcher(password).matches();
        boolean hasDigit = HAS_DIGIT.matcher(password).matches();
        boolean hasSpecial = HAS_SPECIAL_CHAR.matcher(password).matches();

        return longEnough && hasUpper && hasDigit && hasSpecial;
    }

    /**
     * Checks that the cell phone number contains an international country
     * code and is no more than ten characters long (excluding the country
     * code), using a regular expression.
     */
    public boolean checkCellPhoneNumber() {
        if (cellPhoneNumber == null) {
            return false;
        }
        return CELL_PATTERN.matcher(cellPhoneNumber).matches();
    }

    /**
     * Runs all three registration checks and returns the appropriate
     * system message. Checks are evaluated in order (username, then
     * password, then cell number) and the first failure message found is
     * returned; if all three pass, a combined success message is returned.
     */
    public String registerUser() {
        if (!checkUserName()) {
            return USERNAME_FAIL;
        }
        if (!checkPasswordComplexity()) {
            return PASSWORD_FAIL;
        }
        if (!checkCellPhoneNumber()) {
            return CELL_FAIL;
        }
        return USERNAME_SUCCESS + " " + PASSWORD_SUCCESS + " " + CELL_SUCCESS;
    }

    /**
     * Verifies that the supplied username and password match the details
     * captured at registration.
     */
    public boolean loginUser(String enteredUsername, String enteredPassword) {
        if (enteredUsername == null || enteredPassword == null) {
            return false;
        }
        return enteredUsername.equals(this.username)
                && enteredPassword.equals(this.password);
    }

    /**
     * Returns the appropriate login feedback message based on whether the
     * login attempt succeeded.
     */
    public String returnLoginStatus(boolean loginSuccessful) {
        if (loginSuccessful) {
            return "Welcome " + firstName + ", " + lastName
                    + " it is great to see you again.";
        }
        return LOGIN_FAIL;
    }
}
