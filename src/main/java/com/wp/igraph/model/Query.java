package com.wp.igraph.model;

/**
 * 
 * @author varun.kumar
 *
 */
public class Query {
    Long sourceId;
    StoreTable table;
    boolean forward;
    int maxCountCurrent;
    int maxCountHistoric;

    public Query(Long sourceId, StoreTable table, boolean forward, int maxCountCurrent, int maxCountHistoric) {
        super();
        this.sourceId = sourceId;
        this.table = table;
        this.forward = forward;
        this.maxCountCurrent = maxCountCurrent;
        this.maxCountHistoric = maxCountHistoric;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public StoreTable getTable() {
        return table;
    }

    public void setTable(StoreTable table) {
        this.table = table;
    }

    public boolean isForward() {
        return forward;
    }

    public void setForward(boolean forward) {
        this.forward = forward;
    }

    public int getMaxCountCurrent() {
        return maxCountCurrent;
    }

    public void setMaxCountCurrent(int maxCountCurrent) {
        this.maxCountCurrent = maxCountCurrent;
    }

    public int getMaxCountHistoric() {
        return maxCountHistoric;
    }

    public void setMaxCountHistoric(int maxCountHistoric) {
        this.maxCountHistoric = maxCountHistoric;
    }

}
