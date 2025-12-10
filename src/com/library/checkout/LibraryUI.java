package com.library.checkout;// LibraryUI.java
// Madelyn LaPointe

import javax.swing.*;           
import java.awt.*;              
import java.awt.event.*;        

public class LibraryUI {       

    private JFrame loginFrame; // Window for the login screen
    private JFrame userMenuFrame; // Window for the normal user menu
    private JFrame librarianMenuFrame; // Window for the librarian menu

    // Stores the current user's role
    private String currentRole = "";

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

    // Login Screen

    // Builds login window
    public void showLoginWindow() {
        // If a login window was already open before, close it
        if (loginFrame != null) {
            loginFrame.dispose();
        }

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
        JLabel hintLabel = new JLabel("NOTE: librarian (username) and admin (password) will log you into the librarian account"); // Login info

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

        // Button for logging in and button for exiting
        JButton loginButton = new JButton("Login");
        JButton exitButton = new JButton("Exit");

        // Font for buttons
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 15));
        exitButton.setFont(new Font("SansSerif", Font.BOLD, 15));

        // Login button style
        loginButton.setBackground(buttonColor);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);          

        // Exit button style
        exitButton.setBackground(new Color(180, 60, 60));
        exitButton.setForeground(Color.WHITE);
        exitButton.setFocusPainted(false);           

        // Create a panel to hold the two buttons side by side
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(loginButton);               
        buttonPanel.add(exitButton);                 

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
        loginFrame.setSize(600, 450);                
        loginFrame.setLocationRelativeTo(null);     
        loginFrame.setResizable(false);             
        loginFrame.setVisible(true);                 

        // Buttons logic

        // When the "Login" button is clicked, check  username and password
        loginButton.addActionListener(new ActionListener() { // ActionListener to handle clicks
            @Override // Override the actionPerformed method
            public void actionPerformed(ActionEvent e) { 
                String username = userField.getText().trim(); // Get the username text
                String password = new String(passField.getPassword()).trim(); // Get the password text

                // If username box = empty, show an error and stop
                if (username.isEmpty()) {
                    showError("Please enter a username.", "Login Error");
                    return;                                  
                }
                // If username = "librarian" AND password = "admin", then role is librarian (basically the admin)
                if (username.equalsIgnoreCase("librarian") && password.equals("admin")) {
                    currentRole = "librarian"; // Record role              
                    loginFrame.dispose();                     
                    showLibrarianMenu();                      
                } else {
                    currentRole = "user";                     
                    loginFrame.dispose();                     
                    showUserMenu();                           
                }
            }
        });

        // When  "Exit" button is clicked, close the entire program
        exitButton.addActionListener(e -> System.exit(0)); 
    }

    // User Menu

    // Menu for normal users
    private void showUserMenu() {
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
        JLabel headerLabel = new JLabel("User Menu", SwingConstants.CENTER);
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
        viewAllButton.addActionListener(e ->
                showInfo("Viewing all books... (placeholder)", "View All Books"));

        // Click "Search Books by Title"
        searchTitleButton.addActionListener(e ->
                showInfo("Searching books by title... (placeholder)", "Search by Title"));

        // Click "View Books Currently Rented"
        viewRentedButton.addActionListener(e ->
                showInfo("Viewing books currently rented... (placeholder)", "View Rented"));

        // Click "Check Out Book"
        checkoutButton.addActionListener(e ->
                showInfo("Checking out a book... (placeholder)", "Check Out"));

        // Click "Return Book"
        returnButton.addActionListener(e ->
                showInfo("Returning a book... (placeholder)", "Return"));

        // Click "Log Out"
        logoutButton.addActionListener(e -> {
            userMenuFrame.dispose(); 
            currentRole = "";        
            showLoginWindow();       
        });
    }

    // Librarian Menu

    // Menu for librarian users
    private void showLibrarianMenu() {
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
        JLabel headerLabel = new JLabel("Librarian Menu", SwingConstants.CENTER);
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
        viewAllButton.addActionListener(e ->
                showInfo("Viewing all books... (Librarian view placeholder)", "View All Books"));

        // Click "Search Books by Title"
        searchTitleButton.addActionListener(e ->
                showInfo("Searching books by title... (Librarian placeholder)", "Search by Title"));

        // Click "Add New Book"
        addBookButton.addActionListener(e ->
                showInfo("Adding new book... (Librarian only)", "Add Book"));

        // Click "View Renters"
        viewRentersButton.addActionListener(e ->
                showInfo("Viewing renters... (Librarian only)", "View Renters"));

        // Click "Log Out"
        logoutButton.addActionListener(e -> {
            librarianMenuFrame.dispose(); 
            currentRole = "";            
            showLoginWindow();           
        });
    }
}
