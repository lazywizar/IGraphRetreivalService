package com.wp.igraph.datastore.handlers;

import java.io.IOException;

import com.wp.igraph.datastore.DataStoreClient;
import com.wp.igraph.model.StoreTable;

public class EmailLinkageHandler extends AbstractLinkageHandler {
    public EmailLinkageHandler(DataStoreClient datastoreClient) throws IOException {
        super(datastoreClient, StoreTable.PersonEmail);
    }
}
