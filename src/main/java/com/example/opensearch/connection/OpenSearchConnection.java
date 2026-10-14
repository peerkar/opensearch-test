package com.example.opensearch.connection;

import com.example.opensearch.configuration.OpenSearchConfiguration;
import org.apache.hc.client5.http.auth.CredentialsProvider;
import org.apache.hc.client5.http.impl.auth.CredentialsProviderBuilder;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.OpenSearchTransport;
import org.opensearch.client.transport.httpclient5.ApacheHttpClient5TransportBuilder;

import java.io.IOException;

/**
 * An open connection to an OpenSearch service.
 */
public class OpenSearchConnection implements AutoCloseable {

    /**
     * Creates the HTTP transport and client.
     *
     * @param openSearchConfiguration the service endpoint and credentials
     */
    public OpenSearchConnection(OpenSearchConfiguration openSearchConfiguration) {
        this.openSearchTransport = createOpenSearchTransport(openSearchConfiguration);
        this.openSearchClient = new OpenSearchClient(openSearchTransport);
    }

    /**
     * Returns the client for this connection.
     *
     * @return the OpenSearch client
     */
    public OpenSearchClient getClient() {
        return openSearchClient;
    }

    private static OpenSearchTransport createOpenSearchTransport(OpenSearchConfiguration openSearchConfiguration) {

        CredentialsProvider credentialsProvider = CredentialsProviderBuilder.create()
                .add(openSearchConfiguration.host(), openSearchConfiguration.username(), openSearchConfiguration.password())
                .build();

        return ApacheHttpClient5TransportBuilder.builder(openSearchConfiguration.host())
                .setHttpClientConfigCallback(httpAsyncClientBuilder -> httpAsyncClientBuilder
                        .setDefaultCredentialsProvider(credentialsProvider))
                .build();
    }

    /**
     * Closes the transport.
     *
     * @throws IOException if the transport fails to shut down cleanly
     */
    @Override
    public void close() throws IOException {
        openSearchTransport.close();
    }

    private final OpenSearchClient openSearchClient;
    private final OpenSearchTransport openSearchTransport;

}
