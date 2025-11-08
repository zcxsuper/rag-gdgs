//package com.example.config;
//
//import dev.langchain4j.data.segment.TextSegment;
//import dev.langchain4j.store.embedding.EmbeddingStore;
//import dev.langchain4j.store.embedding.milvus.MilvusEmbeddingStore;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class EmbeddingStoreConfig {
//
//    // --- 从 application.properties 注入 Milvus 配置 ---
//    @Value("${milvus.host}")
//    private String host;
//    @Value("${milvus.port}")
//    private Integer port;
//    @Value("${milvus.database}")
//    private String databaseName;
//    @Value("${milvus.collection}")
//    private String collectionName;
//
//    /**
//     * 创建并配置 MilvusEmbeddingStore Bean。
//     * 这个 Bean 将作为向量数据的持久化存储。
//     * * @return 配置好的 MilvusEmbeddingStore 实例。
//     */
//    // 构建向量数据库操作对象
//    @Bean
//    public EmbeddingStore<TextSegment> milvusEmbeddingStore() {
//        return MilvusEmbeddingStore.builder()
//                .host(host)
//                .port(port)
//                .databaseName(databaseName)
//                .collectionName(collectionName)
//                // 维度必须与您的 EmbeddingModel 生成的向量维度一致
//                .dimension(1024)
//                // 如果集合已存在，是否使用现有集合。通常设置为 true
//                .retrieveEmbeddingsOnSearch(true)
//                // 如果集合不存在，自动创建
//                .autoFlushOnInsert(true)
//                .build();
//    }
//
//}
