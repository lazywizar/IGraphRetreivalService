package com.wp.igraph.datastore.handlers;

import com.wp.igraph.model.EntityResult;

/**
 * All relations are wrt a person..
 * 
 * @author varun.kumar
 *
 */
public interface LinkageHandler {

    /**
     * Get person related to entity.
     * 
     * @param entityId
     * @return
     */
    public EntityResult getOutLinkage(Long entityId);
    
    /**
     * Get entities related to person.
     * 
     * @param personId
     * @return
     */
    public EntityResult getInLinkage(Long personId);
}
