package com.ufund.api.ufundapi.model;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User {
    
    private static final Logger LOG = Logger.getLogger(Need.class.getName());

    @JsonProperty("username") private String username;
    @JsonProperty("cart") private ArrayList<Integer> cart; // list of need ids

    public User(
        @JsonProperty("username") String username,
        @JsonProperty("cart") ArrayList<Integer> cart
    ) {
        this.username = username;
        this.cart = cart;
    }

    public String getUsername() {
        return username;
    }

    public ArrayList<Integer> getCart() {
        return cart;
    }

    public void addToCart(int needId) {
        if (this.cart.contains(needId)) {
            this.cart.remove((Object)needId);
        } else {
            this.cart.add(needId);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof User) {
            User o = (User) other;
            return this.username.equals(o.username) && this.cart.equals(o.cart);
        }
        return false;
    }

}
