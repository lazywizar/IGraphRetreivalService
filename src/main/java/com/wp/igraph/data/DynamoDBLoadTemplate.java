package com.wp.igraph.data;

public class DynamoDBLoadTemplate {
    public static final String PERSON_ADDRESS_FORMAT = "{\"PutRequest\": { \"Item\": { \"person_id\": {\"N\": \"%s\"},\"address_id\": {\"N\": \"%s\"},\"creation_date\": {\"N\": \"%s\"}}}}";

    /*
    "         {\n" + 
            "            \"PutRequest\": {\n" + 
            "                \"Item\": {\n" + 
            "                    \"person_id\": {\n" + 
            "                        \"N\": \"%s\"\n" + 
            "                    },\n" + 
            "                    \"address_id\": {\n" + 
            "                        \"N\": \"%s\"\n" + 
            "                    },\n" + 
            "                    \"creation_date\": {\n" + 
            "                        \"N\": \"%s\"\n" + 
            "                    }\n" + 
            "                }\n" + 
            "            }\n" + 
            "        }";
            */
    
            
}
