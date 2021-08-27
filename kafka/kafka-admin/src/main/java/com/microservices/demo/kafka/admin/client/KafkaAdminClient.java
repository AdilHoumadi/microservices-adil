package com.microservices.demo.kafka.admin.client;

import com.microservices.demo.config.KafkaConfigData;
import com.microservices.demo.config.RetryConfigData;
import com.microservices.demo.kafka.admin.exception.KafkaClientException;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicListing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Component
public class KafkaAdminClient {

    private final Logger LOG = LoggerFactory.getLogger(KafkaAdminClient.class);

    @Autowired
    private KafkaConfigData kafkaConfigData;

    @Autowired
    private RetryConfigData retryConfigData;

    @Autowired
    private AdminClient adminClient;

    @Autowired
    private RetryTemplate retryTemplate;

    @Autowired
    private WebClient webClient;

    public void createTopic() {
        CreateTopicsResult createTopicsResults;
        try {
            retryTemplate.execute(this::doCreateTopic);
        } catch (Throwable t) {
            throw new KafkaClientException("Reached Max retry for creating a kafka topic(s)", t);
        }
        checkCreatedTopics();
    }

    public void checkCreatedTopics() {
        Collection<TopicListing> topics = getTopics();
        int retryCount = 1;
        Integer maxAttempts = retryConfigData.getMaxAttempts();
        Long multiplier = retryConfigData.getMultiplier();
        Long sleepTimeMs = retryConfigData.getSleepTimeMs();

        for (String topic : kafkaConfigData.getTopicNamesToCreate()) {
            while (!isTopicCreated(topics, topic)) {
                checkMaxRetry(retryCount++, maxAttempts);
                sleep(sleepTimeMs);
                sleepTimeMs *= multiplier;
                topics = getTopics();
            }
        }
    }

    public void checkSchemaRegistry() {
        LOG.info("Checking schema registry availability");
        int retryCount = 1;
        Integer maxAttempts = retryConfigData.getMaxAttempts();
        Long multiplier = retryConfigData.getMultiplier();
        Long sleepTimeMs = retryConfigData.getSleepTimeMs();
        while (!getSchemaRegistryStatus().is2xxSuccessful()) {
            checkMaxRetry(retryCount++, maxAttempts);
            sleep(sleepTimeMs);
            sleepTimeMs *= multiplier;
        }
        LOG.info("Schema registry is availability");
    }

    private CreateTopicsResult doCreateTopic(RetryContext retryContext) {
        List<String> topicNames = kafkaConfigData.getTopicNamesToCreate();
        LOG.info("Creating {} topic(s) attempts {}", topicNames.size(), retryContext.getRetryCount());
        List<NewTopic> kafkaTopics = topicNames.stream().map(topic -> new NewTopic(
                topic.trim(),
                kafkaConfigData.getNumOfPartitions(),
                kafkaConfigData.getReplicationFactor()
        )).collect(Collectors.toList());
        return adminClient.createTopics(kafkaTopics);
    }

    private Collection<TopicListing> getTopics() {
        Collection<TopicListing> topics;
        try {
            topics = retryTemplate.execute(this::doGetTopics);
        } catch (Throwable t) {
            throw new KafkaClientException("Reached Max retry for creating a kafka topic(s)", t);
        }
        return topics;
    }

    private Collection<TopicListing> doGetTopics(RetryContext retryContext)
            throws ExecutionException, InterruptedException {
        LOG.info("Creating {} topic(s) attempts {}", kafkaConfigData.getTopicNamesToCreate().toArray(),
                retryContext.getRetryCount());
        Collection<TopicListing> topics = adminClient.listTopics().listings().get();
        if (topics != null) {
            topics.forEach(topic -> LOG.debug("Topic with name {}", topic.name()));
        }
        return topics;
    }

    private HttpStatus getSchemaRegistryStatus() {
        try {
            return webClient
                    .method(HttpMethod.GET)
                    .uri(kafkaConfigData.getSchemaRegistryUrl())
                    .exchangeToMono(response -> {
                        if (response.statusCode().is2xxSuccessful()) {
                            return Mono.just(response.statusCode());
                        } else {
                            return Mono.just(HttpStatus.SERVICE_UNAVAILABLE);
                        }
                    })
                    .block();
        } catch (Exception e) {
            return HttpStatus.SERVICE_UNAVAILABLE;
        }
    }

    private void sleep(Long sleepTimeMs) {
        try {
            Thread.sleep(sleepTimeMs);
        } catch (InterruptedException e) {
            throw new KafkaClientException("Error while sleeping for waiting new created topics");
        }
    }

    private void checkMaxRetry(int retry, Integer maxAttempts) {
        if (retry > maxAttempts) {
            throw new KafkaClientException("Reached max retry for reading kafka topic");
        }
    }

    private boolean isTopicCreated(Collection<TopicListing> topics, String topic) {
        if (topics == null) {
            return false;
        }
        return topics.stream().anyMatch(t -> t.name().equals(topic));
    }

}
