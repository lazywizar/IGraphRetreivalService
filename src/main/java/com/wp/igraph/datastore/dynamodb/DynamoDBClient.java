package com.wp.igraph.datastore.dynamodb;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.joda.time.Instant;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.Page;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.RangeKeyCondition;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughputExceededException;
import com.jcabi.aspects.Loggable;
import com.wp.igraph.datastore.DataStoreClient;
import com.wp.igraph.datastore.handlers.EntityHydrationFactory;
import com.wp.igraph.exceptions.EdgeWriteFailedException;
import com.wp.igraph.model.Entity;
import com.wp.igraph.model.EntityType;
import com.wp.igraph.model.StoreTable;

/**
 * 
 * @author varun.kumar
 *
 */
public class DynamoDBClient implements DataStoreClient {
    private AmazonDynamoDBClient dynamoDB;

    private static final int MAX_RETRY = 5;
    private static final Logger LOG = Logger.getLogger(DynamoDBClient.class);
    ExecutorService executor = Executors.newFixedThreadPool(5);
    private static final int DEFAULT_THREAD_POOL_SIZE = 25;
    public static final int GLOBAL_MAX = 10;
    
    private static final Integer MAX_PAGE_SIZE = 1000;


    public DynamoDBClient() throws IOException {
        dynamoDB = new AmazonDynamoDBClient(
                new BasicAWSCredentials("Aasdasdsdsdsdsd", "dsadsfdsfds"));
    }

    @Override
    public void createOrUpdateEdge(EdgeItem edgeItem, StoreTable storeTable) throws EdgeWriteFailedException {
        int retryCount = 0;
        if (edgeItem.sourceId == null || edgeItem.destId == null) {
            throw new IllegalArgumentException("Entries can't be null");
        }

        Table table = new Table(dynamoDB, storeTable.getTableName());
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
    
    public void createOrUpdateEntity(Entity entity) throws EdgeWriteFailedException {
        int retryCount = 0;
        if (entity == null || entity.getId() == null) {
            throw new IllegalArgumentException("Entries can't be null");
        }

        StoreTable storeTable = entity.getStoreTable();
        
        Table table = new Table(dynamoDB, storeTable.getTableName());
        while (retryCount < MAX_RETRY) {
            retryCount++;
            try {
                // Mandatory params
                Item item = new Item();
                
                if(storeTable == StoreTable.PersonPerson) {
                    item.withPrimaryKey(storeTable.getPrimaryKey(), entity.getId())
                        .withNumber(storeTable.getSortKey(), 0);
                } else {
                    item.withPrimaryKey(storeTable.getPrimaryKey(), 0)
                    .withNumber(storeTable.getSortKey(), entity.getId());
                }
                Map<String, Object> attributes = entity.getAttributes();
                for(String key : attributes.keySet()) {
                    Object value = attributes.get(key);
                    if(value instanceof String) {
                        item.withString(key, (String) value);
                    } else {
                        item.withNumber(key, (Integer) value);
                    }
                }
                item.withNumber("created_at", Instant.now().getMillis());
                
                table.putItem(item);
                break;
            } catch (ProvisionedThroughputExceededException ptex) {
                LOG.error("ProvisionedThroughputExceededException : wait for 1 minute " + entity.toString());
                try {
                    Thread.sleep(1000 * 60);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (Exception ex) {
                throw new EdgeWriteFailedException(
                        "DynamoDB Exception: Create items failed for : " + entity.toString(), ex);
            }
        }
    }

    @Loggable(Loggable.INFO)
    public List<EdgeItem> getEdges(Long sourceId, StoreTable storeTable, boolean forward, int maxCurrent, int maxHistoric) {
        if (forward) {
            return getEdgesForward(sourceId, storeTable, maxCurrent, maxHistoric);
        } else {
            return getEdgesReverse(sourceId, storeTable, maxCurrent, maxHistoric);
        }
    }

    public List<EdgeItem> getEdgesForward(final Long sourceId, StoreTable storeTable, final int maxCurrent, final int maxHistoric) {
        Table table = new Table(dynamoDB, storeTable.getTableName());
        List<EdgeItem> edgeItems = new ArrayList<>();
        int count = maxCurrent; //Ignoring hostoric for now.
        
        try {
            // Can easily set max result size here to limit the results.
            ItemCollection<QueryOutcome> items = table.query(storeTable.getPrimaryKey(), sourceId);

            if (null != items) {
                Iterator<Item> iter = items.iterator();

                if(iter != null) {
                    while (iter.hasNext() && count-- > 0) {
                        Item item = iter.next();
                        edgeItems.add(new EdgeItem(item, storeTable));
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("Failed to get items for sourceId : " + sourceId, e);
        }

        return edgeItems;
    }

    private List<EdgeItem> getEdgesReverse(Long sourceId, StoreTable storeTable,  final int maxCurrent, final int maxHistoric) {
        Table table = new Table(dynamoDB, storeTable.getTableName());
        List<EdgeItem> edges = new ArrayList<>();
        int count = maxCurrent; //Ignoring historic for now..
        try {
            Index index = table.getIndex(storeTable.getIndex());
            QuerySpec spec = new QuerySpec().withHashKey(storeTable.getSortKey(), sourceId)
                    .withMaxPageSize(MAX_PAGE_SIZE);
            ItemCollection<QueryOutcome> items = index.query(spec);

            // Process each page of results
            for (Page<Item, QueryOutcome> page : items.pages()) {
                Iterator<Item> iter = page.iterator();
                while (iter.hasNext() && count-- > 0) {
                    edges.add(new EdgeItem(iter.next(), storeTable));
                }
            }
        } catch (Exception e) {
            LOG.error("Failed to get reverse items for : " + sourceId + " from " + storeTable, e);
        }
        return edges;
    }

    public EdgeItem getEdge(Long sourceId, StoreTable storeTable) {
        Table table = new Table(dynamoDB, storeTable.getTableName());
        try {
            Item item = table.getItem(storeTable.getPrimaryKey(), sourceId);
            if (item == null) {
                return null;
            }
            EdgeItem imageItem = new EdgeItem(item, storeTable);

            return imageItem;
        } catch (Exception e) {
            LOG.error("Get Item failed for sourceId : " + sourceId, e);
        }
        return null;
    }

    public Entity getEntity(Long sourceId, StoreTable storeTable) {
        Table table = new Table(dynamoDB, storeTable.getTableName());
        Item item = null;
        if (storeTable == StoreTable.PersonPerson) {            
            try {
                item = table.getItem(storeTable.getPrimaryKey(), sourceId, storeTable.getSortKey(), 0);
            } catch (Exception e) {
                LOG.error("Get Item failed for sourceId : " + sourceId, e);
            }
        } else {
            Index index = table.getIndex(storeTable.getIndex());
            ItemCollection<QueryOutcome> items = index.query(storeTable.getSortKey(), sourceId);
            
            Iterator<Item> iter = items.iterator();
            while (iter != null && iter.hasNext()) {
                Item iterItem = iter.next();
                if(iterItem.getInt(storeTable.getPrimaryKey()) == 0) {
                    item = iterItem;
                    break;
                }
            }
        }
        if (item == null) {
            return null;
        }

        return EntityHydrationFactory.hydrateEntity(item, storeTable);
    }
    
    public void createOrUpdateEntityBootStrap(String filepath, StoreTable storeTable, int threadPoolSize) {
        executor = Executors.newFixedThreadPool(threadPoolSize);
        AtomicInteger counter = new AtomicInteger(0);
        try (Stream<String> stream = Files.lines(Paths.get(filepath))) {
            stream.forEach(line -> {
                Runnable worker = new DataLoadWorker(line, storeTable, counter, this);
                executor.execute(worker);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
    }

    public void createOrUpdateEdgeBootStrap(String filepath, String label, StoreTable storeTable, int threadPoolSize) {
        executor = Executors.newFixedThreadPool(threadPoolSize);
        Table table = new Table(dynamoDB, storeTable.getTableName());
        AtomicInteger counter = new AtomicInteger(0);
        try (Stream<String> stream = Files.lines(Paths.get(filepath))) {
            stream.forEach(line -> {
                Runnable worker = new DataLoadWorker(line, storeTable, table, label, counter);
                executor.execute(worker);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
    }

    @Override
    public void createOrUpdateEdges(List<EdgeItem> edgeItem, StoreTable storeTable) throws EdgeWriteFailedException {
        // TODO Auto-generated method stub
        
    }
}
