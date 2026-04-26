package com.action.springai.sa01springaiprimary.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.zhipuai.ZhiPuAiChatOptions;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.ai.zhipuai.api.ZhiPuAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MultiModelConfiguration {

    @Value("${spring.ai.zhipuai.api-key}")
    private String zpAPIKey;

    @Bean
    public ChatClient zpChatGlm4vFlashClient() {
        ZhiPuAiApi zhiPuAiApi = new ZhiPuAiApi(zpAPIKey);
        ZhiPuAiChatOptions options = ZhiPuAiChatOptions.builder().model("glm-4v-flash").build();
        ChatModel zhiPuAiChatModel = new ZhiPuAiChatModel(zhiPuAiApi, options);
        return ChatClient.create(zhiPuAiChatModel);
    }

    @Bean
    public ChatClient zpChatGlm4PlusClient() {
        ZhiPuAiApi zhiPuAiApi = new ZhiPuAiApi(zpAPIKey);
        ZhiPuAiChatOptions options = ZhiPuAiChatOptions.builder().model("glm-4-plus").build();
        ChatModel zhiPuAiChatModel = new ZhiPuAiChatModel(zhiPuAiApi, options);
        return ChatClient.create(zhiPuAiChatModel);
    }
}
