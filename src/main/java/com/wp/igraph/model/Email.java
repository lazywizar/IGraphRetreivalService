package com.wp.igraph.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.document.Item;

/**
 * 
 * @author varun.kumar
 *
 */
public class Email extends AbstractEntity {
    private static final String EMAIL = "email";
    private static final String EMAIL_ID = "email_id";
    
    private String email;
    List<Person> currentPerson;
    List<Person> historicalPerson;

    public Email(Long id) {
        super(id, EntityType.EMAIL, StoreTable.PersonEmail);
    }
    
    public Email(Item item) {
        this(item.getLong(EMAIL_ID));
        this.email = item.getString(EMAIL);
    }

    public Email(String line) {
        this(Long.parseLong(line.split(",")[0]));
        String[] tokens = line.split(",");
        this.email = tokens[1];
    }
    
    public List<Person> getCurrentPerson() {
        return currentPerson;
    }

    public void setCurrentPerson(List<Person> currentPerson) {
        this.currentPerson = currentPerson;
    }

    public List<Person> getHistoricalPerson() {
        return historicalPerson;
    }

    public void setHistoricalPerson(List<Person> historicalPerson) {
        this.historicalPerson = historicalPerson;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    @Override
    public Map<String, Object> getAttributes() {
        attributes.clear();
        attributes.put(EMAIL, email);
        return attributes;
    }
    
    @Override
    public EntityType getEntityType() {
        return EntityType.EMAIL;
    }

    @Override
    public String toString() {
        return "Email [email=" + email + "]";
    }    
}
