package cn.com.fanyev5.search.service.config.impl;

import cn.com.fanyev5.baseservice.base.config.xml.es.ESConfigs;
import cn.com.fanyev5.baseservice.es.util.ESUtil;
import cn.com.fanyev5.search.constant.SearchConst;
import cn.com.fanyev5.search.service.config.IConfigService;
import cn.com.fanyev5.basecommons.xml.JAXBUtil;

import com.google.common.cache.*;
import org.apache.commons.lang.StringUtils;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.elasticsearch.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 配置服务接口实现
 *
 * @author fanqi427@gmail.com
 * @since 2013-12-20
 */
@Service
public class ConfigServiceImpl implements IConfigService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigServiceImpl.class);

    /**
     * 索引节点映射
     */
    private Map<String, ESConfigs.IndexElement> indexMap = null;
    /**
     * ES服务节点映射
     */
    private Map<String, ESConfigs.ESElement> esMap = null;
    /**
     * ES客户端映射
     */
    private LoadingCache<String, Client> cacheESClient = null;
    /**
     * DB客户端映射
     */
    private LoadingCache<String, DataSource> cacheDBDataSource = null;
    private LoadingCache<String, JdbcTemplate> cacheJdbcTemplate = null;

    public ConfigServiceImpl() {
        ESConfigs config = JAXBUtil.unmarshal(ESConfigs.class, SearchConst.ES_CONFIG_FILE_NAME);
        indexMap = config.getIndexMap();
        esMap = config.getESMap();

        cacheESClient = CacheBuilder.newBuilder().removalListener(new RemovalListener<String, Client>() {
            @Override
            public void onRemoval(RemovalNotification<String, Client> notification) {
                LOGGER.info("ES client remove. key: " + notification.getKey());
                notification.getValue().close();
            }
        }).build(new CacheESClientLoader(esMap));

        cacheDBDataSource = CacheBuilder.newBuilder().removalListener(new RemovalListener<String, DataSource>() {
            @Override
            public void onRemoval(RemovalNotification<String, DataSource> notification) {
                LOGGER.info("DB datasource remove. key: " + notification.getKey());
                notification.getValue().close();
            }
        }).build(new CacheDBDataSourceLoader(indexMap));

        cacheJdbcTemplate = CacheBuilder.newBuilder().build(new CacheJdbcTemplateLoader());
    }

    @PreDestroy
    private void destroy() {
        // 关闭ES Client
        if (cacheESClient != null) {
            try {

                cacheESClient.invalidateAll();
                cacheESClient = null;
            } catch (Exception e) {
                LOGGER.error("Close ES client exception.", e);
            }
        }

        // 关闭DB datasource
        if (cacheDBDataSource != null) {
            try {
                cacheDBDataSource.invalidateAll();
                cacheDBDataSource = null;
            } catch (Exception e) {
                LOGGER.error("Close DB datasource exception.", e);
            }
        }
    }

    @Override
    public ESConfigs.IndexElement getIndex(String indexName) {
        return indexMap.get(indexName);
    }

    @Override
    public boolean isExistIndex(String indexName) {
        if (StringUtils.isBlank(indexName)) {
            return false;
        }
        return (getIndex(indexName) != null);
    }

    @Override
    public ESConfigs.ESElement getEs(String esName) {
        return esMap.get(esName);
    }

    @Override
    public Client getESClient(String esName) {
        try {
            return cacheESClient.get(esName);
        } catch (ExecutionException e) {
            LOGGER.error("Get ES client exception.", e);
        }
        return null;
    }

    @Override
    public JdbcTemplate getJdbcTemplate(String indexName) {
        try {
            return cacheJdbcTemplate.get(indexName);
        } catch (ExecutionException e) {
            LOGGER.error("Get Jdbc Template exception.", e);
        }
        return null;
    }

    /**
     * ES Client加载实现类
     */
    private class CacheESClientLoader extends CacheLoader<String, Client> {

        private Map<String, ESConfigs.ESElement> esMap = null;

        public CacheESClientLoader(Map<String, ESConfigs.ESElement> esMap) {
            this.esMap = esMap;
        }

        @Override
        public Client load(String key) throws Exception {
            LOGGER.info("ES client create... key: " + key);
            ESConfigs.ESElement element = esMap.get(key);
            return ESUtil.buildClient(element);
        }
    }

    /**
     * DB Datasource加载实现类
     */
    private class CacheDBDataSourceLoader extends CacheLoader<String, DataSource> {

        private Map<String, ESConfigs.IndexElement> indexElementMap = null;

        public CacheDBDataSourceLoader(Map<String, ESConfigs.IndexElement> indexElementMap) {
            this.indexElementMap = indexElementMap;
        }

        @Override
        public DataSource load(String key) throws Exception {
            LOGGER.info("DB source create... key: " + key);
            ESConfigs.DataSource element = indexElementMap.get(key).getDataSource();
            return createTomcatDataSource(element);
        }

        private DataSource createTomcatDataSource(ESConfigs.DataSource dataSourceConf) {
            PoolProperties p = new PoolProperties();
            p.setUrl(dataSourceConf.getJdbcUrl());
            p.setDriverClassName(dataSourceConf.getJdbcDriver());
            p.setUsername(dataSourceConf.getJdbcUser());
            p.setPassword(dataSourceConf.getJdbcPwd());
            p.setJmxEnabled(false);
            p.setTestWhileIdle(false);
            p.setTestOnBorrow(true);
            p.setValidationQuery("SELECT 1");
            p.setTestOnReturn(false);
            p.setValidationInterval(30000);
            p.setTimeBetweenEvictionRunsMillis(30000);
            p.setMaxActive(100);
            p.setInitialSize(1);
            p.setMaxWait(10000);
            p.setRemoveAbandonedTimeout(60);
            p.setMinEvictableIdleTimeMillis(30000);
            p.setMinIdle(10);
            p.setLogAbandoned(true);
            p.setRemoveAbandoned(true);
            return new DataSource(p);
        }
    }

    /**
     * Jdbc Template加载实现类
     */
    private class CacheJdbcTemplateLoader extends CacheLoader<String, JdbcTemplate> {

        @Override
        public JdbcTemplate load(String key) throws Exception {
            LOGGER.info("Jdbc template create... key: " + key);
            return new JdbcTemplate(cacheDBDataSource.get(key));
        }
    }

}
