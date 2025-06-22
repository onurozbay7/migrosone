package com.migrosone.application.exception;

public class CourierNotFoundException extends RuntimeException {
    public CourierNotFoundException(String courierId) {
        super("Courier with id '" + courierId + "' not found.");
    }
}
