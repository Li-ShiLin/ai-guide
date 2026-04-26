package com.action.springai.sa01springaiprimary.controller;

import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PromptTemplateAiController {

    private final ChatModel zhiPuAiChatModel;

    public PromptTemplateAiController(ChatModel zhiPuAiChatModel) {
        this.zhiPuAiChatModel = zhiPuAiChatModel;
    }

    @GetMapping("/ai/prompt/template")
    public Object promptTemplate(@RequestParam(name = "username", defaultValue = "张三") String username) {
        // Create ChatClient instances programmatically
        ChatClient zpChatClient = ChatClient.create(zhiPuAiChatModel);
        return zpChatClient.prompt()
                .user(u -> u.text("请帮我写一首诗，作者是{userName}，内容围绕作者的名字去写")
                        .param("userName", username))
                .advisors(new SimpleLoggerAdvisor())
                .call().content();
    }

    @GetMapping("/ai/prompt/template/map")
    public Object promptTemplateByMap(@RequestParam(name = "username", defaultValue = "张三") String username) {
        // Create ChatClient instances programmatically
        ChatClient zpChatClient = ChatClient.create(zhiPuAiChatModel);
        return zpChatClient.prompt()
                .user(u -> u.text("请帮我写一首诗，作者是{userName}，内容围绕作者的名字去写")
                        .params(Map.of("userName", username)))
                .advisors(new SimpleLoggerAdvisor())
                .call().content();
    }
}
