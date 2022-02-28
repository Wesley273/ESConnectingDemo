import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;

import java.util.*;

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
        // -----Update a specific doc's field in a index-----
        Map<String, Object> up = new HashMap<>();
        up.put("Update1", "Update");
        up.put("Update2", new Date());
        up.put("Update3", "I'm updated");
        //New fields are added, and old fields are not deleted
        client.update(x -> x.index("products").id("1").doc(up), Map.class);
        // -----Delete some documents in a specific index-----
        client.delete(r -> r.id("1").index("products"));
        client.delete(r -> r.id("2").index("products"));
        Thread.sleep(1000);
        // -----Add documents in bulk-----
        Map<String, Object> c = new HashMap<>();
        c.put("name", "Tom");
        c.put("postDate", new Date());
        c.put("Notice", "Notice that we have different fields");
        Map<String, Object> d = new HashMap<>();
        d.put("username", "Jack");
        d.put("postDate", new Date());
        d.put("tips", "Notice that we have different fields");
        List<Object> maps = new ArrayList<>();
        maps.add(c);
        maps.add(d);
        List<BulkOperation> bulkOperations = new ArrayList<>();
        //Id is not specified here and will be generated automatically
        maps.forEach(map -> bulkOperations.add(BulkOperation.of(r -> r.index(q -> q.document(map)))));
        client.bulk(x -> x.index("products").operations(bulkOperations));
        Thread.sleep(1000);
        // -----Search all data in "products"-----
        System.out.println("Search all docs in products:");
        SearchResponse<? extends Map> searchResponse = client.search(r -> r.index("products"), Map.class);
        searchResponse.hits().hits().forEach(x -> System.out.println(x.source()));
        // -----Conditional search-----
        System.out.println("Conditional search in products:");
        SearchResponse<? extends Map> conditionalSearch = client.search(s -> s
                        .index("products")
                        .query(q -> q
                                .bool(v -> v
                                        .should(l -> l.match(e -> e.field("name").query(f -> f.stringValue("Tom"))))
                                        .should(l -> l.match(e -> e.field("user").query(f -> f.stringValue("Amy"))))))
                , Map.class);
        conditionalSearch.hits().hits().forEach(x -> System.out.println(x.source()));
        // -----Close the client-----
        restClient.close();
    }
}
