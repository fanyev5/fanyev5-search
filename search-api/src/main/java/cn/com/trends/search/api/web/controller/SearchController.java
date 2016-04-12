package cn.com.trends.search.api.web.controller;

import cn.com.fanyev5.baseservice.es.exception.ParameterParseException;
import cn.com.fanyev5.baseservice.es.model.search.*;
import cn.com.fanyev5.baseservice.es.util.ESUtil;
import cn.com.fanyev5.basecommons.codec.CodecUtil;
import cn.com.fanyev5.basecommons.codec.JSONUtil;
import cn.com.fanyev5.search.constant.enums.APICodeEnum;
import cn.com.fanyev5.search.model.view.SearchDataView;
import cn.com.fanyev5.search.service.config.IConfigService;
import cn.com.fanyev5.search.service.search.ISearchService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 搜索Controller
 *
 * @author fanqi427@gmail.com
 * @since 2013-12-18
 */
@Controller
@RequestMapping("/s")
public class SearchController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchController.class);

    /**
     * 配置服务
     */
    @Autowired
    private IConfigService configService;

    /**
     * 搜索服务
     */
    @Autowired
    private ISearchService searchService;

    private static final Pattern REGEX_NUM = Pattern.compile("\\d+");

    @RequestMapping
    public ModelAndView getAd(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView("/content/json");
        SearchDataView searchDataView = new SearchDataView();
        try {
            // 获取参数
            String key = request.getParameter("key");
            String pageIndexStr = request.getParameter("p");
            String pageSizeStr = request.getParameter("n");

            if (!(configService.isExistIndex(key)
                    && StringUtils.isNotBlank(pageIndexStr) && REGEX_NUM.matcher(pageIndexStr).matches()
                    && StringUtils.isNotBlank(pageSizeStr) && REGEX_NUM.matcher(pageSizeStr).matches())) {
                searchDataView.setCode(APICodeEnum.ERR_PARAM.getIndex());
            } else {
                String searchKeyStrs = CodecUtil.decodeBase64FixUri(request.getParameter("q"));
                String sortStrs = CodecUtil.decodeBase64FixUri(request.getParameter("srts"));
                String facetStrs = CodecUtil.decodeBase64FixUri(request.getParameter("fets"));
                String filterStrs = CodecUtil.decodeBase64FixUri(request.getParameter("fers"));
                String timStrs = CodecUtil.decodeBase64FixUri(request.getParameter("tims"));

                // 解析参数
                int pageIndex = Integer.parseInt(pageIndexStr);
                int pageSize = Integer.parseInt(pageSizeStr);
                List<SearchKeyInfo> searchKeyInfos = ESUtil.parseSearchKeys(searchKeyStrs);
                List<SortInfo> sortInfos = ESUtil.parseSorts(sortStrs);
                List<AbstractFacetInfo> facetInfos = ESUtil.parseFacets(facetStrs);
                List<AbstractFilterInfo> filterInfos = ESUtil.parseFilters(filterStrs);
                List<ValueTrimInfo> trimInfos = ESUtil.parseValueTrims(timStrs);

                // 查询
                ResultInfo info = searchService.query(key, searchKeyInfos, sortInfos, facetInfos, filterInfos, trimInfos, pageIndex, pageSize);
                searchDataView.setCode(APICodeEnum.SUCC.getIndex());
                searchDataView.setData(info);
            }
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
