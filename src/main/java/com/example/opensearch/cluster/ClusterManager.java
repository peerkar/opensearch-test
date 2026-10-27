package com.example.opensearch.cluster;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.ExpandWildcard;
import org.opensearch.client.opensearch.cluster.HealthResponse;
import org.opensearch.client.opensearch.indices.IndicesStatsResponse;
import org.opensearch.client.opensearch.indices.ResolveIndexResponse;
import org.opensearch.client.opensearch.indices.stats.IndicesStatsMetric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Instant;

/**
 * Cluster-level operations.
 */
public class ClusterManager {

    /**
     * @param openSearchClient the client to send requests with
     */
    public ClusterManager(OpenSearchClient openSearchClient) {
        this.openSearchClient = openSearchClient;
    }

    /**
     * Gets the cluster health and user-created indices with their document counts. Logs the health as JSON.
     *
     * @return the cluster status, ready to send to monitoring systems
     * @throws IOException if a request fails
     */
    public ClusterStatus getClusterStatus() throws IOException {
        HealthResponse healthResponse = openSearchClient.cluster().health();

        log.info("Cluster health: {}", healthResponse.toJsonString());

        ResolveIndexResponse resolveIndexResponse = openSearchClient.indices().resolveIndex(builder -> builder
                .name("*")
                .expandWildcards(ExpandWildcard.All));

        // One request for all indices: listing the user indices by name could exceed the URL length limit.
        IndicesStatsResponse indicesStatsResponse = openSearchClient.indices().stats(builder -> builder
                .index("*")
                .expandWildcards(ExpandWildcard.All)
                .metric(IndicesStatsMetric.Docs));

        return new ClusterStatus(
                ClusterStatus.SCHEMA_VERSION,
                healthResponse.clusterName(),
                Instant.now(),
                HealthStatus.of(healthResponse),
                IndexStatus.of(resolveIndexResponse.indices(), indicesStatsResponse.indices()));
    }

    private final OpenSearchClient openSearchClient;

    private static final Logger log = LoggerFactory.getLogger(ClusterManager.class);
}
