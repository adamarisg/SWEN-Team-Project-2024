package com.ufund.api.ufundapi.controller;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufund.api.ufundapi.controller.NeedController;
import com.ufund.api.ufundapi.model.Need;
import com.ufund.api.ufundapi.persistence.NeedDAO;
import com.ufund.api.ufundapi.persistence.NeedFileDAO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class NeedControllerTests {
    // Clears list of needs for testing purposes
    void wipeNeeds() {
        try {
            NeedDAO needDAO = new NeedFileDAO(new ObjectMapper(new JsonFactory()));
            NeedController controller = new NeedController(needDAO);

            ResponseEntity<Need[]> responseEntity = controller.getNeeds();
            Need[] needs = responseEntity.getBody();

            for (Need need : needs) {
                needDAO.deleteNeed(need.getId());
            }
        } catch(IOException ioe) {
            assertTrue(false);
        }
    }

    // Given I submit a need object when no need with the given name exists then the system should create the need, add it to the cupboard, save to the persistent storage, and return the need object and a status code of 201 (CREATED)
    @Test
    void createNeed() {
        try {
            ObjectMapper objectMapper = new ObjectMapper(new JsonFactory());
            NeedFileDAO needDAO1 = new NeedFileDAO(objectMapper);
            NeedController controller1 = new NeedController(needDAO1);
            Need need = new Need(0, "Title", "Description", 1,1);

            ResponseEntity<Need> createResponseEntity = controller1.createNeed(need); // Create need
            assertEquals(HttpStatus.CREATED, createResponseEntity.getStatusCode()); // Verify correct status code
            assertNotNull(createResponseEntity.getBody()); // Assert that a need object is returned
            
            assertEquals(need.getTitle(), controller1.getNeed(createResponseEntity.getBody().getId()).getBody().getTitle()); // Verify that need was added to cabinet

            // Force to load from persistent storage
            NeedFileDAO needDAO2 = new NeedFileDAO(objectMapper);
            NeedController controller2 = new NeedController(needDAO2);
            assertEquals(need.getTitle(), controller2.getNeed(createResponseEntity.getBody().getId()).getBody().getTitle()); // Verify that need was added to persistant storage
        } catch(IOException ioe) {
            assertTrue(false);
        }
    }

    // Given I submit a need object when a need with the given name already exists then the system should return a status code of 409 (CONFLICT)
    // Currently fails; there is no conflict when trying to create a need with the name of an existing need.
    @Test
    void createExistingNeed() {
        try {
            NeedDAO needDAO = new NeedFileDAO(new ObjectMapper(new JsonFactory()));
            NeedController controller = new NeedController(needDAO);
            String name = "Name";
            Need need1 = new Need(0, name, "Need one", 1,1);
            Need need2 = new Need(0, name, "Need two", 1,1);

            ResponseEntity<Need> createResponseEntity1 = controller.createNeed(need1); // Create need
            assertEquals(HttpStatus.CREATED, createResponseEntity1.getStatusCode()); // Make sure need was created successfully

            ResponseEntity<Need> createResponseEntity2 = controller.createNeed(need2); // Attempt to create need with same name
            assertEquals(HttpStatus.CONFLICT, createResponseEntity2.getStatusCode());

        } catch(IOException ioe) {
            assertTrue(false);
        }
    }
    
    //Given an id, if the id is not used, I expect the method to return httpstatus not found
    @Test
    void getMissingNeed() {
        try {
            NeedDAO needDAO = new NeedFileDAO(new ObjectMapper(new JsonFactory()));
            NeedController controller = new NeedController(needDAO);

            ResponseEntity<Need> responseEntity = controller.getNeed(0); // Retrieve need that doesn't exist yet

            assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode()); // Assert correct status
        } catch(IOException ioe) {
            assertTrue(false);
        }
    }

    //Given an id, if the id is that of a valid need, I expect the method to return that need with httpstatus OK
    @Test
    void getValidNeed() {
        try {
            wipeNeeds();
            NeedDAO needDAO = new NeedFileDAO(new ObjectMapper(new JsonFactory()));
            NeedController controller = new NeedController(needDAO);
            Need need = new Need(0, "Title", "Description", 1,1);

            ResponseEntity<Need> createResponseEntity = controller.createNeed(need); // Create need
            assertEquals(HttpStatus.CREATED, createResponseEntity.getStatusCode());
            int id = createResponseEntity.getBody().getId(); // Needs are assigned ids at creation, must retrieve it here

            ResponseEntity<Need> getResponseEntity = controller.getNeed(id); // Retrieve created need

            // Need created by controller is a new need but must have equal attributes
            assertEquals(id, getResponseEntity.getBody().getId());
            assertEquals(need.getTitle(), getResponseEntity.getBody().getTitle());
            assertEquals(need.getDescription(), getResponseEntity.getBody().getDescription());
            assertEquals(need.getCost(), getResponseEntity.getBody().getCost());

            // Assert correct status
            assertEquals(HttpStatus.OK, getResponseEntity.getStatusCode());
        } catch(IOException ioe) {
            assertTrue(false);
        }
    }

    // Given I submit a request for the inventory when products exist in the inventory then the system should return a list of products and a status code of OK
    @Test
    void getCupboard() {
        
        try {
            wipeNeeds();
            NeedDAO needDAO = new NeedFileDAO(new ObjectMapper(new JsonFactory()));
            NeedController controller = new NeedController(needDAO);
            Need need10 = new Need(2, "s", "Description", 7,10);
            Need need = new Need(0, "Title", "Description", 1,1);
            Need need1 = new Need(2, "s", "Description", 4,4);
            Need need2 = new Need(3, "d", "Description", 6,3);
            Need need11 = new Need(2, "s", "Description", 8,1);

            ResponseEntity<Need> createResponseEntity = controller.createNeed(need); // Create need
            ResponseEntity<Need> createResponseEntity1 = controller.createNeed(need1); // Create need
            ResponseEntity<Need> createResponseEntity11 = controller.createNeed(need10); // Create need
            ResponseEntity<Need> createResponseEntity12 = controller.createNeed(need11); // Create need
            ResponseEntity<Need> createResponseEntity2 = controller.createNeed(need2); // Create need
            assertEquals(HttpStatus.CREATED, createResponseEntity.getStatusCode());
            ResponseEntity<Need[]> responseEntity = controller.getNeedsSorted(0);
            ResponseEntity<Need[]> responseEntity1 = controller.getNeedsSorted(1);
            ResponseEntity<Need[]> responseEntity2 = controller.getNeedsSorted(2);
            ResponseEntity<Need[]> responseEntity3 = controller.getNeedsSorted(3);
            ResponseEntity<Need[]> responseEntity4 = controller.getNeedsSorted(4);
            ResponseEntity<Need[]> responseEntity5 = controller.getNeedsSorted(100);
            Need[] needs = responseEntity.getBody();
            
            assertTrue(needs.length > 0); // Assert array is not empty
            assertNotNull(needs[0]); // Assert that array contains product
            assertEquals(HttpStatus.OK, responseEntity.getStatusCode()); // Assert correct status code
        } catch(IOException ioe) {
            assertTrue(false);
        }
    }

    // Given I submit a request for the inventory when no products exist in the inventory then the system should return an empty list of products and a status code of OK
    @Test
    void getEmptyCupboard() {
        wipeNeeds();
        try {
            NeedDAO needDAO = new NeedFileDAO(new ObjectMapper(new JsonFactory()));
            NeedController controller = new NeedController(needDAO);

            ResponseEntity<Need[]> responseEntity = controller.getNeeds();
            Need[] needs = responseEntity.getBody();

            assertTrue(needs.length == 0); // Assert array is empty
            assertEquals(HttpStatus.OK, responseEntity.getStatusCode()); // Assert correct status code
        } catch(IOException ioe) {
            assertTrue(false);
        }
    }

    @Test
    void searchNeeds() {
        wipeNeeds();
        try {
            NeedDAO needDAO = new NeedFileDAO(new ObjectMapper(new JsonFactory()));
            NeedController controller = new NeedController(needDAO);
            
            Need n1 = controller.createNeed(new Need(0, "meed1", "d", 1,1)).getBody();
            Need n2 = controller.createNeed(new Need(0, "need1", "d", 1,1)).getBody();
            Need n3 = controller.createNeed(new Need(0, "need2", "d", 1,1)).getBody();
            Need n4 = controller.createNeed(new Need(0, "need3", "d", 1,1)).getBody();
            Need[] expected = new Need[3];
            expected[0] = n2;
            expected[1] = n3;
            expected[2] = n4;
            
            ResponseEntity<Need[]> result = controller.searchNeeds("nee");
            assertNotNull(result.getBody());
            assertEquals(HttpStatus.OK, result.getStatusCode());
            assertEquals(expected.length, result.getBody().length);
            assertEquals(expected[0].getTitle(), result.getBody()[0].getTitle());


            result = controller.searchNeeds("ddd");
            assertNotNull(result.getBody());
            assertEquals(0, result.getBody().length);
    
        } catch(IOException e) {
            assertTrue(false);
        }
    }       

    @Test
    void deleteNeed() {
        wipeNeeds();
        try {
            NeedDAO needDAO = new NeedFileDAO(new ObjectMapper(new JsonFactory()));
            NeedController controller = new NeedController(needDAO);

            ResponseEntity<Need> result = controller.deleteNeed(0);
            assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());

            controller.createNeed(new Need(0, "s", "d", 1,1));
            result = controller.deleteNeed(1); // first assigned id is 1 not 0!
            assertEquals(HttpStatus.OK, result.getStatusCode());

        } catch(IOException e) {
            assertTrue(false);
        }
    }

    @Test
    void updateNeed() {
        wipeNeeds();
        try {
            NeedDAO needDAO = new NeedFileDAO(new ObjectMapper(new JsonFactory()));
            NeedController controller = new NeedController(needDAO);

            controller.createNeed(new Need(0, "s", "d", 1,1)); // will get assigned id 1

            Need newNeed = new Need(1, "updatedTitle", "NEWDESC", 69,1);
            ResponseEntity<Need> result = controller.updateNeed(newNeed);

            assertEquals(HttpStatus.OK, result.getStatusCode());
            assertNotNull(result.getBody());
            assertEquals(newNeed.getTitle(), result.getBody().getTitle());
            assertEquals(newNeed.getCost(), result.getBody().getCost());

            result = controller.updateNeed(new Need(5, "test2", "d", 3,1));
            assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());

        } catch(IOException e) {
            assertTrue(false);
        }
    }

//tests for urgency
    @Test
    void createNeedWithInvalidUrgencyNum() {
        try {
            ObjectMapper objectMapper = new ObjectMapper(new JsonFactory());
            NeedFileDAO needDAO1 = new NeedFileDAO(objectMapper);
            NeedController controller = new NeedController(needDAO1);

            // creates a need with an invalid urgency number
            Need invalidNeed = new Need(0, "Invalid Title", "Invalid Description", 1, 15); // Invalid urgency (outside 1-10)

            ResponseEntity<Need> createResponseEntity = controller.createNeed(invalidNeed); //create need
            //assertEquals(HttpStatus.BAD_REQUEST, createResponseEntity.getStatusCode()); //check for status code

        } catch (IOException ioe) {
            assertTrue(false);
        }
    }
    
    @Test
    void updateNeedWithInvalidUrgencyNum(){
        try {
            NeedDAO needDAO = new NeedFileDAO(new ObjectMapper(new JsonFactory()));
            NeedController controller = new NeedController(needDAO);

            controller.createNeed(new Need(0, "title", "descrip", 1,1));

            Need newNeed = new Need(1, "updatedTitle", "NEWDESC",50,11);
            ResponseEntity<Need> result = controller.updateNeed(newNeed);

            //assertEquals(HttpStatus.NOT_MODIFIED, result.getStatusCode());

        } catch (Exception e) {
            assertTrue(false);
        }
    }
}