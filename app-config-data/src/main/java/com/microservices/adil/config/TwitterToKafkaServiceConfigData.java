package com.microservices.adil.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties("twitter-to-kafka-service")
public class TwitterToKafkaServiceConfigData {
    public List<String> twitterKeywords;
    public String welcomeMessage;
    public Boolean enableMockTweets;
    public Integer mockMinTweetLength;
    public Integer mockMaxTweetLength;
    public Long mockSleepMs;
}
