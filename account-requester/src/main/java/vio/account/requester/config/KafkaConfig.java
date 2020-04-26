package vio.account.requester.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import vio.account.requester.messaging.BaseMessage;
import vio.account.requester.messaging.MessageRequestAccount;
import vio.account.requester.messaging.MessageRequestAccountResponse;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaServers;

    @Value("${spring.kafka.topic.account.reply}")
    private String replyTopic;

    @Value("${spring.kafka.consumer.reply-group-id}")
    private String replyGroupId;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

   @Bean
    public ConsumerFactory<String, BaseMessage> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        JsonDeserializer<BaseMessage> jsonDeserializer = new JsonDeserializer<>(BaseMessage.class);
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), jsonDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, BaseMessage> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, BaseMessage> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    public ReplyingKafkaTemplate<String, BaseMessage, BaseMessage> replyingKafkaTemplate(
            ProducerFactory<String, BaseMessage> pf,
            ConcurrentKafkaListenerContainerFactory<String, BaseMessage> factory) {
        ConcurrentMessageListenerContainer<String, BaseMessage> replyContainer = factory.createContainer(replyTopic);
        replyContainer.getContainerProperties().setMissingTopicsFatal(false);
        replyContainer.getContainerProperties().setGroupId(replyGroupId);
        return new ReplyingKafkaTemplate<>(pf, replyContainer);
    }

    @Bean
    public KafkaTemplate<String, BaseMessage> replyTemplate(
            ProducerFactory<String, BaseMessage> pf,
            ConcurrentKafkaListenerContainerFactory<String, BaseMessage> factory) {
        KafkaTemplate<String, BaseMessage> kafkaTemplate = new KafkaTemplate<>(pf);
        factory.getContainerProperties().setMissingTopicsFatal(false);
        factory.setReplyTemplate(kafkaTemplate);
        return kafkaTemplate;
    }


    @Bean
    public NewTopic topic1() {
        return new NewTopic("account-request", 1, (short) 1);
    }
}
