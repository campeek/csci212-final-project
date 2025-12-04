#ifndef CSCI212_FINAL_PROJECT_BOOK_H
#define CSCI212_FINAL_PROJECT_BOOK_H
#include <string>
#include<vector>
struct Book {
    std::string author;
    std::string title;
    int serial_number;
    bool checked_out;
};
Book create_book(std::string auth, std::string titl, int sn, bool check);
Book create_book(std::string line);
std::vector<Book> load_books_from_file(std::string path);
void write_books_to_file(std::vector<Book>& books, std::string path);



#endif //CSCI212_FINAL_PROJECT_BOOK_H
