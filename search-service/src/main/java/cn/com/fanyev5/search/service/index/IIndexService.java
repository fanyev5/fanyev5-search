package cn.com.fanyev5.search.service.index;

import java.util.Set;

/**
 * 索引服务接口
 *
 * @author fanqi427@gmail.com
 * @since 2013-12-17
 */
public interface IIndexService {

    /**
     * 构建索引
     *
     * @param indexes 索引名,对应ESconfig中属性
     * @return
     */
    public boolean buildIndex(Set<String> indexes);

    /**
     * 清除索引
     *
     * @param indexes 索引名,对应ESconfig中属性
     * @return
     */
    public boolean cleanIndex(Set<String> indexes);
}
