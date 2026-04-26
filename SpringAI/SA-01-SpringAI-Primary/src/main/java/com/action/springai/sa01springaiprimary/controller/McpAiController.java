package com.action.springai.sa01springaiprimary.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class McpAiController {

    @Autowired
    private ChatModel zhiPuAiChatModel;

    @Autowired
    private ToolCallbackProvider toolCallbackProvider;

    @GetMapping("/ai/mcp")
    public Object mcp(@RequestParam(defaultValue = "我想学习java，请帮我推荐学习资料") String userInput) {
        // Create ChatClient instances programmatically
        ChatClient zpChatClient = ChatClient.create(zhiPuAiChatModel);
        return zpChatClient.prompt(userInput)
                .advisors(new SimpleLoggerAdvisor())
                .toolCallbacks(toolCallbackProvider)
                .call()
                .content();
    }
}
