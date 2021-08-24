package com.microservices.demo.twitter.to.kafka.service.listner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import twitter4j.Status;
import twitter4j.StatusAdapter;

@Component
public class TwitterKafkaStatusListener extends StatusAdapter {

    private final Logger LOG = LoggerFactory.getLogger(TwitterKafkaStatusListener.class);

    @Override
    public void onStatus(Status status) {
        LOG.info("twitter status with text: {}", status.getText());
    }
}
