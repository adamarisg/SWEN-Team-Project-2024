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
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufund.api.ufundapi.model.Need;
import com.ufund.api.ufundapi.model.User;
import com.ufund.api.ufundapi.persistence.NeedDAO;
import com.ufund.api.ufundapi.persistence.NeedFileDAO;
import com.ufund.api.ufundapi.persistence.UserDAO;

@RestController
@RequestMapping("users")
public class UserController {
    
    private static final Logger LOG = Logger.getLogger(UserController.class.getName());
    private UserDAO userDAO;

    public UserController(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * gets all users from the cupboard
     * @return json list of Users
     */
    @GetMapping("")
    public ResponseEntity<User[]> getUsers() {
        LOG.info("GET /users");
        try {
            User[] users = userDAO.getUsers();
            return new ResponseEntity<User[]>(users, HttpStatus.OK);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<User[]>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * gets user with given id
     * @param id id of the user to look for
     * @return json user object if found, 404 error if not found
     */
    @GetMapping("/{username}")
    public ResponseEntity<User> getUser(@PathVariable String username) {
        LOG.info("GET /users/" + username);
        try {
            User user = userDAO.getUser(username);
            if (user != null) {
                return new ResponseEntity<User>(user, HttpStatus.OK);
            } else {
                return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<User>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{username}/cart")
    public ResponseEntity<Need[]> getCart(@PathVariable String username) {
        LOG.info("GET /users/" + username + "/cart");
        try {
            User user = userDAO.getUser(username);
            if (user != null) {
                ObjectMapper objectMapper = new ObjectMapper(new JsonFactory());
                try {
                    NeedDAO needDAO = new NeedFileDAO(objectMapper);
                    ArrayList<Integer> ids = user.getCart();
                    ArrayList<Need> cart = new ArrayList<>();
                    for (int id : ids) {
                        Need need = needDAO.getNeed(id);
                        if (need != null) cart.add(need);
                    }
                    Need[] cartArray = new Need[cart.size()];
                    cart.toArray(cartArray);
                    return new ResponseEntity<Need[]>(cartArray, HttpStatus.OK);
                } catch (IOException e) {
                    LOG.log(Level.SEVERE, "error instantiating NeedFileDAO");
                    LOG.log(Level.SEVERE, e.getLocalizedMessage());
                    return new ResponseEntity<Need[]>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                return new ResponseEntity<Need[]>(HttpStatus.NOT_FOUND);
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<Need[]>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * places a User into the cupboard
     * @param User User to put in the cupboard
     * @return CREATED status on success, CONFLICT status if a User with the title already exists.
     */
    @PostMapping("")
    public ResponseEntity<User> createUser(@RequestBody String username) {
        LOG.info("POST /users (creating user: " + username + ")");
        try {
            User newUser = userDAO.createUser(username);
            if (newUser == null) { // name already taken
                return new ResponseEntity<User>(HttpStatus.CONFLICT);
            }
            return new ResponseEntity<User>(newUser, HttpStatus.CREATED);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage());
            return new ResponseEntity<User>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    /**
     *      EXAMPLE USAGE:
     *      curl -i -X POST 'localhost:8080/users' 
     *      -H "Content-Type: application/json" -d 'putaNameHere'
     */

    /**
     * Updates the {@linkplain User user} with the provided {@linkplain User user}
     * object, if it exists
     * 
     * @param user The {@link User user} to update
     * 
     * @return ResponseEntity with updated {@link User user} object and HTTP status
     *         of OK if updated<br>
     *         ResponseEntity with HTTP status of NOT_FOUND if not found<br>
     *         ResponseEntity with HTTP status of INTERNAL_SERVER_ERROR otherwise
     */
    @PutMapping("")
    public ResponseEntity<User> addToCart(@RequestBody Map<String, String> json) {
        LOG.info(json.toString());
        String username = json.get("username");
        String needIdString = json.get("needId");
        int needId = Integer.valueOf(needIdString);

        LOG.info("adding need:"+needId+" to "+username+"'s cart");
        
        try {
            // update user
            User newUser = userDAO.addItemToUsersCart(username, needId);
            // update was successful
            if (newUser != null)
                return new ResponseEntity<User>(newUser, HttpStatus.OK);
            // user not found
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
     *      EXAMPLE USEAGE: puts need 10 in user 3's cart
     *      curl -i -X PUT 'localhost:8080/users'
     *      -H "Content-Type: application/json" -d '{"username":"name","needId":"10"}'
     *
     *      if the need is already in the cart, then it will be removed from the cart
     */


}
