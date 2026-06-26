package com.ecommerce.api.common;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException of(String resource, String identifier) {
        return new ResourceNotFoundException(resource + " not found: " + identifier);
    }
}
