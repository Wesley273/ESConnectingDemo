import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.ingest.simulate.Document;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientOptions;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DocumentTest {
    public static void main(String[] Args) throws Exception {
        // -----Create the low-level client-----
        RestClient restClient = RestClient.builder(
                new HttpHost("localhost", 9200)).build();
        // -----Create the transport with a Jackson mapper-----
        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());
        // -----And create the API client-----
        ElasticsearchClient client = new ElasticsearchClient(transport);
        // -----Create a index with a document-----
        Map<String, Object> a = new HashMap<>();
        a.put("user", "Amy");
        a.put("postDate", new Date());
        a.put("message", "trying out Elasticsearch");
        //There we use function create()
        client.create(r -> r.document(a).id("1").index("products"));
        // -----Add a document to an existing index-----
        Map<String, Object> b = new HashMap<>();
        b.put("username", "Wesley");
        b.put("postDate", new Date());
        b.put("tips", "Notice that we have different fields");
        client.index(r -> r.document(b).index("products").id("2"));
        // -----Get a document in a specific index-----
        SearchResponse search=client.search(s -> s
                .index("products")
                .query(q -> q
                        .term(t -> t
                                .field("username")
                                .value(v -> v.stringValue("Wesley"))
                        )), Document.class);
        // -----Delete some documents in a specific index-----
        client.delete(r -> r.id("1").index("products"));
        client.delete(r -> r.id("2").index("products"));

        // -----Add documents in bulk-----
        Map<String, Object> c = new HashMap<>();
        c.put("username", "Wes");
        c.put("postDate", new Date());
        c.put("tips", "Notice that we have different fields");
        Map<String, Object> d = new HashMap<>();
        d.put("username", "Weley");
        d.put("postDate", new Date());
        d.put("tips", "Notice that we have different fields");
        //client.bulk(r -> r.index("products").operations(l -> {
        //    l.index(req -> req.document(c).id("3"));
        //    l.index(req -> req.document(d).id("4"));
        //    return l;
        //}));
        // -----Close the client-----
        restClient.close();
    }
}
