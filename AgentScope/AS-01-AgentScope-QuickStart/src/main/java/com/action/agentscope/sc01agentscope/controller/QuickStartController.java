package com.action.agentscope.sc01agentscope.controller;

import io.agentscope.core.agent.Agent;
import io.agentscope.core.agent.Event;
import io.agentscope.core.message.Msg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Objects;

@RestController
public class QuickStartController {

    @Autowired
    private Agent agent;

    @GetMapping("/chat")
    public String chat(@RequestParam(defaultValue = "你好，请做个自我介绍") String question) {
        Msg block = agent.call(Msg.builder().textContent(question).build()).block();
        return block.getTextContent();
    }

    /**
     * 流式输出：将 Agent 的事件流映射为文本片段（SSE）。
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(@RequestParam(defaultValue = "你好，请做个自我介绍") String question) {
        Flux<Event> stream = agent.stream(Msg.builder().textContent(question).build());
        return stream.map(event -> {
            Msg msg = event.getMessage();
            return Objects.nonNull(msg) && Objects.nonNull(msg.getTextContent()) ? msg.getTextContent() : "";
        });
    }
}
