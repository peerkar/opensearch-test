package com.example.opensearch.cluster;

import org.opensearch.client.opensearch.indices.resolve_index.ResolveIndexItem;
import org.opensearch.client.opensearch.indices.stats.IndicesStats;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * The index part of a {@link ClusterStatus}.
 *
 * <p>Version 2 replaced the list of index names with a list of {@link UserIndex} objects.</p>
 *
 * @param schemaVersion version of this part's structure, raised when its fields change
 * @param userIndices   the indices created by users, sorted by name
 */
public record IndexStatus(
        int schemaVersion,
        List<UserIndex> userIndices) {

    /**
     * The schema version of index parts created by this code.
     */
    public static final int SCHEMA_VERSION = 2;

    /**
     * @throws IllegalArgumentException if userIndices is null
     */
    public IndexStatus {
        if (userIndices == null) {
            throw new IllegalArgumentException("userIndices must not be null");
        }
        userIndices = userIndices.stream().sorted(Comparator.comparing(UserIndex::name)).toList();
    }

    /**
     * An index created by a user.
     *
     * @param name          the index name
     * @param documentCount documents in the primary shards, or null when unknown (e.g. the index is closed)
     */
    public record UserIndex(String name, Long documentCount) {

        /**
         * @throws IllegalArgumentException if name is null or blank
         */
        public UserIndex {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("name must not be blank");
            }
        }
    }

    // System and plugin indices are hidden or, by convention, have names starting with a dot.
    static IndexStatus of(List<ResolveIndexItem> resolveIndexItems, Map<String, IndicesStats> indicesStatsByName) {
        List<UserIndex> userIndices = resolveIndexItems.stream()
                .filter(resolveIndexItem -> !resolveIndexItem.attributes().contains("hidden"))
                .map(ResolveIndexItem::name)
                .filter(name -> !name.startsWith("."))
                .map(name -> new UserIndex(name, documentCount(indicesStatsByName.get(name))))
                .toList();

        return new IndexStatus(SCHEMA_VERSION, userIndices);
    }

    private static Long documentCount(IndicesStats indicesStats) {
        if (indicesStats == null || indicesStats.primaries() == null || indicesStats.primaries().docs() == null) {
            return null;
        }
        return indicesStats.primaries().docs().count();
    }
}
