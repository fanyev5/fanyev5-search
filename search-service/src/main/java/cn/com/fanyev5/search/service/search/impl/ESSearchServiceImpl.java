package cn.com.fanyev5.search.service.search.impl;

import cn.com.fanyev5.baseservice.base.config.xml.es.ESConfigs;
import cn.com.fanyev5.baseservice.es.exception.ESException;
import cn.com.fanyev5.baseservice.es.model.search.*;
import cn.com.fanyev5.baseservice.es.util.ESUtil;
import cn.com.fanyev5.search.constant.SearchConst;
import cn.com.fanyev5.search.service.config.IConfigService;
import cn.com.fanyev5.search.service.search.ISearchService;

import org.elasticsearch.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * ES搜索服务接口实现
 *
 * @author fanqi427@gmail.com
 * @since 2013-12-18
 */
@Service
public class ESSearchServiceImpl implements ISearchService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ESSearchServiceImpl.class);

    /**
     * 配置服务
     */
    @Autowired
    private IConfigService configService;

    @Override
    public ResultInfo query(String indexKey
            , List<SearchKeyInfo> searchKeyInfos, List<SortInfo> sortInfos, List<AbstractFacetInfo> facetInfos, List<AbstractFilterInfo> filterInfos
            , List<ValueTrimInfo> trimInfos, int pageIndex, int pageSize) {
        // 强制验证
        if (pageSize <= 0 || pageSize > SearchConst.DEFAULT_PAGE_MAX_SIZE) {
            pageSize = SearchConst.DEFAULT_PAGE_SIZE;
        }
        if (pageIndex <= 0) {
            pageIndex = SearchConst.DEFAULT_PAGE_INDEX;
        }
        // 通过Key获取索引配置信息
        ESConfigs.IndexElement indexElement = configService.getIndex(indexKey);
        try {
            // 获取 ES Client
            Client client = configService.getESClient(indexElement.getEs());
            // 检索
            ResultInfo resultInfo = ESUtil.query(client, indexElement.getIndexName(), indexElement.getIndexType(), searchKeyInfos, sortInfos, facetInfos, filterInfos, pageIndex, pageSize);
            // 控制返回结果属性长度
            if (trimInfos != null && !trimInfos.isEmpty()) {
                for (ValueTrimInfo trimInfo : trimInfos) {
                    if (trimInfo.getLength() > 0) {
                        for (Map<String, Object> objMap : resultInfo.getItems()) {
                            String value = (String) objMap.get(trimInfo.getFieldName());
                            if (value.length() > trimInfo.getLength()) {
                                objMap.put(trimInfo.getFieldName(), value.substring(0, trimInfo.getLength()));
                            }
                        }
                    }
                }
            }
            return resultInfo;
        } catch (Exception e) {
            throw new ESException("Es query exception.", e);
        }
    }

}
