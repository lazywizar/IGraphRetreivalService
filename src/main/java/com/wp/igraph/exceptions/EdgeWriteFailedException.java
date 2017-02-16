package com.wp.igraph.exceptions;

public class EdgeWriteFailedException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 312324L;

    public EdgeWriteFailedException(String message, Throwable ex) {
        super(message, ex);
    }
}
