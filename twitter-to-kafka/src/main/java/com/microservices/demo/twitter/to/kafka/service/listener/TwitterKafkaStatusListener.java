package com.microservices.demo.twitter.to.kafka.service.listener;

import com.microservices.demo.config.KafkaConfigData;
import com.microservices.demo.kafka.avro.model.TwitterAvroModel;
import com.microservices.demo.kafka.producer.config.service.KafkaProducer;
import com.microservices.demo.twitter.to.kafka.service.transformer.TwitterStatusToKafkaAvroTransformer;
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
