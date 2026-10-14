package com.example.opensearch;

import com.example.opensearch.configuration.OpenSearchConfiguration;
import com.example.opensearch.connection.OpenSearchConnection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.endpoints.BooleanResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Connectivity test against OpenSearch service
 */
@EnabledIfEnvironmentVariable(named = "OPENSEARCH_URL", matches = ".+")
class OpenSearchConnectionIT {

    @Test
    void connects() throws IOException {
        try (OpenSearchConnection openSearchConnection = new OpenSearchConnection(OpenSearchConfiguration.fromEnv())) {

            OpenSearchClient openSearchClient = openSearchConnection.getClient();

            BooleanResponse booleanResponse = openSearchClient.ping();

            assertTrue(booleanResponse.value());
        }
    }
}
