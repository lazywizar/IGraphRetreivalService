package com.wp.igraph.datastore;

import java.util.List;
import java.util.Map;

import com.wp.igraph.datastore.dynamodb.EdgeItem;
import com.wp.igraph.exceptions.EdgeWriteFailedException;
import com.wp.igraph.model.EntityType;
import com.wp.igraph.model.StoreTable;

/**
 * An interface which must be implemented by any underlying storage
 * implementation.
 * 
 * @author varun.kumar
 *
 */
public interface DataStoreClient {
    /**
     * 
     * @param sourceId
     * @param destId
     * @param tableMetadata
     * @throws EdgeWriteFailedException
     */
    public void createOrUpdateEdge(EdgeItem item, StoreTable storeTable) throws EdgeWriteFailedException;

    /**
     * Bulk API
     * 
     * @param edgeItem
     * @param storeTable
     * @throws EdgeWriteFailedException
     */
    public void createOrUpdateEdges(List<EdgeItem> edgeItem, StoreTable storeTable) throws EdgeWriteFailedException;
    
    /**
     * 
     * @param sourceId
     * @param storeTable
     * @return
     */
    public List<EdgeItem> getEdges(Long sourceId, StoreTable storeTable, boolean forward, int maxCurrent, int maxHistoric);
}
