import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.GeoDistanceType;
import co.elastic.clients.elasticsearch._types.GeoLocation;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;

import java.util.Map;

public class GeoTest {
    private Query.Builder q;

    public static void main(String[] Args) throws Exception {
        // -----Create the low-level client-----
        RestClient restClient = RestClient.builder(
                new HttpHost("localhost", 9200)).build();
        // -----Create the transport with a Jackson mapper-----
        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());
        // -----And create the API client-----
        ElasticsearchClient client = new ElasticsearchClient(transport);
        // -----Bouding box query-----
        SearchResponse<? extends Map> boundingSearch = client.search(s -> s
                        .index("geo_index")
                        .query(q -> q.geoBoundingBox(g -> g
                                .field("location")
                                .boundingBox(box ->
                                        box.tlbr(t ->
                                                t.topLeft(GeoLocation.of(location -> location.latlon(value ->
                                                        value.lat(31.265395).lon(121.444075))))
                                                        .bottomRight(GeoLocation.of(location -> location.latlon(value ->
                                                                value.lat(31.253845).lon(121.468417))))))))
                , Map.class);
        boundingSearch.hits().hits().forEach(x -> System.out.println(x.source().get("building_name")));
        // -----Circular area query-----
        SearchResponse<? extends Map> circularSearch = client.search(s -> s
                        .index("geo_index")
                        .query(q -> q.geoDistance(g -> g
                                .field("location")
                                .distanceType(GeoDistanceType.Arc)
                                .distance("600m")
                                .location(GeoLocation.of(location -> location.latlon(v ->
                                        v.lon(121.462311).lat(31.256224))))))
                , Map.class);
        circularSearch.hits().hits().forEach(x -> System.out.println(x.source()));
        // -----Close the client-----
        restClient.close();
    }
}
