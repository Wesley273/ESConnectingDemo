import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;

public class IndexTest {
    public static void main(String[] Args) throws Exception {
        // -----Create the low-level client-----
        RestClient restClient = RestClient.builder(
                new HttpHost("localhost", 9200)).build();
        // -----Create the transport with a Jackson mapper-----
        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());
        // -----And create the API client-----
        ElasticsearchClient client = new ElasticsearchClient(transport);
        // -----Create the "products" index-----
        client.indices().create(c -> c.index("products"));
        // -----Check the "products" index-----
        System.out.println("Check Status: " + client.indices().exists(r -> r.index("products")).value());
        // -----Add a alias-----
        client.indices().putAlias(r -> r.index("products").name("pro"));
        // -----Delete a alias-----
        client.indices().deleteAlias(r -> r.index("products").name("pro"));
        // -----Delete the "products" index-----
        System.out.println("Delete Status: " + client.indices().delete(r -> r.index("products")).acknowledged());
        // -----Close the client-----
        restClient.close();
    }
}
