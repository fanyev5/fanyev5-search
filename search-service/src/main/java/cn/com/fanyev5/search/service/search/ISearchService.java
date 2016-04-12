package cn.com.fanyev5.search.service.search;

import java.util.List;

import cn.com.fanyev5.baseservice.es.model.search.*;

/**
 * 搜索服务接口
 *
 * @author fanqi427@gmail.com
 * @since 2013-12-18
 */
public interface ISearchService {

    /**
     * 基于索引Key检索
     *
     * @param indexKey       索引Key
     * @param searchKeyInfos 检索信息集
     * @param sortInfos      排序集
     * @param facetInfos     Facet集
     * @param pageIndex      页码
     * @param pageSize       每页记录数
     * @return 结果集信息
     */
    public ResultInfo query(String indexKey
            , List<SearchKeyInfo> searchKeyInfos, List<SortInfo> sortInfos, List<AbstractFacetInfo> facetInfos, List<AbstractFilterInfo> filterInfos
            , List<ValueTrimInfo> trimInfos, int pageIndex, int pageSize);

}
