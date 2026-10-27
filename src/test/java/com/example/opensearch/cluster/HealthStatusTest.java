package com.example.opensearch.cluster;

import org.junit.jupiter.api.Test;
import org.opensearch.client.opensearch.cluster.HealthResponse;

import static org.junit.jupiter.api.Assertions.*;

class HealthStatusTest {

    @Test
    void ofCopiesFields() {
        HealthResponse healthResponse = HealthResponse.of(builder -> builder
                .clusterName("test-cluster")
                .status(org.opensearch.client.opensearch._types.HealthStatus.Yellow)
                .timedOut(false)
                .numberOfNodes(3)
                .numberOfDataNodes(2)
                .activePrimaryShards(5)
                .activeShards(9)
                .activeShardsPercentAsNumber(90.0)
                .relocatingShards(0)
                .initializingShards(0)
                .unassignedShards(4)
                .delayedUnassignedShards(0)
                .numberOfPendingTasks(0)
                .numberOfInFlightFetch(0)
                .taskMaxWaitingInQueueMillis(0L));

        HealthStatus healthStatus = HealthStatus.of(healthResponse);

        assertEquals(new HealthStatus(HealthStatus.SCHEMA_VERSION, "yellow", 3, 4), healthStatus);
    }
}
