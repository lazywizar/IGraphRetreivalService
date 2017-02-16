package com.wp.igraph.model;

/**
 * DynamoDB table metadata.
 * 
 * @author varun.kumar
 *
 */
public enum StoreTable {
    PersonAddress("PersonAddress", "person_id", "address_id", "address_id-index", EntityType.ADDRESS),
    PersonEmail("PersonEmail", "person_id", "email_id", "email_id-index", EntityType.EMAIL),
    PersonPhone("PersonPhone", "person_id", "phone_id", "phone_id-index", EntityType.PHONE),
    PersonPerson("PersonPerson", "src_person_id", "dest_person_id", "", EntityType.PERSON);
    
    private StoreTable(String tableName, String primaryKey, String sortKey, String index, EntityType resultEntityType) {
        this.tableName = tableName;
        this.primaryKey = primaryKey;
        this.sortKey = sortKey;
        this.index = index;
        this.resultEntityType = resultEntityType;
    }
    
    private String tableName;
    private String primaryKey;
    private String sortKey;
    private String index;
    private EntityType resultEntityType;
    
    public String getTableName() {
        return tableName;
    }
    public String getPrimaryKey() {
        return primaryKey;
    }
    public String getSortKey() {
        return sortKey;
    }
    
    public String getIndex() {
        return index;
    }
    public EntityType getResultEntityType(boolean forward) {
        if(forward) {
            return resultEntityType;
        } else {
            return EntityType.PERSON; //For reverse it is always an Actor (Person in our case.)
        }
    }    
}
