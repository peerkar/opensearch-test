package com.example.opensearch.cluster;

import com.example.opensearch.cluster.IndexStatus.UserIndex;
import org.junit.jupiter.api.Test;
import org.opensearch.client.opensearch.indices.resolve_index.ResolveIndexItem;
import org.opensearch.client.opensearch.indices.stats.IndicesStats;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class IndexStatusTest {

    @Test
    void ofExcludesHiddenAndDotPrefixedIndices() {
        List<ResolveIndexItem> resolveIndexItems = List.of(
                resolveIndexItem("orders", "open"),
                resolveIndexItem(".opendistro_security", "open"),
                resolveIndexItem("top_queries-2026.10.27", "open", "hidden"),
                resolveIndexItem("customers", "open"));

        IndexStatus indexStatus = IndexStatus.of(resolveIndexItems, Map.of());

        assertEquals(List.of("customers", "orders"),
                indexStatus.userIndices().stream().map(UserIndex::name).toList());
    }

    @Test
    void ofSetsDocumentCountAndNullWhenStatsMissing() {
        List<ResolveIndexItem> resolveIndexItems = List.of(
                resolveIndexItem("orders", "open"),
                resolveIndexItem("archive", "closed"));
        Map<String, IndicesStats> indicesStatsByName = Map.of("orders", indicesStats(42));

        IndexStatus indexStatus = IndexStatus.of(resolveIndexItems, indicesStatsByName);

        assertEquals(new IndexStatus(IndexStatus.SCHEMA_VERSION, List.of(
                new UserIndex("archive", null),
                new UserIndex("orders", 42L))), indexStatus);
    }

    private static ResolveIndexItem resolveIndexItem(String name, String... attributes) {
        return ResolveIndexItem.of(builder -> builder.name(name).attributes(List.of(attributes)));
    }

    private static IndicesStats indicesStats(long documentCount) {
        return IndicesStats.of(builder -> builder
                .uuid("test-uuid")
                .primaries(indexStats -> indexStats.docs(docStats -> docStats.count(documentCount)))
                .total(indexStats -> indexStats.docs(docStats -> docStats.count(documentCount * 2))));
    }
}
