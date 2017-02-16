package com.wp.igraph.model;

import java.util.Map;

/**
 * TBD
 * 
 * @author varun.kumar
 *
 */
public interface Entity {
    public Long getId();

    public EntityType getEntityType();
    
    public StoreTable getStoreTable();

    public Map<String, Object> getAttributes();
}
