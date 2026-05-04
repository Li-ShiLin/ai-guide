package com.action.agentscope.sc01agentscope.init;

import io.agentscope.core.studio.StudioManager;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

/**
 * Studio 连接生命周期（初始化 / 关闭），与 {@link Agent} 解耦，避免与 {@code reactAgent} 形成 {@code @DependsOn} 循环。
 * <p>
 * 必须在创建使用 {@link StudioManager#getClient()} 的 Bean（如带 {@link io.agentscope.core.studio.StudioMessageHook} 的 Agent）之前完成初始化。
 */
@Component("studioLifecycle")
public class StudioLifecycle {

    @PostConstruct
    public void initStudio() {
        StudioManager.init()
                .studioUrl("http://192.168.56.15:3000")
                .project("MyProject")
                .runName("demo_" + System.currentTimeMillis())
                .initialize()
                .block();
    }

    @PreDestroy
    public void destroyStudio() {
        StudioManager.shutdown();
    }
}
