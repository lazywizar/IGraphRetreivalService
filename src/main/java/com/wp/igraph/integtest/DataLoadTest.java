package com.wp.igraph.integtest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.joda.time.Instant;

import com.wp.igraph.datastore.EntityManager;
import com.wp.igraph.datastore.dynamodb.DynamoDBClient;
import com.wp.igraph.datastore.dynamodb.EdgeItem;
import com.wp.igraph.exceptions.EdgeWriteFailedException;
import com.wp.igraph.model.Entity;
import com.wp.igraph.model.EntityType;
import com.wp.igraph.model.Query;
import com.wp.igraph.model.StoreTable;

/**
 * 
 * @author varun.kumar
 *
 */
public class DataLoadTest {
    DynamoDBClient client;
    EntityManager entityManager;
    
    private static final Logger LOG = Logger.getLogger(DataLoadTest.class);
    private AtomicInteger count;
    
    static {
        PropertyConfigurator.configure("AkLog4j.cfg");
    }

    public DataLoadTest() throws IOException {
        client = new DynamoDBClient();
        count = new AtomicInteger(0);
        entityManager = new EntityManager();
    }
    
    public void loadEdge(StoreTable table, String filepath, String label) {
        //read file into stream, try-with-resources
        try (Stream<String> stream = Files.lines(Paths.get(filepath))) {
                stream.forEach(line -> postData(line, table, label));
        } catch (IOException e) {
                e.printStackTrace();
        }
    }
    
    public void postData(String line, StoreTable table, String label) {
        String[] tokens = line.split(",");
        try {
            client.createOrUpdateEdge(new EdgeItem(Long.parseLong(tokens[0]), Long.parseLong(tokens[1]), label), table);
            if(count.incrementAndGet() % 1000 == 0) {
                System.out.print(count.get() / 1000 + ", ");
            }
        } catch (NumberFormatException | EdgeWriteFailedException e) {
            LOG.error("Failed to load " + line);
        }
    }
    
    public void bootstrap(String filepath, String label, StoreTable storeTable) {
        client.createOrUpdateEdgeBootStrap(filepath, label, storeTable, 250);
    }
    
    public void bootstrapEntity(String filepath, StoreTable storeTable) {
        client.createOrUpdateEntityBootStrap(filepath, storeTable, 1);
    }
    
    public void testGet() {
        Random rand = new Random();
        for(int i = 0; i< 500; i++) {
            Long start = Instant.now().getMillis();
            List<EdgeItem> edgeItems = client.getEdges((long) rand.nextInt(30000), StoreTable.PersonPerson, true, 10, 10);
            System.out.println("Result size : " + edgeItems.size());
            edgeItems.forEach(System.out::println);
            Long end = Instant.now().getMillis();
            System.out.println("Total request took : " + (end - start) + "ms");
        }
    }
    
    public void testAllEntityGet() throws InterruptedException, ExecutionException {
        Random rand = new Random();
        List<Query> queries = new ArrayList<>();
        for(StoreTable table : StoreTable.values()) {
            queries.add(new Query(1L, table, true, 10, 10));
        }

        for(int i = 0; i< 10; i++) {
            Long start = Instant.now().getMillis();
            Long sourceId = (long) rand.nextInt(30000);
            queries.forEach(r -> r.setSourceId(sourceId));
            Map<EntityType, List<EdgeItem>> results = entityManager.getAllEntities(queries);
            Long end = Instant.now().getMillis();
            System.out.println("Total request took : " + (end - start) + "ms");
            
            results.keySet().forEach(r -> System.out.println("\t" + r + " - " + results.get(r)));
        }
    }
    
    public void testTraverse() throws InterruptedException, ExecutionException {
        for(int i = 0; i < 100; i++) {
            long start = Instant.now().getMillis();
            Entity entity = entityManager.getPersonTraversed​(16998L);
            long end = Instant.now().getMillis();

            System.out.println(entity.toString());            
            System.out.println("Full Traverse Complete in " + (end - start));
        }
    }
    
    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        DataLoadTest test = new DataLoadTest();
        
        test.testTraverse();
        
        //test.testAllEntityGet();
        
        //Map<EntityType, List<EdgeItem>> results = test.entityManager.getAllEntities(5172L, Arrays.asList(StoreTable.values()), true);
        //results.keySet().forEach(r -> System.out.println("\t" + r + " - " + results.get(r)));

        //Map<EntityType, List<EdgeItem>> revResults = test.entityManager.getAllEntities( 587952157L, ImmutableList.of(StoreTable.PersonAddress), false);
        //revResults.keySet().forEach(r -> System.out.println("\t" + r + " - " + revResults.get(r)));
        
        /*
        EntityResult result = test.entityManager.fetchFromTable(587952157L, StoreTable.PersonAddress, false);
        System.out.println(result.getEntityType());
        System.out.println(result.getEdges());
        */
        
        /*
        
        test.bootstrapEntity("/home/ubuntu/wp/sm-data/addresses_sm.txt", StoreTable.PersonAddress);        
        test.bootstrapEntity("/home/ubuntu/wp/sm-data/emails_sm.txt", StoreTable.PersonEmail);
        test.bootstrapEntity("/home/ubuntu/wp/sm-data/phones_sm.txt", StoreTable.PersonPhone);
        test.bootstrapEntity("/home/ubuntu/wp/sm-data/person_sm.txt", StoreTable.PersonPerson);
         */

        /*
        test.bootstrap("/home/ubuntu/wp/sm-data/edge-person-address-current.txt", "C", StoreTable.PersonAddress);
        test.bootstrap("/home/ubuntu/wp/sm-data/edge-person-address-hist.txt", "H", StoreTable.PersonAddress);
        
        test.bootstrap("/home/ubuntu/wp/sm-data/edge-person-email-current.txt", "C", StoreTable.PersonEmail);
        test.bootstrap("/home/ubuntu/wp/sm-data/edge-person-email-hist.txt", "H", StoreTable.PersonEmail);

        test.bootstrap("/home/ubuntu/wp/sm-data/edge-person-phone-current.txt", "C", StoreTable.PersonPhone);
        test.bootstrap("/home/ubuntu/wp/sm-data/edge-person-phone-hist.txt", "H", StoreTable.PersonPhone);
         */
        
        /*
        test.bootstrap("/home/ubuntu/wp/sm-data/edge-spouse-current.txt", "C", StoreTable.PersonPerson);
        test.bootstrap("/home/ubuntu/wp/sm-data/edge-spouse-hist.txt", "H", StoreTable.PersonPerson);
        
        test.bootstrap("/home/ubuntu/wp/sm-data/edge-related-hist.txt", "CR", StoreTable.PersonPerson);
        test.bootstrap("/home/ubuntu/wp/sm-data/edge-related-hist.txt", "HR", StoreTable.PersonPerson);
        */
    }
}
