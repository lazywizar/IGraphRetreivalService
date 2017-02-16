package com.wp.igraph.datastore;

import java.util.List;

import com.wp.igraph.model.Dimension;

/**
 * ServiceImpl for the general client
 * 
 * @author varun.kumar
 *
 */
public class RequestManager {
    EntityManager entityManager;
    
    RequestManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    public void getResult(Long sourceId,  List<Dimension> dimentions) {
        
    }
}
