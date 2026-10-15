package com.scc;

import com.scc.dao.DatabaseConnection;
import com.scc.dao.UserDao;
import com.scc.model.User;

import java.util.Optional;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final UserDao userDao = new UserDao();
    private static User currentUser = null; // the logged-in user (session)

    public static void main(String[] args) {
        DatabaseConnection.initializeDatabase();

        boolean running = true;
        while (running) {
            if (currentUser == null) {
                running = showMainMenu();
            } else {
                showUserMenu();
            }
        }

        System.out.println("Thank you for using the Movie Ticket System. Goodbye!");
        scanner.close();
    }

    // ---------- MAIN MENU (not logged in) ----------
    // returns false when the user chooses to exit
    private static boolean showMainMenu() {
        System.out.println("\n=================================");
        System.out.println("      MOVIE TICKET SYSTEM");
        System.out.println("=================================");
        System.out.println("[1] Register Account");
        System.out.println("[2] Login");
        System.out.println("[3] Exit");
        System.out.print("Choose: ");

        switch (scanner.nextLine().trim()) {
            case "1" -> register();
            case "2" -> login();
            case "3" -> { return false; }
            default -> System.out.println("-> Invalid choice. Try again.");
        }
        return true;
    }

    // ---------- REGISTER ----------
    private static void register() {
        System.out.println("\n--- REGISTER ---");

        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("-> Username and password cannot be empty.");
            return;
        }

        System.out.println("Select role:");
        System.out.println("[1] STAFF (Cashier)");
        System.out.println("[2] CUSTOMER");
        System.out.print("Choose: ");

        String role;
        switch (scanner.nextLine().trim()) {
            case "1" -> role = "STAFF";
            case "2" -> role = "CUSTOMER";
            default -> {
                System.out.println("-> Invalid role. Registration cancelled.");
                return;
            }
        }

        // status is set to PENDING inside UserDao
        userDao.registerUser(username, password, role);
    }

    // ---------- LOGIN ----------
    private static void login() {
        System.out.println("\n--- LOGIN ---");

        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        Optional<User> result = userDao.loginUser(username, password);

        if (result.isEmpty()) {
            System.out.println("-> Invalid username or password.");
            return;
        }

        User user = result.get();

        if (!"APPROVED".equalsIgnoreCase(user.status())) {
            System.out.println("-> Your account is " + user.status() + ". Please wait for admin approval.");
            return;
        }

        currentUser = user;
        System.out.println("-> Login successful! Welcome, " + user.username() + " (" + user.roles() + ")");
    }

    // ---------- USER MENU (logged in) ----------
    private static void showUserMenu() {
        System.out.println("\n=================================");
        System.out.println("Logged in as: " + currentUser);
        System.out.println("=================================");

        // Role-based options (placeholders for the next modules)
        switch (currentUser.roles()) {
            case "ADMIN" -> System.out.println("[1] Manage Movies & Schedules (coming soon)");
            case "STAFF" -> System.out.println("[1] Manage Bookings & Payments (coming soon)");
            case "CUSTOMER" -> System.out.println("[1] View Movies & Book Tickets (coming soon)");
            default -> System.out.println("[1] (no actions available)");
        }
        System.out.println("[2] Logout");
        System.out.println("[3] Exit");
        System.out.print("Choose: ");

        switch (scanner.nextLine().trim()) {
            case "1" -> System.out.println("-> This feature is not available yet.");
            case "2" -> logout();
            case "3" -> {
                logout();
                System.out.println("-> Exiting...");
                scanner.close();
                System.exit(0);
            }
            default -> System.out.println("-> Invalid choice. Try again.");
        }
    }

    // ---------- LOGOUT ----------
    private static void logout() {
        System.out.println("-> " + currentUser.username() + " logged out successfully.");
        currentUser = null; // ends the session, loop goes back to main menu
    }
}