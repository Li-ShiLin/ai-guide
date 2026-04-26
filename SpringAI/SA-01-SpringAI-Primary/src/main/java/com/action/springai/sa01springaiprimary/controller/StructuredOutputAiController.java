package com.action.springai.sa01springaiprimary.controller;

import java.util.List;

import com.action.springai.sa01springaiprimary.entity.User;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StructuredOutputAiController {

    private final ChatModel zhiPuAiChatModel;

    public StructuredOutputAiController(ChatModel zhiPuAiChatModel) {
        this.zhiPuAiChatModel = zhiPuAiChatModel;
    }

    @GetMapping("/ai/json")
    public Object generationJson(@RequestParam(name = "userInput", defaultValue = "随机生成一份用户信息") String userInput) {
        ChatClient zpChatClient = ChatClient.create(zhiPuAiChatModel);
        User zpContent = zpChatClient.prompt(userInput)
                .call().entity(User.class);
        return zpContent;
    }

    @GetMapping("/ai/json/list")
    public Object generationJsonList(
            @RequestParam(name = "userInput", defaultValue = "随机生成3条用户信息") String userInput) {
        ChatClient zpChatClient = ChatClient.create(zhiPuAiChatModel);
        List<User> zpContent = zpChatClient.prompt(userInput)
                .call().entity(new ParameterizedTypeReference<List<User>>() {
                });
        return zpContent;
    }
}
