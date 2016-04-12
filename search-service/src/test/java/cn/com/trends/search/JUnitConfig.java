package cn.com.trends.search;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * JUnit测试基类
 * 
 * @author fanqi427@gmail.com
 * @since 2013-6-18
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:conf/spring-context.xml" })
public class JUnitConfig {

}