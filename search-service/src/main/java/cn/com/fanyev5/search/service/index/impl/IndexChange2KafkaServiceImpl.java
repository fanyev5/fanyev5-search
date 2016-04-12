package cn.com.fanyev5.search.service.index.impl;

import cn.com.fanyev5.baseservice.kafka.producer.IKafkaProducer;
import cn.com.fanyev5.baseservice.kafka.producer.INamedKafkaProducerService;
import cn.com.fanyev5.search.constant.KafkaConstants;
import cn.com.fanyev5.search.model.index.IndexChangeRule;
import cn.com.fanyev5.search.service.index.IIndexChangeService;
import cn.com.fanyev5.basecommons.codec.JSONUtil;

/**
 * 索引变更服务接口实现
 *
 * @author fanqi427@gmail.com
 * @since 2013-12-19
 */
public class IndexChange2KafkaServiceImpl implements IIndexChangeService {
    /**
     * Kafka producer命名服务
     */
    private INamedKafkaProducerService namedKafkaProducerService;

    public IndexChange2KafkaServiceImpl(INamedKafkaProducerService namedKafkaProducerService) {
        this.namedKafkaProducerService = namedKafkaProducerService;
    }

    @Override
    public void sendChange(IndexChangeRule... indexChangeRules) {
        if (indexChangeRules == null || indexChangeRules.length == 0) {
            return;
        }
        IKafkaProducer<String> producer = getKafkaProducerClient();
        producer.send(KafkaConstants.TOPIC_INDEXCHANGE, JSONUtil.obj2Json(indexChangeRules));
    }

    /**
     * 获取KafkaProducer
     *
     * @return
     */
    private IKafkaProducer<String> getKafkaProducerClient() {
        return (IKafkaProducer<String>) namedKafkaProducerService.get(KafkaConstants.PRODUCER_INDEXCHANGE);
    }
}
