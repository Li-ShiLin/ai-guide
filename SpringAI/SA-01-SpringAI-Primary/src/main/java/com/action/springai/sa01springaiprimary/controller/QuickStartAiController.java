package com.action.springai.sa01springaiprimary.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QuickStartAiController {

    private final ChatModel zhiPuAiChatModel;

    public QuickStartAiController(ChatModel zhiPuAiChatModel) {
        this.zhiPuAiChatModel = zhiPuAiChatModel;
    }

    @GetMapping("/ai")
    public String generation(@RequestParam(name = "userInput", defaultValue = "你觉得知识能改变命运么？") String userInput) {
        ChatClient chatClient = ChatClient.create(zhiPuAiChatModel);
        return chatClient.prompt(userInput).call().content();
    }
}
