package com.wp.igraph.datastore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.wp.igraph.datastore.dynamodb.DynamoDBClient;
import com.wp.igraph.datastore.dynamodb.EdgeItem;
import com.wp.igraph.datastore.handlers.EntityHydrationFactory;
import com.wp.igraph.model.Address;
import com.wp.igraph.model.Email;
import com.wp.igraph.model.Entity;
import com.wp.igraph.model.EntityResult;
import com.wp.igraph.model.EntityType;
import com.wp.igraph.model.Person;
import com.wp.igraph.model.Phone;
import com.wp.igraph.model.Query;
import com.wp.igraph.model.StoreTable;

/**
 * 
 * @author varun.kumar
 *
 */
public class EntityManager {
    private DynamoDBClient dynamoDBClient;
    protected static final int GLOBAL_MAX = 10;

    private static final Logger LOG = Logger.getLogger(DynamoDBClient.class);

    public EntityManager() throws IOException {
        dynamoDBClient = new DynamoDBClient();
    }

    public EntityResult fetchFromTable(Query query) {
        // populate results.
        return new EntityResult(query.getTable().getResultEntityType(query.isForward()),
                dynamoDBClient.getEdges(query.getSourceId(), query.getTable(), query.isForward(),
                        query.getMaxCountCurrent(), query.getMaxCountHistoric()));
    }

    public Entity hydrateEntity(Long id, EntityType type) {
        return dynamoDBClient.getEntity(id, EntityHydrationFactory.getTableForType(type));
    }

    public Entity getPersonTraversed​(Long sourceId)
            throws InterruptedException, ExecutionException {
        List<Query> queries = new ArrayList<>();
        queries.add(new Query(sourceId, StoreTable.PersonAddress, true, 1, 5));
        queries.add(new Query(sourceId, StoreTable.PersonEmail, true, 1, 0));
        queries.add(new Query(sourceId, StoreTable.PersonPhone, true, 2, 2));
        queries.add(new Query(sourceId, StoreTable.PersonPerson, true, 10, 0));

        Map<EntityType, List<EdgeItem>> firstHopResults = getAllEntities(queries);

        // Go for second hop of the recursive fields.
        // Address is the only recursive field, hence get it!
        Long currentAddressId = null;
        List<EdgeItem> addressItems = firstHopResults.get(EntityType.ADDRESS);
        if (addressItems != null && !addressItems.isEmpty()) {
            currentAddressId = addressItems.get(0).getDestId();
        }
        
        Map<EntityType, List<EdgeItem>> addressResults = getAllEntities(
                ImmutableList.of(new Query(currentAddressId, StoreTable.PersonAddress, false, 5, 0)));

        
        // Hydrate all Entities in parallel.
        Map<EntityType, List<Long>> mergedResults = getIdsMap(merge(firstHopResults, addressResults));
        //Add the main person too :)
        mergedResults.get(EntityType.PERSON).add(sourceId);
        
        Map<Long, Entity> hydratedEntities = hydrateEntities(mergedResults);
        
        //      hydratedEntities.values().forEach(r -> r.toString());
        
        //Populate the result
        Person person = (Person) hydratedEntities.get(sourceId);
        List<Address> address = new ArrayList<>();
        firstHopResults.get(EntityType.ADDRESS).forEach(r -> address.add((Address) hydratedEntities.get(r.getDestId())));
        
        List<Person> currentAddressPeople = new ArrayList<>();
        addressResults.get(EntityType.PERSON).forEach(r -> currentAddressPeople.add((Person) hydratedEntities.get(r.getSourceId())));
        address.get(0).setCurrentPerson(currentAddressPeople);
        
        List<Email> emails = new ArrayList<>();
        firstHopResults.get(EntityType.EMAIL).forEach(r -> emails.add((Email) hydratedEntities.get(r.getDestId())));
        
        List<Phone> phones = new ArrayList<>();
        firstHopResults.get(EntityType.PHONE).forEach(r -> phones.add((Phone) hydratedEntities.get(r.getDestId())));
        
        List<Person> relations = new ArrayList<>();
        firstHopResults.get(EntityType.PERSON).forEach(r -> relations.add((Person) hydratedEntities.get(r.getDestId())));
        
        person.setCurrentAddresses(address);
        person.setCurrentEmails(emails);
        person.setCurrentPhones(phones);
        person.setCurrentRelation(relations);
        
        //System.out.println("Result \n\n" + person.toString());
        
        return person;
    }

    private Map<EntityType, List<EdgeItem>> merge(Map<EntityType, List<EdgeItem>> map1, Map<EntityType, List<EdgeItem>> map2) {
        map1.keySet().forEach(r -> {
            if(map2.containsKey(r)) {
                map2.get(r).addAll(map1.get(r));
            } else {
                map2.put(r, map1.get(r));
            }
        });
        return map1;
    }
    
    private Map<EntityType, List<Long>> getIdsMap(Map<EntityType, List<EdgeItem>> edges) {
        Map<EntityType, List<Long>> results = new HashMap<EntityType, List<Long>>();
        edges.keySet().forEach(r -> results.put(r, new ArrayList<Long>()));
        edges.keySet().forEach(type -> edges.get(type).forEach(r -> results.get(type).add(r.getDestId())));

        return results;
    }

    public Map<Long, Entity> hydrateEntities(Map<EntityType, List<Long>> entities)
            throws InterruptedException, ExecutionException {
        ListeningExecutorService executor = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
        List<ListenableFuture<Entity>> list = new ArrayList<ListenableFuture<Entity>>();

        for (final EntityType type : entities.keySet()) {
            for (final Long sourceId : entities.get(type)) {
                ListenableFuture<Entity> future = executor.submit(new Callable<Entity>() {
                    public Entity call() throws Exception {
                        return hydrateEntity(sourceId, type);
                    }
                });
                // Add the future to the list
                list.add(future);
            }
        }

        ListenableFuture<List<Entity>> combinedFutures = Futures.allAsList(list);
        List<Entity> resultSets = combinedFutures.get();

        Map<Long, Entity> hydratedEntries = new HashMap<>();

        for (Entity result : resultSets) {
            if(result != null) {
                hydratedEntries.put(result.getId(), result);
            }
        }

        return hydratedEntries;
    }

    public Map<EntityType, List<EdgeItem>> getAllEntities(List<Query> queries)
            throws InterruptedException, ExecutionException {
        ListeningExecutorService executor = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());

        List<ListenableFuture<EntityResult>> list = new ArrayList<ListenableFuture<EntityResult>>();
        // For each table, create an independent thread that will
        // query just that table and return a set of user IDs from it
        for (Query query : queries) {
            ListenableFuture<EntityResult> future = executor.submit(new Callable<EntityResult>() {
                public EntityResult call() throws Exception {
                    return fetchFromTable(query);
                }
            });
            // Add the future to the list
            list.add(future);
        }

        // We want to know when ALL the threads have completed,
        // so we use a Guava function to turn a list of ListenableFutures
        // into a single ListenableFuture
        ListenableFuture<List<EntityResult>> combinedFutures = Futures.allAsList(list);

        // The get on the combined ListenableFuture will now block until
        // ALL the individual threads have completed work.
        List<EntityResult> resultSets = combinedFutures.get();

        // Now all we have to do is combine the individual sets into a
        // single result
        Map<EntityType, List<EdgeItem>> allEntities = new HashMap<>();
        for (EntityResult result : resultSets) {

            // Sort results based on the creationDate, to always maintain a
            // strict order always!!
            Collections.sort(result.getEdges());
            allEntities.put(result.getEntityType(), result.getEdges());
        }

        return allEntities;
    }

    public Map<EntityType, List<EdgeItem>> getEntitiesInParallel(Map<StoreTable, List<Long>> sources, boolean direction)
            throws InterruptedException, ExecutionException {
        ListeningExecutorService executor = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());

        List<ListenableFuture<EntityResult>> list = new ArrayList<ListenableFuture<EntityResult>>();
        // For each table, create an independent thread that will
        // query just that table and return a set of user IDs from it

        for (StoreTable table : sources.keySet()) {
            for (Long sourceId : sources.get(table)) {
                ListenableFuture<EntityResult> future = executor.submit(new Callable<EntityResult>() {
                    public EntityResult call() throws Exception {
                        return fetchFromTable(new Query(sourceId, table, direction, DynamoDBClient.GLOBAL_MAX, DynamoDBClient.GLOBAL_MAX));
                    }
                });
                // Add the future to the list
                list.add(future);
            }
        }

        ListenableFuture<List<EntityResult>> combinedFutures = Futures.allAsList(list);
        List<EntityResult> resultSets = combinedFutures.get();

        Map<EntityType, List<EdgeItem>> allEntities = new HashMap<>();
        for (EntityResult result : resultSets) {
            if (allEntities.containsKey(result.getEntityType())) {
                allEntities.get(result.getEntityType()).addAll(result.getEdges());
            } else {
                allEntities.put(result.getEntityType(), result.getEdges());
            }
        }

        return allEntities;
    }

}
