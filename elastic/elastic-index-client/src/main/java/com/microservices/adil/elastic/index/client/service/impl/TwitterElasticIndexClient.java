package com.microservices.adil.elastic.index.client.service.impl;

import com.microservices.adil.config.ElasticConfigData;
import com.microservices.adil.elastic.index.client.service.ElasticIndexClient;
import com.microservices.adil.elastic.index.client.util.ElasticIndexUtil;
import com.microservices.adil.elastic.model.index.impl.TwitterIndexModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexedObjectInformation;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@ConditionalOnProperty(name="elastic-config.is-repository", havingValue = "true", matchIfMissing = true)
public class TwitterElasticIndexClient implements ElasticIndexClient<TwitterIndexModel> {

    private final Logger LOG = LoggerFactory.getLogger(TwitterElasticIndexClient.class);

    @Autowired
    private ElasticConfigData elasticConfigData;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    private ElasticIndexUtil<TwitterIndexModel> elasticIndexUtil;

    @Override
    public List<String> save(List<TwitterIndexModel> documents) {
        List<IndexQuery> indexQueries = elasticIndexUtil.getIndexQueries(documents);
        List<IndexedObjectInformation> documentIds = elasticsearchOperations.bulkIndex(
                indexQueries,
                IndexCoordinates.of(elasticConfigData.getIndexName())
        );

        List<String> ids = documentIds
                .stream()
                .map(IndexedObjectInformation::getId)
                .collect(Collectors.toList());

        LOG.info("Documents successfully indexed with type: {} and ids: {}",
                TwitterIndexModel.class.getName(),
                ids
        );
        return ids;
    }
}
