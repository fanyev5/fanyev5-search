package cn.com.fanyev5.search.model.index;

import cn.com.fanyev5.search.constant.enums.IndexChangeEnum;

/**
 * 索引变更规则
 *
 * @author fanqi427@gmail.com
 * @since 2013-12-19
 */
public class IndexChangeRule {

    private String indexKey;

    private IndexChangeEnum change;

    private String indexId;

    public IndexChangeRule() {
    }

    public IndexChangeRule(String indexKey, IndexChangeEnum change) {
        this.indexKey = indexKey;
        this.change = change;
    }

    public IndexChangeRule(String indexKey, IndexChangeEnum change, String indexId) {
        this.indexKey = indexKey;
        this.change = change;
        this.indexId = indexId;
    }

    public String getIndexKey() {
        return indexKey;
    }

    public void setIndexKey(String indexKey) {
        this.indexKey = indexKey;
    }

    public IndexChangeEnum getChange() {
        return change;
    }

    public void setChange(IndexChangeEnum change) {
        this.change = change;
    }

    public String getIndexId() {
        return indexId;
    }

    public void setIndexId(String indexId) {
        this.indexId = indexId;
    }
}
