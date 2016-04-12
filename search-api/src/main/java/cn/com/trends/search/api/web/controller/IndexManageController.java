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
 * 索引管理Controller
 * // TODO:此接口为管理接口,慎用
 *
 * @author fanqi427@gmail.com
 * @since 2013-12-18
 */
@Controller
@RequestMapping("/mge/index")
public class IndexManageController {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexManageController.class);

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

    @RequestMapping("load")
    public ModelAndView loadIndex(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView("/content/json");
        SearchDataView searchDataView = new SearchDataView();
        try {
            // 获取参数
            String key = request.getParameter("key");

            if (!configService.isExistIndex(key)) {
                searchDataView.setCode(APICodeEnum.ERR_PARAM.getIndex());
                modelAndView.addObject("content", JSONUtil.obj2Json(searchDataView));
                return modelAndView;
            }

            indexChangeService.sendChange(new IndexChangeRule(key, IndexChangeEnum.LOAD_INDEX));
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

    @RequestMapping("del")
    public ModelAndView deleteIndex(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView("/content/json");
        SearchDataView searchDataView = new SearchDataView();
        try {
            // 获取参数
            String key = request.getParameter("key");

            if (!configService.isExistIndex(key)) {
                searchDataView.setCode(APICodeEnum.ERR_PARAM.getIndex());
                modelAndView.addObject("content", JSONUtil.obj2Json(searchDataView));
                return modelAndView;
            }

            indexChangeService.sendChange(new IndexChangeRule(key, IndexChangeEnum.DELETE_INDEX));
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
