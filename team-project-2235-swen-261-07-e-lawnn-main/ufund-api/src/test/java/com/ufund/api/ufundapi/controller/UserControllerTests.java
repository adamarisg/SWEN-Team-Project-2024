package com.ufund.api.ufundapi.controller;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufund.api.ufundapi.model.Need;
import com.ufund.api.ufundapi.model.User;
import com.ufund.api.ufundapi.persistence.NeedDAO;
import com.ufund.api.ufundapi.persistence.NeedFileDAO;
import com.ufund.api.ufundapi.persistence.UserDAO;
import com.ufund.api.ufundapi.persistence.UserFileDAO;

public class UserControllerTests {
    
    @Test
    void tests() {
        try {

            UserDAO userDAO = new UserFileDAO(new ObjectMapper(new JsonFactory()));
            UserController userController = new UserController(userDAO);
            NeedDAO needDAO = new NeedFileDAO(new ObjectMapper(new JsonFactory()));
            NeedController needController = new NeedController(needDAO);

            // delete all users and needs
            ResponseEntity<Need[]> delNeedRes = needController.getNeeds();
            Need[] needs = delNeedRes.getBody();
            if (needs != null) {
                for (Need need : needs) {
                    needDAO.deleteNeed(need.getId());
                }
            }
            ResponseEntity<User[]> delUserRes = userController.getUsers();
            User[] users = delUserRes.getBody();
            if (users != null) {
                for (User user : users) {
                    userDAO.deleteUser(user.getUsername());
                }
            }

            // createUser
            User user1 = new User("u1", new ArrayList<Integer>());
            User user2 = new User("u2", new ArrayList<Integer>());
            ResponseEntity<User> u1 = userController.createUser("u1");
            ResponseEntity<User> u2 = userController.createUser("u2");
            ResponseEntity<User> u3 = userController.createUser("u1"); // should conflict
            assertEquals(user1, u1.getBody());
            assertEquals(user2, u2.getBody());
            assertEquals(null, u3.getBody());
            assertEquals(HttpStatus.CREATED, u1.getStatusCode());
            assertEquals(HttpStatus.CREATED, u2.getStatusCode());
            assertEquals(HttpStatus.CONFLICT, u3.getStatusCode());

            // getUsers
            User[] expectedUsers = new User[2];
            List.of(user1, user2).toArray(expectedUsers);
            ResponseEntity<User[]> getUsersResponse = userController.getUsers();
            assertArrayEquals(expectedUsers, getUsersResponse.getBody());
            assertEquals(HttpStatus.OK, getUsersResponse.getStatusCode());

            // getUser
            ResponseEntity<User> getUser1 = userController.getUser("u1");
            assertEquals(user1, getUser1.getBody());
            assertEquals(HttpStatus.OK, getUser1.getStatusCode());

            // getUser (non existant user)
            ResponseEntity<User> getUserNEU = userController.getUser("flabberghasted");
            assertEquals(HttpStatus.NOT_FOUND, getUserNEU.getStatusCode());

            // getCart (empty)
            ResponseEntity<Need[]> emptyCartResponse = userController.getCart("u1");
            assertNotNull(emptyCartResponse.getBody());
            assertEquals(0, emptyCartResponse.getBody().length);
            assertEquals(HttpStatus.OK, emptyCartResponse.getStatusCode());

            // getCart (non existant user)
            ResponseEntity<Need[]> NEUCartResponse = userController.getCart("blunderbuss");
            assertEquals(HttpStatus.NOT_FOUND, NEUCartResponse.getStatusCode());

            // addToCart
            Need n1 = new Need(1, "n1", "n1d", 1, 1);
            Need n2 = new Need(2, "n2", "n2d", 2, 1);
            needController.createNeed(n1);
            needController.createNeed(n2);

            HashMap<String, String> json = new HashMap<>(2);

            json.put("username", "u1");
            json.put("needId", String.valueOf(1));
            ResponseEntity<User> n1AddResponse = userController.addToCart(json);
            assertNotNull(n1AddResponse.getBody());
            assertEquals(1, n1AddResponse.getBody().getCart().size());
            assertEquals(HttpStatus.OK, n1AddResponse.getStatusCode());

            json.put("needId", String.valueOf(2));
            ResponseEntity<User> n2AddResponse = userController.addToCart(json);
            assertNotNull(n2AddResponse.getBody());
            assertEquals(2, n2AddResponse.getBody().getCart().size());
            assertEquals(HttpStatus.OK, n2AddResponse.getStatusCode());

            // getCart
            Need[] expectedCart = new Need[2];
            List.of(n1, n2).toArray(expectedCart);
            ResponseEntity<Need[]> cart = userController.getCart("u1");
            assertArrayEquals(expectedCart, cart.getBody());
            assertEquals(HttpStatus.OK, cart.getStatusCode());

            // addToCart (removing)
            json.put("needId", String.valueOf(2));
            ResponseEntity<User> n2RemResponse = userController.addToCart(json);
            assertNotNull(n2RemResponse.getBody());
            assertEquals(1, n2RemResponse.getBody().getCart().size());
            assertEquals(HttpStatus.OK, n2RemResponse.getStatusCode());

            // addToCart (non existent user)
            json.put("username", "buhbingus");
            ResponseEntity<User> NEUResponse = userController.addToCart(json);
            assertEquals(HttpStatus.NOT_FOUND, NEUResponse.getStatusCode());


        } catch (IOException e) {
            assertTrue(false);
        }
    }

}
