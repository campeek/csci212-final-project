package com.library.checkout;

import com.library.checkout.book.Book;
import com.library.checkout.user.User;
import com.library.checkout.user.UserService;

import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Librarian: manages inventory of Book objects (loaded/saved from a simple CSV-like file),
 * coordinates checkouts/returns against UserService, and tracks simple due dates.
 *
 * Integration notes for this repository:
 * - Uses com.library.checkout.book.Book (expects Book(String whole_line) constructor that parses "author,title,number").
 * - Uses com.library.checkout.user.UserService for user lookup.
 * - Persists books to a plain text file where each line is: author,title,serial_number
 *
 * Usage example:
 *   UserService users = new UserService("users.txt");
 *   Librarian lib = new Librarian("books.txt", users);
 *   lib.addBook(new Book("Tolkien","The Hobbit", 1001));
 *   lib.checkoutBook(1001, 0); // user id 0
 */
public class Librarian {

    private final UserService userService;
    private final String booksFilePath;

    // inventory keyed by serial number
    private final Map<Integer, Book> inventory = new HashMap<>();

    // rentals: serial_number -> userId
    private final Map<Integer, Integer> rentals = new HashMap<>();

    // due dates for rented books: serial_number -> due date
    private final Map<Integer, LocalDate> dueDates = new HashMap<>();

    // Defaults (configurable via constructor overload if desired)
    private final int defaultLoanDays = 14;

    public Librarian(String booksFilePath, UserService userService) {
        this.booksFilePath = Objects.requireNonNull(booksFilePath);
        this.userService = Objects.requireNonNull(userService);
        loadBooks();
    }

    // -------------------------
    // Inventory management
    // -------------------------

    /**
     * Adds a book to the inventory and persists.
     */
    public synchronized void addBook(Book b) {
        if (b == null) throw new IllegalArgumentException("book cannot be null");
        int serial = b.get_serial_number();
        inventory.put(serial, b);
        saveBooks();
    }

    /**
     * Removes a book from inventory if it exists and is not currently rented.
     * Returns true if removed.
     */
    public synchronized boolean removeBook(int serialNumber) {
        if (!inventory.containsKey(serialNumber)) return false;
        if (rentals.containsKey(serialNumber)) return false; // can't remove rented book
        inventory.remove(serialNumber);
        saveBooks();
        return true;
    }

    /**
     * Returns an unmodifiable list of all books in inventory.
     */
    public synchronized List<Book> listAllBooks() {
        return Collections.unmodifiableList(new ArrayList<>(inventory.values()));
    }

    /**
     * Search books by title fragment (case-insensitive substring).
     */
    public synchronized List<Book> searchByTitle(String fragment) {
        if (fragment == null) fragment = "";
        String f = fragment.toLowerCase(Locale.ROOT);
        List<Book> out = new ArrayList<>();
        for (Book b : inventory.values()) {
            if (b.get_title().toLowerCase(Locale.ROOT).contains(f)) out.add(b);
        }
        return out;
    }

    /**
     * Search books by author fragment (case-insensitive substring).
     */
    public synchronized List<Book> searchByAuthor(String fragment) {
        if (fragment == null) fragment = "";
        String f = fragment.toLowerCase(Locale.ROOT);
        List<Book> out = new ArrayList<>();
        for (Book b : inventory.values()) {
            if (b.get_author().toLowerCase(Locale.ROOT).contains(f)) out.add(b);
        }
        return out;
    }
    /**
     * Checkout a book by serial number to a user id. Returns the due date.
     *
     * @throws BookNotFoundException if book does not exist
     * @throws BookAlreadyRentedException if book is already rented
     * @throws UserNotFoundException if user id doesn't exist in UserService
     */
    public synchronized LocalDate checkoutBook(int serialNumber, int userId)
            throws BookNotFoundException, BookAlreadyRentedException, UserNotFoundException {

        Book book = inventory.get(serialNumber);
        if (book == null) throw new BookNotFoundException(serialNumber);
        if (rentals.containsKey(serialNumber)) throw new BookAlreadyRentedException(serialNumber);

        Optional<User> userOpt = userService.getUserById(userId);
        if (userOpt.isEmpty()) throw new UserNotFoundException(userId);

        LocalDate due = LocalDate.now().plusDays(defaultLoanDays);
        rentals.put(serialNumber, userId);
        dueDates.put(serialNumber, due);
        return due;
    }

    /**
     * Return a book. If the book is overdue, returns the fine amount (in currency units).
     * Returns 0.0 if no fine.
     *
     * @throws BookNotFoundException if book does not exist
     * @throws NotRentedException if book is not currently rented
     * @throws NotRentedByUserException if the provided userId is not the renter
     */
    public synchronized double returnBook(int serialNumber, int userId)
            throws BookNotFoundException, NotRentedException, NotRentedByUserException {

        Book book = inventory.get(serialNumber);
        if (book == null) throw new BookNotFoundException(serialNumber);

        if (!rentals.containsKey(serialNumber)) throw new NotRentedException(serialNumber);

        int renterId = rentals.get(serialNumber);
        if (renterId != userId) throw new NotRentedByUserException(serialNumber, renterId, userId);

        LocalDate due = dueDates.get(serialNumber);
        LocalDate now = LocalDate.now();
        double fine = 0.0;
        if (due != null && now.isAfter(due)) {
            long daysOver = ChronoUnit.DAYS.between(due, now);
            final double finePerDay = 0.50; // fixed policy; adjust if desired
            fine = daysOver * finePerDay;
        }

        rentals.remove(serialNumber);
        dueDates.remove(serialNumber);
        return fine;
    }

    /**
     * Returns true if the book is currently rented.
     */
    public synchronized boolean isRented(int serialNumber) {
        return rentals.containsKey(serialNumber);
    }

    /**
     * Returns the user id who has rented the book, or Optional.empty() if not rented.
     */
    public synchronized Optional<Integer> getRenter(int serialNumber) {
        return Optional.ofNullable(rentals.get(serialNumber));
    }

    /**
     * Returns the due date for a rented book, or Optional.empty() if not rented / no due date.
     */
    public synchronized Optional<LocalDate> getDueDate(int serialNumber) {
        return Optional.ofNullable(dueDates.get(serialNumber));
    }

    /**
     * Returns a human-readable list of current rentals: one entry per rented book with book info,
     * renter id and renter name (if available), and due date.
     */
    public synchronized List<String> listRenters() {
        List<String> out = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : rentals.entrySet()) {
            int serial = entry.getKey();
            int userId = entry.getValue();
            Book b = inventory.get(serial);
            String title = (b != null) ? b.get_title() : "(unknown book)";
            Optional<User> uOpt = userService.getUserById(userId);
            String userDesc = uOpt.map(u -> u.id() + " - " + u.name()).orElse(String.valueOf(userId));
            String due = dueDates.get(serial) != null ? dueDates.get(serial).toString() : "no due date";
            out.add(String.format("%d: \"%s\" by %s — rented by %s — due %s",
                    serial, title, (b != null ? b.get_author() : "unknown"), userDesc, due));
        }
        return out;
    }

    // -------------------------
    // Persistence
    // -------------------------

    /**
     * Load books from the booksFilePath. If the file does not exist, inventory is left empty.
     * Expected line format (matching Book(String whole_line)): author,title,serial_number
     */
    private synchronized void loadBooks() {
        inventory.clear();
        File f = new File(booksFilePath);
        if (!f.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                Book b = new Book(line);
                inventory.put(b.get_serial_number(), b);
            }
        } catch (IOException e) {
            // Fail quietly in library context; could be logged or rethrown as runtime in stricter apps
            System.err.println("Failed to load books from " + booksFilePath + ": " + e.getMessage());
        }
    }

    /**
     * Save the current inventory to booksFilePath. Each book written as: author,title,serial_number
     */
    private synchronized void saveBooks() {
        File f = new File(booksFilePath);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(f, false))) {
            for (Book b : inventory.values()) {
                String line = String.format("%s,%s,%d", escapeCsv(b.get_author()), escapeCsv(b.get_title()), b.get_serial_number());
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Failed to save books to " + booksFilePath + ": " + e.getMessage());
        }
    }

    // Simple CSV escape for commas inside author/title (wrap in quotes if contains comma or quote)
    private String escapeCsv(String s) {
        if (s == null) return "";
        if (s.contains(",") || s.contains("\"")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        } else {
            return s;
        }
    }

    // -------------------------
    // Exceptions
    // -------------------------

    public static class BookNotFoundException extends Exception {
        public BookNotFoundException(int serial) {
            super("Book not found: serial=" + serial);
        }
    }

    public static class BookAlreadyRentedException extends Exception {
        public BookAlreadyRentedException(int serial) {
            super("Book already rented: serial=" + serial);
        }
    }

    public static class NotRentedException extends Exception {
        public NotRentedException(int serial) {
            super("Book is not rented: serial=" + serial);
        }
    }

    public static class NotRentedByUserException extends Exception {
        public NotRentedByUserException(int serial, int actualRenterId, int attemptedByUserId) {
            super("Book serial=" + serial + " is rented by user " + actualRenterId + ", not by user " + attemptedByUserId);
        }
    }

    public static class UserNotFoundException extends Exception {
        public UserNotFoundException(int userId) {
            super("User not found: id=" + userId);
        }
    }
}
