package com.microservices.adil.elastic.index.client.repository;

import com.microservices.adil.elastic.model.index.impl.TwitterIndexModel;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface TwitterElasticsearchIndexRepository extends ElasticsearchRepository<TwitterIndexModel, String> {
}
