package com.tarunnalla.inventory.common;

/**
 * The request is valid but conflicts with the current state,
 * e.g. a duplicate SKU or removing more stock than is available.
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
