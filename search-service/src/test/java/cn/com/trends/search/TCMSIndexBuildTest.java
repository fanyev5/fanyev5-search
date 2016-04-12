package cn.com.trends.search;

import cn.com.fanyev5.basecommons.codec.JSONUtil;
import junit.framework.TestCase;
import org.apache.commons.lang.time.DateUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.AndFilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.facet.Facet;
import org.elasticsearch.search.facet.FacetBuilder;
import org.elasticsearch.search.facet.FacetBuilders;
import org.elasticsearch.search.facet.Facets;
import org.elasticsearch.search.facet.range.RangeFacet;
import org.elasticsearch.search.facet.terms.TermsFacet;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Date;
import java.util.Map;

/**
 * ES API Test
 *
 * @author fanqi427@gmail.com
 * @since 2013-09-12
 */
public class TCMSIndexBuildTest {

    private Client client = null;

    @Before
    public void before() {
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("cluster.name", "elasticsearch")
                .put("client.transport.sniff", true)
                .build();
        client = new TransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress("192.1.1.101", 9300))
                .addTransportAddress(new InetSocketTransportAddress("192.1.1.101", 9301))
        ;
    }

    @After
    public void after() {
        if (client != null) {
            client.close();
        }
    }

    @Test
    @Ignore
    public void testSearchResponseByFacet() {
        FacetBuilder facetCatidBuilder = FacetBuilders.termsFacet("catidFacet").field("cid").size(Integer.MAX_VALUE);
        Date date = new Date();
        long time0 = date.getTime() / 1000;
        long time1 = DateUtils.addDays(date, -7).getTime() / 1000;
        long time2 = DateUtils.addMonths(date, -1).getTime() / 1000;
        long time3 = DateUtils.addMonths(date, -3).getTime() / 1000;
        long time4 = DateUtils.addYears(date, -1).getTime() / 1000;
        FacetBuilder facetTimeBuilder = FacetBuilders.rangeFacet("timeFacet").field("timed")
                .addRange(time1, time0)
                .addRange(time2, time0)
                .addRange(time3, time0)
                .addRange(time4, time0)
                .addRange(0, time4);
        AndFilterBuilder andFilterBuilder = FilterBuilders.andFilter(FilterBuilders.termFilter("cid", 235));
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch("tcms").setTypes("wenzhang")
                .addFacet(facetCatidBuilder.facetFilter(andFilterBuilder))
                .addFacet(facetTimeBuilder.facetFilter(andFilterBuilder))
                .setQuery(QueryBuilders.boolQuery()
                        .must(QueryBuilders.fieldQuery("tle", "小虫"))
                        .must(QueryBuilders.fieldQuery("tle", "戒指")))
                .addSort(SortBuilders.fieldSort("timed").order(SortOrder.DESC))
                .setFilter(andFilterBuilder)
                .setFrom(0).setSize(30);
        SearchResponse response = searchRequestBuilder.execute().actionGet();
        Facets facets = response.getFacets();
        if (facets != null) {
            for (Map.Entry<String, Facet> facetEntry : facets.getFacets().entrySet()) {
                System.out.print("==> facet: " + facetEntry.getKey() + " > ");
                if (facetEntry.getValue() instanceof TermsFacet) {
                    for (TermsFacet.Entry entry : ((TermsFacet) facetEntry.getValue()).getEntries()) {
                        System.out.print(String.format("{%s:%s}", entry.getTerm(), entry.getCount()));
                    }
                } else if (facetEntry.getValue() instanceof RangeFacet) {
                    for (RangeFacet.Entry entry : ((RangeFacet) facetEntry.getValue()).getEntries()) {
                        System.out.print(String.format("{%s-%s:%s}", entry.getTo(), entry.getFrom(), entry.getCount()));
                    }

                } else {
                    throw new RuntimeException("Facet type exception. class: " + facetEntry);
                }
                System.out.println();
            }
        }
        System.out.println(String.format("==> [fieldQuery] total: %s, time: %s", response.getHits().getTotalHits(), response.getTookInMillis()));
        for (SearchHit hit : response.getHits().hits()) {
            System.out.println(String.format("id:%s, score:%s, source:%s", hit.getId(), hit.getScore(), JSONUtil.obj2Json(hit.getSource())));
        }
    }

    @Test
    @Ignore
    public void testCleanIndex() {
        TestCase.assertNotNull(client);
//        client.admin().indices().prepareDelete("tcms").execute().actionGet();
    }

}
