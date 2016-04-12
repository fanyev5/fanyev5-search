package cn.com.fanyev5.search.model.view;

import cn.com.fanyev5.baseservice.es.model.search.ResultInfo;

/**
 * 搜索结果View
 *
 * @author fanqi427@gmail.com
 * @since 2013-12-18
 */
public class SearchDataView {

    private Status status = new Status();

    private ResultInfo data;

    public void setCode(int code) {
        status.setCode(code);
    }

    public void setDesc(String desc) {
        status.setDesc(desc);
    }

    public Status getStatus() {
        return status;
    }

    public ResultInfo getData() {
        return data;
    }

    public void setData(ResultInfo data) {
        this.data = data;
    }

    /**
     * 状态封装
     */
    public static class Status {

        private int code = -1;

        private String desc;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

    }
}
