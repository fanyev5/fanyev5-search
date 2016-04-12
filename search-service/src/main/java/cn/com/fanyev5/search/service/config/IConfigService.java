package cn.com.fanyev5.search.service.config;

import org.elasticsearch.client.Client;
import org.springframework.jdbc.core.JdbcTemplate;

import cn.com.fanyev5.baseservice.base.config.xml.es.ESConfigs;

/**
 * 配置服务接口
 *
 * @author fanqi427@gmail.com
 * @since 2013-12-20
 */
public interface IConfigService {

    /**
     * 获取索引节点配置
     *
     * @param indexName 索引Name
     * @return
     */
    ESConfigs.IndexElement getIndex(String indexName);

    /**
     * 索引是否存在
     *
     * @param indexName 索引Name
     * @return
     */
    boolean isExistIndex(String indexName);

    /**
     * 获取ES节点配置
     *
     * @param esName ES Name
     * @return
     */
    ESConfigs.ESElement getEs(String esName);

    /**
     * 获取ES Client
     *
     * @param esName ES Name
     * @return
     */
    Client getESClient(String esName);

    /**
     * 获取索引对应JdbcTemplate
     *
     * @param indexName 索引Name
     * @return
     */
    JdbcTemplate getJdbcTemplate(String indexName);
}
