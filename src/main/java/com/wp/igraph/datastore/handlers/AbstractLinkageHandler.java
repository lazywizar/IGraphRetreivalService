package com.wp.igraph.datastore.handlers;

import java.io.IOException;

import com.wp.igraph.datastore.DataStoreClient;
import com.wp.igraph.model.EntityResult;
import com.wp.igraph.model.StoreTable;

/**
 * 
 * @author varun.kumar
 *
 */
public abstract class AbstractLinkageHandler implements LinkageHandler {
    private static final int GLOBAL_MAX = 10;
    DataStoreClient datastoreClient;
    StoreTable table;
    
    AbstractLinkageHandler(DataStoreClient datastoreClient, StoreTable table) throws IOException {
        super();
        this.datastoreClient = datastoreClient;
        this.table = table;
    }
    
    /**
     * Get person related to entity.
     * 
     * @param addressId
     * @return
     */
    @Override
    public EntityResult getOutLinkage(Long addressId) {
        return new EntityResult(table.getResultEntityType(false), datastoreClient.getEdges(addressId, table, false, GLOBAL_MAX, GLOBAL_MAX));
    }
    
    /**
     * Get entities related to person.
     * 
     * @param personId
     * @return
     */
    @Override
    public EntityResult getInLinkage(Long personId) {
        return new EntityResult(table.getResultEntityType(true), datastoreClient.getEdges(personId, table, true, GLOBAL_MAX, GLOBAL_MAX));
    }
}
