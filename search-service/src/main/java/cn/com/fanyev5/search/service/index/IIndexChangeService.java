package cn.com.fanyev5.search.service.index;

import cn.com.fanyev5.search.model.index.IndexChangeRule;

/**
 * 索引变更服务接口
 *
 * @author fanqi427@gmail.com
 * @since 2013-12-19
 */
public interface IIndexChangeService {

    /**
     * 发送变更信息
     *
     * @param indexChangeRules 变更规则内容集
     */
    void sendChange(IndexChangeRule... indexChangeRules);
}
