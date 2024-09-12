package com.ufund.api.ufundapi.model;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

public class NeedTests {

    @Test
    void test() {
        Need n1 = new Need(1, "sdf", "desc", 24, 5);
        Need n2 = new Need(4, "sdf", "desc", 24, 6);
        assertFalse(n1.equals(n2));
        assertFalse(n1.equals("sdf"));
    }

}
