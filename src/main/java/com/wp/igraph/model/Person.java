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
public class Person extends AbstractEntity {
    private Gender gender;
    private String name;
    private String dob;
    private List<Email> currentEmails;
    private List<Phone> currentPhones;
    private List<Address> currentAddresses ;
    private List<Person> currentRelation ;
    
    private List<Email> historicEmails;
    private List<Phone> historicPhones;
    private List<Address> historicAddresses;
    private List<Person> historicRelation ;
    

    private static final String DOB = "DOB";
    private static final String GENDER = "gender";
    private static final String NAME = "name";
    private static final String SRC_PERSON_ID = "src_person_id";
    
    public Person(Long id) {
        super(id, EntityType.PERSON, StoreTable.PersonPerson);
    }

    public Person(Item item) {
        this(item.getLong(SRC_PERSON_ID));
        this.name = item.getString(NAME);
        this.gender = Gender.valueOf(item.getString(GENDER));
        this.dob = item.getString(DOB);
    }
    
    public Person(String line) {
        this(Long.parseLong(line.split(",")[0]));
        String[] tokens = line.split(",");
        this.name = tokens[1];
        this.gender = Gender.valueOf(tokens[2]);
        this.dob = tokens[3];
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Email> getCurrentEmails() {
        return currentEmails;
    }

    public void setCurrentEmails(List<Email> currentEmails) {
        this.currentEmails = currentEmails;
    }

    public List<Phone> getCurrentPhones() {
        return currentPhones;
    }

    public void setCurrentPhones(List<Phone> currentPhones) {
        this.currentPhones = currentPhones;
    }

    public List<Address> getCurrentAddresses() {
        return currentAddresses;
    }

    public void setCurrentAddresses(List<Address> currentAddresses) {
        this.currentAddresses = currentAddresses;
    }

    public List<Email> getHistoricalEmails() {
        return historicEmails;
    }

    public void setHistoricalEmails(List<Email> historicalEmails) {
        this.historicEmails = historicalEmails;
    }

    public List<Phone> getHistoricalPhones() {
        return historicPhones;
    }

    public void setHistoricalPhones(List<Phone> historicalPhones) {
        this.historicPhones = historicalPhones;
    }

    public List<Address> getHistoricalAddresses() {
        return historicAddresses;
    }

    public void setHistoricAddresses(List<Address> historicAddresses) {
        this.historicAddresses = historicAddresses;
    }

    public List<Person> getCurrentRelation() {
        return currentRelation;
    }

    public void setCurrentRelation(List<Person> currentRelation) {
        this.currentRelation = currentRelation;
    }

    public List<Email> getHistoricEmails() {
        return historicEmails;
    }

    public void setHistoricEmails(List<Email> historicEmails) {
        this.historicEmails = historicEmails;
    }

    public List<Phone> getHistoricPhones() {
        return historicPhones;
    }

    public void setHistoricPhones(List<Phone> historicPhones) {
        this.historicPhones = historicPhones;
    }

    public List<Person> getHistoricRelation() {
        return historicRelation;
    }

    public void setHistoricRelation(List<Person> historicRelation) {
        this.historicRelation = historicRelation;
    }

    public List<Address> getHistoricAddresses() {
        return historicAddresses;
    }

    @Override
    public Map<String, Object> getAttributes() {
        attributes.clear();
        attributes.put(NAME, name);
        attributes.put(DOB, dob);
        attributes.put(GENDER, gender.toString());
        
        return attributes;
    }
    
    
    @Override
    public EntityType getEntityType() {
        return getEntityType();
    }

    @Override
    public String toString() {
        return "Person [gender=" + gender + ", name=" + name + ", dob=" + dob + ", currentEmails=" + currentEmails
                + ", currentPhones=" + currentPhones + ", currentAddresses=" + currentAddresses + ", currentRelation="
                + currentRelation + ", historicEmails=" + historicEmails + ", historicPhones=" + historicPhones
                + ", historicAddresses=" + historicAddresses + ", historicRelation=" + historicRelation + "]";
    }

}
