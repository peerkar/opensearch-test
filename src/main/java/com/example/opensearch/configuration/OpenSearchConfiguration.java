package com.example.opensearch.configuration;

import com.example.opensearch.exception.OpenSearchConfigurationException;
import org.apache.hc.core5.http.HttpHost;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Settings for connecting to an OpenSearch service
 *
 * @param host     the OpenSearch HTTPS endpoint
 * @param username the user to authenticate as
 * @param password the user's password
 */
public record OpenSearchConfiguration(HttpHost host, String username, char[] password) {

    /**
     * Reads the settings from the following environment variables:
     * <ul>
     * <li>{@code OPENSEARCH_URL}</li>
     * <li>{@code OPENSEARCH_USERNAME}</li>
     * <li>{@code OPENSEARCH_PASSWORD}</li>
     *
     * <p>The connection always uses TLS.</p>
     *
     * @return the configuration
     * @throws OpenSearchConfigurationException if a variable is missing, blank, or invalid
     */
    public static OpenSearchConfiguration fromEnv() {
        return new OpenSearchConfiguration(
                parseHttpHost(readEnv("OPENSEARCH_URL")),
                readEnv("OPENSEARCH_USERNAME"),
                readEnv("OPENSEARCH_PASSWORD").toCharArray());
    }

    private static HttpHost parseHttpHost(String uriString) {

        try {
            URI uri = new URI(uriString);
            return new HttpHost("https", uri.getHost(), uri.getPort());

        } catch (URISyntaxException uriSyntaxException) {
            throw new OpenSearchConfigurationException(uriString + " is not a valid URL");
        }
    }

    private static String readEnv(String name) {
        String value = System.getenv(name);

        if (value == null || value.isBlank()) {
            throw new OpenSearchConfigurationException("Missing environment variable " + name);
        }
        return value;
    }
}
