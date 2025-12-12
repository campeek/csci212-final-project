import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

// Sorting helper for the `book` class
public class BookSorter {

    public static final int BY_TITLE = 1;
    public static final int BY_AUTHOR = 2;
    public static final int BY_SERIAL_NUMBER = 3;

    /**
     * Core sorting method.
     * option: 1 = title, 2 = author, 3 = serial number
     */
    public static void sortBooks(List<book> books, int option) {
        if (books == null || books.isEmpty()) {
            System.out.println("No books to sort.");
            return;
        }

        switch (option) {
            case BY_TITLE:
                Collections.sort(books, new Comparator<book>() {
                    @Override
                    public int compare(book b1, book b2) {
                        return b1.get_title().compareToIgnoreCase(b2.get_title());
                    }
                });
                System.out.println("Books sorted by TITLE (A–Z).");
                break;

            case BY_AUTHOR:
                Collections.sort(books, new Comparator<book>() {
                    @Override
                    public int compare(book b1, book b2) {
                        return b1.get_author().compareToIgnoreCase(b2.get_author());
                    }
                });
                System.out.println("Books sorted by AUTHOR (A–Z).");
                break;

            case BY_SERIAL_NUMBER:
                Collections.sort(books, new Comparator<book>() {
                    @Override
                    public int compare(book b1, book b2) {
                        return Integer.compare(b1.get_serial_number(), b2.get_serial_number());
                    }
                });
                System.out.println("Books sorted by SERIAL NUMBER (low → high).");
                break;

            default:
                System.out.println("Invalid sorting option. No sorting applied.");
        }
    }

    /**
     * Shows a menu, asks the user how to sort, then calls sortBooks.
     * You can call this from your main program.
     */
    public static void sortBooksMenu(List<book> books, Scanner in) {
        if (books == null || books.isEmpty()) {
            System.out.println("No books to sort.");
            return;
        }

        System.out.println("\n=== Library Sorting Menu ===");
        System.out.println("1. Sort by Title (A–Z)");
        System.out.println("2. Sort by Author (A–Z)");
        System.out.println("3. Sort by Serial Number (low → high)");
        System.out.print("Enter your choice: ");

        int choice;
        try {
            choice = Integer.parseInt(in.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Sorting cancelled.");
            return;
        }

        sortBooks(books, choice);
    }
}
