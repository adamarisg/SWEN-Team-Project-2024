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
import com.ufund.api.ufundapi.model.Need;

@Component
public class NeedFileDAO implements NeedDAO {
    
    private static final Logger LOG = Logger.getLogger(NeedFileDAO.class.getName());
    Map<Integer, Need> needs;
    private ObjectMapper objectMapper;
    private static int nextId;

    /**
     * THIS IS A HARD CODED FILEPATH!!
     * PERHAPS WE SHOULD CHANGE THIS TO AN ATTRIBUTE IN /src/main/resources/application.properties
     * (this is how it's done in the heroes-api, but I couldn't get it working -Nathan k)
     */
    private final static String filename = "data/needs.json";

    public NeedFileDAO(ObjectMapper objectMapper) throws IOException {
        this.objectMapper = objectMapper;
        load();
    }

    /**
     * returns the next available id number
     */
    private synchronized static int nextId() {
        int id = nextId;
        ++nextId;
        return id;
    }

    /**
     * returns a list of all needs in the cupboard
     */
    private Need[] getNeedsArray() {
        return getNeedsArray(null);
    }

    /**
     * returns a list of Needs whos title contains the given string
     */
    private Need[] getNeedsArray(String containsText) {
        ArrayList<Need> needArrayList = new ArrayList<Need>();
        for (Need need : needs.values()) {
            if (containsText == null || need.getTitle().contains(containsText)) {
                needArrayList.add(need);
            }
        }
        Need[] needArray = new Need[needArrayList.size()];
        needArrayList.toArray(needArray);
        return needArray;
    }

    /**
     * write the local hashmap to the file
     */
    private boolean save() throws IOException {
        Need[] needArray = getNeedsArray();
        objectMapper.writeValue(new File(filename), needArray);
        return true;
    }

    /**
     * loads the contents of the file into the local hashmap
     */
    private boolean load() throws IOException {
        needs = new TreeMap<>();
        nextId = 0;
        Need[] needArray = objectMapper.readValue(new File(filename), Need[].class);
        for (Need need : needArray) {
            needs.put(need.getId(), need);
            if (need.getId() > nextId) nextId = need.getId();
        }
        ++nextId;
        return true;
    }

    /**
     * returns a list of all needs in the cupboard
     */
    @Override
    public Need[] getNeeds() throws IOException {
        synchronized(needs) {
            return getNeedsArray();
        }
    }

    /**
     * returns a list of Needs whos title contains the given string
     */
    @Override
    public Need[] findNeeds(String containsText) throws IOException {
        synchronized(needs) {
            return getNeedsArray(containsText);
        }
    }

    /**
     * finds the need with the given id and returns it.
     * if a need with that id is unable to be found, return null
     */
    @Override
    public Need getNeed(int id) throws IOException {
        synchronized(needs) {
            if (needs.containsKey(id)) {
                return needs.get(id);
            } else {
                return null;
            }
        }
    }

    /**
     * writes a given need to the file
     * returns the new need with it's assigned id number
     */
    @Override
    public Need createNeed(Need need) throws IOException {
        synchronized(needs) {
            for (Need n : needs.values()) {
                if (n.getTitle().equals(need.getTitle())) {
                    LOG.log(Level.WARNING, "title: " + n.getTitle() + " already exists");
                    return null;
                }
            }
            Need newNeed = new Need(nextId(), need.getTitle(), need.getDescription(), need.getCost(),need.getUrgency()); 
            needs.put(newNeed.getId(), newNeed);
            save();
            return newNeed;
        }
    }

    /**
     * finds a need with the same id number and replaces it with the given need
     * if a need with the same id is unable to be found, it returns null
     * otherwise it returns the updatedd need.
     */
    @Override
    public Need updateNeed(Need need) throws IOException {
        synchronized(needs) {
            if (!needs.containsKey(need.getId())) { // need not found
                return null; 
            }
            needs.put(need.getId(), need);
            save();
            return need;
        }
    }

    /**
     * finds the need with the given id and deletes it
     * if unable to find a need with that id, return false
     * otherwise return true if the deletion was successful.
     */
    @Override
    public boolean deleteNeed(int id) throws IOException {
        synchronized(needs) {
            if (needs.containsKey(id)) {
                needs.remove(id);
                return save();
            } else {
                return false;
            }
        }
    }

}
