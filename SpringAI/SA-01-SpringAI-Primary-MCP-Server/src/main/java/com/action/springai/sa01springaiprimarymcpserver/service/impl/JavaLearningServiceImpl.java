package com.action.springai.sa01springaiprimarymcpserver.service.impl;

import com.action.springai.sa01springaiprimarymcpserver.service.JavaLearningService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
public class JavaLearningServiceImpl implements JavaLearningService {

    @Override
    @Tool(description = "推荐SpringAI学习资料")
    public String recommendArticle() {
        return "请访问SpringAI官方文档：https://spring.io/projects/spring-ai，了解最新的SpringAI技术和最佳实践。";
    }

    @Override
    @Tool(description = "推荐Java后端实践项目")
    public String recommendVideo() {
        return "请访问GitHub上的SpringAI示例项目：ai-guide，了解如何在Java后端项目中集成和使用SpringAI：";
    }
}
