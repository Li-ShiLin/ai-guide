package com.action.agentscope.sc01agentscope.config;

import io.agentscope.core.model.OllamaChatModel;
import io.agentscope.core.model.ollama.OllamaOptions;
import io.agentscope.core.model.ollama.ThinkOption;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OllamaChatModel 配置类
 * <p>
 * 1.确保 Ollama 启动，且模型已拉取
 * 2.启动 Ollama：ollama serve
 * 3.确认模型存在：ollama list，看是否有你配置的 qwen3.5:0.8b
 * 4.如果没有，先拉取：ollama pull qwen3.5:0.8b
 */
@Configuration
public class OllamaChatModelConfig {

    /**
     * 禁用Ollama的思考模式
     * 关闭思考模式:  响应极快，资源占用极低，成本更低。能直接获取最终答案，输出内容更简洁。
     */
    @Bean
    public OllamaChatModel ollamaChatModel() {
        return OllamaChatModel.builder()
                .defaultOptions(OllamaOptions.builder().thinkOption(ThinkOption.ThinkBoolean.DISABLED).build())
                .modelName("qwen3.5:0.8b")
                .build();
    }

    /*
    @Bean
    public OllamaChatModel ollamaChatModel() {
        return OllamaChatModel.builder()
                .modelName("qwen3.5:0.8b")
                .build();
    }
    */
}
