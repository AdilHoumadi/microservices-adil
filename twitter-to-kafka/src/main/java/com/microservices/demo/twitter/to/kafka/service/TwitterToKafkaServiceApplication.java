package com.microservices.demo.twitter.to.kafka.service;

import com.microservices.demo.config.TwitterToKafkaServiceConfigData;
import com.microservices.demo.twitter.to.kafka.service.runner.StreamRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.util.Arrays;


@SpringBootApplication
@ComponentScan("com.microservices.demo")
public class TwitterToKafkaServiceApplication implements CommandLineRunner {

    private final Logger LOG = LoggerFactory.getLogger(TwitterToKafkaServiceApplication.class);

    @Autowired
    private TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData;

    @Autowired
    private StreamRunner twitterStreamRunner;

    public static void main(String[] args) {
        SpringApplication.run(TwitterToKafkaServiceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        LOG.info(Arrays.toString(twitterToKafkaServiceConfigData.twitterKeywords.toArray(new String[] {})));
        LOG.info(twitterToKafkaServiceConfigData.welcomeMessage);
        twitterStreamRunner.start();
    }
}
