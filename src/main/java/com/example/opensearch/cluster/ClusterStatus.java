package com.example.opensearch.cluster;

import java.time.Instant;

/**
 * Snapshot of a cluster's state for sending to external monitoring systems.
 *
 * <p>An envelope around the different kinds of status. Each part has its own schema version, so the
 * parts can evolve independently; this record's version covers only the envelope's own fields.
 * Uses only JDK types and this package's records so consumers don't depend on the OpenSearch client.</p>
 *
 * @param schemaVersion version of the envelope's structure, raised when its fields change
 * @param clusterName   the cluster the snapshot was taken from
 * @param collectedAt   when the snapshot was taken (UTC)
 * @param health        the cluster health
 * @param indices       the indices in the cluster
 */
public record ClusterStatus(
        int schemaVersion,
        String clusterName,
        Instant collectedAt,
        HealthStatus health,
        IndexStatus indices) {

    /**
     * The schema version of envelopes created by this code.
     */
    public static final int SCHEMA_VERSION = 1;

    /**
     * @throws IllegalArgumentException if clusterName is null or blank, or any other value is null
     */
    public ClusterStatus {
        if (clusterName == null || clusterName.isBlank()) {
            throw new IllegalArgumentException("clusterName must not be blank");
        }
        if (collectedAt == null) {
            throw new IllegalArgumentException("collectedAt must not be null");
        }
        if (health == null) {
            throw new IllegalArgumentException("health must not be null");
        }
        if (indices == null) {
            throw new IllegalArgumentException("indices must not be null");
        }
    }
}
