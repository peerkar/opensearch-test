package com.example.opensearch;

import com.example.opensearch.configuration.OpenSearchConfiguration;
import com.example.opensearch.connection.OpenSearchConnection;
import com.example.opensearch.exception.OpenSearchConfigurationException;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.OpenSearchException;
import org.opensearch.client.opensearch.core.InfoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * The main program
 */
public class Main {

    static void main() {
        try (OpenSearchConnection openSearchConnection = new OpenSearchConnection(OpenSearchConfiguration.fromEnv())) {
            System.out.println("The cluster version is " + getClusterVersion(openSearchConnection));
        } catch (OpenSearchConfigurationException openSearchConfigurationException) {
            log.error("Invalid configuration: {}", openSearchConfigurationException.getMessage());
            System.exit(1);
        } catch (IOException | OpenSearchException exception) {
            log.error("Cannot connect to OpenSearch: {}", exception.getMessage());
            log.debug("Details", exception);
            System.exit(1);
        }
    }

    private static String getClusterVersion(OpenSearchConnection openSearchConnection) throws IOException {
        OpenSearchClient openSearchClient = openSearchConnection.getClient();

        InfoResponse infoResponse = openSearchClient.info();

        return infoResponse.version().number();
    }

    private static final Logger log = LoggerFactory.getLogger(Main.class);
}
