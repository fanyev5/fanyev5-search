package cn.com.trends.search.api.life.impl;

import cn.com.fanyev5.baseservice.kafka.worker.IKafkaConsumerWorkerService;
import cn.com.fanyev5.baseservice.kafka.worker.IKafkaProducerWorkerService;
import cn.com.fanyev5.basecommons.life.impl.AbstractSpringLifeService;
import cn.com.fanyev5.search.constant.KafkaConstants;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Kafka服务生命周期服务
 *
 * @author fanqi427@gmail.com
 * @since 2013-7-4
 */
@Service
public class KafkaLifeServiceImpl extends AbstractSpringLifeService {

    /**
     * Kafka producer工作服务
     */
    @Autowired
    private IKafkaProducerWorkerService kafkaProducerWorkerService;

    /**
     * Kafka consumer
     */
    @Autowired
    private IKafkaConsumerWorkerService kafkaConsumerWorkerService;

    @Override
    public void start() {
        kafkaProducerWorkerService.execStart(Sets.newHashSet(KafkaConstants.PRODUCER_INDEXCHANGE));
        kafkaConsumerWorkerService.execStart(Sets.newHashSet(KafkaConstants.CONSUMER_INDEXCHANGE));
    }

    @Override
    public void stop() {
        kafkaProducerWorkerService.execStop();
        kafkaConsumerWorkerService.execStop();
    }

}
