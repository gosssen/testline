package com.goss.testline.controller;

import com.linecorp.bot.messaging.model.TextMessage;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.message.Message;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.spring.boot.handler.annotation.EventMapping;
import com.linecorp.bot.spring.boot.handler.annotation.LineMessageHandler;
import com.linecorp.bot.webhook.model.MessageEvent;
import com.linecorp.bot.webhook.model.TextMessageContent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@LineMessageHandler
public class TestController {



    @EventMapping
    public TextMessage test(MessageEvent messageEvent) {
        var message = (TextMessageContent) messageEvent.message();
        System.out.println(messageEvent.source().userId());
        System.out.println(message.text());
        return new TextMessage("13");
    }

}
