package com.microservices.demo.twitter.to.kafka.service.transformer;

import com.microservices.demo.kafka.avro.model.TwitterAvroModel;
import org.springframework.stereotype.Component;
import twitter4j.Status;

@Component
public class TwitterStatusToKafkaAvroTransformer {

    public TwitterAvroModel transform(Status status) {
        return TwitterAvroModel.newBuilder()
                .setCreatedAt(status.getCreatedAt().getTime())
                .setId(status.getUser().getId())
                .setText(status.getText())
                .setUserId(status.getId())
                .build();
    }
}
