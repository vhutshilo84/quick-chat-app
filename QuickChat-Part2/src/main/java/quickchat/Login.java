package quickchat;

/**
 * Login.java
 *
 * Handles registration and login validation for the QuickChat application
 * (PROG5121 PoE - Part 1, reused unchanged in Part 2 so that users can only
 * send messages once they have logged in successfully).
 *
 * Covers:
 *  - Username format checking
 *  - Password complexity checking
 *  - Cell phone number checking (international code + length)
 *  - Registration message logic
 *  - Login verification and status messaging
 *
 * The actual validation rules live in {@link ValidationUtils} so that Part 2
 * can reuse the cell phone rule when a message recipient is captured.
 */
public class Login {

    // ---- Stored registration details ----
    private final String username;
    private final String password;
    private final String cellPhoneNumber;
    private final String firstName;
    private final String lastName;

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

    /**
     * Creates a Login object holding the details a user has entered at
     * registration time.
     *
     * @param username        the registered username
     * @param password        the registered password
     * @param cellPhoneNumber the registered cell phone number
     * @param firstName       the user's first name
     * @param lastName        the user's last name
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

    public String getPassword() {
        return password;
    }

    public String getCellPhoneNumber() {
        return cellPhoneNumber;
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
     *
     * @return {@code true} when the username is correctly formatted
     */
    public boolean checkUserName() {
        return ValidationUtils.isValidUsername(username);
    }

    /**
     * Checks that the password:
     *  - is at least eight characters long
     *  - contains a capital letter
     *  - contains a number
     *  - contains a special character
     *
     * @return {@code true} when every complexity rule is met
     */
    public boolean checkPasswordComplexity() {
        return ValidationUtils.isValidPassword(password);
    }

    /**
     * Checks that the cell phone number contains an international country
     * code and is no more than ten characters long (excluding the country
     * code), using a regular expression.
     *
     * @return {@code true} when the number is correctly formatted
     */
    public boolean checkCellPhoneNumber() {
        return ValidationUtils.isValidCellNumber(cellPhoneNumber);
    }

    /**
     * Runs all three registration checks and returns the appropriate
     * system message. Checks are evaluated in order (username, then
     * password, then cell number) and the first failure message found is
     * returned; if all three pass, a combined success message is returned.
     *
     * @return the registration feedback message
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
     *
     * @param enteredUsername the username typed at the login screen
     * @param enteredPassword the password typed at the login screen
     * @return {@code true} when the credentials are correct
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
     *
     * @param loginSuccessful result of {@link #loginUser(String, String)}
     * @return the welcome message or the login failure message
     */
    public String returnLoginStatus(boolean loginSuccessful) {
        if (loginSuccessful) {
            return "Welcome " + firstName + ", " + lastName
                    + " it is great to see you again.";
        }
        return LOGIN_FAIL;
    }
}
