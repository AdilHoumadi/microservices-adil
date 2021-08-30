package com.microservices.adil.twitter.to.kafka.service.listener;

import com.microservices.adil.config.KafkaConfigData;
import com.microservices.adil.twitter.to.kafka.service.transformer.TwitterStatusToKafkaAvroTransformer;
import com.microservices.adil.kafka.avro.model.TwitterAvroModel;
import com.microservices.adil.kafka.producer.config.service.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import twitter4j.Status;
import twitter4j.StatusAdapter;

@Component
public class TwitterKafkaStatusListener extends StatusAdapter {

    private final Logger LOG = LoggerFactory.getLogger(TwitterKafkaStatusListener.class);

    @Autowired
    KafkaConfigData kafkaConfigData;

    @Autowired
    KafkaProducer<Long, TwitterAvroModel> kafkaProducer;

    @Autowired
    TwitterStatusToKafkaAvroTransformer twitterStatusToKafkaAvroTransformer;

    @Override
    public void onStatus(Status status) {
        LOG.info("Received twitter status with id: {} sending to kafka topic {}",
            status.getId(),
            kafkaConfigData.getTopicName()
        );
        TwitterAvroModel twitterAvroModel = twitterStatusToKafkaAvroTransformer.transform(status);
        kafkaProducer.send(kafkaConfigData.getTopicName(), twitterAvroModel.getUserId(), twitterAvroModel);
    }
}
