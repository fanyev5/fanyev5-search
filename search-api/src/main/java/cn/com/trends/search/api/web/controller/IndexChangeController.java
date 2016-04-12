package cn.com.trends.search.api.web.controller;

import cn.com.fanyev5.baseservice.es.exception.ParameterParseException;
import cn.com.fanyev5.basecommons.codec.JSONUtil;
import cn.com.fanyev5.search.constant.enums.APICodeEnum;
import cn.com.fanyev5.search.constant.enums.IndexChangeEnum;
import cn.com.fanyev5.search.model.index.IndexChangeRule;
import cn.com.fanyev5.search.model.view.SearchDataView;
import cn.com.fanyev5.search.service.config.IConfigService;
import cn.com.fanyev5.search.service.index.IIndexChangeService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 索引变更Controller
 *
 * @author fanqi427@gmail.com
 * @since 2013-12-18
 */
@Controller
@RequestMapping("/index")
public class IndexChangeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexChangeController.class);

    /**
     * 配置服务
     */
    @Autowired
    private IConfigService configService;

    /**
     * 索引变更服务
     */
    @Autowired
    private IIndexChangeService indexChangeService;

    @RequestMapping
    public ModelAndView changeIndex(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView("/content/json");
        SearchDataView searchDataView = new SearchDataView();
        try {
            // 获取参数
            String key = request.getParameter("key");
            String changeStrs = request.getParameter("c");

            if (!configService.isExistIndex(key) || StringUtils.isBlank(changeStrs)) {
                searchDataView.setCode(APICodeEnum.ERR_PARAM.getIndex());
                modelAndView.addObject("content", JSONUtil.obj2Json(searchDataView));
                return modelAndView;
            }

            String[] changeArr = StringUtils.split(changeStrs, ';');
            IndexChangeRule[] changeRules = new IndexChangeRule[changeArr.length];
            for (int i = 0; i < changeArr.length; i++) {
                String[] itemArr = StringUtils.split(changeArr[i], ',');
                if (itemArr.length < 2) {
                    searchDataView.setCode(APICodeEnum.ERR_PARAM.getIndex());
                    modelAndView.addObject("content", JSONUtil.obj2Json(searchDataView));
                    return modelAndView;
                }

                String type = itemArr[0];
                String id = itemArr[1];
                // 解析参数
                IndexChangeEnum indexChange = IndexChangeEnum.valueOf(StringUtils.upperCase(type));
                if (indexChange == null || StringUtils.isBlank(id)
                        || !(IndexChangeEnum.ADD.equals(indexChange) || IndexChangeEnum.DELETE.equals(indexChange) || IndexChangeEnum.UPDATE.equals(indexChange))) {
                    searchDataView.setCode(APICodeEnum.ERR_PARAM.getIndex());
                    modelAndView.addObject("content", JSONUtil.obj2Json(searchDataView));
                    return modelAndView;
                }
                changeRules[i] = new IndexChangeRule(key, indexChange, id);
            }

            // 发送变更信息
            indexChangeService.sendChange(changeRules);
            searchDataView.setCode(APICodeEnum.SUCC.getIndex());
        } catch (ParameterParseException e) {
            searchDataView.setCode(APICodeEnum.ERR_PARAM.getIndex());
            LOGGER.warn("Controller warn.", e);
        } catch (Exception e) {
            searchDataView.setCode(APICodeEnum.ERR_SYS.getIndex());
            LOGGER.error("Controller exception.", e);
        }
        modelAndView.addObject("content", JSONUtil.obj2Json(searchDataView));
        return modelAndView;
    }
}
