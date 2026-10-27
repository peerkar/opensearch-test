package com.example.opensearch.cluster;

import org.opensearch.client.opensearch.cluster.HealthResponse;

/**
 * The health part of a {@link ClusterStatus}.
 *
 * @param schemaVersion    version of this part's structure, raised when its fields change
 * @param status           {@code green}, {@code yellow} or {@code red}, as reported by OpenSearch
 * @param numberOfNodes    nodes in the cluster
 * @param unassignedShards shards not assigned to any node
 */
public record HealthStatus(
        int schemaVersion,
        String status,
        int numberOfNodes,
        int unassignedShards) {

    /**
     * The schema version of health parts created by this code.
     */
    public static final int SCHEMA_VERSION = 1;

    /**
     * @throws IllegalArgumentException if status is null or blank
     */
    public HealthStatus {
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("status must not be blank");
        }
    }

    static HealthStatus of(HealthResponse healthResponse) {
        return new HealthStatus(
                SCHEMA_VERSION,
                healthResponse.status().jsonValue(),
                healthResponse.numberOfNodes(),
                healthResponse.unassignedShards());
    }
}
