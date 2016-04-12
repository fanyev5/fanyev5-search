package cn.com.fanyev5.search.service.index.impl;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.Set;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;

import cn.com.fanyev5.baseservice.base.config.xml.es.ESConfigs;
import cn.com.fanyev5.baseservice.es.model.index.ESFieldInfo;
import cn.com.fanyev5.search.service.config.IConfigService;
import cn.com.fanyev5.search.service.index.IIndexService;

/**
 * ES索引服务接口实现
 *
 * @author fanqi427@gmail.com
 * @since 2013-12-17
 */
@Service
public class ESIndexServiceImpl implements IIndexService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ESIndexServiceImpl.class);

	/**
	 * 配置服务
	 */
	@Autowired
	private IConfigService configService;

	/**
	 * JDBC批量获取大小
	 */
	private int JDBC_BATCH_SIZE = 5000;

	@Override
	public boolean buildIndex(Set<String> indexes) {
		try {
			// 多索引
			for (String key : indexes) {
				LOGGER.info("ES index build... :" + key);
				ESConfigs.IndexElement indexElement = configService.getIndex(key);
				ESConfigs.DataSource dataSourceConfig = indexElement.getDataSource();
				ESFieldInfo esFieldInfo = new ESFieldInfo(indexElement.getFields());

				if ("jdbc".equalsIgnoreCase(dataSourceConfig.getType())) {
					Client client = configService.getESClient(indexElement.getEs());
					JdbcTemplate jdbcTemplate = configService.getJdbcTemplate(key);
					for (int offset = 0;; offset += JDBC_BATCH_SIZE) {
						// 批处理构建索引
						BulkRequestBuilder bulkRequest = client.prepareBulk();
						// DB中获取数据
						String sql = String.format("%s limit ?,?", dataSourceConfig.getJdbcQueryLoad());
						SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, offset, JDBC_BATCH_SIZE);
						while (sqlRowSet.next()) {
							// 拼装索引Field集
							Map<String, Object> fieldMap = Maps.newHashMap();
							fieldMap.put(esFieldInfo.getIdElement().getName(),
									sqlRowSet.getObject(esFieldInfo.getIdElement().getName()));

							for (ESConfigs.FieldElement fieldElement : esFieldInfo.getFieldElements()) {
								//判断field的数据类型，mapping后add
								if (fieldElement.getType() != null) {
									if ("float".equals(fieldElement.getType().toLowerCase())) {
										fieldMap.put(fieldElement.getName(),
												Float.parseFloat(sqlRowSet.getObject(fieldElement.getName()) + ""));
									}
								} else
									fieldMap.put(fieldElement.getName(), sqlRowSet.getObject(fieldElement.getName()));

							}
							bulkRequest.add(client
									.prepareIndex(indexElement.getIndexName(), indexElement.getIndexType(),
											sqlRowSet.getString(esFieldInfo.getIdElement().getName()))
									.setSource(fieldMap));
						}
						// 构建索引
						if (bulkRequest.numberOfActions() > 0) {
							LOGGER.info("ES create index size: " + bulkRequest.numberOfActions());
							bulkRequest.execute();
						}
						if (bulkRequest.numberOfActions() < JDBC_BATCH_SIZE) {
							break;
						}
					}
				} else {
					throw new IllegalArgumentException("Index data source type. " + dataSourceConfig.getType());
				}
				LOGGER.info("ES index build end. :" + key);
			}
		} catch (Exception e) {
			LOGGER.error("ES build indexes exception.", e);
			return false;
		}
		return true;
	}

	@Override
	public boolean cleanIndex(Set<String> indexSet) {
		// 多索引
		try {
			for (String key : indexSet) {
				LOGGER.info("ES index clear... :" + key);
				ESConfigs.IndexElement indexElement = configService.getIndex(key);
				Client client = configService.getESClient(indexElement.getEs());
				// 删除指定索引
				client.admin().indices().prepareDelete(indexElement.getIndexName()).execute();
			}
		} catch (Exception e) {
			LOGGER.error("ES clear indexes exception.", e);
			return false;
		}
		return true;
	}
}
