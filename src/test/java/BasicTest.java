import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;

import java.io.IOException;

public class BasicTest {
    public static void main(String[] Args) {
        // Create the low-level client
        RestClient restClient = RestClient.builder(
                new HttpHost("localhost", 9200)).build();
        // Create the transport with a Jackson mapper
        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());
        // And create the API client
        ElasticsearchClient client = new ElasticsearchClient(transport);
        // Create the "products" index
        try {
            System.out.println("Create Status: " + client.indices().create(c -> c.index("products")).acknowledged());
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Check the "products" index
        try {
            System.out.println("Check Status: " + client.indices().exists(r -> r.index("products")).value());
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Delete the "products" index
        try {
            System.out.println("Delete Status: " + client.indices().delete(r -> r.index("products")).acknowledged());
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Close the client
        try {
            restClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
