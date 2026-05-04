package com.action.agentscope.sc01agentscope.init;

import io.agentscope.core.agent.Agent;
import io.agentscope.core.message.Msg;
import io.agentscope.core.studio.StudioManager;
import io.agentscope.core.studio.StudioUserAgent;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Studio 控制台对话循环（与 HTTP {@code /chat} 等并行）。
 * <p>
 * Studio 初始化见 {@link StudioLifecycle}；{@link Agent} 使用 {@link Lazy}，避免与 Bean 工厂阶段形成循环依赖。
 * <p>
 * 工具 {@link com.action.agentscope.sc01agentscope.tool.DateTools} 在 {@link com.action.agentscope.sc01agentscope.config.ReactAgentConfig} 中注册给 Agent。
 */
@Component("studioAgentRunner")
public class StudioAgentRunner implements ApplicationRunner {

    private final Agent agent;

    public StudioAgentRunner(@Lazy Agent agent) {
        this.agent = agent;
    }

    @Override
    public void run(ApplicationArguments args) {
        StudioUserAgent user = StudioUserAgent.builder()
                .name("User")
                .studioClient(StudioManager.getClient())
                .webSocketClient(StudioManager.getWebSocketClient())
                .build();

        System.out.println("Starting conversation (type 'exit' to quit)");
        System.out.println("Open http://192.168.56.15:3000 to interact\n");

        Msg msg = null;
        int turn = 1;
        while (true) {
            System.out.println("[Turn " + turn + "] Waiting for user input...");
            msg = user.call(msg).block();

            if (Objects.isNull(msg) || "exit".equalsIgnoreCase(msg.getTextContent())) {
                System.out.println("\nConversation ended");
                break;
            }

            System.out.println("[Turn " + turn + "] User: " + msg.getTextContent());
            msg = agent.call(msg).block();

            if (Objects.nonNull(msg)) {
                System.out.println("[Turn " + turn + "] Agent: "
                        + msg.getTextContent() + "\n");
            }
            turn++;
        }
    }
}
