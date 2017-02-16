package com.wp.igraph.data;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.wp.igraph.datastore.dynamodb.DynamoDBClient;
import com.wp.igraph.model.EdgeLabel;
import com.wp.igraph.model.EntityType;

/**
 * 
 * @author varun.kumar
 *
 */
public class SampleDataGenerator {
    private DataGeneratorHelper dataGeneratorHelper;
    
    public SampleDataGenerator() {
        this.dataGeneratorHelper = new DataGeneratorHelper();
    }

    public void generateEntities(EntityType type, Long count, String fileName) {
        PrintWriter fstream = null;
        try {
            fstream = new PrintWriter(new FileWriter(fileName));
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        while (count-- > 0) {
            fstream.println(dataGeneratorHelper.genrateRandomEntityString(type));
        }
        fstream.flush();
        fstream.close();
    }
    
    public void generateEdges(EntityType sourceType, EntityType destType, EdgeLabel label, int minCount, int maxCount, String fileName) {
        PrintWriter fstream = null;
        try {
            fstream = new PrintWriter(new FileWriter(fileName));
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        for(int index = sourceType.getStartKey(); index <= sourceType.getEndKey(); index++) {
            //Get 0 or more than one associations of an actor to subject.
            int count = dataGeneratorHelper.randInt(minCount, maxCount);
            while(count-- > 0) {
                
                
                fstream.println(index + "," + dataGeneratorHelper.randInt(destType.getStartKey(), destType.getEndKey()));
            }
        }
        fstream.flush();
        fstream.close();
    }
    
    /**
     * These are transitive relations like realted_to/spouse.
     */
    public void generateActorActorEdges(EntityType sourceType, EdgeLabel label, int minCount, int maxCount, boolean exclusiveRelation, String fileName) {
        PrintWriter fstream = null;
        try {
            fstream = new PrintWriter(new FileWriter(fileName));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        
        List<Integer> users = new ArrayList<Integer>();
        for(int i = sourceType.getStartKey(); i<= sourceType.getEndKey()/2; i++) {
            users.add(i);
        }

        System.out.println("built list");
        
        int endIndex = users.size() - 1;
        
        //Assuming 100 mil unmarried
        while(endIndex > 10000000) {
            int firstIndex = dataGeneratorHelper.randInt(0, endIndex);
            int secondIndex = dataGeneratorHelper.randInt(0, endIndex);
            
            //can't marry oneself.
            while(secondIndex == firstIndex) {
                secondIndex = dataGeneratorHelper.randInt(0, endIndex);
            }
            int firstUserValue = users.get(firstIndex);
            int secondUserValue = users.get(secondIndex);

            fstream.println(firstUserValue + "," + secondUserValue);
            fstream.println(secondUserValue + "," + firstUserValue);
            
            swap(users, firstIndex, endIndex);
            endIndex--;
            swap(users, secondIndex, endIndex);
            endIndex--;
        }
        
        fstream.flush();
        fstream.close();
    }
    
    private void swap(List<Integer> list, int i, int j) {
        int temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }
    
    public void generateAllEntities() {
        System.out.println("Starting person");
        generateEntities(EntityType.PERSON, 400000000L, "src/main/resources/person.txt");
        
        System.out.println("Starting address");
        generateEntities(EntityType.ADDRESS,250000000L, "src/main/resources/addresses.txt");

        //System.out.println("Starting Business");
        //generateEntities(EntityType.BUSINESS, 50000000L, "src/main/resources/business.txt");
        
        System.out.println("Starting email");
        generateEntities(EntityType.EMAIL, 500000000L, "src/main/resources/emails.txt");

        System.out.println("Starting Phone");
        generateEntities(EntityType.PHONE, 500000000L, "src/main/resources/phones.txt");
    }
    
    public void generateAllEntitiesSmallerSet() {
        System.out.println("Starting person");
        generateEntities(EntityType.PERSON, 100000L, "src/main/resources/person_sm.txt");
        
        System.out.println("Starting address");
        generateEntities(EntityType.ADDRESS, 50000L, "src/main/resources/addresses_sm.txt");

        //System.out.println("Starting Business");
        //generateEntities(EntityType.BUSINESS, 10000000L, "src/main/resources/business.txt");
        
        System.out.println("Starting email");
        generateEntities(EntityType.EMAIL, 50000L, "src/main/resources/emails_sm.txt");

        System.out.println("Starting Phone");
        generateEntities(EntityType.PHONE, 50000L, "src/main/resources/phones_sm.txt");
    }
    
    public void generateAllEdges() {
        System.out.println("Starting person -> address");
        generateEdges(EntityType.PERSON, EntityType.ADDRESS, EdgeLabel.CURRENT_ADDRESS, 1, 3, "src/main/resources/edge-person-address-current.txt");
        generateEdges(EntityType.PERSON, EntityType.ADDRESS, EdgeLabel.HISTORICAL_ADDRESS, 0, 2, "src/main/resources/edge-person-address-hist.txt");
                
        System.out.println("Starting person -> email");
        generateEdges(EntityType.PERSON, EntityType.EMAIL, EdgeLabel.CURRENT_EMAIL, 0, 2, "src/main/resources/edge-person-email-current.txt");
        generateEdges(EntityType.PERSON, EntityType.EMAIL, EdgeLabel.HISTORICAL_EMAIL, 0, 1, "src/main/resources/edge-person-email-hist.txt");
        
        System.out.println("Starting person -> phone");
        generateEdges(EntityType.PERSON, EntityType.PHONE, EdgeLabel.CURRENT_PHONE, 1, 2, "src/main/resources/edge-person-phone-current.txt");
        generateEdges(EntityType.PERSON, EntityType.PHONE, EdgeLabel.HISTORICAL_PHONE, 0, 1, "src/main/resources/edge-person-phone-hist.txt");

        System.out.println("Starting person -> person spouse");
        generateEdges(EntityType.PERSON, EntityType.PERSON, EdgeLabel.SPOUSE, 0, 1, "src/main/resources/edge-spouse-current.txt");
        generateEdges(EntityType.PERSON, EntityType.PERSON, EdgeLabel.HISTORICAL_SPOUSE, 0, 3, "src/main/resources/edge-spouse-hist.txt");

        System.out.println("Starting person -> person related current");
        generateEdges(EntityType.PERSON, EntityType.PERSON, EdgeLabel.RELATED, 1, 5, "src/main/resources/edge-related-current.txt");
        System.out.println("Starting person -> person related historic");
        generateEdges(EntityType.PERSON, EntityType.PERSON, EdgeLabel.RELATED, 0, 2, "src/main/resources/edge-related-hist.txt");
    }
    
    public static void main(String[] args) {
        SampleDataGenerator sampleDataGenerator = new SampleDataGenerator();
        
        sampleDataGenerator.generateAllEntitiesSmallerSet();
        sampleDataGenerator.generateAllEdges();
        
    }
}
