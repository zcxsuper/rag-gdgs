# RAG-GDGS

基于 RAG（检索增强生成）技术的智能问答系统，支持文档上传、管理和基于文档的智能对话。

## 📋 项目简介

RAG-GDGS 是一个基于 Spring Boot 和 LangChain4j 构建的智能问答系统，集成了文件管理、会话管理和 AI 对话功能。 通过向量数据库存储和检索，实现基于知识库的智能问答。

## 🛠️ 技术栈

### 核心框架
- **后端框架**: Spring Boot 3.5.3
- **Java 版本**: Java 17
- **Web 框架**: Spring Web + Spring WebFlux（混合模式，支持流式响应 SSE）
- **构建工具**: Maven

### AI 框架
- **LangChain4j**: 1.0.1-beta6

### 数据存储
- **数据库**: MySQL 8.0.36
- **ORM**: MyBatis 3.0.3 + MyBatis Plus 3.5.5
- **缓存**: Redis 8.2.0
- **消息队列**: RabbitMQ 3.8-management
- **向量数据库**: Milvus 2.6.1

### 安全与工具
- **认证**: JWT（java-jwt 4.4.0）
- **安全框架**: Spring Security Core
- **AOP**: Spring AOP
- **工具库**: Lombok
- **序列化**: Protobuf Java 3.25.3

## ✨ 主要功能

### 1. 用户管理
- 用户注册/登录
- JWT Token 认证
- 个人信息管理
- 用户注销
- 管理员用户管理（查看用户列表、删除用户等）

### 2. 文件管理
- 文件上传
- 文件下载
- 文件夹创建
- 文件/文件夹重命名
- 文件移动
- 文件删除
- 文件列表查询

### 3. 会话管理
- 创建会话
- 查看所有会话
- 查询会话消息历史
- 更新会话标题
- 删除会话
- 查询会话消息历史

### 4. AI 智能对话
- 基于 RAG 的流式对话
- 支持会话记忆
- 支持本地文件（LOCAL）和在线（ONLINE）两种会话模式
- 实时流式响应（Server-Sent Events）

## 📁 项目结构

```
rag-gdgs/
├── src/main/java/com/example/
│   ├── advice/              # 全局异常处理
│   ├── annotation/          # 自定义注解（权限控制、聊天流程）
│   ├── aspect/              # AOP 切面
│   ├── cache/               # MyBatis Redis 缓存
│   ├── config/              # 配置类
│   ├── controller/          # REST API 控制器
│   ├── domain/              # 领域模型（实体、DTO、VO）
│   ├── enums/               # 枚举类
│   ├── exception/           # 自定义异常
│   ├── interceptor/         # 拦截器
│   ├── listener/            # 消息监听器
│   ├── mapper/              # MyBatis Mapper
│   ├── repository/          # 数据仓库
│   ├── service/             # 业务服务层
│   ├── tool/                # 工具类
│   └── util/                # 工具类（JWT、加密等）
├── src/main/resources/
│   ├── application.yml      # 主配置文件
│   ├── application-dev.yml  # 开发环境配置
│   ├── application-pro.yml  # 生产环境配置
│   ├── logback-spring.xml   # 日志配置
│   └── content/             # 文档存储目录
├── docker/                   # Docker 相关配置
│   ├── docker-compose.yaml  # Docker Compose 配置
│   ├── mysql/               # MySQL 配置       
│   └── redis/               # Redis 配置
├── ENV_VARIABLES.md         # 环境变量配置指南
├── CONFIG_PRIORITY.md       # 配置优先级说明
└── pom.xml                  # Maven 依赖配置
```

