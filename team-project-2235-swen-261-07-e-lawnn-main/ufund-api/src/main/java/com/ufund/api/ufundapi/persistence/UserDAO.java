package com.ufund.api.ufundapi.persistence;

import java.io.IOException;

import com.ufund.api.ufundapi.model.User;

public interface UserDAO {

    /**
     * returns a list of all users in the cupboard
     */
    User[] getUsers() throws IOException;

    /**
     * finds the user with the given id and returns it.
     * if a user with that id is unable to be found, return null
     */
    User getUser(String username) throws IOException;

    /**
     * writes a given user to the file
     * if the user with the same title already exists, it will return null
     * otherwise it will return the new user with it's assigned id number
     */
    User createUser(String username) throws IOException;

    /**
     * finds a user with the same id number and replaces it with the given user
     * if a user with the same id is unable to be found, it returns null
     * otherwise it returns the updatedd user.
     */
    User addItemToUsersCart(String username, int needId) throws IOException;

    boolean deleteUser(String username) throws IOException;

}