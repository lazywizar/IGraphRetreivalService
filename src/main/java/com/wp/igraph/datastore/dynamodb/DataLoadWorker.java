package com.wp.igraph.datastore.dynamodb;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.joda.time.Instant;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughputExceededException;
import com.wp.igraph.datastore.handlers.EntityHydrationFactory;
import com.wp.igraph.exceptions.EdgeWriteFailedException;
import com.wp.igraph.model.Entity;
import com.wp.igraph.model.StoreTable;

public class DataLoadWorker implements Runnable {

    private static final int MAX_RETRY = 5;
    private static final Logger LOG = Logger.getLogger(DataLoadWorker.class);
    
    private String line;
    StoreTable storeTable;
    Table table; 
    String label;
    AtomicInteger counter;
    DynamoDBClient dynamoDBClient;

    public DataLoadWorker(String line, StoreTable storeTable, AtomicInteger counter, DynamoDBClient dynamoDBClient) {
        super();
        this.line = line;
        this.counter = counter;
        this.storeTable = storeTable;
        this.dynamoDBClient = dynamoDBClient;
    }

    public DataLoadWorker(String line, StoreTable storeTable, Table table, String label, AtomicInteger counter) {
        super();
        this.line = line;
        this.storeTable = storeTable;
        this.table = table;
        this.label = label;
        this.counter = counter;
    }

    @Override
    public void run() {
        try {
            if(dynamoDBClient == null) {
                insertIntoTable();
            } else {
                insertIntoDynamoDB();
            }
            if(counter.incrementAndGet() % 1000 == 0) {
                System.out.println(Instant.now() + " : Completed : " + counter.get());
            }
        } catch (NumberFormatException | EdgeWriteFailedException e) {
            LOG.error("Failed to process line : " + line , e);
        }
    }

    private void insertIntoTable() throws NumberFormatException, EdgeWriteFailedException {
        String[] tokens = line.split(",");
        insertIntoTable(new EdgeItem(Long.parseLong(tokens[0]), Long.parseLong(tokens[1]), label));
    }

    private void insertIntoDynamoDB() {
        try {
            Entity entity = EntityHydrationFactory.parse(line, storeTable);
            dynamoDBClient.createOrUpdateEntity(entity);
        } catch (EdgeWriteFailedException e) {
            LOG.error("Exception while writing entity " + line, e);
        }
    }
    
    private void insertIntoTable(EdgeItem edgeItem)
            throws EdgeWriteFailedException {
        int retryCount = 0;
        while (retryCount < MAX_RETRY) {
            retryCount++;
            try {
                // Mandatory params
                Item item = new Item().withPrimaryKey(storeTable.getPrimaryKey(), edgeItem.getSourceId())
                        .withNumber(storeTable.getSortKey(), edgeItem.getDestId())
                        .withString("label", edgeItem.getLabel()).withNumber("created_at", Instant.now().getMillis());
                table.putItem(item);
                break;
            } catch (ProvisionedThroughputExceededException ptex) {
                LOG.error("ProvisionedThroughputExceededException : wait for 1 minute " + edgeItem.toString());
                try {
                    Thread.sleep(1000 * 60);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (Exception ex) {
                throw new EdgeWriteFailedException(
                        "DynamoDB Exception: Create items failed for : " + edgeItem.toString(), ex);
            }
        }
    }
}
