package com.action.springai.sa01springaiprimary.controller;

import java.nio.charset.StandardCharsets;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;

@RestController
public class StreamAiController {

    private final ChatModel zhiPuAiChatModel;

    public StreamAiController(ChatModel zhiPuAiChatModel) {
        this.zhiPuAiChatModel = zhiPuAiChatModel;
    }

    /**
     * 使用带 UTF-8 charset 的 MediaType，并通过 ResponseEntity 显式声明 Content-Type，
     * 避免 SSE 流被按 ISO-8859-1 写出导致中文乱码。
     */
    private static final MediaType TEXT_EVENT_STREAM_UTF8 =
            new MediaType("text", "event-stream", StandardCharsets.UTF_8);

    @GetMapping("/ai/stream")
    public ResponseEntity<Flux<String>> streamContent(
            @RequestParam(defaultValue = "和我说一个长笑话") String userInput) {
        // Create ChatClient instances programmatically
        ChatClient zpChatClient = ChatClient.create(zhiPuAiChatModel);
        Flux<String> content = zpChatClient.prompt(userInput)
                .advisors(new SimpleLoggerAdvisor())
                .stream().content();
        return ResponseEntity.ok()
                .contentType(TEXT_EVENT_STREAM_UTF8)
                .body(content);
    }
}
