package com.action.agentscope.sc01agentscope;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Sc01AgentscopeQuikstartApplication {

    public static void main(String[] args) {
        // 加载 .env 文件
        Dotenv load = Dotenv.configure().ignoreIfMissing().load();
        load.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

        SpringApplication.run(Sc01AgentscopeQuikstartApplication.class, args);
    }
}
