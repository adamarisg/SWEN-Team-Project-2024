package com.ufund.api.ufundapi.model;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

public class UserTests {
    
    @Test
    void test() {
        User u1 = new User("first", new ArrayList<Integer>());
        User u2 = new User("other", new ArrayList<Integer>());
        assertFalse(u1.equals(u2));
        assertFalse(u1.equals("sdf"));
    }

}
