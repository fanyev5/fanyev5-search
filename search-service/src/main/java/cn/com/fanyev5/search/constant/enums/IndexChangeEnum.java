package cn.com.fanyev5.search.constant.enums;

/**
 * 索引变更枚举
 *
 * @author fanqi427@gmail.com
 * @since 2013-6-18
 */
public enum IndexChangeEnum {
    ADD,
    DELETE,
    UPDATE,
    /**
     * 加载整个索引
     */
    LOAD_INDEX,
    /**
     * 删除整个索引
     */
    DELETE_INDEX
}
