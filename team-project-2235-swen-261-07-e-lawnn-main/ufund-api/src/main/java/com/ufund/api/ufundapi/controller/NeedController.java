package com.ufund.api.ufundapi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ufund.api.ufundapi.model.Need;
import com.ufund.api.ufundapi.persistence.NeedDAO;

/**
 * Handles the REST API requests for the Hero resource
 * <p>
 * {@literal @}RestController Spring annotation identifies this class as a REST
 * API
 * method handler to the Spring framework
 * 
 * @author LAWNN
 */

@RestController
@RequestMapping("inventory")
public class NeedController {

    private static final Logger LOG = Logger.getLogger(NeedController.class.getName());
    private NeedDAO needDAO;

    public NeedController(NeedDAO needDAO) {
        this.needDAO = needDAO;
    }

    /**
     * gets all needs from the cupboard
     * @return json list of Needs
     */
    @GetMapping("")
    public ResponseEntity<Need[]> getNeeds() {
        LOG.info("GET /inventory");
        try {
            Need[] needs = needDAO.getNeeds();
            return new ResponseEntity<Need[]>(needs, HttpStatus.OK);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<Need[]>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
     * Sorts an array of needs based on a given attribute
     * Quick Sort algorithm adapted from my Analysis of Algorithms notes
     */
    private void needQuickSort(Need[] needs, int start, int end, int sortBy) {
        if (start < end) {
            int m = needPartition(needs, start, end, sortBy);
            needQuickSort(needs, start, m - 1, sortBy);
            needQuickSort(needs, m + 1, end, sortBy);
        }
    }

    /*
     * Part of quick sort algorithm
     * Sorts members into two sides depending on whether they are less than or equal to the final member (pivot)
     * @return int new index of pivot
     */
    private int needPartition(Need[] needs, int start, int end, int sortBy) {
        int i = start - 1; 
        int j = start;
        while (j < end) {
            if (compareNeeds(needs[j], needs[end], sortBy) <= 0) {
                i++;
                // Swap needs at positions i and j
                Need temp = needs[i];
                needs[i] = needs[j];
                needs[j] = temp;
            }
            j++;
        }
        Need temp = needs[end];
        needs[end] = needs[i + 1];
        needs[i + 1] = temp;
        return i + 1;
    }

    /*
     * Compares two needs based on criteria specified by sortBy argument
     * @return int less than 0 if first need less than second, greater than 0 if greater, 0 if equal
     */
    private int compareNeeds(Need need1, Need need2, int sortBy) {
        switch (sortBy) {
            case 1: // Sort by ID
                // if (need1.getId() > need2.getId()) return 1;
                // if (need1.getId() < need2.getId()) return -1;
                // return 0;
                return Integer.compare(need1.getId(), need2.getId());
            case 2: // Sort by name
                return need1.getTitle().compareTo(need2.getTitle());
            case 3: // Sort by cost asc
                // if (need1.getCost() < need2.getCost()) return -1;
                // if (need1.getCost() > need2.getCost()) return 1;
                // return 0;
                return Integer.compare(need1.getCost(), need2.getCost());
            case 4: // sort by cost desc
                return Integer.compare(need2.getCost(), need1.getCost());
            case 5: // Sort by urgency
                // if (need1.getUrgency() > need2.getUrgency()) return -1;
                // if (need1.getUrgency() < need2.getUrgency()) return 1;
                // return 0;
                return Integer.compare(need2.getUrgency(), need1.getUrgency());
            
            default:
                return 0;
        }
    }

    /**
     * gets all needs from the cupboard sorted by a specified attribute
     * @return json list of Needs
     */
    @GetMapping("/sort/{sortBy}")
    public ResponseEntity<Need[]> getNeedsSorted(@PathVariable int sortBy) {
        /*
         * sortBy values
         * 0 = do not sort
         * 1 = sort by ID
         * 2 = sort by name (alphabetical order)
         * 3 = sort by cost
         */
        LOG.info("GET /inventory/sort/" + String.valueOf(sortBy));
        try {
            Need[] needs = needDAO.getNeeds();
            if (sortBy != 0) needQuickSort(needs, 0, needs.length - 1, sortBy);
            return new ResponseEntity<Need[]>(needs, HttpStatus.OK);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<Need[]>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * gets need with given id
     * @param id id of the need to look for
     * @return json need object if found, 404 error if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Need> getNeed(@PathVariable int id) {
        LOG.info("GET /inventory/" + String.valueOf(id));
        try {
            Need need = needDAO.getNeed(id);
            if (need != null) {
                return new ResponseEntity<Need>(need, HttpStatus.OK);
            } else {
                return new ResponseEntity<Need>(HttpStatus.NOT_FOUND);
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<Need>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * places a need into the cupboard
     * @param need need to put in the cupboard
     * @return CREATED status on success, CONFLICT status if a need with the title already exists.
     */
    @PostMapping("")
    public ResponseEntity<Need> createNeed(@RequestBody Need need) {
        LOG.info("POST /inventory " + need.toString());
        try {
            Need newNeed = needDAO.createNeed(need);
            if (newNeed == null) { // name already taken
                return new ResponseEntity<Need>(HttpStatus.CONFLICT);
            }
            return new ResponseEntity<Need>(newNeed, HttpStatus.CREATED);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<Need>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes a {@linkplain Need need} with the given id
     * 
     * @param id The id of the {@link Need need} to deleted
     * 
     * @return ResponseEntity the HTTP status of OK if deleted<br>
     *         ResponseEntity with the HTTP status of NOT_FOUND if not found<br>
     *         ResponseEntity with the HTTP status of INTERNAL_SERVER_ERROR
     *         otherwise
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Need> deleteNeed(@PathVariable int id) {
        LOG.info("DELETE /inventory/" + id);

        try {
            // need is found and deleted
            if (needDAO.deleteNeed(id))
                return new ResponseEntity<>(HttpStatus.OK);
            // need is not found
            else
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // something went wrong
        catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Updates the {@linkplain Need need} with the provided {@linkplain Need need}
     * object, if it exists
     * 
     * @param need The {@link Need need} to update
     * 
     * @return ResponseEntity with updated {@link Need need} object and HTTP status
     *         of OK if updated<br>
     *         ResponseEntity with HTTP status of NOT_FOUND if not found<br>
     *         ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     */
    @PutMapping("")
    public ResponseEntity<Need> updateNeed(@RequestBody Need need) {
        LOG.info("PUT /inventory/ " + need);

        try {
            // update need
            Need newNeed = needDAO.updateNeed(need);
            // update was successful
            if (newNeed != null)
                return new ResponseEntity<Need>(newNeed, HttpStatus.OK);
            // need not found
            else
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // something went wrong
        catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Responds to the GET request for all {@linkplain Need need} whose name
     * contains
     * the text in name
     * 
     * @param name The name parameter which contains the text used to find the
     *             {@link Need needs}
     * 
     * @return ResponseEntity with array of {@link Need need} objects (may be empty)
     *         and
     *         HTTP status of OK<br>
     *         ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     *         <p>
     *         Example: Find all needs that contain the text "ma"
     *         GET http://localhost:8080/heroes/?name=ma
     */
    @GetMapping("/")
    public ResponseEntity<Need[]> searchNeeds(@RequestParam String name) {
        LOG.info("GET /needs/?name=" + name);
        try {
            // find the needs
            Need[] needs = needDAO.findNeeds(name);
            // search worked (returns empty list if nothings found)
            return new ResponseEntity<Need[]>(needs, HttpStatus.OK);
        }
        // an error occurred
        catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * sample curl command to test:
     * 
     * curl -i -X POST 'http://localhost:8080/inventory' -H "Content-Type:
     * application/json"
     * -d '{"id":3,"title":"anotherNeed","description":"asdfasdfasdf","cost":15}'
     * 
     */
}
