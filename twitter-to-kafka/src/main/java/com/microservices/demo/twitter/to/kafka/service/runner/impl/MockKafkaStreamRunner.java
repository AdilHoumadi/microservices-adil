package com.microservices.demo.twitter.to.kafka.service.runner.impl;

import com.microservices.demo.config.TwitterToKafkaServiceConfigData;
import com.microservices.demo.twitter.to.kafka.service.exception.TwitterToKafkaServiceException;
import com.microservices.demo.twitter.to.kafka.service.listner.TwitterKafkaStatusListener;
import com.microservices.demo.twitter.to.kafka.service.runner.StreamRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

@Component
@ConditionalOnProperty(name="twitter-to-kafka-service.enable-mock-tweets", havingValue = "true")
public class MockKafkaStreamRunner implements StreamRunner {

    private final Logger LOG = LoggerFactory.getLogger(TwitterKafkaStatusListener.class);

    @Autowired
    private TwitterKafkaStatusListener twitterKafkaStatusListener;

    @Autowired
    private TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData;

    private static final Random RANDOM = new Random();

    private static final String[] WORDS = new String[]{
            "Lorem",
            "Aliquam",
            "Vestibulum",
            "Nunc",
            "Cras",
            "Vivamus",
            "Praesent",
            "Fusce",
            "Integer",
            "Vestibulum",
            "Ut aliquam",
            "Cras",
            "Donec"
    };

    private static final String TWEET_AS_RAW_JSON = "{\n" +
            "    \"created_at\": \"{0}\",\n" +
            "    \"id\": {1},\n" +
            "    \"text\": \"{2}\",\n" +
            "    \"user\": {\n" +
            "      \"id\": \"{3}\"\n" +
            "    }\n" +
            "}";

    private static final String TWITTER_STATUS_DATE_FORMAT = "EEE MMM dd HH:mm:ss zzz yyyy";

    @Override
    public void start() throws TwitterException {

        String[] keywords = twitterToKafkaServiceConfigData.getTwitterKeywords().toArray(new String[]{});
        Integer maxTweetLength = twitterToKafkaServiceConfigData.mockMaxTweetLength;
        Integer minTweetLength = twitterToKafkaServiceConfigData.mockMinTweetLength;
        Long sleepMs = twitterToKafkaServiceConfigData.mockSleepMs;

        LOG.info("Starting mock filtering twitter streams for keywords {}", Arrays.toString(keywords));
        simulateTwitterStream(keywords, maxTweetLength, minTweetLength, sleepMs);

    }

    private void simulateTwitterStream(String[] keywords, Integer maxTweetLength, Integer minTweetLength, Long sleepMs) {
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                while (true) {
                    String formattedTweetAsJson = getFormattedTweet(keywords, maxTweetLength, minTweetLength);
                    Status status = TwitterObjectFactory.createStatus(formattedTweetAsJson);
                    twitterKafkaStatusListener.onStatus(status);
                    sleep(sleepMs);
                }
            } catch (TwitterException e) {
                LOG.error("Error create twitter status", e);
            }
        });
    }

    private void sleep(Long sleepMs) {
        try {
            Thread.sleep(sleepMs);
        } catch (InterruptedException e) {
            throw new TwitterToKafkaServiceException("Error while sleeping for waiting new status to create");
        }
    }

    private String getFormattedTweet(String[] keywords, Integer maxTweetLength, Integer minTweetLength) {
        String[] params = new String[] {
                ZonedDateTime.now().format(DateTimeFormatter.ofPattern(TWITTER_STATUS_DATE_FORMAT, Locale.ENGLISH)),
                String.valueOf(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE)),
                getRandomTweetContent(keywords, maxTweetLength, minTweetLength),
                String.valueOf(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE)),
        };

        LOG.info(formatTweetAsJsonParams(params));

        return formatTweetAsJsonParams(params);
    }

    private String formatTweetAsJsonParams(String[] params) {
        String tweet = TWEET_AS_RAW_JSON;
        for (int i = 0; i < params.length; i++) {
            tweet = tweet.replace("{"+i+"}", params[i]);
        }
        return tweet;
    }

    private String getRandomTweetContent(String[] keywords, Integer maxTweetLength, Integer minTweetLength) {
        StringBuilder tweet = new StringBuilder();
        int tweetLength = RANDOM.nextInt(maxTweetLength - minTweetLength + 1) + minTweetLength;
        return constructRandomTweet(keywords, tweet, tweetLength);
    }

    private String constructRandomTweet(String[] keywords, StringBuilder tweet, int tweetLength) {
        for (int i = 0; i < tweetLength; i++) {
            tweet.append(WORDS[RANDOM.nextInt(keywords.length)]).append(" ");
            if (i == tweetLength / 2) {
                tweet.append(WORDS[RANDOM.nextInt(keywords.length)]).append(" ");
            }
        }
        return tweet.toString().trim();
    }
}
