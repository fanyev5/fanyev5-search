package cn.com.fanyev5.search.constant;

/**
 * Kafka 常量
 *
 * @author fanqi427@gmail.com
 * @since 2013-07-25
 */
public final class KafkaConstants {

    private KafkaConstants() {
    }

    /**
     * Kafka topic: 搜索变更
     */
    public static final String TOPIC_INDEXCHANGE = "topic-indexchange";

    /**
     * Kafka producer: 搜索变更
     */
    public static final String PRODUCER_INDEXCHANGE = "producer-indexchange";

    /**
     * Kafka consumer: 搜索变更
     */
    public static final String CONSUMER_INDEXCHANGE = "consumer-indexchange";

}
