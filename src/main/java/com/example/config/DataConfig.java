package com.example.config;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.milvus.MilvusEmbeddingStore;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataConfig {

    @Autowired
    private ChatMemoryStore redisChatMemoryStore;

    // --- 从 application.properties 注入 Milvus 配置 ---
    @Value("${milvus.host}")
    private String host;
    @Value("${milvus.port}")
    private Integer port;
    @Value("${milvus.database}")
    private String databaseName;
    @Value("${milvus.collection}")
    private String collectionName;

    // 构建会话记忆对象
    @Bean
    public ChatMemory chatMemory(){
        MessageWindowChatMemory memory = MessageWindowChatMemory.builder()
                .maxMessages(20)
                .build();
        return memory;
    }

    // 构建ChatMemoryProvider对象
    @Bean
    public ChatMemoryProvider chatMemoryProvider() {
        ChatMemoryProvider chatMemoryProvider = new ChatMemoryProvider() {
            @Override
            public ChatMemory get(Object memoryId) {
                return MessageWindowChatMemory.builder()
                        .id(memoryId)
                        .maxMessages(20)
                        .chatMemoryStore(redisChatMemoryStore)
                        .build();
            }
        };
        return chatMemoryProvider;
    }

    /**
     * 创建并配置 MilvusEmbeddingStore Bean。
     * 这个 Bean 将作为向量数据的持久化存储。
     * * @return 配置好的 MilvusEmbeddingStore 实例。
     */
    // 构建向量数据库操作对象
    @Bean
    public EmbeddingStore<TextSegment> milvusEmbeddingStore() {
        return MilvusEmbeddingStore.builder()
                .host(host)
                .port(port)
                .databaseName(databaseName)
                .collectionName(collectionName)
                // 维度必须与您的 EmbeddingModel 生成的向量维度一致
                .dimension(1024)
                // 如果集合已存在，是否使用现有集合。通常设置为 true
                .retrieveEmbeddingsOnSearch(true)
                // 如果集合不存在，自动创建
                .autoFlushOnInsert(true)
                .build();
    }

    // 构建向量数据库检索对象
    @Bean
    public ContentRetriever contentRetriever(EmbeddingStore<TextSegment> milvusEmbeddingStore, EmbeddingModel embeddingModel) {
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(milvusEmbeddingStore) // 使用注入的 Milvus 向量存储
                .embeddingModel(embeddingModel) // 使用注入的向量模型
                .maxResults(3)                  // 指定最多返回3个最相关的结果
                .minScore(0.5)                  // 指定相关性分数的最小阈值，过滤掉不相关的结果
                .build();
    }

    /**
     * 使用 CommandLineRunner 在 Spring Boot 应用启动后执行数据注入逻辑。
     *
     * @param milvusEmbeddingStore  自动注入上面定义的 MilvusEmbeddingStore Bean
     * @param embeddingModel  自动注入您在别处定义的 EmbeddingModel Bean
     * @return CommandLineRunner 实例
     */
    /*@Bean
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
    }*/


}
