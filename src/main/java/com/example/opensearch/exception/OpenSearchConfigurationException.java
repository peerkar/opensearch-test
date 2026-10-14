package com.example.opensearch.exception;

/**
 * Thrown when configuration settings are missing or invalid.
 */
public class OpenSearchConfigurationException extends RuntimeException {

    public OpenSearchConfigurationException(String message) {
        super(message);
    }
}
