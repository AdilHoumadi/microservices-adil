package com.microservices.adil.elastic.index.client.service;

import com.microservices.adil.elastic.model.index.IndexModel;

import java.util.List;

public interface ElasticIndexClient<T extends IndexModel> {
    List<String> save(List<T> documents);
}
