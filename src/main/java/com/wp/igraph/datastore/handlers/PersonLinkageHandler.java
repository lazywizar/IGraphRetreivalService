package com.wp.igraph.datastore.handlers;

import java.io.IOException;

import com.wp.igraph.datastore.DataStoreClient;
import com.wp.igraph.datastore.dynamodb.DynamoDBClient;
import com.wp.igraph.model.EntityResult;
import com.wp.igraph.model.StoreTable;

public class PersonLinkageHandler extends AbstractLinkageHandler {
    public PersonLinkageHandler(DataStoreClient datastoreClient) throws IOException {
        super(datastoreClient, StoreTable.PersonPerson);
    }
    
    /**
     * Get person related to person... we don't need a reverse here. Data is complete.
     * 
     * @param addressId
     * @return
     */
    @Override
    public EntityResult getInLinkage(Long addressId) {
        throw new RuntimeException("Person-Person reverse linkage is same as person linkage, use In linkages.");
    }
    
    /**
     * Get persons related to person.
     * 
     * @param personId
     * @return
     */
    @Override
    public EntityResult getOutLinkage(Long personId) {
        return new EntityResult(table.getResultEntityType(true),
                datastoreClient.getEdges(personId, table, true, DynamoDBClient.GLOBAL_MAX, DynamoDBClient.GLOBAL_MAX));
    }
}
