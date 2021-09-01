package com.microservices.adil.elastic.index.client.service.impl;

import com.microservices.adil.elastic.index.client.repository.TwitterElasticsearchIndexRepository;
import com.microservices.adil.elastic.index.client.service.ElasticIndexClient;
import com.microservices.adil.elastic.model.index.impl.TwitterIndexModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Primary
public class TwitterElasticRepositoryIndexClient implements ElasticIndexClient<TwitterIndexModel> {

    private final Logger LOG = LoggerFactory.getLogger(TwitterElasticRepositoryIndexClient.class);

    @Autowired
    private TwitterElasticsearchIndexRepository twitterElasticsearchIndexRepository;

    @Override
    public List<String> save(List<TwitterIndexModel> documents) {
        List<TwitterIndexModel> indexQueries = (List<TwitterIndexModel>) twitterElasticsearchIndexRepository.saveAll(documents);
        List<String> ids = indexQueries.stream().map(TwitterIndexModel::getId).collect(Collectors.toList());
        LOG.info("Documents successfully indexed with type: {} and ids: {}",
                TwitterIndexModel.class.getName(),
                ids
        );
        return ids;
    }
}
