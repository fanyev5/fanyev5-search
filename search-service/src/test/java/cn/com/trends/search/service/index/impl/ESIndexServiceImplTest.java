package cn.com.trends.search.service.index.impl;

import cn.com.fanyev5.search.service.index.IIndexService;
import cn.com.trends.search.JUnitConfig;

import com.google.common.collect.Sets;
import junit.framework.TestCase;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * ESIndexServiceImpl Test
 *
 * @author fanqi427@gmail.com
 * @since 2013-12-17
 */
public class ESIndexServiceImplTest extends JUnitConfig {

    @Autowired
    private IIndexService indexService;

    @Test
    //@Ignore
    public void testBuildIndex() {
        TestCase.assertNotNull(indexService);
        indexService.buildIndex(Sets.newHashSet("7sef9sd8f9sd8f9sd8f0dsf80sd8f0ds"));
    }

    @Test
    //@Ignore
    public void testCleanIndex() {
        TestCase.assertNotNull(indexService);
        indexService.cleanIndex(Sets.newHashSet("7sef9sd8f9sd8f9sd8f0dsf80sd8f0ds"));
    }
}
