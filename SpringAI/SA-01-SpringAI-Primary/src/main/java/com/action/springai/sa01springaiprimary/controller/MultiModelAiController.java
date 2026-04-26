package com.action.springai.sa01springaiprimary.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MultiModelAiController {

    @Autowired
    private ChatClient zpChatGlm4vFlashClient;

    @Autowired
    private ChatClient zpChatGlm4PlusClient;

    @GetMapping("/ai/client/configuration/model1")
    public Object clientConfiguration1(
            @RequestParam(defaultValue = "详细介绍你是什么AI模型？具体版本是什么？") String userInput) {
        return zpChatGlm4PlusClient.prompt(userInput)
                .call().content();
    }

    @GetMapping("/ai/client/configuration/model2")
    public Object clientConfiguration2(
            @RequestParam(defaultValue = "详细介绍你是什么AI模型？具体版本是什么？") String userInput) {
        return zpChatGlm4vFlashClient.prompt(userInput)
                .call().content();
    }
}
