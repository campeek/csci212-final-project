// LibraryUI.java
// Madelyn LaPointe

package com.library.checkout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import com.library.checkout.book.Book;             // Book class
import com.library.checkout.book.BookSorter;       // Sorting helper
import com.library.checkout.user.User;             // User class (for login + role + id)
import com.library.checkout.user.UserService;      // User database logic

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LibraryUI {

    private JFrame loginFrame; // Window for the login screen
    private JFrame userMenuFrame; // Window for the normal user menu
    private JFrame librarianMenuFrame; // Window for the librarian menu

    // Stores the current user's role
    private String currentRole = "";

    // Store current logged-in user id so we DON'T ask for it on checkout/return
    private Integer currentUserId = null;

    // Backend objects
    private UserService userService = null;              // Reads/writes users.txt
    private Librarian librarian = null;                  // Reads/writes books.txt + uses UserService

    // Files (these need to match where your txt files actually are in the project)
    private static final String USERS_FILE = "com/library/checkout/users.txt";
    private static final String BOOKS_FILE = "com/library/checkout/books.txt";

    // MAIN PROGRAM

    public static void main(String[] args) {

        try {
            // Loop through all installed look-and-feel options on this computer
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                // If 'Nimbus' is found
                if ("Nimbus".equals(info.getName())) {
                    // Then tell Swing to use that style
                    UIManager.setLookAndFeel(info.getClassName());
                    break; // Stop looking once Nimbus is found
                }
            }
        } catch (Exception e) {
            // If anything goes wrong, just move on and use default 'look-and-feel'
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LibraryUI().showLoginWindow();
            }
        });
    }

    // Pop-ups

    private void showInfo(String msg, String title) {
        JOptionPane.showMessageDialog(
                null, // To center it (no parent component)
                msg,
                title,
                JOptionPane.INFORMATION_MESSAGE // Type of dialog: informational
        );
    }

    // Shows an error pop-up window with a message and title
    private void showError(String msg, String title) {
        JOptionPane.showMessageDialog(
                null,
                msg,
                title,
                JOptionPane.ERROR_MESSAGE // Type of dialog: error
        );
    }

    // make sure backend is ready before we try to do anything
    private boolean initBackendIfNeeded() {
        if (userService != null && librarian != null) {
            return true;
        }

        try {
            java.nio.file.Path usersPath = java.nio.file.Paths.get(USERS_FILE);
            java.nio.file.Path booksPath = java.nio.file.Paths.get(BOOKS_FILE);

            // create the files if they don't exist so it doesn't crash on startup
            if (!java.nio.file.Files.exists(usersPath)) {
                java.nio.file.Files.createDirectories(usersPath.getParent());
                java.nio.file.Files.createFile(usersPath);
            }
            if (!java.nio.file.Files.exists(booksPath)) {
                java.nio.file.Files.createDirectories(booksPath.getParent());
                java.nio.file.Files.createFile(booksPath);
            }

            userService = new UserService(USERS_FILE);
            librarian = new Librarian(BOOKS_FILE, userService);

            return true;

        } catch (Exception ex) {
            showError(
                    "Backend failed to load.\n\n" +
                    "Make sure these files exist in your project:\n" +
                    " - " + USERS_FILE + "\n" +
                    " - " + BOOKS_FILE + "\n\n" +
                    "Error: " + ex.getMessage(),
                    "Startup Error"
            );
            return false;
        }
    }

    private ArrayList<Book> getSortedBooksCopy(List<Book> books) {

        ArrayList<Book> copy = new ArrayList<>(books);

        // If there are no books, just return the empty list
        if (copy.isEmpty()) {
            return copy;
        }

        // Popup choices for sorting
        String[] options = {"Title (A-Z)", "Author (A-Z)", "Serial Number (low-high)"};

        int choice = JOptionPane.showOptionDialog(
                null,
                "How would you like to sort the books?",
                "Sort Books",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        // Sort based on what they clicked
        if (choice == 0) {
            BookSorter.sortBooks(copy, BookSorter.BY_TITLE);
        } else if (choice == 1) {
            BookSorter.sortBooks(copy, BookSorter.BY_AUTHOR);
        } else if (choice == 2) {
            BookSorter.sortBooks(copy, BookSorter.BY_SERIAL_NUMBER);
        }
        // If they close the popup, we just return unsorted copy

        return copy;
    }

    // Convert a list of Book objects into a readable string for popups
    private String booksToPrettyString(List<Book> books) {

        if (books == null || books.isEmpty()) {
            return "No books found.";
        }

        StringBuilder sb = new StringBuilder();

        for (Book b : books) {
            int serial = b.get_serial_number();

            sb.append(b.get_title())
              .append(" by ")
              .append(b.get_author())
              .append(" (serial ")
              .append(serial)
              .append(") ");

            // Show if rented or available using librarian logic
            if (librarian != null && librarian.isRented(serial)) {
                sb.append("[RENTED]");
            } else {
                sb.append("[AVAILABLE]");
            }

            sb.append("\n");
        }

        return sb.toString();
    }

    // Creates an account using UserService
    private void createAccountFlow() {
        if (!initBackendIfNeeded()) return;

        String username = JOptionPane.showInputDialog(null, "Create username:");
        if (username == null) return;
        username = username.trim();
        if (username.isEmpty()) {
            showError("Username cannot be empty.", "Create Account");
            return;
        }

        // prevent duplicates
        Optional<User> existing = userService.getUserByUsername(username);
        if (existing.isPresent()) {
            showError("That username already exists. Pick another.", "Create Account");
            return;
        }

        String password = JOptionPane.showInputDialog(null, "Create password:");
        if (password == null) return;
        password = password.trim();
        if (password.isEmpty()) {
            showError("Password cannot be empty.", "Create Account");
            return;
        }

        // Default role for created accounts
        String role = "user";

        // Create user + save to users.txt
        userService.addUser(username, password, role);

        // Fetch user back so we can show their id
        Optional<User> newUserOpt = userService.getUserByUsername(username);
        if (newUserOpt.isPresent()) {
            User newUser = newUserOpt.get();
            showInfo("Account created!\nYour User ID is: " + newUser.id() + "\nRole: " + newUser.roles(),
                    "Create Account");
        } else {
            showInfo("Account created! (Could not re-load user ID, but it was saved.)", "Create Account");
        }
    }

    // Login Screen

    // Builds login window
    public void showLoginWindow() {
        // If a login window was already open before, close it
        if (loginFrame != null) {
            loginFrame.dispose();
        }

        // reset login session values
        currentRole = "";
        currentUserId = null;

        // New window with the title "Library System - Login"
        loginFrame = new JFrame("Library System - Login");
        // When this window is closed, exit the entire program
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Defining some colors for the UI
        Color bgColor = new Color(235, 242, 250); // Background color (light)
        Color headerColor = new Color(33, 70, 120); // Header (dark blue)
        Color cardColor = Color.WHITE; // Middle card white
        Color buttonColor = new Color(52, 120, 200); // Buttons blue

        // Main background panel
        JPanel backgroundPanel = new JPanel(new BorderLayout());
        backgroundPanel.setBackground(bgColor); // Background color
        // Padding:
        backgroundPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header bar

        JPanel headerPanel = new JPanel(); // Header panel
        headerPanel.setBackground(headerColor); // Header color
        // Padding:.
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Text label for the header
        JLabel headerLabel = new JLabel("Library Management System", SwingConstants.CENTER);
        headerLabel.setForeground(Color.WHITE); // White text
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 24)); // Big bold font

        // BorderLayout in the header panel
        headerPanel.setLayout(new BorderLayout());
        headerPanel.add(headerLabel, BorderLayout.CENTER);

        // Card panel

        JPanel cardPanel = new JPanel();
        cardPanel.setBackground(cardColor); // White background
        // Padding:
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1), // Thin gray outline
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));
        // BorderLayout [to split card into form (center) and buttons (south)]
        cardPanel.setLayout(new BorderLayout(0, 15)); // Horizontal, vertical (gaps)

        // Panel to hold the labels and text fields in a grid
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 12));
        formPanel.setOpaque(false);

        // Create the form elements (labels, text fields, password field)
        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField();
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField();

        JLabel hintLabel = new JLabel(
                "<html><div style='width:360px;'>"
                        + "To log in, enter your custom username and password.<br>"
                        + "If you don’t have an account, click <b>Create Account</b>."
                        + "</div></html>"
        );

        // Fonts
        Font labelFont = new Font("SansSerif", Font.PLAIN, 15);
        userLabel.setFont(labelFont);
        passLabel.setFont(labelFont);
        userField.setFont(labelFont);
        passField.setFont(labelFont);
        hintLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
        hintLabel.setForeground(new Color(90, 90, 90));

        // Add the components to the form panel in grid order
        formPanel.add(userLabel); // Row 1, column 1
        formPanel.add(userField); // Row 1, column 2
        formPanel.add(passLabel); // Row 2, column 1
        formPanel.add(passField); // Row 2, column 2
        formPanel.add(new JLabel("")); // Row 3, column 1
        formPanel.add(hintLabel);     // Row 3, column 2

        // Buttons
        JButton loginButton = new JButton("Login");
        JButton createAccountButton = new JButton("Create Account");
        JButton exitButton = new JButton("Exit");

        // Font for buttons
        Font btnFont = new Font("SansSerif", Font.BOLD, 15);
        loginButton.setFont(btnFont);
        createAccountButton.setFont(btnFont);
        exitButton.setFont(btnFont);

        // Login button style
        loginButton.setBackground(buttonColor);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);

        // Create account style (slightly different so it stands out)
        createAccountButton.setBackground(new Color(70, 160, 90));
        createAccountButton.setForeground(Color.WHITE);
        createAccountButton.setFocusPainted(false);

        // Exit button style
        exitButton.setBackground(new Color(180, 60, 60));
        exitButton.setForeground(Color.WHITE);
        exitButton.setFocusPainted(false);

        // Button layout:
        // Row 1: Login + Exit
        // Row 2: Create Account centered
        JPanel buttonRow1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonRow1.setOpaque(false);
        buttonRow1.add(loginButton);
        buttonRow1.add(exitButton);

        JPanel buttonRow2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonRow2.setOpaque(false);
        buttonRow2.add(createAccountButton);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new GridLayout(2, 1, 0, 8));
        buttonPanel.add(buttonRow1);
        buttonPanel.add(buttonRow2);

        // Add the form panel and button panel into the card panel
        cardPanel.add(formPanel, BorderLayout.CENTER);   // Form in the middle of the card
        cardPanel.add(buttonPanel, BorderLayout.SOUTH);  // Buttons at the bottom of the card

        // Add the header panel to the top of the background panel
        backgroundPanel.add(headerPanel, BorderLayout.NORTH);

        // Create a wrapper panel to center the cardPanel
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(cardPanel); // Place the card panel in the center
        backgroundPanel.add(centerWrapper, BorderLayout.CENTER);

        // Use the background panel as the content of the login window
        loginFrame.setContentPane(backgroundPanel);
        loginFrame.setSize(900, 600);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setResizable(true);
        loginFrame.setVisible(true);

        // Buttons logic

        // When the "Login" button is clicked, check username and password against UserService
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!initBackendIfNeeded()) return;

                String username = userField.getText().trim();
                String password = new String(passField.getPassword()).trim();

                if (username.isEmpty() || password.isEmpty()) {
                    showError("Please enter BOTH username and password.", "Login Error");
                    return;
                }

                Optional<User> userOpt = userService.getUserByUsername(username);
                if (userOpt.isEmpty()) {
                    showError("Username not found. Click Create Account if you need one.", "Login Error");
                    return;
                }

                User u = userOpt.get();

                // Password check
                if (!u.password().equals(password)) {
                    showError("Incorrect password.", "Login Error");
                    return;
                }

                // Successful login
                currentUserId = u.id();
                currentRole = u.roles();

                loginFrame.dispose();

                // If they are librarian role, show librarian menu, else user menu
                if (currentRole != null && currentRole.equalsIgnoreCase("librarian")) {
                    showLibrarianMenu();
                } else {
                    showUserMenu();
                }
            }
        });

        // Create Account button
        createAccountButton.addActionListener(e -> createAccountFlow());

        // When "Exit" button is clicked, close the entire program
        exitButton.addActionListener(e -> System.exit(0));
    }

    // User Menu

    // Menu for normal users
    private void showUserMenu() {
        if (!initBackendIfNeeded()) {
            // if backend dies, just kick back to login window
            showLoginWindow();
            return;
        }

        if (userMenuFrame != null) {
            userMenuFrame.dispose();
        }

        // Create a new window for the user menu
        userMenuFrame = new JFrame("Library - User Menu");
        userMenuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Colors for menu
        Color bgColor = new Color(235, 242, 250);
        Color headerColor = new Color(33, 70, 120);
        Color buttonColor = new Color(52, 120, 200);

        // Background panel for window
        JPanel backgroundPanel = new JPanel(new BorderLayout());
        backgroundPanel.setBackground(bgColor);
        backgroundPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Header bar at the top
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(headerColor);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        String idText = (currentUserId == null) ? "" : (" (ID: " + currentUserId + ")");
        JLabel headerLabel = new JLabel("User Menu" + idText, SwingConstants.CENTER);

        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        headerPanel.setLayout(new BorderLayout());
        headerPanel.add(headerLabel, BorderLayout.CENTER);

        // Card panel for buttons
        JPanel cardPanel = new JPanel();
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(20, 40, 20, 40)
        ));
        cardPanel.setLayout(new GridLayout(6, 1, 10, 12)); // 6 buttons stacked vertically

        // Buttons for user actions
        JButton viewAllButton = new JButton("View All Books");
        JButton searchTitleButton = new JButton("Search Books by Title");
        JButton viewRentedButton = new JButton("View Books Currently Rented");
        JButton checkoutButton = new JButton("Check Out Book");
        JButton returnButton = new JButton("Return Book");
        JButton logoutButton = new JButton("Log Out");

        // Style (menu buttons)
        Font btnFont = new Font("SansSerif", Font.BOLD, 15);
        JButton[] buttons = {viewAllButton, searchTitleButton, viewRentedButton, checkoutButton, returnButton, logoutButton};
        for (JButton b : buttons) {
            b.setFont(btnFont);
            b.setBackground(buttonColor);
            b.setForeground(Color.WHITE);
            b.setFocusPainted(false);
        }

        // Add the buttons to the card panel
        cardPanel.add(viewAllButton);
        cardPanel.add(searchTitleButton);
        cardPanel.add(viewRentedButton);
        cardPanel.add(checkoutButton);
        cardPanel.add(returnButton);
        cardPanel.add(logoutButton);

        // Wrapper to center the card in the window
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(cardPanel);

        // Add header and center card to background
        backgroundPanel.add(headerPanel, BorderLayout.NORTH);
        backgroundPanel.add(centerWrapper, BorderLayout.CENTER);

        // Set up frame
        userMenuFrame.setContentPane(backgroundPanel);
        userMenuFrame.setSize(550, 380);
        userMenuFrame.setLocationRelativeTo(null);
        userMenuFrame.setResizable(false);
        userMenuFrame.setVisible(true);

        // Button actions for the user menu

        // Click "View All Books"
        viewAllButton.addActionListener(e -> {
            List<Book> allBooks = librarian.listAllBooks();

            // Ask for sorting, then show list
            ArrayList<Book> sorted = getSortedBooksCopy(allBooks);

            showInfo(booksToPrettyString(sorted), "View All Books");
        });

        // Click "Search Books by Title"
        searchTitleButton.addActionListener(e -> {
            String query = JOptionPane.showInputDialog(null, "Enter part of the title:");
            if (query == null || query.trim().isEmpty()) return;

            List<Book> results = librarian.searchByTitle(query.trim());

            // Ask for sorting, then show results
            ArrayList<Book> sorted = getSortedBooksCopy(results);

            showInfo(booksToPrettyString(sorted), "Search by Title");
        });

        // Click "View Books Currently Rented"
        viewRentedButton.addActionListener(e -> {
            List<Book> allBooks = librarian.listAllBooks();

            StringBuilder sb = new StringBuilder();

            for (Book b : allBooks) {
                if (librarian.isRented(b.get_serial_number())) {
                    sb.append(b.get_title())
                      .append(" by ")
                      .append(b.get_author())
                      .append(" (serial ")
                      .append(b.get_serial_number())
                      .append(")\n");
                }
            }

            if (sb.length() == 0) {
                showInfo("No books are currently rented.", "View Rented");
            } else {
                showInfo(sb.toString(), "View Rented");
            }
        });

        // Click "Check Out Book"
        checkoutButton.addActionListener(e -> {
            if (currentUserId == null) {
                showError("No logged-in user ID found. Please log in again.", "Check Out");
                return;
            }

            String serialText = JOptionPane.showInputDialog(null, "Enter the serial number:");
            if (serialText == null || serialText.trim().isEmpty()) return;

            try {
                int serial = Integer.parseInt(serialText.trim());
                int userId = currentUserId;

                LocalDate due = librarian.checkoutBook(serial, userId);

                showInfo("Checked out successfully!\nDue date: " + due, "Check Out");

            } catch (NumberFormatException ex) {
                showError("Serial number must be a number.", "Check Out");
            } catch (Librarian.BookNotFoundException ex) {
                showError(ex.getMessage(), "Check Out");
            } catch (Librarian.BookAlreadyRentedException ex) {
                showError(ex.getMessage(), "Check Out");
            } catch (Librarian.UserNotFoundException ex) {
                showError(ex.getMessage(), "Check Out");
            }
        });

        // Click "Return Book"
        returnButton.addActionListener(e -> {
            if (currentUserId == null) {
                showError("No logged-in user ID found. Please log in again.", "Return");
                return;
            }

            String serialText = JOptionPane.showInputDialog(null, "Enter the serial number:");
            if (serialText == null || serialText.trim().isEmpty()) return;

            try {
                int serial = Integer.parseInt(serialText.trim());
                int userId = currentUserId;

                double fine = librarian.returnBook(serial, userId);

                if (fine > 0) {
                    showInfo("Returned successfully!\nFine due: $" + fine, "Return");
                } else {
                    showInfo("Returned successfully!\nNo fine due.", "Return");
                }

            } catch (NumberFormatException ex) {
                showError("Serial number must be a number.", "Return");
            } catch (Librarian.BookNotFoundException ex) {
                showError(ex.getMessage(), "Return");
            } catch (Librarian.NotRentedException ex) {
                showError(ex.getMessage(), "Return");
            } catch (Librarian.NotRentedByUserException ex) {
                showError(ex.getMessage(), "Return");
            }
        });

        // Click "Log Out"
        logoutButton.addActionListener(e -> {
            userMenuFrame.dispose();
            currentRole = "";
            currentUserId = null;
            showLoginWindow();
        });
    }

    // Librarian Menu

    // Menu for librarian users
    private void showLibrarianMenu() {
        if (!initBackendIfNeeded()) {
            showLoginWindow();
            return;
        }

        if (librarianMenuFrame != null) {
            librarianMenuFrame.dispose();
        }

        // Create a new window for the librarian menu
        librarianMenuFrame = new JFrame("Library - Librarian Menu");
        librarianMenuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Colors
        Color bgColor = new Color(235, 242, 250);
        Color headerColor = new Color(33, 70, 120);
        Color buttonColor = new Color(52, 120, 200);

        // Background panel with padding
        JPanel backgroundPanel = new JPanel(new BorderLayout());
        backgroundPanel.setBackground(bgColor);
        backgroundPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Header bar at top
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(headerColor);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        String idText = (currentUserId == null) ? "" : (" (ID: " + currentUserId + ")");
        JLabel headerLabel = new JLabel("Librarian Menu" + idText, SwingConstants.CENTER);

        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        headerPanel.setLayout(new BorderLayout());
        headerPanel.add(headerLabel, BorderLayout.CENTER);

        // Card for librarian actions
        JPanel cardPanel = new JPanel();
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(20, 40, 20, 40)
        ));
        cardPanel.setLayout(new GridLayout(5, 1, 10, 12)); // Five buttons stacked

        // Buttons for actions
        JButton viewAllButton = new JButton("View All Books");
        JButton searchTitleButton = new JButton("Search Books by Title");
        JButton addBookButton = new JButton("Add New Book");
        JButton viewRentersButton = new JButton("View Renters");
        JButton logoutButton = new JButton("Log Out");

        // Style buttons
        Font btnFont = new Font("SansSerif", Font.BOLD, 15);
        JButton[] buttons = {viewAllButton, searchTitleButton, addBookButton, viewRentersButton, logoutButton};
        for (JButton b : buttons) {
            b.setFont(btnFont);
            b.setBackground(buttonColor);
            b.setForeground(Color.WHITE);
            b.setFocusPainted(false);
        }

        // Add buttons to card panel
        cardPanel.add(viewAllButton);
        cardPanel.add(searchTitleButton);
        cardPanel.add(addBookButton);
        cardPanel.add(viewRentersButton);
        cardPanel.add(logoutButton);

        // Center the card panel
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(cardPanel);

        // Add header and center card to background
        backgroundPanel.add(headerPanel, BorderLayout.NORTH);
        backgroundPanel.add(centerWrapper, BorderLayout.CENTER);

        // Set up frame
        librarianMenuFrame.setContentPane(backgroundPanel);
        librarianMenuFrame.setSize(550, 380);
        librarianMenuFrame.setLocationRelativeTo(null);
        librarianMenuFrame.setResizable(false);
        librarianMenuFrame.setVisible(true);

        // Button Actions

        // Click "View All Books"
        viewAllButton.addActionListener(e -> {
            List<Book> allBooks = librarian.listAllBooks();

            // Ask for sorting, then show list
            ArrayList<Book> sorted = getSortedBooksCopy(allBooks);

            showInfo(booksToPrettyString(sorted), "View All Books");
        });

        // Click "Search Books by Title"
        searchTitleButton.addActionListener(e -> {
            String query = JOptionPane.showInputDialog(null, "Enter part of the title:");
            if (query == null || query.trim().isEmpty()) return;

            List<Book> results = librarian.searchByTitle(query.trim());

            // Ask for sorting, then show results
            ArrayList<Book> sorted = getSortedBooksCopy(results);

            showInfo(booksToPrettyString(sorted), "Search by Title");
        });

        // Click "Add New Book"
        addBookButton.addActionListener(e -> {
            String author = JOptionPane.showInputDialog(null, "Enter author:");
            if (author == null || author.trim().isEmpty()) return;

            String title = JOptionPane.showInputDialog(null, "Enter title:");
            if (title == null || title.trim().isEmpty()) return;

            String serialText = JOptionPane.showInputDialog(null, "Enter serial number:");
            if (serialText == null || serialText.trim().isEmpty()) return;

            try {
                int serial = Integer.parseInt(serialText.trim());

                // Uses your Book constructor (author, title, serial)
                Book b = new Book(author.trim(), title.trim(), serial);

                // Uses librarian logic (adds + saves to books.txt)
                librarian.addBook(b);

                showInfo("Book added successfully!", "Add Book");

            } catch (NumberFormatException ex) {
                showError("Serial number must be a number.", "Add Book");
            }
        });

        // Click "View Renters"
        viewRentersButton.addActionListener(e -> {
            List<String> renters = librarian.listRenters();

            if (renters.isEmpty()) {
                showInfo("No active rentals.", "View Renters");
                return;
            }

            StringBuilder sb = new StringBuilder();
            for (String line : renters) {
                sb.append(line).append("\n");
            }

            showInfo(sb.toString(), "View Renters");
        });

        // Click "Log Out"
        logoutButton.addActionListener(e -> {
            librarianMenuFrame.dispose();
            currentRole = "";
            currentUserId = null;
            showLoginWindow();
        });
    }
}
