package com.action.agentscope.sc01agentscope.config;

import io.agentscope.core.ReActAgent;
import io.agentscope.core.agent.Agent;
import io.agentscope.core.model.OllamaChatModel;
import io.agentscope.core.studio.StudioManager;
import io.agentscope.core.studio.StudioMessageHook;
import io.agentscope.core.tool.Toolkit;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;

@Configuration
public class ReactAgentConfig {


    @Bean
    @DependsOn("studioLifecycle")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Agent reactAgent(OllamaChatModel ollamaChatModel) {
        Toolkit toolkit = new Toolkit();
        return ReActAgent.builder()
                .name("Hello Agent")
                .hook(new StudioMessageHook(StudioManager.getClient()))
                .toolkit(toolkit)
                .model(ollamaChatModel)
                .build();
    }
}
