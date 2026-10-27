package com.example.opensearch.cluster;

import com.example.opensearch.configuration.OpenSearchConfiguration;
import com.example.opensearch.connection.OpenSearchConnection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cluster operations against OpenSearch service
 */
@EnabledIfEnvironmentVariable(named = "OPENSEARCH_URL", matches = ".+")
class ClusterManagerIT {

    @Test
    void getClusterStatusReturnsAllParts() throws IOException {
        try (OpenSearchConnection openSearchConnection = new OpenSearchConnection(OpenSearchConfiguration.fromEnv())) {

            ClusterManager clusterManager = new ClusterManager(openSearchConnection.getClient());

            ClusterStatus clusterStatus = clusterManager.getClusterStatus();

            assertNotNull(clusterStatus.clusterName());
            assertNotNull(clusterStatus.health().status());
            assertTrue(clusterStatus.indices().userIndices().stream().noneMatch(userIndex -> userIndex.name().startsWith(".")),
                    () -> "System indices listed: " + clusterStatus.indices().userIndices());
            // Closed indices have no stats, so their count is null.
            assertTrue(clusterStatus.indices().userIndices().stream()
                            .allMatch(userIndex -> userIndex.documentCount() == null || userIndex.documentCount() >= 0),
                    () -> "Negative document count: " + clusterStatus.indices().userIndices());
        }
    }
}
