package com.wp.igraph.model;

import java.util.List;

/**
 * 
 * @author varun.kumar
 *
 */
public class Dimension {
    private Relation dimension;
    private int maxCount;
    private RelationType relationType;
    private List<Dimension> recursiveDimension;
    
    public Dimension(Relation dimension, int maxCount, RelationType relationType, List<Dimension> recursiveDimension) {
        super();
        this.dimension = dimension;
        this.maxCount = maxCount;
        this.relationType = relationType;
        this.recursiveDimension = recursiveDimension;
    }

    public Relation getDimension() {
        return dimension;
    }

    public void setDimension(Relation dimension) {
        this.dimension = dimension;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    public RelationType getRelationType() {
        return relationType;
    }

    public void setRelationType(RelationType relationType) {
        this.relationType = relationType;
    }

    public List<Dimension> getRecursiveDimension() {
        return recursiveDimension;
    }

    public void setRecursiveDimension(List<Dimension> recursiveDimension) {
        this.recursiveDimension = recursiveDimension;
    }

    @Override
    public String toString() {
        return "ResultDimension [dimension=" + dimension + ", maxCount=" + maxCount + ", relationType=" + relationType
                + ", recursiveDimension=" + recursiveDimension + "]";
    }
}
