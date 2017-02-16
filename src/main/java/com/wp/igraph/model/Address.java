package com.wp.igraph.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.document.Item;

public class Address extends AbstractEntity {
    public static final String PINCODE = "pincode";
    public static final String COUNTRY = "country";
    public static final String STATE = "state";
    public static final String CITY = "city";
    public static final String NUMBER = "number";
    public static final String ADDRESS_ID = "address_id";

    String number;
    String city;
    String state;
    String country;
    String pincode;
    Double lat;
    Double lang;
    List<Person> currentPerson;
    List<Person> historicalPerson;

    public Address(Long id) {
        super(id, EntityType.ADDRESS, StoreTable.PersonAddress);        
    }

    public Address(Item item) {
        this(item.getLong(ADDRESS_ID));
        this.number = item.getString(NUMBER);
        this.city = item.getString(CITY);
        this.state = item.getString(STATE);
        this.country = item.getString(COUNTRY);
        this.pincode = item.getString(PINCODE);
    }
    
    public Address(String line) {
        this(Long.parseLong(line.split(",")[0]));
        String[] tokens = line.split(",");
        this.number = tokens[1];
        this.city = tokens[2];
        this.state = tokens[3];
        if(tokens.length > 4) {
            this.country = tokens[4];
        }
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLang() {
        return lang;
    }

    public void setLang(Double lang) {
        this.lang = lang;
    }
    
    @Override
    public Map<String, Object> getAttributes() {
        attributes.clear();
        attributes.put(NUMBER, number);
        attributes.put(CITY, city);
        attributes.put(STATE, state);
        attributes.put(COUNTRY, country);        
        return attributes;
    }

    @Override
    public String toString() {
        return "Address [number=" + number + ", city=" + city + ", state=" + state + ", country=" + country
                + ", pincode=" + pincode + ", lat=" + lat + ", lang=" + lang + "]";
    }    
}
