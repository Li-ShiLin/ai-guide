package com.action.springai.sa01springaiprimary.controller;

import java.time.LocalDateTime;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FunctionCallingAiController {

    @Autowired
    private ChatModel zhiPuAiChatModel;

    static class DateTimeTools {

        @Tool(description = "获取当前时间")
        String getCurrentDateTime() {
            return LocalDateTime.now()
                    .atZone(LocaleContextHolder.getTimeZone().toZoneId())
                    .toString();
        }

        @Tool(description = "获取用户的生日，入参是用户名，出参是年月日 如 1994年1月1日")
        String getBirthdayDate(String userName) {
            if ("张三".equals(userName)) {
                return "1994年8月29日";
            } else if ("李斯".equals(userName)) {
                return "1994年4月29日";
            } else {
                return "没有这个人的信息";
            }
        }
    }

    @GetMapping("/ai/tools")
    public Object tools(@RequestParam(defaultValue = "下周五是几号") String userInput) {
        // Create ChatClient instances programmatically
        ChatClient zpChatClient = ChatClient.create(zhiPuAiChatModel);
        return zpChatClient.prompt(userInput)
                .advisors(new SimpleLoggerAdvisor())
                .tools(new DateTimeTools())
                .call().content();
    }
}
