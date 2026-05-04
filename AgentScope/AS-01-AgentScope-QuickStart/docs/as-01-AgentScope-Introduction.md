[AgentScope Java - AgentScope Java](https://java.agentscope.io/zh/intro.html#)

# AgentScope Java

用 Java 构建生产级 AI 智能体。

## 1. 什么是 AgentScope Java？
AgentScope Java 是一个面向智能体的编程框架，用于构建 LLM 驱动的应用程序。它提供了创建智能体所需的一切：ReAct 推理、工具调用、内存管理、多智能体协作等。

## 2. 核心亮点
### 2.1 智能体自主，全程可控
AgentScope 采用 ReAct（推理-行动）范式，使智能体能够自主规划和执行复杂任务。与传统工作流方法不同，ReAct 智能体能够动态决定使用哪些工具以及何时使用，实时适应不断变化的需求。
在生产环境中，没有控制的自主性是一种隐患。AgentScope 提供了全面的运行时干预机制：

- 安全中断：在任意时刻暂停智能体执行，同时保留完整上下文和工具状态，支持无数据丢失的无缝恢复。
- 优雅取消：终止长时间运行或无响应的工具调用，不破坏智能体状态，支持立即恢复与重定向。
- 人机协作：通过 Hook 系统在任意推理步骤注入修正、额外上下文或指导，保持人类对关键决策的监督。

### 2.2 内置工具
AgentScope 包含生产就绪的工具，用于解决智能体开发中的常见挑战：
- PlanNotebook：结构化任务管理系统，将复杂目标分解为有序、可追踪步骤，支持多并发计划的创建、修改、暂停与恢复。
- 结构化输出：自纠错输出解析器，保障类型安全响应。LLM 输出偏离预期格式时，系统会自动检测并引导修正，可直接映射到 Java POJO。
- 长期记忆：跨会话持久化内存存储，支持语义搜索，支持自动管理、智能体控制记录或混合模式，并支持多租户隔离。
- RAG（检索增强生成）：与企业知识库无缝集成，支持自托管嵌入检索与托管服务（如阿里云百炼），使响应基于权威数据源。

### 2.3 无缝集成
AgentScope 设计为与现有企业基础设施低成本集成：
- MCP 协议：可与任何 MCP 兼容服务器集成，即时扩展智能体能力，连接文件系统、数据库、Web 浏览器、代码解释器等生态，无需编写大量自定义集成代码。
- A2A 协议：通过扩展模块实现分布式多智能体协作。可将智能体能力注册到 Nacos 等注册中心（如 `agentscope-extensions-nacos-a2a`），支持像调用微服务一样发现与调用其他智能体。

### 2.4 生产级别
为企业部署需求而构建：
- 高性能：基于 Project Reactor 的响应式架构，确保非阻塞执行；支持通过 Micronaut/Quarkus 进行 GraalVM 原生镜像编译，适配 Serverless 与自动扩缩容环境。
- 可观测性：提供可插拔 Tracer SPI，并支持 OpenTelemetry 扩展，实现整个智能体执行链路的分布式追踪。AgentScope Studio 提供可视化调试、实时监控与全面日志记录。

## 3. 系统要求
- JDK 17 或更高版本
- Maven 或 Gradle

## 4. 快速开始
按照以下步骤开始使用 AgentScope Java：
1. 安装：在项目中配置 AgentScope Java
2. 核心概念：理解核心概念与架构
3. 构建第一个智能体：创建一个可工作的智能体

## 5. 快速示例
```java
import io.agentscope.core.ReActAgent;
import io.agentscope.core.model.DashScopeChatModel;
import io.agentscope.core.message.Msg;

// 创建智能体并内联配置模型
ReActAgent agent = ReActAgent.builder()
    .name("Assistant")
    .sysPrompt("你是一个有帮助的 AI 助手。")
    .model(DashScopeChatModel.builder()
        .apiKey(System.getenv("DASHSCOPE_API_KEY"))
        .modelName("qwen3-max")
        .build())
    .build();

Msg response = agent.call(Msg.builder()
    .textContent("你好！")
    .build()).block();

System.out.println(response.getTextContent());
```

## 6. 进阶指南
熟悉基础后，可以探索以下功能：
### 6.1 模型集成
- 模型集成：配置不同的 LLM 提供商
- 多模态：视觉和多模态能力

### 6.2 工具与知识
- 工具系统：使用基于注解的工具注册创建和使用工具
- MCP：模型上下文协议支持，实现高级工具集成
- RAG：检索增强生成，提供知识增强响应
- 结构化输出：类型安全输出解析，支持自动纠错

### 6.3 行为控制
- 钩子系统：使用事件钩子监控和定制智能体行为
- 内存管理：管理对话历史和长期内存
- 计划：复杂多步骤任务的计划管理
- 智能体配置：高级智能体配置选项

### 6.4 多智能体系统
- 管道：使用顺序与并行执行构建多智能体工作流
- A2A 协议：Agent2Agent 协议支持
- 状态管理：跨会话持久化与恢复智能体状态

### 6.5 可观测性与调试
- AgentScope Studio：可视化调试与监控

## 7. AI 辅助开发
AgentScope 文档支持 `llms.txt` 标准，使 Claude Code、Cursor、Windsurf 等 AI 编程助手能够理解 AgentScope API 并生成更准确的代码。
Cursor 快速设置：
1. 打开 Cursor 设置 -> Features -> Docs
2. 点击 `+ Add new Doc`
3. 添加 URL：`https://java.agentscope.io/llms-full.txt`
更多工具和详细配置，请参阅「使用 AI 编程」。

## 8. 社区
- 官方文档：<https://java.agentscope.io>