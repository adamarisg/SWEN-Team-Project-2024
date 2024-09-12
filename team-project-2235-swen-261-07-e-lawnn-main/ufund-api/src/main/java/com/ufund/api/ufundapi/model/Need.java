package com.ufund.api.ufundapi.model;

import java.util.logging.Logger;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a Need entity
 * 
 * @author LAWNN
 */
public class Need {

    private static final Logger LOG = Logger.getLogger(Need.class.getName());

    @JsonProperty("id") private int id;
    @JsonProperty("title") private String title;
    @JsonProperty("description") private String description;
    @JsonProperty("cost") private int cost;
    @JsonProperty("urgency")private int urgency;

    public Need(@JsonProperty("id") int id, @JsonProperty("title") String title,
        @JsonProperty("description") String description, @JsonProperty("cost") int cost, @JsonProperty("urgency") int urgency) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.cost = cost;
        this.urgency = urgency;
    }

    public int getId() {return id;}

    public String getTitle() {return title;}

    public String getDescription() {return description;}

    public int getCost() {return cost;}

    public int getUrgency() {return urgency;}

    @Override
    public String toString() {
        return String.format("Need [id=%d, title=%s, cost=%d, desc=%s, urge=%d]",id,title,cost,description,urgency);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Need) {
            Need o = (Need) other;
            return this.id == o.id;
        }
        return false;
    }
}
