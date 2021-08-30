package com.microservices.adil.twitter.to.kafka.service.runner.impl;

import com.microservices.adil.config.TwitterToKafkaServiceConfigData;
import com.microservices.adil.twitter.to.kafka.service.runner.StreamRunner;
import com.microservices.adil.twitter.to.kafka.service.listener.TwitterKafkaStatusListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import twitter4j.*;

import javax.annotation.PreDestroy;
import java.util.Arrays;

@Component
@ConditionalOnProperty(name="twitter-to-kafka-service.enable-mock-tweets", havingValue = "false", matchIfMissing = true)
public class TwitterKafkaStreamRunner implements StreamRunner {

    private final Logger LOG = LoggerFactory.getLogger(TwitterKafkaStatusListener.class);

    @Autowired
    private TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData;

    @Autowired
    private TwitterKafkaStatusListener twitterKafkaStatusListener;

    private TwitterStream twitterStream;

    @Override
    public void start() {
        twitterStream = new TwitterStreamFactory().getInstance();
        twitterStream.addListener(twitterKafkaStatusListener);
        enableFilters();
    }

    @PreDestroy
    public void shutdown() {
        if(twitterStream != null) {
            LOG.info("close twitter stream");
            twitterStream.shutdown();
        }
    }

    private void enableFilters() {
        String[] keywords = twitterToKafkaServiceConfigData.twitterKeywords.toArray(new String[]{});
        FilterQuery filterQuery = new FilterQuery(keywords);
        twitterStream.filter(filterQuery);
        LOG.info("Started filtering twitter stream for keywords {}", Arrays.toString(keywords));
    }
}
