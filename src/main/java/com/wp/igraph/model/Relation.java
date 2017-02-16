package com.wp.igraph.model;

/**
 * Dimension of results. Which defines the Shape, that is 
 *      1. Recursively what linkages and linked entities should be retrieved 
 *      2. Limits on the number of linkages to return
 * 
 * @author varun.kumar
 *
 */
public enum Relation {
    PERSON,   //Flat person attributes
    PERSON_PERSON_RELATED, // Person who are related to the subject
    EMAIL,   //Flat Email details
    PERSON_EMAIL, //Emails related to a gives person (This it in a way of direction Person -> Email)
    EMAIL_PERSON, //Persons related to given email id
    PHONE,        //Phone attributes
    PERSON_PHONE, //Phone numbers related to person.
    PHONE_PERSON,     //Person Related to Phone number
    ADDRESS,      //Address attributes
    PERSON_ADDRESS, //Addresses Related to a person
    ADDRESS_PERSON; //Person who were associated with the address.
}
