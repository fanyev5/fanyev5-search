package cn.com.trends.search.service.search.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;

import cn.com.fanyev5.baseservice.es.constant.enums.SortOrderEnum;
import cn.com.fanyev5.baseservice.es.model.search.AbstractFacetInfo;
import cn.com.fanyev5.baseservice.es.model.search.AbstractFilterInfo;
import cn.com.fanyev5.baseservice.es.model.search.FacetRangeInfo;
import cn.com.fanyev5.baseservice.es.model.search.FacetTermsInfo;
import cn.com.fanyev5.baseservice.es.model.search.FilterRangeInfo;
import cn.com.fanyev5.baseservice.es.model.search.FilterTermsInfo;
import cn.com.fanyev5.baseservice.es.model.search.ResultInfo;
import cn.com.fanyev5.baseservice.es.model.search.SearchKeyInfo;
import cn.com.fanyev5.baseservice.es.model.search.SearchRequestParameter;
import cn.com.fanyev5.baseservice.es.model.search.SortInfo;
import cn.com.fanyev5.baseservice.es.model.search.ValueTrimInfo;
import cn.com.fanyev5.baseservice.es.util.ESUtil;
import cn.com.fanyev5.search.service.search.ISearchService;
import cn.com.fanyev5.basecommons.codec.JSONUtil;
import cn.com.trends.search.JUnitConfig;
import junit.framework.TestCase;

/**
 * ESSearchServiceImpl Test
 *
 * @author fanqi427@gmail.com
 * @since 2013-12-18
 */
public class ESSearchServiceImplTest extends JUnitConfig {

	@Autowired
	private ISearchService searchService;

	@Test
	@Ignore
	public void testQuery() {
		List<SearchKeyInfo> searchKeyInfos = Lists.newArrayList(new SearchKeyInfo("tle", "小虫"),
				new SearchKeyInfo("tle", "戒指"));
		List<SortInfo> sorts = Lists.newArrayList(new SortInfo("timed", SortOrderEnum.DESC),
				new SortInfo("cid", SortOrderEnum.ASC));
		Date date = new Date();
		long time0 = date.getTime() / 1000;
		long time1 = DateUtils.addDays(date, -7).getTime() / 1000;
		long time2 = DateUtils.addMonths(date, -1).getTime() / 1000;
		long time3 = DateUtils.addMonths(date, -3).getTime() / 1000;
		long time4 = DateUtils.addYears(date, -1).getTime() / 1000;
		List<AbstractFacetInfo> facetInfos = Lists.newArrayList(new FacetTermsInfo("cid", Integer.MAX_VALUE),
				new FacetRangeInfo("timed",
						Lists.newArrayList(new FacetRangeInfo.Entry(time1, time0),
								new FacetRangeInfo.Entry(time2, time0), new FacetRangeInfo.Entry(time3, time0),
								new FacetRangeInfo.Entry(time4, time0), new FacetRangeInfo.Entry(0, time0))));
		List<AbstractFilterInfo> filterInfos = Lists.newArrayList(
				(AbstractFilterInfo) new FilterTermsInfo("cid", new String[] { "314", "611" }),
				(AbstractFilterInfo) new FilterRangeInfo("id", 0, 100000),
				(AbstractFilterInfo) new FilterRangeInfo("timed", 0, 1259051280));
		List<ValueTrimInfo> trimInfos = Lists.newArrayList(new ValueTrimInfo("cnt", 30));
		int pageIndex = 1;
		int pageSize = 5;
		ResultInfo info = searchService.query("4b7719caf59441bab7fa5783ab446a16", searchKeyInfos, sorts, facetInfos,
				filterInfos, trimInfos, pageIndex, pageSize);
		System.out.println(JSONUtil.obj2Json(info));
	}

	@Test
	// @Ignore
	public void testQueryParse() {
		TestCase.assertNotNull(searchService);

		String searchKeyStrs = "title,杰威尔;";
		String sortStrs = "asc,price;";
		// String facetStrs =
		// "terms,cid,1000;range,timed,1386826041:1387430841,1384838841:1387430841,1379568441:1387430841,1355894841:1387430841,0:1387430841;";
		// String filterStrs =
		// "terms,cid,314,611;range,id,0:100000;range,timed,0:1259051280;";
		String timStrs = "title,30;";

		List<SearchKeyInfo> searchKeyInfos = ESUtil.parseSearchKeys(searchKeyStrs);
		List<SortInfo> sortInfos = ESUtil.parseSorts(sortStrs);
		// List<AbstractFacetInfo> facetInfos = ESUtil.parseFacets(facetStrs);
		// List<AbstractFilterInfo> filterInfos =
		// ESUtil.parseFilters(filterStrs);
		List<ValueTrimInfo> trimInfos = ESUtil.parseValueTrims(timStrs);
		int pageIndex = 1;
		int pageSize = 30;
		ResultInfo info = searchService.query("7sef9sd8f9sd8f9sd8f0dsf80sd8f0ds", searchKeyInfos, sortInfos, null, null,
				trimInfos, pageIndex, pageSize);
		System.out.println(JSONUtil.obj2Json(info));
	}

	@Test
	// @Ignore
	public void test() {
		SearchRequestParameter requestParameter = new SearchRequestParameter();
		requestParameter.setIndexName("4b7719caf59441bab7fa5783ab446a16");
		requestParameter.setPageIndex(1);
		requestParameter.setPageSize(15);

		requestParameter
				.setSearchKeyInfos(Lists.newArrayList(new SearchKeyInfo("tle", "时尚"), new SearchKeyInfo("tle", "明星")));

		requestParameter.setSortInfos(Lists.newArrayList(new SortInfo("timed", SortOrderEnum.DESC)));

		Date date = new Date();
		long time0 = date.getTime() / 1000;
		long time1 = DateUtils.addDays(date, -7).getTime() / 1000;
		long time2 = DateUtils.addMonths(date, -1).getTime() / 1000;
		long time3 = DateUtils.addMonths(date, -3).getTime() / 1000;
		long time4 = DateUtils.addYears(date, -1).getTime() / 1000;
		requestParameter
				.setFacetInfos(Lists.newArrayList((AbstractFacetInfo) new FacetTermsInfo("cid", Integer.MAX_VALUE),
						(AbstractFacetInfo) new FacetRangeInfo("timed",
								Lists.newArrayList(new FacetRangeInfo.Entry(time1, time0),
										new FacetRangeInfo.Entry(time2, time0), new FacetRangeInfo.Entry(time3, time0),
										new FacetRangeInfo.Entry(time4, time0), new FacetRangeInfo.Entry(0, time4)))));

		requestParameter.setFilterInfos(Lists.newArrayList(
				(AbstractFilterInfo) new FilterTermsInfo("cid", new String[] { "168", "214", "326" }),
				(AbstractFilterInfo) new FilterRangeInfo("id", 0, 80000)));

		requestParameter.setTrimInfos(Lists.newArrayList(new ValueTrimInfo("cnt", 50)));

		System.out.println("http://192.1.1.103:8092/s/?" + ESUtil.formatRUI(requestParameter));
	}

	@Test
	// @Ignore
	public void testBase64() {

		String s = "desc,clicks;";

		String z = "dGl0bGUs5YiY5b635Y2OO3RpdGxlLOmxvDs=";

		System.out.println(Base64.getBase64(s));

		System.out.println(Base64.getFromBase64(z));

	}

}
