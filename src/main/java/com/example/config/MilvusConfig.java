/*
package com.example.config;

import io.milvus.v2.client.ConnectConfig;
import io.milvus.v2.client.MilvusClientV2;
import io.milvus.v2.common.DataType;
import io.milvus.v2.common.IndexParam;
import io.milvus.v2.service.collection.request.*;
import io.milvus.v2.service.database.request.CreateDatabaseReq;
import io.milvus.v2.service.database.request.DropDatabaseReq;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Configuration
public class MilvusConfig {

    @Value("${milvus.host}")
    private String host;
    @Value("${milvus.port}")
    private Integer port;
    @Value("${milvus.token}")
    private String token;
    @Value("${milvus.database}")
    private String milvus_db;
    @Value("${milvus.collection}")
    private String collectionName;

    @Bean
    public MilvusClientV2 milvusClient() throws InterruptedException {
        ConnectConfig connectConfig = ConnectConfig.builder()
                .uri("http://" + host + ":" + port)
                .token(token)
                .build();
        MilvusClientV2 client = new MilvusClientV2(connectConfig);

        // 查询已有数据库列表
        List<String> existingDatabases = client.listDatabases().getDatabaseNames();

        // 判断目标数据库是否存在，不存在则创建
        if (!existingDatabases.contains(milvus_db)) {
            client.createDatabase(CreateDatabaseReq.builder()
                    .databaseName(milvus_db)
                    .build());
        }

        client.useDatabase(milvus_db);

        if (!client.hasCollection(HasCollectionReq.builder().collectionName(collectionName).build())) {

            // 定义 Schema
            CreateCollectionReq.CollectionSchema schema = MilvusClientV2.CreateSchema();
            schema.addField(AddFieldReq.builder()
                    .fieldName("id")
                    .dataType(DataType.Int64)
                    .isPrimaryKey(true)
                    .autoID(false)
                    .build());
            schema.addField(AddFieldReq.builder()
                    .fieldName("content")
                    .dataType(DataType.VarChar)
                    .maxLength(1024)
                    .build());
            schema.addField(AddFieldReq.builder()
                    .fieldName("embedding")
                    .dataType(DataType.FloatVector)
                    .dimension(1024)
                    .build());

            // 定义索引
            IndexParam indexParamForIdField = IndexParam.builder()
                    .fieldName("id")
                    .indexType(IndexParam.IndexType.AUTOINDEX)
                    .build();
            IndexParam indexParamForVectorField = IndexParam.builder()
                    .fieldName("embedding")
                    .indexType(IndexParam.IndexType.AUTOINDEX)
                    .metricType(IndexParam.MetricType.COSINE)
                    .build();

            List<IndexParam> indexParams = new ArrayList<>();
            indexParams.add(indexParamForIdField);
            indexParams.add(indexParamForVectorField);

            // Create a collection with schema and index parameters
            CreateCollectionReq createCollectionReq = CreateCollectionReq.builder()
                    .collectionName(collectionName)
                    .collectionSchema(schema)
                    .numShards(1) // With shard number
                    .indexParams(indexParams)
                    .build();
            client.createCollection(createCollectionReq);

            // Get load state of the collection
            GetLoadStateReq getLoadStateReq = GetLoadStateReq.builder()
                    .collectionName(collectionName)
                    .build();
            Boolean loaded = client.getLoadState(getLoadStateReq);
            System.out.println(loaded);
        }

        // 删除 Collection & Database
        */
/*DropCollectionReq dropQuickSetupParam = DropCollectionReq.builder()
                .collectionName(collectionName)
                .build();
        client.dropCollection(dropQuickSetupParam);
        client.dropDatabase(DropDatabaseReq.builder()
                .databaseName(milvus_db)
                .build());*//*


        return client;
    }

}

*/
