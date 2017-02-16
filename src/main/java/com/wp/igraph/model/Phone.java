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
public class Phone extends AbstractEntity {
    
    String number;
    int type;
    int carrierId;
    List<Person> currentPerson;
    List<Person> historicalPerson;

    private static final String CARRIER_ID = "carrierId";
    private static final String TYPE = "type";
    private static final String NUMBER = "number";
    private static final String PHONE_ID = "phone_id";
    
    public Phone(Long id) {
        super(id, EntityType.PHONE, StoreTable.PersonPhone);
    }

    public Phone(Item item) {
        this(item.getLong(PHONE_ID));
        this.number = item.getString(NUMBER);
        this.type = item.getInt(TYPE);
        this.carrierId = item.getInt(CARRIER_ID);
    }
    
    public Phone(String line) {
        this(Long.parseLong(line.split(",")[0]));
        String[] tokens = line.split(",");
        this.number = tokens[1];
        this.type = Integer.parseInt(tokens[2]);
        this.carrierId = Integer.parseInt(tokens[3]);
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
    
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCarrierId() {
        return carrierId;
    }

    public void setCarrierId(int carrierId) {
        this.carrierId = carrierId;
    }
    

    @Override
    public Map<String, Object> getAttributes() {
        attributes.clear();
        attributes.put(NUMBER, number);
        attributes.put(TYPE, type);
        attributes.put(CARRIER_ID, carrierId);
        
        return attributes;
    }
    
    @Override
    public EntityType getEntityType() {
        return EntityType.PHONE;
    }

    @Override
    public String toString() {
        return "Phone [number=" + number + ", type=" + type + ", carrierId=" + carrierId + "]";
    }

}
