package com.wp.igraph.model;

/**
 * 
 * @author varun.kumar
 *
 */
public class HydratedEntityResult {
    private EntityType entityType;
    private Entity entity;
    
    
    public HydratedEntityResult(EntityType entityType, Entity entity) {
        super();
        this.entityType = entityType;
        this.entity = entity;
    }
    
    public EntityType getEntityType() {
        return entityType;
    }
    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }
    public Entity getEntity() {
        return entity;
    }
    public void setEntity(Entity entity) {
        this.entity = entity;
    }
    
    
    
    
}
