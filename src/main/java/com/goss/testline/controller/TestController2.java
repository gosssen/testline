package com.goss.testline.controller;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/gogo")
public class TestController2 {
    @Value("${line.bot.channelToken}")
    private String token;
    @PostMapping("/test1")
    public void pushMessage(String notifyId, String content) {
        Message message = new TextMessage("conten");
        PushMessage pushMessage = new PushMessage("U82327253cb0523741862cc62b7104455", message);
        LineMessagingClient build = LineMessagingClient.builder(token).build();
        build.pushMessage(pushMessage);
    }
}
