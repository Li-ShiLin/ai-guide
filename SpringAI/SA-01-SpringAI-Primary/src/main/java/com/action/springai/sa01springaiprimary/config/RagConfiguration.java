package com.action.springai.sa01springaiprimary.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Objects;
import org.springframework.ai.chroma.vectorstore.ChromaApi;
import org.springframework.ai.chroma.vectorstore.ChromaApi.CreateCollectionRequest;
import org.springframework.ai.chroma.vectorstore.ChromaVectorStore;
import org.springframework.ai.document.DocumentTransformer;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.client.RestClient;

@Configuration
public class RagConfiguration {

    @Bean
    public DocumentTransformer documentTransformer() {
        return new TokenTextSplitter();
    }

    @Bean
    public ChromaApi chromaApi(RestClient.Builder restClientBuilder,
                               @Value("${app.rag.chroma-url}") String chromaUrl) {
        return new ChromaApi(chromaUrl, restClientBuilder, new ObjectMapper());
    }

    @Bean
    @Lazy
    public VectorStore chromaVectorStore(
            EmbeddingModel embeddingModel,
            ChromaApi chromaApi,
            @Value("${app.rag.tenant-name}") String tenantName,
            @Value("${app.rag.database-name}") String databaseName,
            @Value("${app.rag.collection-name}") String collectionName) {
        ensureTenantAndDatabase(chromaApi, tenantName, databaseName);

        boolean collectionExists = false;
        try {
            collectionExists = Objects.nonNull(chromaApi.getCollection(tenantName, databaseName, collectionName));
        } catch (Exception ex) {
            collectionExists = false;
        }
        if (!collectionExists) {
            chromaApi.createCollection(tenantName, databaseName, new CreateCollectionRequest(collectionName));
        }

        return ChromaVectorStore.builder(chromaApi, embeddingModel)
                .tenantName(tenantName)
                .databaseName(databaseName)
                .collectionName(collectionName)
                .initializeSchema(true)
                .build();
    }

    private void ensureTenantAndDatabase(ChromaApi chromaApi, String tenantName, String databaseName) {
        boolean tenantExists = false;
        try {
            tenantExists = Objects.nonNull(chromaApi.getTenant(tenantName));
        } catch (Exception ex) {
            tenantExists = false;
        }
        if (!tenantExists) {
            chromaApi.createTenant(tenantName);
        }

        boolean databaseExists = false;
        try {
            databaseExists = Objects.nonNull(chromaApi.getDatabase(tenantName, databaseName));
        } catch (Exception ex) {
            databaseExists = false;
        }
        if (!databaseExists) {
            chromaApi.createDatabase(tenantName, databaseName);
        }
    }
}
