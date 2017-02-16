package com.wp.igraph.datastore.dynamodb;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.wp.igraph.model.StoreTable;

/**
 * 
 * @author varun.kumar
 *
 */
public class EdgeItem implements Comparable<EdgeItem> {
    Long sourceId;
    Long destId;
    String label; 
    Long createdAt; 

    public EdgeItem() {
        super();
    }

    public EdgeItem(Item item, StoreTable table) {
        super();
        if (item == null) {
            return;
        }
        this.sourceId = item.getLong(table.getPrimaryKey());
        this.destId = item.getLong(table.getSortKey());
        this.label = item.getString("label");
        this.createdAt = item.getLong("created_at");
    }
    
    public EdgeItem(Long sourceId, Long destId, String label) {
        super();
        this.sourceId = sourceId;
        this.destId = destId;
        this.label = label;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public Long getDestId() {
        return destId;
    }

    public void setDestId(Long destId) {
        this.destId = destId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((destId == null) ? 0 : destId.hashCode());
        result = prime * result + ((label == null) ? 0 : label.hashCode());
        result = prime * result + ((sourceId == null) ? 0 : sourceId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EdgeItem other = (EdgeItem) obj;
        if (destId == null) {
            if (other.destId != null)
                return false;
        } else if (!destId.equals(other.destId))
            return false;
        if (label == null) {
            if (other.label != null)
                return false;
        } else if (!label.equals(other.label))
            return false;
        if (sourceId == null) {
            if (other.sourceId != null)
                return false;
        } else if (!sourceId.equals(other.sourceId))
            return false;
        return true;
    }

    
    
    @Override
    public String toString() {
        return "EdgeItem [sourceId=" + sourceId + ", destId=" + destId + ", label=" + label + ", createdAt="
                + createdAt + "]";
    }

    @Override
    public int compareTo(EdgeItem o) {
        return (int) (createdAt - o.createdAt);
    }
}
