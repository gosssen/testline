package com.goss.testline.controller;

import com.goss.testline.cctv.CctvService;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;

@Slf4j
@LineMessageHandler
@RequiredArgsConstructor
public class CctvController {

    private final CctvService cctvService;
    private final LineMessagingClient lineMessagingClient;

    @EventMapping
    public void handleTextMessageEvent(MessageEvent<TextMessageContent> event) {
        String userId = event.getSource().getUserId();
        String keyword = event.getMessage().getText();
        String replyToken = event.getReplyToken();
        log.info("userId:{}, keyword:{}", userId, keyword);
        reply(replyToken, cctvService.processQuestion(keyword));
    }

    private void reply(String replyToken, Message message) {
        ReplyMessage replyMessage = new ReplyMessage(replyToken, message);
        try {
            lineMessagingClient.replyMessage(replyMessage).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error while replying to message", e);
        }
    }
}