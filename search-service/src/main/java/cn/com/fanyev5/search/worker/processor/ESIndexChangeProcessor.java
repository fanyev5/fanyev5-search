package cn.com.fanyev5.search.worker.processor;

import cn.com.fanyev5.baseservice.base.config.xml.es.ESConfigs;
import cn.com.fanyev5.baseservice.kafka.processor.IKafkaMessageProcessor;
import cn.com.fanyev5.search.model.index.IndexChangeRule;
import cn.com.fanyev5.search.service.config.IConfigService;
import cn.com.fanyev5.search.service.index.IIndexService;
import cn.com.fanyev5.search.service.index.impl.ESIndexServiceImpl;
import cn.com.fanyev5.basecommons.codec.JSONUtil;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

/**
 * 索引变更Processor
 *
 * @author fanqi427@gmail.com
 * @since 2013-12-19
 */
public class ESIndexChangeProcessor implements IKafkaMessageProcessor<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ESIndexServiceImpl.class);

    private static final TypeReference<IndexChangeRule[]> TYPEREFERENCE_INDEX_CHANGE = new TypeReference<IndexChangeRule[]>() {
    };

    /**
     * 配置服务
     */
    @Autowired
    private IConfigService configService;

    /**
     * 索引服务
     */
    @Autowired
    private IIndexService indexService;

    @Override
    public boolean process(String topic, String msg) {
        if (StringUtils.isBlank(msg)) {
            return false;
        }
        try {
            boolean isSucc = true;
            IndexChangeRule[] changeRules = JSONUtil.json2Obj(msg, TYPEREFERENCE_INDEX_CHANGE);

            Map<String, BulkRequestBuilder> requestBuilderMap = Maps.newHashMap();
            for (IndexChangeRule changeRule : changeRules) {
                ESConfigs.IndexElement indexElement = configService.getIndex(changeRule.getIndexKey());
                switch (changeRule.getChange()) {
                    case ADD:
                    case UPDATE:
                        // 获取数据
                        ESConfigs.DataSource dataSource = indexElement.getDataSource();
                        if ("jdbc".equalsIgnoreCase(dataSource.getType())) {
                            JdbcTemplate jdbcTemplate = configService.getJdbcTemplate(changeRule.getIndexKey());
                            Map<String, Object> data = jdbcTemplate.queryForMap(dataSource.getJdbcQueryOne(), changeRule.getIndexId());
                            BulkRequestBuilder requestBuilder = getBulkRequest(requestBuilderMap, indexElement.getEs());
                            requestBuilder.add(new IndexRequest(indexElement.getIndexName(), indexElement.getIndexType(), changeRule.getIndexId()).source(data));
                        } else {
                            throw new IllegalArgumentException("Data source type");
                        }
                        // 更新索引
                        break;
                    case DELETE:
                        // 删除索引
                        BulkRequestBuilder requestBuilder = getBulkRequest(requestBuilderMap, indexElement.getEs());
                        requestBuilder.add(new DeleteRequest(indexElement.getIndexName(), indexElement.getIndexType(), changeRule.getIndexId()));
                        break;
                    case DELETE_INDEX:
                        // 删除整个索引
                        isSucc = indexService.cleanIndex(Sets.newHashSet(changeRule.getIndexKey()));
                        break;
                    case LOAD_INDEX:
                        // 加载整个索引
                        isSucc = indexService.buildIndex(Sets.newHashSet(changeRule.getIndexKey()));
                        break;
                }
            }

            // 批量执行ES索引变更
            if (!requestBuilderMap.isEmpty()) {
                for (BulkRequestBuilder bulkRequestBuilder : requestBuilderMap.values()) {
                    if (bulkRequestBuilder.numberOfActions() > 0) {
                        BulkResponse response = bulkRequestBuilder.execute().actionGet();
                        if (response.hasFailures()) {
                            isSucc = false;
                        }
                    }
                }
            }
            return isSucc;
        } catch (Exception e) {
            LOGGER.error("Index change exception.", e);
            return false;
        }
    }

    private BulkRequestBuilder getBulkRequest(Map<String, BulkRequestBuilder> requestBuilderMap, String key) {
        BulkRequestBuilder requestBuilder = requestBuilderMap.get(key);
        if (requestBuilder == null) {
            Client client = configService.getESClient(key);
            requestBuilder = client.prepareBulk();
            requestBuilderMap.put(key, requestBuilder);
        }
        return requestBuilder;
    }
}