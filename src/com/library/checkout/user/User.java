package com.library.checkout.user;

// User.java
// Cameron Peek
// 12/9/2025

/**
 * Holds User data
 * @param id ID of user (must be unique)
 * @param name Username of user
 * @param password Password of user
 * @param roles Roles of user
 * @author Cameron Peek
 */

public record User (int id, String name, String password, String roles) {
    @Override
    public String toString(){
        return id+","+name+","+password+","+roles;
    }
}
