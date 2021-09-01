package com.microservices.adil.elastic.config;

import com.microservices.adil.config.ElasticConfigData;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Objects;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.microservices.adil.elastic")
public class ElasticsearchConfig extends AbstractElasticsearchConfiguration {

    @Autowired
    private ElasticConfigData elasticConfigData;

    @Override
    @Bean
    public RestHighLevelClient elasticsearchClient() {
        UriComponents serverUri = UriComponentsBuilder
                .fromHttpUrl(elasticConfigData.getConnectionUrl())
                .build();

        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
                AuthScope.ANY,
                new UsernamePasswordCredentials(
                        elasticConfigData.getUsername(),
                        elasticConfigData.getPassword()));

        HttpHost host = new HttpHost(
                Objects.requireNonNull(serverUri.getHost()),
                serverUri.getPort(),
                serverUri.getScheme()
        );

        return new RestHighLevelClient(
                RestClient.builder(host)
                        .setRequestConfigCallback(reqConfig -> reqConfig
                                .setConnectTimeout(elasticConfigData.getConnectTimeoutMs())
                                .setSocketTimeout(elasticConfigData.getSocketTimeoutMs()))
                .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
                                .setDefaultCredentialsProvider(credentialsProvider)
                )
        );
    }

    @Bean
    public ElasticsearchOperations elasticsearchOperations() {
        return new ElasticsearchRestTemplate(elasticsearchClient());
    }
}
