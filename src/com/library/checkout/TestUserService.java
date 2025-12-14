package com.library.checkout;

import com.library.checkout.user.User;
import com.library.checkout.user.UserService;

import java.util.ArrayList;
import java.util.Optional;

// TestUserService.java
// Cameron Peek
// 12/9/2025
public class TestUserService {
    static UserService userService;
    public static void main(String[] args){
        userService = new UserService("users.txt");

        checkLoadedUsers();

        System.out.println("\n Adding test users...");
        // add some test users
        userService.addUser("Cameron", "camscoolpassword", "user");
        userService.addUser("Richard 'Tricky Dick' Nixon", "watergatewascool", "user");
        userService.addUser("Erykah Badu", "idontknowanythingabouterykahbadu", "librarian");

        // check again
        checkLoadedUsers();

        // update one
        System.out.println("\n Updating test user...");
        userService.updateUserById(2, new User(2, "Eric Clapton", "laylaismyonlygoodsong", "user"));

        checkLoadedUsers();
    }

    private static void checkLoadedUsers(){
        System.out.println("Loaded Users: ");

        // check loaded users
        ArrayList<User> allUsers = userService.getAllUsers();
        if(!allUsers.isEmpty()){
            for(User u:allUsers){
                System.out.println(u.toString());
            }
        } else {
            System.out.println("No users loaded");
        }
    }
}
