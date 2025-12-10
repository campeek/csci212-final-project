package com.library.checkout.user;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

// UserService.java
// Cameron Peek
// 12/9/2025

/**
 * Interfaces with user database to serialize users and provide a CRUD API for them
 * @author Cameron Peek
 */
public class UserService {

    private final String filePath;

    // using HashMap to organize users by id
    private final HashMap<Integer, User> users = new HashMap<>();

    public UserService(String filePath){
        this.filePath = filePath;
        loadUsers();
    }

    // CRUD operations

    /**
     * Adds and serializes a new user
     * @param username Username of the new user
     * @param password Password of the new user
     * @param role Role of the new user
     */
    public void addUser(String username, String password, String role){
        // when creating new users, using the hashmap size as the id SHOULD prevent collisions
        // I guarantee that will break at some point tho.

        User newUser = new User(users.size(), username, password, role);
        users.put(newUser.id(), newUser);
        appendUser(newUser);
    }

    /**
     * Updates and serializes a user
     * @param id ID of the user to be updated
     * @param newUser The new User object containing the updated user info
     */
    public void updateUserById(int id, User newUser){
        // Java records can't be changed after they're created, so we just nuke the old user
        // and replace them with a new one (rip old user :c)
        users.put(id, newUser);
        saveUsers();
    }

    /**
     * Gets user by {@code id}
     * @param id ID of the user to be found
     * @return An {@code Optional<User>} containing the User if it exists.
     */
    public Optional<User> getUserById(int id){
        if(users.containsKey(id)){
            return Optional.of(users.get(id));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Gets user by {@code username}
     * @param username Username of the user to be found
     * @return An {@code Optional<User>} containing the User if it exists
     */
    public Optional<User> getUserByUsername(String username){
        for(Map.Entry<Integer, User> entry : users.entrySet()){ // iterate through all hashmap entries
            User u = entry.getValue(); // pull User object from Entry
            if(u.name().equals(username)){ // if user exists
                return Optional.of(u);     // return them
            }
        }
        return Optional.empty(); // otherwise return nothing
    }

    /**
     * Gets all users
     * @return An {@code Optional<ArrayList<User>>} containing all users
     */
    // cursed return type vv
    public Optional<ArrayList<User>> getAllUsers(){
        ArrayList<User> userArrayList = new ArrayList<>();
        for(Map.Entry<Integer, User> entry: users.entrySet()){ // dump all users into an arraylist
            User user = entry.getValue();
            userArrayList.add(user);
        }
        if(userArrayList.isEmpty()){ // if there's no users, return nothing
            return Optional.empty();
        } else {
            return Optional.of(userArrayList); // otherwise, return the list.
        }
    }

    private void loadUsers(){
        FileReader fr;
        try{
            fr = new FileReader(filePath); // open file
        } catch (FileNotFoundException e) { // if the file doesn't exist,
            throw new RuntimeException(e); // freak out
        }

        try{
            //List<String> lines = fr.readAllLines();
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            for(String line : lines){
                String[] userData = line.split(","); // split the line by commas

                int id = Integer.parseInt(userData[0]); // parameters for our new user
                String username = userData[1];
                String password = userData[2];
                String roles = userData[3];

                User newUser = new User(id, username, password, roles);

                if(users.containsKey(id)){ // if there's already a user with this id loaded,
                    throw new RuntimeException("user id collision"); // freak out
                } else{
                    users.put(id, newUser);
                }
            }

            fr.close(); // close the file when we're done
        } catch (IOException e) {
            throw new RuntimeException(e); // freak out if something goes wrong
        }
    }

    private void saveUsers(){
        try{
            FileWriter fw = new FileWriter(filePath);

            for(Map.Entry<Integer, User> entry : users.entrySet()){ // iterate through users
                User user = entry.getValue();
                fw.write(user.toString()+"\n"); // write user info to file
            }

            fw.close(); // close file
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void appendUser(User user){
        try{
            FileWriter fw = new FileWriter(filePath, true);
            fw.write(user.toString());

            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
