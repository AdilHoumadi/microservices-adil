package com.microservices.adil.kafka.to.elastic.service.consumer.impl;

import com.microservices.adil.config.KafkaConfigData;
import com.microservices.adil.elastic.index.client.service.ElasticIndexClient;
import com.microservices.adil.elastic.model.index.impl.TwitterIndexModel;
import com.microservices.adil.kafka.admin.client.KafkaAdminClient;
import com.microservices.adil.kafka.to.elastic.service.consumer.KafkaConsumer;
import com.microservices.adil.kafka.avro.model.TwitterAvroModel;
import com.microservices.adil.kafka.to.elastic.service.transformer.AvroToElasticModelTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TwitterKafkaConsumer implements KafkaConsumer<TwitterAvroModel> {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterKafkaConsumer.class);

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @Autowired
    private KafkaAdminClient kafkaAdminClient;

    @Autowired
    private KafkaConfigData kafkaConfigData;

    @Autowired
    private ElasticIndexClient<TwitterIndexModel> elasticIndexClient;

    @Autowired
    private AvroToElasticModelTransformer avroToElasticModelTransformer;

    @Value("${kafka-consumer-config.consumer-group-id}")
    private String consumerGroupId;

    @EventListener
    public void onAppStarted(ApplicationStartedEvent event) {
        kafkaAdminClient.checkCreatedTopics();
        LOG.info("Topic with name {} is ready for operations", kafkaConfigData.getTopicName());
        kafkaListenerEndpointRegistry.getListenerContainer(consumerGroupId).start();
    }

    @Override
    @KafkaListener(
            id="${kafka-consumer-config.consumer-group-id}",
            topics = "${kafka-config.topic-name}")
    public void receive(@Payload List<TwitterAvroModel> messages,
                        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<Long> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        LOG.info("{} messages, keys = {}, partitions = {}, offsets = {}. Sending to Elastic, Thread ID = {}",
                messages.size(),
                keys.toString(),
                partitions.toString(),
                offsets.toString(),
                Thread.currentThread().getId()
        );
        List<TwitterIndexModel> twitterIndexModels = avroToElasticModelTransformer.getElasticModels(messages);
        List<String> ids = elasticIndexClient.save(twitterIndexModels);
        LOG.info("Documents save to elasticsearch with ids: {}", ids);
    }
}
