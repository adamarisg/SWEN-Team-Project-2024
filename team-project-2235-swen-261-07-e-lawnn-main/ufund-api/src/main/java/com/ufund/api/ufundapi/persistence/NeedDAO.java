package com.ufund.api.ufundapi.persistence;

import java.io.IOException;

import com.ufund.api.ufundapi.model.Need;

public interface NeedDAO {

    /**
     * returns a list of all needs in the cupboard
     */
    Need[] getNeeds() throws IOException;

    /**
     * returns a list of Needs whos title contains the given string
     */
    Need[] findNeeds(String containsText) throws IOException;

    /**
     * finds the need with the given id and returns it.
     * if a need with that id is unable to be found, return null
     */
    Need getNeed(int id) throws IOException;

    /**
     * writes a given need to the file
     * if the need with the same title already exists, it will return null
     * otherwise it will return the new need with it's assigned id number
     */
    Need createNeed(Need need) throws IOException;

    /**
     * finds a need with the same id number and replaces it with the given need
     * if a need with the same id is unable to be found, it returns null
     * otherwise it returns the updatedd need.
     */
    Need updateNeed(Need need) throws IOException;

    /**
     * finds the need with the given id and deletes it
     * if unable to find a need with that id, return false
     * otherwise return true if the deletion was successful.
     */
    boolean deleteNeed(int id) throws IOException;
}