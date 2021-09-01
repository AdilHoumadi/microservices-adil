package com.microservices.adil.elastic.index.client.util;

import com.microservices.adil.elastic.model.index.IndexModel;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ElasticIndexUtil<T extends IndexModel> {

    public List<IndexQuery> getIndexQueries(List<T> documents) {
        return documents.stream()
                .map(d -> new IndexQueryBuilder()
                        .withId(d.getId())
                        .withObject(d)
                        .build()
                ).collect(Collectors.toList());
    }
}
