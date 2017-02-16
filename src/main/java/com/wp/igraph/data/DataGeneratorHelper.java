package com.wp.igraph.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;
import com.wp.igraph.model.EntityType;

/**
 * 
 * @author varun.kumar
 *
 */
public class DataGeneratorHelper {

    private Random random;
    private Instant now;
    private List<String> namesWithGender;
    private List<String> addressSuffix;

    private int namesSize;
    private int addressSize;
    
    private static final List<String> emailDomains = ImmutableList.of("@gmail.com", "@yahoo.com", "@msn.com", "@rediff.com");
    private static final List<String> phoneCarriers = ImmutableList.of("AT&T", "TMobile", "Verizon", "MetroPCS","SomeJunkLine");
    //private Map<UUID, Integer> UUIDKey; 
    private AtomicLong serialNumber;
    
    public DataGeneratorHelper() {
        this.random = new Random();
        this.now = Instant.now();
        this.namesWithGender = getNames();
        this.addressSuffix = getAddressSuffixs();
        this.addressSize = addressSuffix.size() - 1;
        this.namesSize = namesWithGender.size() - 1;
        //UUIDKey = new HashMap<>();
        serialNumber = new AtomicLong(1L);
    }
    
    public String getRandomPersonString() {
        String nameGender =  namesWithGender.get(random.nextInt(namesSize));
        //UUIDKey.put(UUID.randomUUID(), serialNumber.get());
        String[] tokens = nameGender.split(",");
        //return  "SET " + serialNumber.getAndIncrement() + " '{\"n\":\"" + tokens[0] + "\",\"g\":\"" + tokens[1] + "\",\"d\":\"" + getRandomDOB() + "\"}'";

        return  serialNumber.getAndIncrement() + "," + tokens[0] + "," + tokens[1] + "," + getRandomDOB();
    }
    
    public String getRandomAddressString() {
        String address =  addressSuffix.get(random.nextInt(addressSize));
        //return "SET " + serialNumber.getAndIncrement()  + " '{\"n\":\"" + (random.nextInt(89000) +  10000) + "\"," + address + "}'";    

        return serialNumber.getAndIncrement()  + "," + (random.nextInt(89000) +  10000) + "," + address;        
    }
    
    public String getRandomEmailString() {
        String name =  namesWithGender.get(random.nextInt(namesSize));
        String email =  name.replace(",", "").replaceAll(" ", "_").toLowerCase().substring(0, 3 + random.nextInt(name.length() - 3)) + emailDomains.get(random.nextInt(4));
        //return "SET " + serialNumber.getAndIncrement()  + " '{\"e\":\"" + email + "\"}'";
        
        return serialNumber.getAndIncrement()  + "," + email;
    }    
 
    public String getRandomPhoneString() {
        Long number =  random.nextInt(2000000000) + 2000000000L;
        //return "SET " + serialNumber.getAndIncrement()  + " '{\"n\":\"" + number.toString() + "\",\"t\":" + random.nextInt(5) + ",\"c\":\"" + random.nextInt(10) + "\"}'";  

        return serialNumber.getAndIncrement()  + "," + number.toString() + "," + random.nextInt(5) + "," + random.nextInt(10);  
    }

    public String getRandomBusiness() {
        String name =  namesWithGender.get(random.nextInt(namesSize));
        String[] array = name.split(" ");
        return serialNumber.getAndIncrement()  + "," + array[1] + " Private Ltd." + ", " + random.nextInt(1000);
    }
    
    public String genrateRandomEntityString(EntityType type) {
        switch (type) {
        case PERSON:
            return getRandomPersonString();
        case ADDRESS:
            return getRandomAddressString();
        case EMAIL:
            return getRandomEmailString();
        case PHONE:
            return getRandomPhoneString();
        default:
            break;
        }
        return null;
    }
    
    public String getEmails(String name) {
        String email =  name.replace(",", "").replaceAll(" ", "_").toLowerCase().substring(0, 3 + random.nextInt(name.length() - 3));
        StringBuilder emails = new StringBuilder(email + emailDomains.get(random.nextInt(4)));
        for(int i = 0; i < random.nextInt(2); i++) {
            emails.append("|").append(email + "_" + i + emailDomains.get(random.nextInt(4)));
        }
        return emails.toString();
    }
     
    private List<String> getAddressSuffixs() {
        List<String> addressSuffix = new ArrayList<String>();

        // read file into stream
        try (Stream<String> stream = Files.lines(Paths.get("src/main/resources", "address.txt"))) {

            stream.forEach(r -> addressSuffix.add(r));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return addressSuffix;
    }

    
    private List<String> getNames() {
        List<String> namesWithGender = new ArrayList<String>();

        // read file into stream
        try (Stream<String> stream = Files.lines(Paths.get("src/main/resources", "Names.txt"))) {

            stream.forEach(r -> namesWithGender.add(r));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return namesWithGender;
    }

    public String getRandomDOB() {
        return now.minus(random.nextInt(20000) + 18, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS).toString().replace("T00:00:00Z", "");
    }
    
    public String getRandomEdge(EntityType source, EntityType dest) {
        return randInt(source.getStartKey(), source.getEndKey()) + "," + randInt(dest.getStartKey(), dest.getEndKey());
    }
    
    public int randInt(int min, int max) {
        // Can also use java 1.7+ API. But its fine for now.
        //randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);
        return random.nextInt((max - min) + 1) + min;
    }
}
