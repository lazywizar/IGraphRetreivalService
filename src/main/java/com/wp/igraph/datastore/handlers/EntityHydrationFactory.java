package com.wp.igraph.datastore.handlers;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.wp.igraph.model.Address;
import com.wp.igraph.model.Email;
import com.wp.igraph.model.Entity;
import com.wp.igraph.model.EntityType;
import com.wp.igraph.model.Person;
import com.wp.igraph.model.Phone;
import com.wp.igraph.model.StoreTable;

/**
 * 
 * @author varun.kumar
 *
 */
public class EntityHydrationFactory {
    
    public static Entity hydrateEntity(Item item, StoreTable table) {
        switch (table) {
        case PersonPerson:
            return new Person(item);

        case PersonEmail:
            return new Email(item);

        case PersonPhone:
            return new Phone(item);

        case PersonAddress:
            return new Address(item);

        default:
            break;
        }
        
        return null;
    }
 
    public static StoreTable getTableForType(EntityType entityType) {
        switch (entityType) {
        case PERSON:
            return StoreTable.PersonPerson;

        case EMAIL:
            return StoreTable.PersonEmail;

        case ADDRESS:
            return StoreTable.PersonAddress;

        case PHONE:
            return StoreTable.PersonPhone;

        default:
            return null;
        }
    }
    
    public static Entity parse(String line, StoreTable table) {
        switch (table) {
        case PersonPerson:
            return new Person(line);

        case PersonEmail:
            return new Email(line);

        case PersonPhone:
            return new Phone(line);

        case PersonAddress:
            return new Address(line);

        default:
            break;
        }
        
        return null;
    }
}
