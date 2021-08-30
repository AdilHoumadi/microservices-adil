package com.microservices.adil.twitter.to.kafka.service.init.impl;

import com.microservices.adil.config.KafkaConfigData;
import com.microservices.adil.kafka.admin.client.KafkaAdminClient;
import com.microservices.adil.twitter.to.kafka.service.init.StreamInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class KafkaStreamInitializer implements StreamInitializer {

    private final Logger LOG = LoggerFactory.getLogger(KafkaStreamInitializer.class);

    @Autowired
    private KafkaConfigData kafkaConfigData;

    @Autowired
    private KafkaAdminClient kafkaAdminClient;

    @Override
    public void init() {
        kafkaAdminClient.createTopic();
        kafkaAdminClient.checkSchemaRegistry();
        LOG.info("Topic with name {} is ready for operations", kafkaConfigData.getTopicName());
    }
}
