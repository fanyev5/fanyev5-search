package cn.com.fanyev5.search.constant.enums;

import cn.com.fanyev5.basecommons.enums.IndexedEnum;

import java.util.List;


/**
 * API code枚举
 *
 * @author fanqi427@gmail.com
 * @since 2013-6-18
 */
public enum APICodeEnum implements IndexedEnum {

    /**
     * 成功
     */
    SUCC(0),
    /**
     * 系统错误
     */
    ERR_SYS(1),
    /**
     * 参数错误
     */
    ERR_PARAM(2),
    /**
     * 验证错误
     */
    ERR_VALIDATE(4);

    private static final List<APICodeEnum> INDEXS = IndexedEnumUtil.toIndexes(APICodeEnum.values());

    /**
     * 索引
     */
    private final int index;

    APICodeEnum(int index) {
        this.index = index;
    }

    @Override
    public int getIndex() {
        return index;
    }

    /**
     * 根据index取得相应的Enum
     *
     * @param index
     * @return
     */
    public static APICodeEnum indexOf(final int index) {
        return IndexedEnumUtil.valueOf(INDEXS, index);
    }

}
