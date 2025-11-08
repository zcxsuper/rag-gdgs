/*
package com.example.config;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ContentRetrieverConfig {


    // 构建向量数据库检索对象
//    @Bean
//    public ContentRetriever contentRetriever(EmbeddingStore<TextSegment> milvusEmbeddingStore, EmbeddingModel embeddingModel) {
//        return EmbeddingStoreContentRetriever.builder()
//                .embeddingStore(milvusEmbeddingStore) // 使用注入的 Milvus 向量存储
//                .embeddingModel(embeddingModel) // 使用注入的向量模型
//                .maxResults(3)                  // 指定最多返回3个最相关的结果
//                .minScore(0.5)                  // 指定相关性分数的最小阈值，过滤掉不相关的结果
//                .build();
//    }

    */
/**
     * 使用 CommandLineRunner 在 Spring Boot 应用启动后执行数据注入逻辑。
     *
     * @param milvusEmbeddingStore  自动注入上面定义的 MilvusEmbeddingStore Bean
     * @param embeddingModel  自动注入您在别处定义的 EmbeddingModel Bean
     * @return CommandLineRunner 实例
     *//*

    @Bean
    public CommandLineRunner dataIngestor(EmbeddingModel embeddingModel, EmbeddingStore<TextSegment> milvusEmbeddingStore) {
        return args -> {
            System.out.println("--- 开始加载和处理文档 ---");

            // 1. 从 classpath 加载文档 (这里以PDF为例)
            // "content" 是您 resources 目录下的文件夹名
            List<Document> documents = ClassPathDocumentLoader.loadDocuments(
                    "content", new ApachePdfBoxDocumentParser()
            );
            System.out.println("成功加载 " + documents.size() + " 个文档。");

            // 2. 定义文档分割器
            // 将长文档切分为最大500个字符的片段，片段间重叠100个字符
            DocumentSplitter documentSplitter = DocumentSplitters.recursive(500, 100);

            // 3. 创建 EmbeddingStoreIngestor
            // 这个工具类会自动处理：分割 -> 向量化 -> 存储
            EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                    .documentSplitter(documentSplitter) // 1️⃣ 文档切分器
                    .embeddingModel(embeddingModel) // 2️⃣ 向量模型
                    .embeddingStore(milvusEmbeddingStore) // 3️⃣ 向量存储
                    .build();

            // 4. 执行注入过程
            System.out.println("--- 开始将文档注入 Milvus... ---");
            ingestor.ingest(documents);
            System.out.println("--- 文档注入完成 ---");
        };
    }
}
*/
