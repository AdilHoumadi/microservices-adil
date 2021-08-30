package com.microservices.adil.twitter.to.kafka.service;

import com.microservices.adil.twitter.to.kafka.service.runner.StreamRunner;
import com.microservices.adil.twitter.to.kafka.service.init.StreamInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.kafka.core.KafkaTemplate;


@SpringBootApplication
@ComponentScan("com.microservices.adil")
public class TwitterToKafkaServiceApplication implements CommandLineRunner {

    private final Logger LOG = LoggerFactory.getLogger(TwitterToKafkaServiceApplication.class);

    @Autowired
    private StreamRunner twitterStreamRunner;

    @Autowired
    private StreamInitializer streamInitializer;

    @Autowired
    private KafkaTemplate<Long, Long> kafkaTemplate;

    public static void main(String[] args) {
        SpringApplication.run(TwitterToKafkaServiceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        LOG.info("Application started ...");
        streamInitializer.init();
        twitterStreamRunner.start();
    }
}
