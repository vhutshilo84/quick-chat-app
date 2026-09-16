package quickchat;

import java.util.Scanner;

/**
 * Main.java
 *
 * Simple console driver that demonstrates the registration and login
 * feature required for PROG5121 PoE Part 1. No GUI / JOptionPane is used,
 * per the assignment instructions.
 */
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Login login = null;

        System.out.println("=== QuickChat Registration ===");

        // Keep asking for details until registration succeeds, so the user
        // can see and correct each validation message in turn.
        boolean registered = false;
        while (!registered) {
            System.out.print("Enter a username (must contain '_' and be <= 5 characters): ");
            String username = scanner.nextLine();

            System.out.print("Enter a password (8+ chars, capital letter, number, special character): ");
            String password = scanner.nextLine();

            System.out.print("Enter your cell phone number (e.g. +27838968976): ");
            String cellPhoneNumber = scanner.nextLine();

            System.out.print("Enter your first name: ");
            String firstName = scanner.nextLine();

            System.out.print("Enter your last name: ");
            String lastName = scanner.nextLine();

            login = new Login(username, password, cellPhoneNumber, firstName, lastName);
            String result = login.registerUser();
            System.out.println(result);

            registered = login.checkUserName()
                    && login.checkPasswordComplexity()
                    && login.checkCellPhoneNumber();

            if (!registered) {
                System.out.println("Please try registering again.\n");
            }
        }

        System.out.println("\n=== QuickChat Login ===");
        System.out.print("Enter your username: ");
        String loginUsername = scanner.nextLine();
        System.out.print("Enter your password: ");
        String loginPassword = scanner.nextLine();

        boolean success = login.loginUser(loginUsername, loginPassword);
        System.out.println(login.returnLoginStatus(success));

        scanner.close();
    }
}
