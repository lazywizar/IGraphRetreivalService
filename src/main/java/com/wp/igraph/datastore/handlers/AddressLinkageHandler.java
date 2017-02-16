package com.wp.igraph.datastore.handlers;

import java.io.IOException;

import com.wp.igraph.datastore.DataStoreClient;
import com.wp.igraph.model.StoreTable;

/**
 * 
 * @author varun.kumar
 *
 */
public class AddressLinkageHandler extends AbstractLinkageHandler {
    
    public AddressLinkageHandler(DataStoreClient datastoreClient) throws IOException {
        super(datastoreClient, StoreTable.PersonAddress);
    }
}
