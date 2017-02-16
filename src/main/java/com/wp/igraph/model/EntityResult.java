package com.wp.igraph.model;

import java.util.List;

import com.wp.igraph.datastore.dynamodb.EdgeItem;

/**
 * 
 * @author varun.kumar
 *
 */
public class EntityResult {
    private EntityType entityType;
    private List<EdgeItem> edges;
    
    public EntityResult(EntityType entityType, List<EdgeItem> ids) {
        super();
        this.entityType = entityType;
        this.edges = ids;
    }
    
    public EntityType getEntityType() {
        return entityType;
    }
    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }
    public List<EdgeItem> getEdges() {
        return edges;
    }
    public void setIds(List<EdgeItem> edges) {
        this.edges = edges;
    }
    
    
}
