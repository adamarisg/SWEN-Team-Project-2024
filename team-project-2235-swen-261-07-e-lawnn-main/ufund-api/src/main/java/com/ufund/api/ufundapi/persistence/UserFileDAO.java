package com.ufund.api.ufundapi.persistence;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufund.api.ufundapi.model.User;

@Component
public class UserFileDAO implements UserDAO {

    private static final Logger LOG = Logger.getLogger(UserFileDAO.class.getName());
    Map<String, User> users;
    private ObjectMapper objectMapper;

    private final static String filename = "data/users.json";

    public UserFileDAO(ObjectMapper objectMapper) throws IOException {
        this.objectMapper = objectMapper;
        load();
    }

    public User[] getUsers() {
        User[] userArray = new User[users.size()];
        users.values().toArray(userArray);
        return userArray;
    }

    /**
     * write the local hashmap to the file
     */
    private boolean save() throws IOException {
        User[] userArray = getUsers();
        objectMapper.writeValue(new File(filename), userArray);
        return true;
    }

    /**
     * loads the contents of the file into the local hashmap
     */
    private boolean load() throws IOException {
        this.users = new TreeMap<>();
        User[] userArray = objectMapper.readValue(new File(filename), User[].class);
        for (User user : userArray) {
            this.users.put(user.getUsername(), user);
        }
        return true;
    }

    /**
     * finds the user with the given id and returns it.
     * if a user with that id is unable to be found, return null
     */
    @Override
    public User getUser(String username) throws IOException {
        synchronized(users) {
            if (users.containsKey(username)) {
                return users.get(username);
            } else {
                return null;
            }
        }
    }

    /**
     * writes a given user to the file
     * returns the new user with it's assigned id number
     */
    @Override
    public User createUser(String username) throws IOException {
        synchronized(users) {
            for (User u : users.values()) {
                if (u.getUsername().equals(username)) {
                    LOG.log(Level.WARNING, "username: " + u.getUsername() + " already taken");
                    return null;
                }
            }
            User newUser = new User(username, new ArrayList<Integer>());
            users.put(username, newUser);
            save();
            return newUser;
        }
    }

    /**
     * finds a need with the same id number and replaces it with the given need
     * if a need with the same id is unable to be found, it returns null
     * otherwise it returns the updatedd need.
     */
    @Override
    public User addItemToUsersCart(String username, int needId) throws IOException {
        synchronized(users) {
            if (!users.containsKey(username)) {
                return null; // user not found
            } else {
                User u = users.remove(username);
                u.addToCart(needId);
                users.put(username, u);
                save();
                return u;
            }
        }
    }

    @Override
    public boolean deleteUser(String username) throws IOException{
        synchronized(users) {
            if (users.containsKey(username)) {
                users.remove(username);
                return save();
            } else {
                return false;
            }
        }
    }

}
