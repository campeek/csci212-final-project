package com.library.checkout.book;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Book {
	private static final String BOOKS_PATH ="src/com/library/checkout/book/Books.txt";
	private String author;
	private String title;
	private int serial_number;
	private boolean checked_out;
	public Book(){
		this.author="";
		this.title="";
		this.serial_number=-1;
		this.checked_out=false;
	}
	public Book(String author_in, String title_in, int serial_number_in, boolean check){
		this.author=author_in;
		this.title=title_in;
		this.serial_number=serial_number_in;
		this.checked_out=check;
	}
	public Book(String whole_line) {
		try {
			String[] temp=whole_line.split(",");
			this.author=temp[0];
			this.title=temp[1];
			this.serial_number=Integer.parseInt(temp[2]);
			if(temp[3].equals("true")){
				this.checked_out=true;
			}
			else{
				this.checked_out=false;
			}
		}
		catch(Exception e) {
			System.err.println("error converting whole string to Book. remember to use the format 'author,title,number,boolean'");
		}
	}
	public String get_author(){
		return this.author;
	}
	public void set_author(String author_in){
		this.author=author_in;
	}
	public String get_title(){
		return this.title;
	}
	public void set_title(String title_in){
		this.title=title_in;
	}
	public int get_serial_number(){
		return this.serial_number;
	}
	public void set_serial_number(int serial_number_in){
		this.serial_number=serial_number_in;
	}
	public boolean get_checked_out(){
		return this.checked_out;
	}
	public void set_checked_out(boolean check){
		this.checked_out=check;
	}
	public String to_string(){
		return(this.author +","+ this.title +","+this.serial_number +","+this.checked_out);
	}
	public Vector read_books_from_file(){
		Vector<Book> Book_list = new Vector<>();
		String temp_line;
		String temp_auth;
		String temp_title;
		int temp_SN;
		boolean temp_check;
		try (Scanner scanner = new Scanner(new File(BOOKS_PATH))) {
        	while (scanner.hasNextLine()) {
                temp_line = scanner.nextLine();
				try {
					String[] temp=temp_line.split(",");
					temp_auth=temp[0];
					temp_title=temp[1];
					temp_SN=Integer.parseInt(temp[2]);
					if(temp[3].equals("true")){
						temp_check=true;
					}
					else{
						temp_check=false;
					}
					Book temp_book = new Book(temp_auth,temp_title,temp_SN,temp_check);
				    Book_list.add(temp_book);
				}
				catch(Exception e) {
					System.err.println("error converting whole string to Book. remember to use the format 'author,title,number,boolean'");
					continue;
				}	
            }
        } 
		catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        }
		return Book_list;
	}
	public void write_books_to_file(Vector<Book> list){ //clears book.txt, fills it in with books from the vector that is fed into it
		try {
	        FileWriter file_writer = new FileWriter(BOOKS_PATH, false);
			PrintWriter printWriter = new PrintWriter(file_writer);
			for (Book element : list) {
        		printWriter.println(element.to_string()); 
        	}
    	}
		catch (IOException e) {
        	System.err.println("An IOException occurred: " + e.getMessage());
    	}
	}
	public void write_book_to_file(Book book){ //appends one book to the end of the book file.
		try {
	        FileWriter file_writer = new FileWriter(BOOKS_PATH, true);
        	PrintWriter printWriter = new PrintWriter(file_writer);
        	printWriter.println(book.to_string()); // Writes the element followed by a new line			
    	}
		catch (IOException e) {
        	System.err.println("An IOException occurred: " + e.getMessage());
    	}
	}
}
