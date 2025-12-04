

#include "Book.h"
#include <iostream>
#include <sstream>
#include <fstream>
#include <bits/stdc++.h>



Book create_book(std::string auth, std::string titl, int sn, bool check){
    return {auth,titl,sn, check};
}
//stole this from Cameron because I read his code and realized reading in from just one line is a good idea
Book create_book(std::string line){
    std::stringstream ss(line);

    std::string author, title, sn_temp, co_temp;
    int sn;
    bool checked_out;
    try{
		getline(ss, author, ',');
		getline(ss, title, ',');
		getline(ss, sn_temp, ',');
		sn=stoi(sn_temp);
		getline(ss, co_temp, ',');
		transform(co_temp.begin(), co_temp.end(), co_temp.begin(),::tolower);
		if(co_temp=="true"){
			checked_out=true;
		}
		else if(co_temp=="false"){
			checked_out=false;
		}
		else{
			std::cerr<<"Error: entered checked out status was neither true nor false. Defaulting to false";
			checked_out=false;
		}
    }catch(...){
    	std::cerr<<"Error: could not create book from line."<<std::endl;
    }
    return {author,title,sn,checked_out};
}
std::vector<Book> load_books_from_file(std::string path) {
    std::vector<Book> books;

    // TODO: exception handling
    std::ifstream file(path);
    std::string line;
    while (std::getline(file, line)) {
        if (!line.empty()) {
            books.push_back(create_book(line));
        }
    }
    return books;
}
void write_books_to_file(std::vector<Book> books, std::string path) {
    // TODO: exception handling
	std::ofstream file(path);
    std::string temp="";
    for (Book book : books) {

    	if(book.checked_out==1){
    		temp="true";
    	}
    	else{
    		temp="false";
    	}
        file<<book.author<<","<<book.title<<","<<book.serial_number<<","<<temp <<std::endl;
    }
    file.close();
}
