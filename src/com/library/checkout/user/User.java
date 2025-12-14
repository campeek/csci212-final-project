package com.library.checkout.user;

// User.java
// Cameron Peek
// 12/9/2025

import com.library.checkout.book.Book;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Holds User data
 *
 * @author Cameron Peek
 */

public final class User {
    private final int id;
    private final String name;
    private final String password;
    private final String roles;

    private final ArrayList<Integer> checkedOutBooks;

    /**
     * @param id       ID of user (must be unique)
     * @param name     Username of user
     * @param password Password of user
     * @param roles    Roles of user
     *
     */
    public User(int id, String name, String password, String roles) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.roles = roles;
        checkedOutBooks = new ArrayList<>();
    }

    @Override
    public String toString() {
        StringBuilder user = new StringBuilder(id + "," + name + "," + password + "," + roles + ",");
        for(Integer i : checkedOutBooks){
            user.append(i).append(",");
        }

        return user.toString();
    }

    public int id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String password() {
        return password;
    }

    public String roles() {
        return roles;
    }

    public ArrayList<Integer> checkedOutBooks(){
        return checkedOutBooks;
    }

    public void addBook(int bookId){
        checkedOutBooks.add(bookId);
    }
    public void removeBook(int bookId){
        checkedOutBooks.remove((Object)bookId); // have to cast this to Object because otherwise remove() will see it as an index.
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (User) obj;
        return this.id == that.id &&
                Objects.equals(this.name, that.name) &&
                Objects.equals(this.password, that.password) &&
                Objects.equals(this.roles, that.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, password, roles);
    }

}
