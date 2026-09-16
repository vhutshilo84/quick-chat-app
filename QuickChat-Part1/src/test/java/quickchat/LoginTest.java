package quickchat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * LoginTest.java
 *
 * Unit tests for Login.java, using the exact test data supplied in the
 * PROG5121 Part 1 brief so results line up with the marking guide.
 */
public class LoginTest {

    private Login login;

    @BeforeEach
    void setUp() {
        // Default "correctly formatted" registration used by most tests;
        // individual tests override fields where the brief specifies
        // different (deliberately invalid) test data.
        login = new Login("kyl_1", "Ch&&sec@ke99!", "+27838968976",
                "Kyle", "Smith");
    }

    // ---------------- Username tests ----------------

    @Test
    void testUsernameCorrectlyFormatted() {
        login = new Login("kyl_1", "Ch&&sec@ke99!", "+27838968976",
                "Kyle", "Smith");
        assertTrue(login.checkUserName());
    }

    @Test
    void testUsernameIncorrectlyFormatted() {
        login = new Login("kyle!!!!!!!", "Ch&&sec@ke99!", "+27838968976",
                "Kyle", "Smith");
        assertFalse(login.checkUserName());
    }

    // ---------------- Password tests ----------------

    @Test
    void testPasswordMeetsComplexity() {
        login = new Login("kyl_1", "Ch&&sec@ke99!", "+27838968976",
                "Kyle", "Smith");
        assertTrue(login.checkPasswordComplexity());
    }

    @Test
    void testPasswordDoesNotMeetComplexity() {
        login = new Login("kyl_1", "password", "+27838968976",
                "Kyle", "Smith");
        assertFalse(login.checkPasswordComplexity());
    }

    // ---------------- Cell phone number tests ----------------

    @Test
    void testCellPhoneNumberCorrectlyFormatted() {
        login = new Login("kyl_1", "Ch&&sec@ke99!", "+27838968976",
                "Kyle", "Smith");
        assertTrue(login.checkCellPhoneNumber());
    }

    @Test
    void testCellPhoneNumberIncorrectlyFormatted() {
        login = new Login("kyl_1", "Ch&&sec@ke99!", "08966553",
                "Kyle", "Smith");
        assertFalse(login.checkCellPhoneNumber());
    }

    // ---------------- registerUser() message tests ----------------

    @Test
    void testRegisterUserSuccessMessage() {
        login = new Login("kyl_1", "Ch&&sec@ke99!", "+27838968976",
                "Kyle", "Smith");
        String expected = Login.USERNAME_SUCCESS + " " + Login.PASSWORD_SUCCESS
                + " " + Login.CELL_SUCCESS;
        assertEquals(expected, login.registerUser());
    }

    @Test
    void testRegisterUserUsernameFailMessage() {
        login = new Login("kyle!!!!!!!", "Ch&&sec@ke99!", "+27838968976",
                "Kyle", "Smith");
        assertEquals(Login.USERNAME_FAIL, login.registerUser());
    }

    @Test
    void testRegisterUserPasswordFailMessage() {
        login = new Login("kyl_1", "password", "+27838968976",
                "Kyle", "Smith");
        assertEquals(Login.PASSWORD_FAIL, login.registerUser());
    }

    @Test
    void testRegisterUserCellFailMessage() {
        login = new Login("kyl_1", "Ch&&sec@ke99!", "08966553",
                "Kyle", "Smith");
        assertEquals(Login.CELL_FAIL, login.registerUser());
    }

    // ---------------- Login tests ----------------

    @Test
    void testLoginSuccessful() {
        login = new Login("kyl_1", "Ch&&sec@ke99!", "+27838968976",
                "Kyle", "Smith");
        assertTrue(login.loginUser("kyl_1", "Ch&&sec@ke99!"));
    }

    @Test
    void testLoginFailed() {
        login = new Login("kyl_1", "Ch&&sec@ke99!", "+27838968976",
                "Kyle", "Smith");
        assertFalse(login.loginUser("kyl_1", "wrongPassword1!"));
    }

    @Test
    void testReturnLoginStatusSuccess() {
        login = new Login("kyl_1", "Ch&&sec@ke99!", "+27838968976",
                "Kyle", "Smith");
        assertEquals("Welcome Kyle, Smith it is great to see you again.",
                login.returnLoginStatus(true));
    }

    @Test
    void testReturnLoginStatusFailure() {
        login = new Login("kyl_1", "Ch&&sec@ke99!", "+27838968976",
                "Kyle", "Smith");
        assertEquals(Login.LOGIN_FAIL, login.returnLoginStatus(false));
    }
}
