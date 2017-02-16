package com.wp.igraph.model;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 
 * @author varun.kumar
 *
 */
public abstract class AbstractEntity implements Entity {
    Long id;
    EntityType entityType;
    StoreTable storeTable;
    
    Map<String, Object> attributes;
    static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    
    public AbstractEntity(Long id, EntityType entityType, StoreTable storeTable) {
        this.id = id;
        this.entityType = entityType;
        this.storeTable = storeTable;
        attributes = new HashMap<>();
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    @Override
    public EntityType getEntityType() {
        return entityType;
    }

    @Override
    public StoreTable getStoreTable() {
        return storeTable;
    }
    
    /*
    @Override
    public String toString() {
        return id == null ? "" : gson.toJson(this);
    }*/
}
