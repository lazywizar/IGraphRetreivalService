package com.wp.igraph.model;

public enum EntityType {
    PERSON("", 0, 100000), 
    ADDRESS("", 100001,150000), 
    //BUSINESS(null, 0, 0), 
    EMAIL("", 150001, 200000), 
    PHONE("", 200001, 250000);    

    /*
    PERSON(0, 400000000), 
    ADDRESS(400000001,650000000), 
    BUSINESS(650000001, 700000000), 
    EMAIL(700000001, 1200000000), 
    PHONE(1200000001, 1700000000);
     */
        
    private String tableName;
    private int startKey;
    private int endKey;

    private EntityType(String tableName, int startKey, int endKey) {
        this.tableName = tableName;
        this.startKey = startKey;
        this.endKey = endKey;
    }

    public String getTableName() {
        return tableName;
    }

    public int getStartKey() {
        return startKey;
    }

    public int getEndKey() {
        return endKey;
    }    
    
    
}
