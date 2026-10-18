package quickchat;

import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Main.java
 *
 * Console driver for QuickChat (PROG5121 PoE, Parts 1 and 2).
 *
 * Flow:
 * <ol>
 *   <li>Register a user (Part 1 rules: username, password, cell number).</li>
 *   <li>Log in - messages may only be sent after a successful login.</li>
 *   <li>Display "Welcome to QuickChat." and ask how many messages will be
 *       entered.</li>
 *   <li>Show the numeric menu (1 = Send Messages, 2 = Show recently sent
 *       messages, 3 = Quit) inside a while loop that runs until the user
 *       quits.</li>
 *   <li>Use a for loop to capture exactly the number of messages the user
 *       asked for, using the loop counter as the message number.</li>
 * </ol>
 *
 * No GUI or JOptionPane is used, as required by the brief.
 */
public class Main {

    /** How many login attempts a user gets before the app closes. */
    private static final int MAX_LOGIN_ATTEMPTS = 3;

    /** Welcome message required by the brief. */
    private static final String WELCOME_MESSAGE = "Welcome to QuickChat.";

    /**
     * Starts the console application.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            run(scanner);
        }
    }

    /**
     * Runs the full QuickChat flow. Kept separate from {@link #main(String[])}
     * so that it can be driven with any {@link Scanner} (for example during
     * a demonstration or a scripted run).
     *
     * @param scanner the source of user input
     */
    static void run(Scanner scanner) {
        try {
            System.out.println("=== QuickChat Registration ===");
            Login user = register(scanner);

            System.out.println();
            System.out.println("=== QuickChat Login ===");
            if (!login(scanner, user)) {
                return;
            }

            System.out.println(WELCOME_MESSAGE);
            int messageQuota = readMessageQuota(scanner);
            runMenu(scanner, messageQuota);
        } catch (NoMoreInputException e) {
            System.out.println();
            System.out.println("No more input was received, so QuickChat is closing.");
        }
    }

    // ------------------------------------------------------------------
    // Registration and login
    // ------------------------------------------------------------------

    /**
     * Captures registration details and keeps asking until every Part 1 rule
     * is satisfied.
     *
     * @param scanner the source of user input
     * @return the successfully registered user
     */
    private static Login register(Scanner scanner) {
        while (true) {
            String username = readLine(scanner,
                    "Enter a username (must contain '_' and be no more than 5 characters): ");
            String password = readLine(scanner,
                    "Enter a password (at least 8 characters, a capital letter, a number and a special character): ");
            String cellPhoneNumber = readLine(scanner,
                    "Enter your cell phone number (e.g. +27838968976): ");
            String firstName = readLine(scanner, "Enter your first name: ");
            String lastName = readLine(scanner, "Enter your last name: ");

            Login login = new Login(username, password, cellPhoneNumber,
                    firstName, lastName);
            System.out.println(login.registerUser());

            boolean registered = login.checkUserName()
                    && login.checkPasswordComplexity()
                    && login.checkCellPhoneNumber();
            if (registered) {
                return login;
            }
            System.out.println("Please try registering again.");
            System.out.println();
        }
    }

    /**
     * Asks for login details and verifies them against the registered user.
     *
     * @param scanner the source of user input
     * @param user    the registered user
     * @return {@code true} when the user logged in successfully
     */
    private static boolean login(Scanner scanner, Login user) {
        for (int attempt = 1; attempt <= MAX_LOGIN_ATTEMPTS; attempt++) {
            String username = readLine(scanner, "Enter your username: ");
            String password = readLine(scanner, "Enter your password: ");

            boolean success = user.loginUser(username, password);
            System.out.println(user.returnLoginStatus(success));
            if (success) {
                return true;
            }
            System.out.println("Login attempt " + attempt + " of "
                    + MAX_LOGIN_ATTEMPTS + " failed.");
        }
        System.out.println("Too many failed login attempts. QuickChat will close.");
        return false;
    }

    // ------------------------------------------------------------------
    // Menu
    // ------------------------------------------------------------------

    /**
     * Displays the numeric menu and processes the user's choice until the
     * user selects Quit.
     *
     * @param scanner      the source of user input
     * @param messageQuota how many messages the user may enter
     */
    private static void runMenu(Scanner scanner, int messageQuota) {
        int messagesEntered = 0;
        boolean running = true;

        while (running) {
            printMenu();
            String choice = readLine(scanner, "Choose an option: ").trim();

            switch (choice) {
                case "1":
                    messagesEntered = sendMessages(scanner, messagesEntered,
                            messageQuota);
                    break;
                case "2":
                    System.out.println("Coming Soon.");
                    break;
                case "3":
                    running = false;
                    System.out.println("Goodbye.");
                    break;
                default:
                    System.out.println("'" + choice
                            + "' is not a valid option. Please enter 1, 2 or 3.");
                    break;
            }
        }
    }

    /** Prints the numeric menu. */
    private static void printMenu() {
        System.out.println();
        System.out.println("--- QuickChat Menu ---");
        System.out.println("1) Send Messages");
        System.out.println("2) Show recently sent messages");
        System.out.println("3) Quit");
    }

    /**
     * Captures the requested number of messages with a for loop. The loop
     * counter is used as the message number, and the loop stops as soon as the
     * user's quota has been reached.
     *
     * @param scanner         the source of user input
     * @param messagesEntered how many messages have already been captured
     * @param messageQuota    the total number of messages allowed
     * @return the new number of messages captured
     */
    private static int sendMessages(Scanner scanner, int messagesEntered,
                                    int messageQuota) {
        if (messagesEntered >= messageQuota) {
            System.out.println("You have already entered all " + messageQuota
                    + " of the messages allowed for this session.");
            return messagesEntered;
        }

        for (int messageNumber = messagesEntered; messageNumber < messageQuota;
             messageNumber++) {
            System.out.println();
            System.out.println("--- Message " + (messageNumber + 1) + " of "
                    + messageQuota + " ---");

            String recipient = readRecipient(scanner);
            String messageText = readMessageText(scanner);

            Message message = new Message(messageNumber, recipient, messageText);
            System.out.println(message.displayMessageID());
            System.out.println(message.getMessageDetails());

            int action = readAction(scanner);
            System.out.println(message.SentMessage(action));
            if (action == Message.DISREGARD) {
                confirmDeletion(scanner);
            }
            System.out.println();
        }

        System.out.println("Total messages sent: " + Message.returnTotalMessagess());
        return messageQuota;
    }

    // ------------------------------------------------------------------
    // Input helpers
    // ------------------------------------------------------------------

    /**
     * Asks how many messages the user wants to enter.
     *
     * @param scanner the source of user input
     * @return a positive message count
     */
    private static int readMessageQuota(Scanner scanner) {
        while (true) {
            String input = readLine(scanner,
                    "How many messages do you wish to send? ").trim();
            try {
                int quota = Integer.parseInt(input);
                if (quota > 0) {
                    return quota;
                }
            } catch (NumberFormatException e) {
                // Fall through to the message below.
            }
            System.out.println("Please enter a whole number greater than zero.");
        }
    }

    /**
     * Reads a recipient cell number and re-prompts until it is valid.
     *
     * @param scanner the source of user input
     * @return a valid international cell number
     */
    private static String readRecipient(Scanner scanner) {
        while (true) {
            String recipient = readLine(scanner,
                    "Enter the recipient's cell number (e.g. +27718693002): ").trim();
            if (ValidationUtils.isValidCellNumber(recipient)) {
                System.out.println(Message.RECIPIENT_SUCCESS);
                return recipient;
            }
            System.out.println(Message.RECIPIENT_FAIL);
        }
    }

    /**
     * Reads the message text and re-prompts until it is 250 characters or less.
     *
     * @param scanner the source of user input
     * @return the message text
     */
    private static String readMessageText(Scanner scanner) {
        while (true) {
            String message = readLine(scanner, "Enter the message (maximum "
                    + Message.MAX_MESSAGE_LENGTH + " characters): ");
            if (ValidationUtils.isValidMessageLength(message)) {
                System.out.println(Message.MESSAGE_READY);
                return message;
            }
            System.out.println(ValidationUtils.messageLengthFeedback(message));
        }
    }

    /**
     * Asks what should happen to the captured message.
     *
     * @param scanner the source of user input
     * @return {@link Message#SEND}, {@link Message#DISREGARD} or
     *         {@link Message#STORE}
     */
    private static int readAction(Scanner scanner) {
        while (true) {
            System.out.println("What would you like to do with this message?");
            System.out.println("  1) Send Message");
            System.out.println("  2) Disregard Message");
            System.out.println("  3) Store Message to send later");
            String choice = readLine(scanner, "Choose an option: ").trim();

            switch (choice) {
                case "1":
                    return Message.SEND;
                case "2":
                    return Message.DISREGARD;
                case "3":
                    return Message.STORE;
                default:
                    System.out.println("Please enter 1, 2 or 3.");
                    break;
            }
        }
    }

    /**
     * Confirms that a disregarded message should be deleted, as the brief's
     * "Press 0 to delete the message" instruction requires.
     *
     * @param scanner the source of user input
     */
    private static void confirmDeletion(Scanner scanner) {
        String input = readLine(scanner, "Press 0 to delete the message: ").trim();
        if ("0".equals(input)) {
            System.out.println("Message deleted.");
        } else {
            System.out.println("The message was not sent, stored or deleted.");
        }
    }

    /**
     * Prints a prompt and reads one line of input.
     *
     * @param scanner the source of user input
     * @param prompt  the prompt to display
     * @return the line that was entered
     * @throws NoMoreInputException when the input stream has ended
     */
    private static String readLine(Scanner scanner, String prompt) {
        System.out.print(prompt);
        if (!scanner.hasNextLine()) {
            throw new NoMoreInputException();
        }
        return scanner.nextLine();
    }

    /**
     * Signals that the input stream ended, so the console app can shut down
     * cleanly instead of printing a stack trace.
     */
    private static final class NoMoreInputException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        NoMoreInputException() {
            super("No more input available");
        }
    }
}
