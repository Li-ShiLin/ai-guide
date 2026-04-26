package com.action.springai.sa01springaiprimarymcpserver.config;

import com.action.springai.sa01springaiprimarymcpserver.service.JavaLearningService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ToolCallbackProviderConfig {

    @Bean
    public ToolCallbackProvider gzhRecommendTools(JavaLearningService learningService) {
        return MethodToolCallbackProvider.builder().toolObjects(learningService).build();
    }
}
