package com.action.springai.sa01springaiprimary.controller;

import java.util.List;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentTransformer;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RagAiController {

    private static final Logger log = LoggerFactory.getLogger(RagAiController.class);

    private final ChatModel zhiPuAiChatModel;
    private final VectorStore vectorStore;
    private final DocumentTransformer documentTransformer;

    @Value("${app.rag.pdf-path}")
    private Resource pdfResource;

    public RagAiController(ChatModel zhiPuAiChatModel, VectorStore vectorStore,
                           DocumentTransformer documentTransformer) {
        this.zhiPuAiChatModel = zhiPuAiChatModel;
        this.vectorStore = vectorStore;
        this.documentTransformer = documentTransformer;
    }

    @PostConstruct
    public void init() {
        if (!pdfResource.exists()) {
            log.warn("RAG 初始化跳过：未找到 PDF 文件 {}", pdfResource);
            return;
        }
        TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(pdfResource);
        List<Document> documents = tikaDocumentReader.get();
        List<Document> transformedDocs = documentTransformer.apply(documents);
        vectorStore.accept(transformedDocs);
        log.info("RAG 向量数据初始化完成，共 {} 个切片", transformedDocs.size());
    }

    @GetMapping("/ai/rag")
    public Object rag(@RequestParam(defaultValue = "我想学习SpringAI，请给出一些教程和代码案例") String userInput) {
        ChatClient zpChatClient = ChatClient.create(zhiPuAiChatModel);
        return zpChatClient.prompt(userInput)
                .advisors(new SimpleLoggerAdvisor(), new QuestionAnswerAdvisor(vectorStore))
                .call().content();
    }
}
